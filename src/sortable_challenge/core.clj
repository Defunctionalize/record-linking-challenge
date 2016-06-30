(ns sortable-challenge.core
  (:require [clojure.data.json :as json]
            [sortable-challenge.constants :refer :all]
            [sortable-challenge.utils :refer :all]
            [clojure.string :as str]
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
                  ->case-insensitive-pattern
                  ) title)))

(defn remove-contained-items [& products]
  (apply concat
         (for [[a b] (combinations products 2)]
           (let [[a-words b-words]
                 (map (fn [product] (->> product
                                         :model
                                         split-and-isolate-digits
                                         (into #{}))) [a b])]
             (cond
               (superset? a-words b-words) [a]
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

(defn link-records [products listings]
  (->> listings
       (group-by (partial match-listing-to-products products))
       (into {})))

(->> LISTINGS
     (take 1000)
     (link-records PRODUCTS))


(defn divide-and-conquer [number-of-threads func combining-func coll]
  (let [sections (partition-all (/ (count coll) number-of-threads) coll)
        result-chan (chan)]
    (doall (map #(go (>! result-chan (func %))) sections))
    (combining-func (take (count sections) (repeatedly #(<!! result-chan))))))



(->> (concat (map vector (map :manufacturer PRODUCTS) (repeat :product))
             (map vector (map :manufacturer LISTINGS) (repeat :listing)))
     (into #{})
     sort
     )

(def no-diference #(->> % (take 2) (= [nil nil])))

(defn label-pairs [labels & pairs]
  (for [pair pairs]
    (->> pair
         (map vector labels)
         (into {}))))

(defn -main []
  (->> LISTINGS
       (divide-and-conquer 64 #(group-by (partial match-listing-to-products PRODUCTS) %) #(apply merge-with concat %))
       (filter #(-> % first (= :no-definitive-match) not))
       (apply label-pairs [:product_name :listings])
       (map #(json/write-str % :escape-unicode false))
       (map-indexed #(spit "results.txt" (str (if (not (= %1 0)) "\n") %2) :append true))
       time))

(->> [[:a :b] ["hello" "world"]]
     (apply label-pairs)
     (diff [{:a "hello" :b "world"}])
     no-diference
     test/is)

;(->> [{:product_name   "canon_eos_500d",
;       :manufacturer   "canon",
;       :model          "500d",
;       :family         "eos",
;       :announced-date "2009-03-24t20:00:00.000-04:00"}
;      {:product_name   "canon_eos_rebel_t1i",
;       :manufacturer   "canon",
;       :model          "t1i",
;       :family         "rebel",
;       :announced-date "2009-03-24t20:00:00.000-04:00"}]
;     (apply remove-contained-items)
;     (diff ))

;-----------------------------------------------
;-----------CORNER-CASES------------------------
;-----------------------------------------------

(superset? #{"wg" "gps" "1"} #{"wg" "1"} )
(def corner-listing {:title "Fujifilm FinePix S2500HD 12MP Digital Camera with 18x Optical Dual Image Stabilized Zoom BigVALUEInc Accessory Saver 16GB NiMH Bundle",
                     :manufacturer "Fuji",
                     :currency "USD",
                     :price "239.95"})
(def corner-product {:product_name "Fujifilm_FinePix_S2500HD",
                     :manufacturer "Fujifilm",
                     :model "S2500HD",
                     :family "FinePix",
                     :announced-date "2010-02-01T19:00:00.000-05:00"})
;(first corner-case)
;;
;(contains-model? corner-listing corner-product)
;(contains-all-product-words? corner-listing corner-product)
(which-product [corner-product]
               corner-listing)

(nth corner-case 2)

;-----------ACCESSORY---------------------------
;-----------------------------------------------
;{({:product_name "nikon_d3100", :manufacturer "nikon", :model "d3100", :announced-date "2010-08-18t20:00:00.000-04:00"}
;   {:product_name "nikon_d3000", :manufacturer "nikon", :model "d3000", :announced-date "2009-07-29t20:00:00.000-04:00"}) [{:title "nikon 85mm f/3.5 g vr af-s dx ed micro-nikkor lens + uv filter + accessory kit for nikon d300s, d300, d40, d60, d5000, d7000, d90, d3000 & d3100 digital slr cameras",
;                                                                                                                            :manufacturer "nikon",
;                                                                                                                            :currency "usd",
;
;
;;-----------DUPLICATE--------------------------
;-----------------------------------------------                                                                                                                      :price "529.95"}],
; ({:product_name "samsung-sl202",
;   :manufacturer "samsung",
;   :model "sl202",
;   :announced-date "2009-02-16t19:00:00.000-05:00"}
;   {:product_name "samsung_sl202",
;    :manufacturer "samsung",
;    :model "sl202",
;    :announced-date "2009-02-16t19:00:00.000-05:00"}) [{:title "samsung sl202 10mp digital camera with 3x optical zoom and 2.7 inch lcd (pink)",
;                                                        :manufacturer "samsung",
;                                                        :currency "usd",
;                                                        :price "83.76"}
;                                                       {:title "samsung sl202 10mp digital camera with 3x optical zoom and 2.7 inch lcd (pink)",
;                                                        :manufacturer "samsung",
;                                                        :currency "usd",
;                                                        :price "137.75"}],

;-----------THIS BULLSHIT-----------------------
;-----------------------------------------------
; ({:product_name "canon_eos_rebel_t1i",
;   :manufacturer "canon",
;   :model "t1i",
;   :family "rebel",
;   :announced-date "2009-03-24t20:00:00.000-04:00"}
;   {:product_name "canon_eos_500d",
;    :manufacturer "canon",
;    :model "500d",
;    :family "eos",
;    :announced-date "2009-03-24t20:00:00.000-04:00"}) [{:title "canon eos rebel t1i 500d 15.1 mp digital slr camera with canon ef-s 18-55mm is lens + 16 gb memory card + multi-coated uv filter + multi-coated polarizer filter + corel mediaone plus software + 11 piece accessory kit + camera holster case + 3 year warranty repair contract",
;                                                        :manufacturer "canon",
;                                                        :currency "usd",
;                                                        :price "1020.00"}
;                                                       {:title "canon eos rebel t1i 500d 15.1 mp digital slr camera with canon ef-s 18-55mm is lens + canon 75-300mm f/4-5.6 telephoto zoom lens + .42x wide angle lens with macro + +1, +2, +4, +10 4 piece macro close up kit + 16 gb memory card + multi-coated uv filter (2) + multi-coated polarizer filter + digital slave flash + 50\" titanium anodized tripod + deluxe camera bag + 6 piece accessory kit + 3 year warranty repair contract",
;                                                        :manufacturer "canon",
;                                                        :currency "usd",
;                                                        :price "1295.00"}
;                                                       {:title "canon eos rebel t1i 500d 15.1 mp digital slr camera with canon ef-s 18-55mm f/3.5-5.6 is slr lens + tamron 75-300 f/4-5.6 ld lens + .42x wide angle lens with macro + +1, +2, +4, +10 4 piece macro close up kit + 16 gb memory card + multi-coated uv filter (2) + multi-coated polarizer filter + digital slave flash + 50 \" tripod + deluxe padded camera bag + 6 piece accessory kit + 3 year warranty repair contract",
;                                                        :manufacturer "canon",
;                                                        :currency "usd",
;                                                        :price "1294.95"}
;                                                       {:title "canon eos rebel t1i 500d 15.1 mp digital slr camera with canon ef-s 18-55mm f/3.5-5.6 is slr lens + tamron 75-300 f/4-5.6 ld lens + 16 gb memory card + multi-coated uv filter (2) + multi-coated polarizer filter + digital slave flash + 50\" tripod + deluxe padded camera bag + 6 piece accessory kit + 3 year warranty repair contract",
;                                                        :manufacturer "canon",
;                                                        :currency "usd",
;                                                        :price "1252.00"}
;                                                       {:title "20 piece all purpose kit with canon eos rebel t1i 500d 15.1 mp digital slr camera (black body) + sigma 28-300mm f/3.5-6.3 dg if macro aspherical lens + 8 gb memory card + multi-coated 3 piece filter kit + premier holster case + 50\" tripod + 6 piece camera accessory kit + 3 year celltime warranty repair contract",
;                                                        :manufacturer "canon",
;                                                        :currency "usd",
;                                                        :price "1189.95"}],

;-----------CONTAINMENT-------------------------
;-----------------------------------------------

; ({:product_name "pentax-wg-1-gps",
;   :manufacturer "pentax",
;   :model "wg-1 gps",
;   :family "optio",
;   :announced-date "2011-02-06t19:00:00.000-05:00"}
;   {:product_name "pentax-wg-1",
;    :manufacturer "pentax",
;    :model "wg-1",
;    :family "optio",
;    :announced-date "2011-02-06t19:00:00.000-05:00"}) [{:title "pentax optio wg-1 gps 14 mp rugged waterproof digital camera with 5x optical zoom, 2.7-inch lcd and gps funtionality (green )",
;                                                        :manufacturer "pentax canada",
;                                                        :currency "cad",
;                                                        :price "387.33"}]}