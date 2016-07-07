(ns sortable-challenge.core
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.core.async :refer [go <!! >!! <! >! chan onto-chan close!]]
            [clojure.math.combinatorics :refer [combinations]]
            [clojure.set :refer [superset?]])
  (:gen-class))

(defmacro let-> [binding body value] `(let [~binding ~value] ~body))

(def ->case-insensitive-pattern #(->> % (str "(?i)") re-pattern))
(def ->non-capture-group #(str "(?:" % ")"))
(def SEPARATOR-PATTERN-STR "[\\s\\_\\-]")
(def SEPARATOR-PATTERN (->case-insensitive-pattern SEPARATOR-PATTERN-STR))
(def surrounded-by-sep #(str SEPARATOR-PATTERN-STR % SEPARATOR-PATTERN-STR))
(def split-words #(str/split % SEPARATOR-PATTERN))
(defn split-digit-letter-pairs [word]  (str/split word #"(?<=\d)(?=[^\d])|(?<=[^\d])(?=\d)"))
(defn split-and-isolate-digits [string] (->> string split-words (map split-digit-letter-pairs) flatten))
(defn progress->out [message data] (println message) data)

; A word on general strategy for splitting and searching strings.
; In general, we want to treat - _ and whitespace the same.  I refer to them as separators.
; I split anything separated, and I split letters and digits away from each other.
; I refer to these groupings of imporant string components occasionally as "words" e.g. contains-model-words?

(defn ->data [str-list]
  (-> str-list
      (#(str "[" % "]"))
      (json/read-str :key-fn keyword)))

(def LISTINGS (-> "listings.txt" io/resource slurp ->data))
(def PRODUCTS (-> "products.txt" io/resource slurp ->data))

(defn contains-all-family-words? [listing product]
  "does the given listing title contain all the words words listed in the 'family' tag?"
  (let [{:keys [family] :or {family ""}} product
        {:keys [title]} listing
        family-words (split-and-isolate-digits family)]
    (->> family-words
         (map ->non-capture-group)
         (interpose "|")
         (apply str)
         ->case-insensitive-pattern
         (#(re-seq % title))
         (into #{})
         count
         (= (count family-words)))))

(defn contains-model? [listing product]
  "does the listing title contain the model in some form?

  model strings are broken into components, by separating on both whitespace and by the border between digit groups
  and character groups.

  To avoid false positives, model components must be bordered by at least one separator on either side.
  To avoid false negatives, model components may be separated by zero or more separators.
  "
  (let [{:keys [model]} product
        {:keys [title]} listing
        model-pattern (->> model
                           split-and-isolate-digits
                           (interpose (str SEPARATOR-PATTERN-STR "*"))
                           (apply str)
                           surrounded-by-sep
                           ->case-insensitive-pattern)]
    (re-find model-pattern title)))

(defn remove-subset-products [& products]
  "designed for corner cases where two products' model designations are the same, but one designation is more specific

  e.g. Pentax-WG-1-GPS vs Pentax-WG-1.
  We remove the less specific option, because entering this filter means the requirements for the both products
  have been filled, therefor the listing must be referring to the more specific designation"
  (let [extract-model-words (fn [product] (->> product :model split-and-isolate-digits (into #{})))]
    (apply concat (for [[a b] (combinations products 2)]
                    (let [[a-words b-words] (map extract-model-words [a b])]
                      (cond (superset? a-words b-words) [a]
                            (superset? b-words a-words) [b]
                            :else [a b]))))))

(def known-aliases-and-misspellings
  {"fuji" "fujifilm"
   "canoon" "canon"
   "hp" "hewlett packard"})

(defn manufacturer-match? [listing product]
  "Determine if the manufacturer of the product and listing match.  Some manufacturers have aliases."
  (str/includes?
    (let [lower-name (str/lower-case (:manufacturer listing))] (known-aliases-and-misspellings lower-name lower-name))
    (let [lower-name (str/lower-case (:manufacturer product))] (known-aliases-and-misspellings lower-name lower-name))))



(defn divide-and-conquer [combining-func number-of-threads func coll]
  "Perform map related work in parallel and combine the results.  Uses cors.async go blocks."
  (let [sections (partition-all (/ (count coll) number-of-threads) coll)
        result-chan (chan)]
    (doall (for [section sections] (go (>! result-chan (func section)))))
    (combining-func (doall (for [section sections] (<!! result-chan))))))

(def divide-and-merge (partial divide-and-conquer #(apply merge-with concat %) 64))

(defn match-type [[products]]
  (cond (->> products count (= 1)) :single-match
        (->> products count (< 1)) :multiple-product-matches
        :else :no-match))

(defn label-pairs [labels & pairs] (for [pair pairs] (->> pair (map vector labels) (into {}))))

(defn ->categories [product-matches]
  "Process data into easily readable categories based on the number of product matches discovered"
  (let [{:keys [single-match multiple-product-matches no-match]} (group-by match-type product-matches)]
    {:single-match             (->> single-match
                                    (map #(vector (:product_name (first (first %))) (second %)))
                                    (apply label-pairs [:product_name :listings]))
     :multiple-product-matches (into {} multiple-product-matches)
     :no-match                 (second (first no-match))}))

(defn prettify-records [records]
  (->> records
       (map #(json/write-str % :escape-unicode false))
       (interpose "\n")
       (apply str)
       (str/trim)))

(defn header [header-str]
  (->> ["===================================================="
        "===================================================="
        header-str
        "===================================================="
        "====================================================\n"]
       (interpose "\n")
       (apply str)))

; These last two functions represent the heart of the functionality.

(defn which-product [products listing]
  "To which of the given products does the given listing correspond?

  To decide, we pipe the products through a couple of filters.
  Filters can be reordered or added to as new corner cases are discovered"
  (->> products
       (filter (partial manufacturer-match? listing))
       (filter (partial contains-model? listing))
       (#(if (-> % count (= 1)) % (filter (partial contains-all-family-words? listing) %)))
       (#(if (-> % count (= 1)) % (apply remove-subset-products %)))))

(defn -main [& [result-output-file secondary-output-file]]
  (let [result-output-file (or result-output-file "results.txt")
        secondary-output-file (or secondary-output-file "secondary.txt")]
    (println "Okay!  Java's done loading clojure.")
    (println (str "So, we'll be writing the primary match results to " result-output-file))
    (println (str "And anything that doesn't get properly matched will be output to " secondary-output-file))
    (->> LISTINGS
         (progress->out "linking records...")
         (divide-and-merge #(group-by (partial which-product PRODUCTS) %))
         (divide-and-merge ->categories)
         (let->
           {:keys [multiple-product-matches single-match no-match]}
           (do
             (spit result-output-file (prettify-records single-match) :append true)
             (println (str "wrote positive match results to " result-output-file))
             (spit secondary-output-file (header "MULTIPLE MATCHES") :append true)
             (spit secondary-output-file (prettify-records multiple-product-matches) :append true)
             (spit secondary-output-file (header "NO SUITABLE MATCH MATCH") :append true)
             (spit secondary-output-file (prettify-records no-match) :append true)
             (println (str "wrote inconclusive and no match results to " secondary-output-file))))
         time)))

(-main)