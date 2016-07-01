(ns sortable-challenge.core
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.core.async :refer [go <!! >!! <! >! chan onto-chan close!]]
            [clojure.math.combinatorics :refer [combinations]]
            [clojure.set :refer [superset?]]
            [clojure.data :refer [diff]]
            [clojure.test :as test]
            )
  (:gen-class))

(def SEPARATOR-PATTERN-STR "[\\s\\_\\-]")
(def ->non-capture-group #(str "(?:" % ")"))
(def surrounded-by-sep #(str SEPARATOR-PATTERN-STR % SEPARATOR-PATTERN-STR))
(defn split-digit-letter-pairs [word]  (str/split word #"(?<=\d)(?=[^\d])|(?<=[^\d])(?=\d)"))
(def ->case-insensitive-pattern #(->> % (str "(?i)") re-pattern))
(def SEPARATOR-PATTERN (->case-insensitive-pattern SEPARATOR-PATTERN-STR))
(def split-words #(str/split % SEPARATOR-PATTERN))
(defn split-and-isolate-digits [string] (->> string split-words (map split-digit-letter-pairs) flatten))

(defn ->data [str-list]
  (-> str-list
      (#(str "[" % "]"))
      (json/read-str :key-fn keyword)))

(def LISTINGS (-> "listings.txt" io/resource slurp ->data))
(def PRODUCTS (-> "products.txt" io/resource slurp ->data))

(defn contains-all-words? [important-words string]
  (->> important-words
       (map ->non-capture-group)
       (interpose "|")
       (apply str)
       ->case-insensitive-pattern
       (#(re-seq % string))
       (into #{})
       count
       (= (count important-words))))

(defn contains-all-product-words? [listing product]
  (let [{:keys [manufacturer model product_name]} product
        {:keys [title]} listing
        product-words    (->> product_name
                              split-and-isolate-digits
                              (filter #(not (= manufacturer %)))
                              (filter (->> model
                                           split-and-isolate-digits
                                           (into #{})
                                           complement)))]
    (if (empty? product-words) true? (contains-all-words? product-words title))))

(defn contains-model? [listing product]
  (let [{:keys [model]} product
        {:keys [title]} listing]
    (re-find (->> model
                  split-words
                  (map split-digit-letter-pairs)
                  flatten
                  (interpose (str SEPARATOR-PATTERN-STR "*"))
                  (apply str)
                  surrounded-by-sep
                  ->case-insensitive-pattern)
             title)))

(defn remove-contained-items [& products]
  (apply concat
         (for [[a b] (combinations products 2)]
           (let [[a-words b-words] (map (fn [product]
                                          (->> product
                                               :model
                                               split-and-isolate-digits
                                               (into #{})))
                                        [a b])]
             (cond (superset? a-words b-words) [a]
                   (superset? b-words a-words) [b]
                   :else [a b])))))

(def known-aliases-and-misspellings
  {"fuji" "fujifilm"
   "canoon" "canon"
   "hp" "hewlett packard"})

(defn manufacturer-match? [listing product]
  (str/includes?
    (let [lower-name (str/lower-case (:manufacturer listing))] (known-aliases-and-misspellings lower-name lower-name))
    (let [lower-name (str/lower-case (:manufacturer product))] (known-aliases-and-misspellings lower-name lower-name))
    ))

(defn which-product [products listing]
  (->> products
       (filter (partial manufacturer-match? listing))
       (filter (partial contains-model? listing))
       (#(if (-> % count (= 1)) % (filter (partial contains-all-product-words? listing) %)))
       (#(if (-> % count (= 1)) % (apply remove-contained-items %)))))

(defn match-listing-to-products [products listing]
  (->> listing
       (which-product products)
       (#(cond
          (->> % count (= 1)) (-> % first :product_name)
          :else :no-definitive-match))
       )
  )

(defn divide-and-conquer [number-of-threads func combining-func coll]
  (let [sections (partition-all (/ (count coll) number-of-threads) coll)
        result-chan (chan)]
    (doall (map #(go (>! result-chan (func %))) sections))
    (combining-func (take (count sections) (repeatedly #(<!! result-chan))))))

(defn label-pairs [labels & pairs]
  (for [pair pairs]
    (->> pair
         (map vector labels)
         (into {}))))

(defn write-to-file! [file contents]
  (with-open [writer (io/writer (io/as-file file) :append true)]
    (binding [*out* writer]
      (println contents))))

(defn link-records [products listings]
  (->> LISTINGS
       (divide-and-conquer 64 #(group-by (partial match-listing-to-products PRODUCTS) %) #(apply merge-with concat %))
       (filter #(-> % first (= :no-definitive-match) not))
       (apply label-pairs [:product_name :listings])))

(defn -main [output-file]
  (->> [PRODUCTS LISTINGS]
       (apply link-records)
       (map #(json/write-str % :escape-unicode false))
       (interpose "\n")
       (apply str)
       (write-to-file! output-file)
       time))

(def example-listing {:title       "Fujifilm FinePix S2500HD 12MP Digital Camera with 18x Optical Dual Image Stabilized Zoom BigVALUEInc Accessory Saver 16GB NiMH Bundle",
                                   :manufacturer "Fuji",
                      :currency     "USD",
                      :price        "239.95"})
(def example-product {:product_name  "Fujifilm_FinePix_S2500HD",
                                     :manufacturer   "Fujifilm",
                      :model          "S2500HD",
                      :family         "FinePix",
                      :announced-date "2010-02-01T19:00:00.000-05:00"})

(def no-difference #(->> % (take 2) (= [nil nil])))

(->> [[:a :b] ["hello" "world"]]
     (apply label-pairs)
     (diff [{:a "hello" :b "world"}])
     no-difference
     test/is)

(->> [{:product_name "pentax-wg-1-gps", :manufacturer "pentax", :model "wg-1 gps", :family "optio", :announced-date "2011-02-06t19:00:00.000-05:00"}
      {:product_name "pentax-wg-1", :manufacturer "pentax", :model "wg-1", :family "optio", :announced-date "2011-02-06t19:00:00.000-05:00"}]
     (apply remove-contained-items)
     (diff [{:product_name "pentax-wg-1-gps", :manufacturer "pentax", :model "wg-1 gps", :family "optio", :announced-date "2011-02-06t19:00:00.000-05:00"}])
     no-difference
     test/is)

(->> [{:product_name "canon_eos_500d", :manufacturer "canon", :model "500d", :family "eos", :announced-date "2009-03-24t20:00:00.000-04:00"}
      {:product_name "canon_eos_rebel_t1i", :manufacturer "canon", :model "t1i", :family "rebel", :announced-date "2009-03-24t20:00:00.000-04:00"}]
     (apply remove-contained-items)
     (diff [{:product_name "canon_eos_500d", :manufacturer "canon", :model "500d", :family "eos", :announced-date "2009-03-24t20:00:00.000-04:00"}
            {:product_name "canon_eos_rebel_t1i", :manufacturer "canon", :model "t1i", :family "rebel", :announced-date "2009-03-24t20:00:00.000-04:00"}])
     no-difference
     test/is)

(->> [[{:product_name "Fujifilm_FinePix_S2500HD", :manufacturer "Fujifilm", :model "S2500HD", :family "FinePix", :announced-date "2010-02-01T19:00:00.000-05:00"}]
      {:title "Fujifilm FinePix S2500HD 12MP Digital Camera with 18x Optical Dual Image Stabilized Zoom BigVALUEInc Accessory Saver 16GB NiMH Bundle", :manufacturer "Fuji", :currency "USD", :price "239.95"}]
     (apply which-product)
     (diff [{:product_name "Fujifilm_FinePix_S2500HD", :manufacturer "Fujifilm", :model "S2500HD", :family "FinePix", :announced-date "2010-02-01T19:00:00.000-05:00"}])
     no-difference
     test/is)

(->> [example-listing example-product]
     (apply contains-model?)
     (diff " S2500HD ")
     no-difference
     test/is)

(->> [example-listing example-product]
     (apply contains-all-product-words?)
     (diff true)
     no-difference
     test/is)

(->> [example-listing example-product]
     (apply manufacturer-match?)
     (diff true)
     no-difference
     test/is)

(->> [example-listing example-product]
     (apply contains-all-product-words?)
     (diff true)
     no-difference
     test/is)