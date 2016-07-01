(ns sortable-challenge.core-test
  (:require [clojure.test :refer :all]
            [clojure.data :refer [diff]]
            [sortable-challenge.core :refer :all]))

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

(deftest label-pars-test
     (->> [[:a :b] ["hello" "world"]]
          (apply label-pairs)
          (diff [{:a "hello" :b "world"}])
          no-difference
          is))

(deftest remove-contained-items-test
     (->> [{:product_name "pentax-wg-1-gps", :manufacturer "pentax", :model "wg-1 gps", :family "optio", :announced-date "2011-02-06t19:00:00.000-05:00"}
           {:product_name "pentax-wg-1", :manufacturer "pentax", :model "wg-1", :family "optio", :announced-date "2011-02-06t19:00:00.000-05:00"}]
          (apply remove-contained-items)
          (diff [{:product_name "pentax-wg-1-gps", :manufacturer "pentax", :model "wg-1 gps", :family "optio", :announced-date "2011-02-06t19:00:00.000-05:00"}])
          no-difference
          is)

     (->> [{:product_name "canon_eos_500d", :manufacturer "canon", :model "500d", :family "eos", :announced-date "2009-03-24t20:00:00.000-04:00"}
           {:product_name "canon_eos_rebel_t1i", :manufacturer "canon", :model "t1i", :family "rebel", :announced-date "2009-03-24t20:00:00.000-04:00"}]
          (apply remove-contained-items)
          (diff [{:product_name "canon_eos_500d", :manufacturer "canon", :model "500d", :family "eos", :announced-date "2009-03-24t20:00:00.000-04:00"}
                 {:product_name "canon_eos_rebel_t1i", :manufacturer "canon", :model "t1i", :family "rebel", :announced-date "2009-03-24t20:00:00.000-04:00"}])
          no-difference
          is))

(deftest which-product-test
     (->> [[{:product_name "Fujifilm_FinePix_S2500HD", :manufacturer "Fujifilm", :model "S2500HD", :family "FinePix", :announced-date "2010-02-01T19:00:00.000-05:00"}]
           {:title "Fujifilm FinePix S2500HD 12MP Digital Camera with 18x Optical Dual Image Stabilized Zoom BigVALUEInc Accessory Saver 16GB NiMH Bundle", :manufacturer "Fuji", :currency "USD", :price "239.95"}]
          (apply which-product)
          (diff [{:product_name "Fujifilm_FinePix_S2500HD", :manufacturer "Fujifilm", :model "S2500HD", :family "FinePix", :announced-date "2010-02-01T19:00:00.000-05:00"}])
          no-difference
          is))

(deftest contains-model?-test
     (->> [example-listing example-product]
          (apply contains-model?)
          (diff " S2500HD ")
          no-difference
          is))

(deftest contains-all-product-words?-test
     (->> [example-listing example-product]
          (apply contains-all-product-words?)
          (diff true)
          no-difference
          is)
     (->> [example-listing example-product]
          (apply contains-all-product-words?)
          (diff true)
          no-difference
          is))

(deftest manufacturer-match?-test
     (->> [example-listing example-product]
          (apply manufacturer-match?)
          (diff true)
          no-difference
          is))

