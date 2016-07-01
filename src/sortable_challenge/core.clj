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

(defn link-records [products listings]
  (->> listings
       (group-by (partial match-listing-to-products products))
       (into {})))

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

(defn -main []
  (->> LISTINGS
       (divide-and-conquer 64 #(group-by (partial match-listing-to-products PRODUCTS) %) #(apply merge-with concat %))
       (filter #(-> % first (= :no-definitive-match) not))
       (apply label-pairs [:product_name :listings])
       (map #(json/write-str % :escape-unicode false))
       (map-indexed #(spit "results.txt" (str (if (not (= %1 0)) "\n") %2) :append true))
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

(->> [PRODUCTS (->> LISTINGS (drop 2000) (take 10))]
     (apply link-records)
     (diff {:no-definitive-match [{:title "Canon EOS Rebel XS 1000D Black SLR 10.1 MP Digital Camera with Canon EF-S 18-55mm f/3.5-5.6 IS SLR Lens + Tamron 75-300 f/4-5.6 LD Lens + Rokinon 500mm F/8 Lens with 2x Converter (=1000mm) + 16 GB Memory Card + Multi-Coated UV Filter (2) + Multi-Coated Polarizer Filter + Digital Slave Flash + 50\" Tripod + Deluxe Padded Camera Bag + 6 Piece Accessory Kit + 3 Year Warranty Repair Contract", :manufacturer "Canon", :currency "USD", :price "929.95"} {:title "Canon EOS Rebel XS (a.k.a. 1000D) SLR Digital Camera Kit with Tamron AF28-80mm F/3.5-5.6 Aspherical Lens and Tamron AF75-300mm F/4-5.6 LD Lens + SSE Best Value 8GB, Carrying Case, Battery & Lens Complete Accessories Package", :manufacturer "Digital", :currency "USD", :price "849.95"} {:title "Canon EOS 60D DSLR Camera with Canon EF-S 18-135mm f/3.5-5.6 IS Lens and Tamron Zoom Telephoto AF 70-300mm f/4-5.6 Di LD Macro Autofocus Lens + SSE Best Value 32GB Accessory Package", :manufacturer "Sunset Electronics", :currency "USD", :price "1899.99"} {:title "Sony Alpha A580/L 16.2MP HD DSLR Camera and 18-55mm F3.5-5.6 Lens with Sony SAL552002 Telephoto Zoom Lens and SAL50F18 50mm Lens + Accessory Kit", :manufacturer "Sony", :currency "USD", :price "1249.00"}], "Canon_EOS_7D" [{:title "Canon EOS 7D 18 MP CMOS Digital SLR Camera with EF-S 18-200mm f/3.5-5.6 IS Standard Zoom Lens + 16GB Deluxe Accessory Kit", :manufacturer "Canon", :currency "USD", :price "2549.99"}], "Canon_EOS_60D" [{:title "Canon EOS 60D Digital SLR Camera / Lens Kit, Black with EF 18-135mm f/3.5-5.6 IS Lens - U.S.A. Warranty - 2 4GB SDHC Memory Cards (total of 8GB), Slinger Camera Bag, Spare LP E6 Lithium-Ion Rehargeable Battery. USB 2.0 SD Card Reader", :manufacturer "Canon", :currency "USD", :price "1387.04"}], "Sony_SLT-A33" [{:title "Sony Alpha SLT-A33 DSLR with Translucent Mirror Technology and 3D Sweep Panorama (Body Only) with Sony SAL75300 75-300mm f/4.5-5.6 Compact Super Telephoto Zoom Lens + 8GB Accessory Kit", :manufacturer "Sony", :currency "USD", :price "869.00"}], "Sony_Alpha_DSLR-A390" [{:title "Sony DSLR-A390 Alpha Digital SLR Camera w/ Sony SAL 18-55mm f/3.5-5.6 DT AF Lens NPFH50 Filter Kit Wide Angle Lens 8GB DavisMAX Super Bundle", :manufacturer "Sony", :currency "USD", :price "699.99"}], "Olympus-E-PL2" [{:title "Olympus Pen E-PL2 Micro 4/3 Interchangeable Digital Camera & 14-42mm II Lens (Silver) with M.Zuiko 40-150mm Lens + 8GB Card + Case + Accessory Kit", :manufacturer "Olympus", :currency "USD", :price "799.95"}], "Canon_EOS_Rebel_T1i" [{:title "Canon EOS Digital Rebel T1i SLR Camera Dental Kit - Economy Version - Black Finish- - with Sigma 105/2.8 Macro Lens, Adorama Macro Ring Flash, Spare LP-E5 Type Battery, AA NiMH Batteries w/Charger, 8GB SD Memory Card, Card Reader, Backpack / Shoulder Bag", :manufacturer "Canon", :currency "USD", :price "1287.45"}]})
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