(ns sortable-challenge.core
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.core.async :refer [go <!! >!! <! >! chan onto-chan close!]]
            [clojure.math.combinatorics :refer [combinations]]
            [clojure.set :refer [superset?]])
  (:gen-class))

(def ->case-insensitive-pattern #(->> % (str "(?i)") re-pattern))
(def ->non-capture-group #(str "(?:" % ")"))
(def SEPARATOR-PATTERN-STR "[\\s\\_\\-]")
(def SEPARATOR-PATTERN (->case-insensitive-pattern SEPARATOR-PATTERN-STR))
(def surrounded-by-sep #(str SEPARATOR-PATTERN-STR % SEPARATOR-PATTERN-STR))
(def split-words #(str/split % SEPARATOR-PATTERN))
(defn split-digit-letter-pairs [word]  (str/split word #"(?<=\d)(?=[^\d])|(?<=[^\d])(?=\d)"))
(defn split-and-isolate-digits [string] (->> string split-words (map split-digit-letter-pairs) flatten))
(defn progress->out [message data] (println message) data)

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

(defn divide-and-conquer [combining-func number-of-threads func coll]
  (let [sections (partition-all (/ (count coll) number-of-threads) coll)
        result-chan (chan)]
    (doall (map #(go (>! result-chan (func %))) sections))
    (combining-func (take (count sections) (repeatedly #(<!! result-chan))))))

(def divide-and-merge (partial divide-and-conquer #(apply merge-with concat %) 8))

(defn label-pairs [labels & pairs]
  (for [pair pairs]
    (->> pair
         (map vector labels)
         (into {}))))

(defn write-to-file! [file contents]
  (with-open [writer (io/writer (io/as-file file) :append true)]
    (binding [*out* writer]
      (println contents))))

(defn match-type [[products]]
  (cond
    (->> products count (= 1)) :single-match
    (->> products count (< 1)) :multiple-product-matches
    :else :no-match
    ))

(defn ->categories [product-matches]
  (let [{:keys [single-match multiple-product-matches no-match]} (group-by match-type product-matches)]
    {:single-match             (->> single-match
                                    (map #(vector (:product_name (first (first %))) (second %)))
                                    (apply label-pairs [:product_name :listings]))
     :multiple-product-matches (into {} multiple-product-matches)
     :no-match                 (second (first no-match))}))

(defn write-results [output-location result-data]
  (->> result-data
       (map #(json/write-str % :escape-unicode false))
       (interpose "\n")
       (apply str)
       (write-to-file! output-location)))

(defn header [header-str]
  (->> ["===================================================="
        "===================================================="
        header-str
        "===================================================="
        "====================================================\n"]
       (interpose "\n")
       (apply str)))


(defn -main [& [result-output-file secondary-output-file]]
  (let [result-output-file (or result-output-file "results.txt")
        secondary-output-file (or secondary-output-file "secondary.txt")]
    (println "Okay!  Java's done loading clojure.")
    (println (str "So, we'll be writing the primary match results to " result-output-file))
    (println (str "And anything that doesn't get properly matched will be output to " secondary-output-file))
    (->> LISTINGS
         (progress->out "linking records...")
         (divide-and-merge #(group-by (partial which-product PRODUCTS) %))
         (progress->out "categorizing output..")
         (divide-and-merge ->categories)
         (#(let [{:keys [multiple-product-matches single-match no-match]} %]
            (println "preparing positive match data for write")
            (write-results result-output-file single-match)
            (println (str "wrote positive match results to " result-output-file))

            (println "preparing secondary data for write")
            (spit secondary-output-file (header "MULTIPLE MATCHES") :append true)
            (write-results secondary-output-file multiple-product-matches)
            (spit secondary-output-file (header "NO SUITABLE MATCH MATCH") :append true)
            (write-results secondary-output-file no-match)
            (println (str "writing inconclusive and no match results to " secondary-output-file))))
         time)))