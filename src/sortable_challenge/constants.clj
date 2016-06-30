(ns sortable-challenge.constants
  (:require [schema.core :as schema :include-macros true]
            [clojure.test :as test]
            [clojure.data.json :as json]
            [clojure.string :as str]
            [clojure.java.io :as io]))

; 2011-04-28T19:00:00.000-05:00

;(def ISO-8601-DATETIME-FORMAT
;  ; taken from https://gist.github.com/musubu/2596655
;  #"^([\+-]?\d{4}(?!\d{2}\b))((-?)((0[1-9]|1[0-2])(\3([12]\d|0[1-9]|3[01]))?|W([0-4]\d|5[0-2])(-?[1-7])?|(00[1-9]|0[1-9]\d|[12]\d{2}|3([0-5]\d|6[1-6])))([T\s]((([01]\d|2[0-3])((:?)[0-5]\d)?|24\:?00)([\.,]\d+(?!:))?)?(\17[0-5]\d([\.,]\d+)?)?([zZ]|([\+-])([01]\d|2[0-3]):?([0-5]\d)?)?)?)?$")
;
;(def ISO-4127-CURRENCY-CODE-FORMAT
;  ; taken from https://coderwall.com/p/eoftpq/iso-4127-regular-expression
;  #"^(AED|AFN|ALL|AMD|ANG|AOA|ARS|AUD|AWG|AZN|BAM|BBD|BDT|BGN|BHD|BIF|BMD|BND|BOB|BOV|BRL|BSD|BTN|BWP|BYR|BZD|CAD|CDF|CHE|CHF|CHW|CLF|CLP|CNY|COP|COU|CRC|CUC|CUP|CVE|CZK|DJF|DKK|DOP|DZD|EGP|ERN|ETB|EUR|FJD|FKP|GBP|GEL|GHS|GIP|GMD|GNF|GTQ|GYD|HKD|HNL|HRK|HTG|HUF|IDR|ILS|INR|IQD|IRR|ISK|JMD|JOD|JPY|KES|KGS|KHR|KMF|KPW|KRW|KWD|KYD|KZT|LAK|LBP|LKR|LRD|LSL|LTL|LVL|LYD|MAD|MDL|MGA|MKD|MMK|MNT|MOP|MRO|MUR|MVR|MWK|MXN|MXV|MYR|MZN|NAD|NGN|NIO|NOK|NPR|NZD|OMR|PAB|PEN|PGK|PHP|PKR|PLN|PYG|QAR|RON|RSD|RUB|RWF|SAR|SBD|SCR|SDG|SEK|SGD|SHP|SLL|SOS|SRD|SSP|STD|SVC|SYP|SZL|THB|TJS|TMT|TND|TOP|TRY|TTD|TWD|TZS|UAH|UGX|USD|USN|USS|UYI|UYU|UZS|VEF|VND|VUV|WST|XAF|XAG|XAU|XBA|XBB|XBC|XBD|XCD|XDR|XFU|XOF|XPD|XPF|XPT|XSU|XTS|XUA|XXX|YER|ZAR|ZMW|ZWL)$")
;
;(def SIMPLE-PRICE-FORMAT
;  #"[0-9]+([,.][0-9]{1,2})?")
;
;(defn FormattedString [format] (schema/pred #(re-seq  format %)))
;
;(def DateTimeString (FormattedString ISO-8601-DATETIME-FORMAT))
;(def CurrencyCodeString (FormattedString ISO-4127-CURRENCY-CODE-FORMAT))
;(def PriceString (FormattedString SIMPLE-PRICE-FORMAT))
;
;(def Product
;  {:product_name schema/Str
;   :manufacturer schema/Str
;   (schema/optional-key :family) schema/Str
;   :announed-date DateTimeString})
;
;(def Listing
;  {:title schema/Str
;   :manufacturer schema/Str
;   :currency CurrencyCodeString
;   :price PriceString})
;
;(def Result
;  {:product_name schema/Str
;   :listings [Listing]})
;
;
;
;(->> "2011-04-28T19:00:00.000-05:00"
;    (schema/validate DateTimeString)
;     test/is)
;
;(->> {:product_name "a;wrlgknrea;gn"
;      :manufacturer "lkgnjslgekn"
;      :family "wejkgwkgn"
;      :announed-date "2011-04-28T19:00:00.000-05:00"}
;     (schema/validate Product)
;     test/is)
;
;(->> {:title "kuhgsaeuhbgseikgbh"
;      :manufacturer "ksrbhgksrbg"
;      :currency "USD"
;      :price "1.99"}
;     (schema/validate Listing)
;     test/is)
;
;(->> {:product_name "kwurhbgwirkgh"
;      :listings [{:title "kuhgsaeuhbgseikgbh"
;                  :manufacturer "ksrbhgksrbg"
;                  :currency "USD"
;                  :price "1.99"}]}
;     (schema/validate Result))

(defn ->data [str-list]
  (-> str-list
      (#(str "[" % "]"))
      ;str/lower-case
      (json/read-str :key-fn keyword)))

(def LISTINGS (-> "listings.txt"
                  io/resource
                  slurp
                  ->data))
(def PRODUCTS (-> "products.txt"
                  io/resource
                  slurp
                  ->data))

(def corner-case (-> "corner_cases.txt"
                     io/resource
                     slurp
                     ->data))

(def sample-products (->data
                       "{\"product_name\":\"Sony_Cyber-shot_DSC-W310\",\"manufacturer\":\"Sony\",\"model\":\"DSC-W310\",\"family\":\"Cyber-shot\",\"announced-date\":\"2010-01-06T19:00:00.000-05:00\"}
                     {\"product_name\":\"Samsung_TL240\",\"manufacturer\":\"Samsung\",\"model\":\"TL240\",\"announced-date\":\"2010-01-05T19:00:00.000-05:00\"}
                     {\"product_name\":\"Nikon-s6100\",\"manufacturer\":\"Nikon\",\"model\":\"S6100\",\"family\":\"Coolpix\",\"announced-date\":\"2011-02-08T19:00:00.000-05:00\"}
                     {\"product_name\":\"Samsung_TL220\",\"manufacturer\":\"Samsung\",\"model\":\"TL220\",\"announced-date\":\"2009-08-12T20:00:00.000-04:00\"}
                     {\"product_name\":\"Fujifilm-T205\",\"manufacturer\":\"Fujifilm\",\"model\":\"T205\",\"family\":\"FinePix\",\"announced-date\":\"2011-02-15T19:00:00.000-05:00\"}
                     {\"product_name\":\"Casio_QV-5000SX\",\"manufacturer\":\"Casio\",\"model\":\"QV-5000SX\",\"announced-date\":\"1998-04-19T20:00:00.000-04:00\"}
                     {\"product_name\":\"Canon_Digital_IXUS_130_IS\",\"manufacturer\":\"Canon\",\"model\":\"130 IS\",\"family\":\"Digital IXUS\",\"announced-date\":\"2010-02-07T19:00:00.000-05:00\"}
                     {\"product_name\":\"Leica_Digilux\",\"manufacturer\":\"Leica\",\"model\":\"Digilux\",\"announced-date\":\"1998-09-15T20:00:00.000-04:00\"}
                     {\"product_name\":\"Fujifilm_FinePix_1500\",\"manufacturer\":\"Fujifilm\",\"model\":\"1500\",\"family\":\"FinePix\",\"announced-date\":\"1999-05-17T20:00:00.000-04:00\"}
                     {\"product_name\":\"Sony-HX100v\",\"manufacturer\":\"Sony\",\"model\":\"DSC-HX100v\",\"family\":\"Cybershot\",\"announced-date\":\"2011-02-01T19:00:00.000-05:00\"}
                     {\"product_name\":\"Fujifilm-Z900EXR\",\"manufacturer\":\"Fujifilm\",\"model\":\"Z900EXR\",\"family\":\"Finepix\",\"announced-date\":\"2011-04-04T20:00:00.000-04:00\"}
                     {\"product_name\":\"Olympus_mju_9010\",\"manufacturer\":\"Olympus\",\"model\":\"mju 9010\",\"announced-date\":\"2010-01-06T19:00:00.000-05:00\"}
                     {\"product_name\":\"Canon_PowerShot_SD980_IS\",\"manufacturer\":\"Canon\",\"model\":\"SD980 IS\",\"family\":\"PowerShot\",\"announced-date\":\"2009-08-18T20:00:00.000-04:00\"}
                     {\"product_name\":\"Canon-IXUS-310HS\",\"manufacturer\":\"Canon\",\"model\":\"310 HS\",\"family\":\"IXUS\",\"announced-date\":\"2011-02-06T19:00:00.000-05:00\"}
                     {\"product_name\":\"Casio_Exilim_EX-H20G\",\"manufacturer\":\"Casio\",\"model\":\"EX-H20g\",\"family\":\"Exilim\",\"announced-date\":\"2010-09-19T20:00:00.000-04:00\"}
                     {\"product_name\":\"Olympus-T-110\",\"manufacturer\":\"Olympus\",\"model\":\"T110\",\"announced-date\":\"2010-08-17T20:00:00.000-04:00\"}
                     {\"product_name\":\"Kodak_EasyShare_M575\",\"manufacturer\":\"Kodak\",\"model\":\"M575\",\"family\":\"EasyShare\",\"announced-date\":\"2010-01-04T19:00:00.000-05:00\"}
                     {\"product_name\":\"Toshiba_PDR-M60\",\"manufacturer\":\"Toshiba\",\"model\":\"PDR-M60\",\"announced-date\":\"2000-02-02T19:00:00.000-05:00\"}
                     {\"product_name\":\"Sony_Alpha_NEX-3\",\"manufacturer\":\"Sony\",\"model\":\"NEX-3\",\"family\":\"Alpha\",\"announced-date\":\"2010-05-10T20:00:00.000-04:00\"}
                     {\"product_name\":\"Casio_Exilim_EX-Z29\",\"manufacturer\":\"Casio\",\"model\":\"EX-Z29\",\"family\":\"Exilim\",\"announced-date\":\"2009-03-02T19:00:00.000-05:00\"}
                     {\"product_name\":\"Sony-TX10\",\"manufacturer\":\"Sony\",\"model\":\"TX10\",\"family\":\"Cybershot\",\"announced-date\":\"2011-02-15T19:00:00.000-05:00\"}
                     {\"product_name\":\"Canon_Digital_IXUS_95_IS\",\"manufacturer\":\"Canon\",\"model\":\"95 IS\",\"family\":\"Digital IXUS\",\"announced-date\":\"2009-02-17T19:00:00.000-05:00\"}
                     {\"product_name\":\"Fujifilm-AX305\",\"manufacturer\":\"Fujifilm\",\"model\":\"AX305\",\"family\":\"FinePix\",\"announced-date\":\"2011-02-15T19:00:00.000-05:00\"}
                     {\"product_name\":\"Nikon_Coolpix_900S\",\"manufacturer\":\"Nikon\",\"model\":\"900S\",\"family\":\"Coolpix\",\"announced-date\":\"1998-10-25T19:00:00.000-05:00\"}
                     {\"product_name\":\"Casio_Exilim_EX-FC100\",\"manufacturer\":\"Casio\",\"model\":\"EX-FC100\",\"family\":\"Exilim\",\"announced-date\":\"2009-01-07T19:00:00.000-05:00\"}
                     {\"product_name\":\"Samsung_HZ15W\",\"manufacturer\":\"Samsung\",\"model\":\"HZ15W\",\"announced-date\":\"2009-02-22T19:00:00.000-05:00\"}
                     {\"product_name\":\"Sony_Cyber-shot_DSC-WX5\",\"manufacturer\":\"Sony\",\"model\":\"DSC-WX5\",\"family\":\"Cyber-shot\",\"announced-date\":\"2010-07-07T20:00:00.000-04:00\"}
                     {\"product_name\":\"Agfa_ePhoto_1280\",\"manufacturer\":\"Agfa\",\"model\":\"1280\",\"family\":\"ePhoto\",\"announced-date\":\"1997-10-08T20:00:00.000-04:00\"}
                     {\"product_name\":\"Fujifilm_FinePix_A170\",\"manufacturer\":\"Fujifilm\",\"model\":\"A170\",\"family\":\"FinePix\",\"announced-date\":\"2009-07-21T20:00:00.000-04:00\"}
                     {\"product_name\":\"Casio_QV-5500SX\",\"manufacturer\":\"Casio\",\"model\":\"QV-5500SX\",\"announced-date\":\"1999-07-19T20:00:00.000-04:00\"}
                     {\"product_name\":\"Casio_Exilim_EX-Z2000\",\"manufacturer\":\"Casio\",\"model\":\"EX-Z2000\",\"family\":\"Exilim\",\"announced-date\":\"2010-01-05T19:00:00.000-05:00\"}
                     {\"product_name\":\"Kodak_EasyShare_C140\",\"manufacturer\":\"Kodak\",\"model\":\"C140\",\"family\":\"EasyShare\",\"announced-date\":\"2009-01-07T19:00:00.000-05:00\"}
                     {\"product_name\":\"Samsung_ST10\",\"manufacturer\":\"Samsung\",\"model\":\"ST10\",\"announced-date\":\"2009-01-07T19:00:00.000-05:00\"}
                     {\"product_name\":\"Casio_QV-2000UX\",\"manufacturer\":\"Casio\",\"model\":\"QV-2000UX\",\"announced-date\":\"1999-07-19T20:00:00.000-04:00\"}
                     {\"product_name\":\"Sony_Cyber-shot_DSC-S930\",\"manufacturer\":\"Sony\",\"model\":\"DSC-S930\",\"family\":\"Cyber-shot\",\"announced-date\":\"2009-01-07T19:00:00.000-05:00\"}
                     {\"product_name\":\"Fujifilm_FinePix_S2500HD\",\"manufacturer\":\"Fujifilm\",\"model\":\"S2500HD\",\"family\":\"FinePix\",\"announced-date\":\"2010-02-01T19:00:00.000-05:00\"}
                     {\"product_name\":\"Fujifilm-AX300\",\"manufacturer\":\"Fujifilm\",\"model\":\"AX300\",\"family\":\"FinePix\",\"announced-date\":\"2011-02-15T19:00:00.000-05:00\"}
                     {\"product_name\":\"Canon_PowerShot_A5_Zoom\",\"manufacturer\":\"Canon\",\"model\":\"A5 Zoom\",\"family\":\"PowerShot\",\"announced-date\":\"1999-02-17T19:00:00.000-05:00\"}
                     {\"product_name\":\"Panasonic_Lumix_DMC-FX580\",\"manufacturer\":\"Panasonic\",\"model\":\"DMC-FX580\",\"family\":\"Lumix\",\"announced-date\":\"2009-01-26T19:00:00.000-05:00\"}
                     {\"product_name\":\"Samsung_PL100\",\"manufacturer\":\"Samsung\",\"model\":\"PL100\",\"announced-date\":\"2010-01-05T19:00:00.000-05:00\"}
                     {\"product_name\":\"Samsung-ES70\",\"manufacturer\":\"Samsung\",\"model\":\"ES70\",\"announced-date\":\"2009-01-07T19:00:00.000-05:00\"}
                     {\"product_name\":\"Fujifilm_FinePix_S1500\",\"manufacturer\":\"Fujifilm\",\"model\":\"S1500\",\"family\":\"FinePix\",\"announced-date\":\"2009-02-16T19:00:00.000-05:00\"}
                     {\"product_name\":\"Nikon_Coolpix_L100\",\"manufacturer\":\"Nikon\",\"model\":\"L100\",\"family\":\"Coolpix\",\"announced-date\":\"2009-02-02T19:00:00.000-05:00\"}
                     {\"product_name\":\"Casio_QV-7000SX\",\"manufacturer\":\"Casio\",\"model\":\"QV-7000SX\",\"announced-date\":\"1998-08-17T20:00:00.000-04:00\"}
                     {\"product_name\":\"Olympus_C-2500_L\",\"manufacturer\":\"Olympus\",\"model\":\"C-2500 L\",\"announced-date\":\"1999-03-17T19:00:00.000-05:00\"}
                     {\"product_name\":\"Olympus_FE-5010\",\"manufacturer\":\"Olympus\",\"model\":\"FE-5010\",\"announced-date\":\"2009-01-06T19:00:00.000-05:00\"}
                     {\"product_name\":\"Fujifilm_FinePix_4700_Zoom\",\"manufacturer\":\"Fujifilm\",\"model\":\"4700 Zoom\",\"family\":\"FinePix\",\"announced-date\":\"2000-01-30T19:00:00.000-05:00\"}
                     {\"product_name\":\"Fujifilm_FinePix_A100\",\"manufacturer\":\"Fujifilm\",\"model\":\"A100\",\"family\":\"FinePix\",\"announced-date\":\"2009-02-03T19:00:00.000-05:00\"}
                     {\"product_name\":\"Olympus_C-2000_Zoom\",\"manufacturer\":\"Olympus\",\"model\":\"C-2000 Zoom\",\"announced-date\":\"1999-02-15T19:00:00.000-05:00\"}
                     {\"product_name\":\"Olympus_mju_Tough_8010\",\"manufacturer\":\"Olympus\",\"model\":\"mju Tough 8010\",\"announced-date\":\"2010-02-01T19:00:00.000-05:00\"}
                     {\"product_name\":\"Canon_PowerShot_A5\",\"manufacturer\":\"Canon\",\"model\":\"A5\",\"family\":\"PowerShot\",\"announced-date\":\"1998-03-26T19:00:00.000-05:00\"}
                     {\"product_name\":\"Casio_Exilim_EX-Z400\",\"manufacturer\":\"Casio\",\"model\":\"EX-Z400\",\"family\":\"Exilim\",\"announced-date\":\"2009-01-07T19:00:00.000-05:00\"}
                     {\"product_name\":\"Casio_Exilim_EX-FC150\",\"manufacturer\":\"Casio\",\"model\":\"EX-FC150\",\"family\":\"Exilim\",\"announced-date\":\"2009-11-15T19:00:00.000-05:00\"}
                     {\"product_name\":\"Panasonic-TZ20\",\"manufacturer\":\"Panasonic\",\"model\":\"DMC-TZ20\",\"family\":\"Lumix\",\"announced-date\":\"2011-01-24T19:00:00.000-05:00\"}
                     {\"product_name\":\"Panasonic-DMC-S1\",\"manufacturer\":\"Panasonic\",\"model\":\"DMC-S1\",\"family\":\"Lumix\",\"announced-date\":\"2011-01-04T19:00:00.000-05:00\"}
                     {\"product_name\":\"Canon_EOS_60D\",\"manufacturer\":\"Canon\",\"model\":\"60D\",\"family\":\"EOS\",\"announced-date\":\"2010-08-25T20:00:00.000-04:00\"}
                     {\"product_name\":\"Epson_PhotoPC_700\",\"manufacturer\":\"Epson\",\"model\":\"700\",\"family\":\"PhotoPC\",\"announced-date\":\"1998-07-04T20:00:00.000-04:00\"}
                     {\"product_name\":\"Casio-EX-ZR100\",\"manufacturer\":\"Casio\",\"model\":\"ZR100\",\"family\":\"Exilim\",\"announced-date\":\"2011-02-14T19:00:00.000-05:00\"}
                     {\"product_name\":\"Pentax_Optio_WS80\",\"manufacturer\":\"Pentax\",\"model\":\"WS80\",\"family\":\"Optio\",\"announced-date\":\"2009-08-04T20:00:00.000-04:00\"}
                     {\"product_name\":\"Samsung-PL51\",\"manufacturer\":\"Samsung\",\"model\":\"PL51\",\"announced-date\":\"2010-01-19T19:00:00.000-05:00\"}
                     {\"product_name\":\"Kodak_DC3400\",\"manufacturer\":\"Kodak\",\"model\":\"DC3400\",\"announced-date\":\"2000-07-31T20:00:00.000-04:00\"}
                     {\"product_name\":\"HP_Photosmart_C618\",\"manufacturer\":\"HP\",\"model\":\"C618\",\"family\":\"Photosmart\",\"announced-date\":\"2000-02-02T19:00:00.000-05:00\"}
                     {\"product_name\":\"Casio_QV-2300UX\",\"manufacturer\":\"Casio\",\"model\":\"QV-2300UX\",\"announced-date\":\"2000-06-21T20:00:00.000-04:00\"}
                     {\"product_name\":\"Panasonic_Lumix_DMC-TZ8\",\"manufacturer\":\"Panasonic\",\"model\":\"DMC-TZ8\",\"family\":\"Lumix\",\"announced-date\":\"2010-01-25T19:00:00.000-05:00\"}
                     {\"product_name\":\"Nikon-s4100\",\"manufacturer\":\"Nikon\",\"model\":\"S4100\",\"family\":\"Coolpix\",\"announced-date\":\"2011-02-08T19:00:00.000-05:00\"}
                     {\"product_name\":\"Casio_QV-3000EX\",\"manufacturer\":\"Casio\",\"model\":\"QV-3000EX\",\"announced-date\":\"2000-01-03T19:00:00.000-05:00\"}
                     {\"product_name\":\"Nikon_Coolpix_100\",\"manufacturer\":\"Nikon\",\"model\":\"100\",\"family\":\"Coolpix\",\"announced-date\":\"1997-01-19T19:00:00.000-05:00\"}
                     {\"product_name\":\"Panasonic_Lumix_DMC-FX48\",\"manufacturer\":\"Panasonic\",\"model\":\"DMC-FX48\",\"family\":\"Lumix\",\"announced-date\":\"2009-01-26T19:00:00.000-05:00\"}
                     {\"product_name\":\"Kodak_DC200_plus\",\"manufacturer\":\"Kodak\",\"model\":\"DC200 plus\",\"announced-date\":\"1999-02-25T19:00:00.000-05:00\"}
                     {\"product_name\":\"Fujifilm-JX200\",\"manufacturer\":\"Fujifilm\",\"model\":\"JX200\",\"family\":\"FinePix\",\"announced-date\":\"2010-02-02T19:00:00.000-05:00\"}
                     {\"product_name\":\"Samsung_TL350\",\"manufacturer\":\"Samsung\",\"model\":\"TL350\",\"announced-date\":\"2010-02-19T19:00:00.000-05:00\"}
                     {\"product_name\":\"Kodak_DC260\",\"manufacturer\":\"Kodak\",\"model\":\"DC260\",\"announced-date\":\"1998-05-19T20:00:00.000-04:00\"}
                     {\"product_name\":\"Kyocera_Finecam_3300\",\"manufacturer\":\"Kyocera\",\"model\":\"3300\",\"family\":\"Finecam\",\"announced-date\":\"2000-07-17T20:00:00.000-04:00\"}
                     {\"product_name\":\"Olympus_PEN_E-P2\",\"manufacturer\":\"Olympus\",\"model\":\"PEN E-P2\",\"announced-date\":\"2009-11-04T19:00:00.000-05:00\"}
                     {\"product_name\":\"Sony_Cyber-shot_DSC-D700\",\"manufacturer\":\"Sony\",\"model\":\"DSC-D700\",\"family\":\"Cyber-shot\",\"announced-date\":\"1998-09-26T20:00:00.000-04:00\"}
                     {\"product_name\":\"Panasonic_Lumix_DMC-FX70\",\"manufacturer\":\"Panasonic\",\"model\":\"DMC-FX70\",\"family\":\"Lumix\",\"announced-date\":\"2010-05-31T20:00:00.000-04:00\"}
                     {\"product_name\":\"Kodak_EasyShare_M550\",\"manufacturer\":\"Kodak\",\"model\":\"M550\",\"family\":\"EasyShare\",\"announced-date\":\"2010-01-04T19:00:00.000-05:00\"}
                     {\"product_name\":\"Kodak_EasyShare_Z981\",\"manufacturer\":\"Kodak\",\"model\":\"Z981\",\"family\":\"EasyShare\",\"announced-date\":\"2010-01-04T19:00:00.000-05:00\"}
                     {\"product_name\":\"Nikon_Coolpix_S5100\",\"manufacturer\":\"Nikon\",\"model\":\"S5100\",\"family\":\"Coolpix\",\"announced-date\":\"2010-08-16T20:00:00.000-04:00\"}
                     {\"product_name\":\"Canon-A3300-IS\",\"manufacturer\":\"Canon\",\"model\":\"A3300 IS\",\"family\":\"PowerShot\",\"announced-date\":\"2011-01-04T19:00:00.000-05:00\"}
                     {\"product_name\":\"Pentax_Optio_E70\",\"manufacturer\":\"Pentax\",\"model\":\"E70\",\"family\":\"Optio\",\"announced-date\":\"2009-01-04T19:00:00.000-05:00\"}
                     {\"product_name\":\"Sony_Cyber-shot_DSC-H20\",\"manufacturer\":\"Sony\",\"model\":\"DSC-H20\",\"family\":\"Cyber-shot\",\"announced-date\":\"2009-02-16T19:00:00.000-05:00\"}
                     {\"product_name\":\"Canon_Digital_IXUS_200_IS\",\"manufacturer\":\"Canon\",\"model\":\"200 IS\",\"family\":\"Digital IXUS\",\"announced-date\":\"2009-08-18T20:00:00.000-04:00\"}
                     {\"product_name\":\"Kodak_EasyShare_M381\",\"manufacturer\":\"Kodak\",\"model\":\"M381\",\"family\":\"EasyShare\",\"announced-date\":\"2009-07-28T20:00:00.000-04:00\"}
                     {\"product_name\":\"Fujifilm_FinePix_4900_Zoom\",\"manufacturer\":\"Fujifilm\",\"model\":\"4900 Zoom\",\"family\":\"FinePix\",\"announced-date\":\"2000-07-31T20:00:00.000-04:00\"}
                     {\"product_name\":\"Samsung_CL5\",\"manufacturer\":\"Samsung\",\"model\":\"CL5\",\"announced-date\":\"2009-02-22T19:00:00.000-05:00\"}
                     {\"product_name\":\"Leica_Digilux_4.3\",\"manufacturer\":\"Leica\",\"model\":\"4.3\",\"family\":\"Digilux\",\"announced-date\":\"2000-08-31T20:00:00.000-04:00\"}
                     {\"product_name\":\"Samsung_ES55\",\"manufacturer\":\"Samsung\",\"model\":\"ES55\",\"announced-date\":\"2009-01-07T19:00:00.000-05:00\"}
                     {\"product_name\":\"Nikon_Coolpix_950\",\"manufacturer\":\"Nikon\",\"model\":\"950\",\"family\":\"Coolpix\",\"announced-date\":\"1999-02-14T19:00:00.000-05:00\"}
                     {\"product_name\":\"Canon-A3200-IS\",\"manufacturer\":\"Canon\",\"model\":\"A3200 IS\",\"family\":\"PowerShot\",\"announced-date\":\"2011-01-04T19:00:00.000-05:00\"}
                     {\"product_name\":\"Fujifilm_FinePix_2400_Zoom\",\"manufacturer\":\"Fujifilm\",\"model\":\"2400 Zoom\",\"family\":\"FinePix\",\"announced-date\":\"2000-09-05T20:00:00.000-04:00\"}
                     {\"product_name\":\"Fujifilm_FinePix_JZ500\",\"manufacturer\":\"Fujifilm\",\"model\":\"JZ500\",\"family\":\"FinePix\",\"announced-date\":\"2010-02-01T19:00:00.000-05:00\"}
                     {\"product_name\":\"Panasonic_Lumix_DMC-FH22\",\"manufacturer\":\"Panasonic\",\"model\":\"DMC-FH22\",\"family\":\"Lumix\",\"announced-date\":\"2010-01-05T19:00:00.000-05:00\"}
                     {\"product_name\":\"Panasonic_Lumix_DMC-FX60\",\"manufacturer\":\"Panasonic\",\"model\":\"DMC-FX60\",\"family\":\"Lumix\",\"announced-date\":\"2009-07-26T20:00:00.000-04:00\"}"))



(def sample-listings (->data

                       "{\"title\":\"LED Flash Macro Ring Light (48 X LED) with 6 Adapter Rings for For Canon/Sony/Nikon/Sigma Lenses\",\"manufacturer\":\"Neewer Electronics Accessories\",\"currency\":\"CAD\",\"price\":\"35.99\"}
                       {\"title\":\"Canon PowerShot SX130IS 12.1 MP Digital Camera with 12x Wide Angle Optical Image Stabilized Zoom with 3.0-Inch LCD\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"199.96\"}
                       {\"title\":\"Canon PowerShot SX130IS 12.1 MP Digital Camera with 12x Wide Angle Optical Image Stabilized Zoom with 3.0-Inch LCD\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"209.00\"}
                       {\"title\":\"Canon PowerShot D10 12.1 MP Waterproof Digital Camera with 3x Optical Image Stabilized Zoom and 2.5-inch LCD (Blue/Silver)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"306.24\"}
                       {\"title\":\"Canon PowerShot D10 12.1 MP Waterproof Digital Camera with 3x Optical Image Stabilized Zoom and 2.5-inch LCD (Blue/Silver)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"420.33\"}
                       {\"title\":\"Genuine Samsung EB575152VU i9000 GalaxyS Battery\",\"manufacturer\":\"Samsung\",\"currency\":\"CAD\",\"price\":\"13.99\"}
                       {\"title\":\"Canon PowerShot A1200 (Black)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"129.99\"}
                       {\"title\":\"Canon PowerShot A495 10.1 MP Digital Camera with 3.3x Optical Zoom and 2.5-Inch LCD (Blue)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"88.00\"}
                       {\"title\":\"Canon PowerShot A495 10.1 MP Digital Camera with 3.3x Optical Zoom and 2.5-Inch LCD (Blue)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"129.92\"}
                       {\"title\":\"Canon PowerShot ELPH 300 HS (Black)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"259.99\"}
                       {\"title\":\"Canon PowerShot ELPH 300 HS (Black)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"289.00\"}
                       {\"title\":\"Canon PowerShot SD1400IS 14.1 MP Digital Camera with 4x Wide Angle Optical Image Stabilized Zoom and 2.7-Inch LCD (Black)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"313.33\"}
                       {\"title\":\"Kodak EasyShare Z981 14MP Digital Camera with Schneider-Kreuznach Variogon 26x Wide Angle Optical Image Stabilized Zoom Lens and 3.0 Inch LCD\",\"manufacturer\":\"Kodak\",\"currency\":\"CAD\",\"price\":\"299.00\"}
                       {\"title\":\"Kodak EasyShare Z981 14MP Digital Camera with Schneider-Kreuznach Variogon 26x Wide Angle Optical Image Stabilized Zoom Lens and 3.0 Inch LCD\",\"manufacturer\":\"Kodak\",\"currency\":\"CAD\",\"price\":\"299.95\"}
                       {\"title\":\"Nikon EN-EL9a 1080mAh Ultra High Capacity Li-ion Battery Pack for Nikon D40, D40x, D60, D3000, & D5000 Digital SLR Cameras\",\"manufacturer\":\"Nikon\",\"currency\":\"CAD\",\"price\":\"29.75\"}
                       {\"title\":\"Canon NB-7L Lithium-Ion Battery for G10, G11, G12 Cameras\",\"manufacturer\":\"Canon\",\"currency\":\"CAD\",\"price\":\"44.99\"}
                       {\"title\":\"Canon LP-E6 Battery for Canon EOS 5D Mark II 7D LPE6\",\"manufacturer\":\"Canon\",\"currency\":\"CAD\",\"price\":\"54.88\"}
                       {\"title\":\"PENTAX Optio WG-1 GPS 14 MP Rugged Waterproof Digital Camera with 5X Optical Zoom, 2.7-inch LCD and GPS Funtionality (Green )\",\"manufacturer\":\"Pentax Canada\",\"currency\":\"CAD\",\"price\":\"387.33\"}
                       {\"title\":\"Canon PowerShot ELPH 300 HS (Silver)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"253.24\"}
                       {\"title\":\"Canon PowerShot ELPH 300 HS (Silver)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"402.46\"}
                       {\"title\":\"Canon PowerShot SD1400IS 14.1 MP Digital Camera with 4x Wide Angle Optical Image Stabilized Zoom and 2.7-Inch LCD (Silver)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"288.99\"}
                       {\"title\":\"Genuine Canon BP-511A Battery Pack for EOS 5D, 50D, 40D, 30D, 20D, 10D, D. Rebel\",\"manufacturer\":\"Canon\",\"currency\":\"CAD\",\"price\":\"39.88\"}
                       {\"title\":\"Olympus PEN E-PL1 12.3MP Live MOS Micro Four Thirds Interchangeable Lens Digital Camera with 14-42mm f/3.5-5.6 Zuiko Digital Zoom Lens (Black)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"429.98\"}
                       {\"title\":\"Olympus PEN E-PL1 12.3MP Live MOS Micro Four Thirds Interchangeable Lens Digital Camera with 14-42mm f/3.5-5.6 Zuiko Digital Zoom Lens (Black)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"598.97\"}
                       {\"title\":\"Canon PowerShot A1200 (Silver)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"124.99\"}
                       {\"title\":\"Canon PowerShot A495 10.1 MP Digital Camera with 3.3x Optical Zoom and 2.5-Inch LCD (Red)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"105.39\"}
                       {\"title\":\"Canon PowerShot A495 10.1 MP Digital Camera with 3.3x Optical Zoom and 2.5-Inch LCD (Red)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"150.97\"}
                       {\"title\":\"Olympus LI-42B Battery for SP Series\",\"manufacturer\":\"Olympus\",\"currency\":\"CAD\",\"price\":\"29.99\"}
                       {\"title\":\"Panasonic Lumix DMC-FH25 16.1 MP Digital Camera with x LEICA Optical Zoom and 2.7-Inch LCD (Red)\",\"manufacturer\":\"Panasonic\",\"currency\":\"CAD\",\"price\":\"210.73\"}
                       {\"title\":\"Canon PowerShot A3100IS 12.1 MP Digital Camera with 4x Optical Image Stabilized Zoom and 2.7-Inch LCD (Silver)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"131.87\"}
                       {\"title\":\"Canon PowerShot A3100IS 12.1 MP Digital Camera with 4x Optical Image Stabilized Zoom and 2.7-Inch LCD (Silver)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"159.99\"}
                       {\"title\":\"Panasonic Lumix DMW-BCG10 for Lumix ZS7, ZS5, TZ10, TZ8 Series\",\"manufacturer\":\"Panasonic\",\"currency\":\"CAD\",\"price\":\"39.99\"}
                       {\"title\":\"Panasonic Lumix DMC-TS3 12.1 MP Waterproof Digital Camera with 4.6x Optical Zoom and 2.7-Inch LCD, GPS, 3D  (Orange)\",\"manufacturer\":\"Panasonic\",\"currency\":\"CAD\",\"price\":\"399.99\"}
                       {\"title\":\"Panasonic Lumix DMC-TS3 12.1 MP Waterproof Digital Camera with 4.6x Optical Zoom and 2.7-Inch LCD, GPS, 3D  (Orange)\",\"manufacturer\":\"Panasonic\",\"currency\":\"CAD\",\"price\":\"419.95\"}
                       {\"title\":\"Sony T Series DSC-T99 14.1 Megapixel DSC Camera with Super HAD CCD Image Sensor (Silver)\",\"manufacturer\":\"Sony\",\"currency\":\"CAD\",\"price\":\"196.87\"}
                       {\"title\":\"Sony T Series DSC-T99 14.1 Megapixel DSC Camera with Super HAD CCD Image Sensor (Silver)\",\"manufacturer\":\"Sony\",\"currency\":\"CAD\",\"price\":\"245.99\"}
                       {\"title\":\"Canon PowerShot SD4000IS 10 MP CMOS Digital Camera with 3.8x Optical Zoom and f/2.0 Lens (Silver)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"372.59\"}
                       {\"title\":\"PENTAX Optio WG-1 14 MP Rugged Waterproof Digital Camera with 5X Optical Zoom and 2.7-inch LCD (Black)\",\"manufacturer\":\"Pentax Canada\",\"currency\":\"CAD\",\"price\":\"349.99\"}
                       {\"title\":\"PENTAX Optio WG-1 14 MP Rugged Waterproof Digital Camera with 5X Optical Zoom and 2.7-inch LCD (Black)\",\"manufacturer\":\"Pentax Canada\",\"currency\":\"CAD\",\"price\":\"437.97\"}
                       {\"title\":\"Olympus Stylus Tough 8010 14MP Digital Camera with 5x Wide Angle Zoom and 2.7 inch LCD (Black)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"299.99\"}
                       {\"title\":\"Olympus Stylus Tough 8010 14MP Digital Camera with 5x Wide Angle Zoom and 2.7 inch LCD (Black)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"365.95\"}
                       {\"title\":\"Nikon EN-EL5 1100mAh Ultra High Capacity Li-ion Battery Pack for Nikon P100, P90, P80, P6000\",\"manufacturer\":\"Nikon\",\"currency\":\"CAD\",\"price\":\"19.99\"}
                       {\"title\":\"Lithium-Ion Battery for Canon NB-4L Digital Camera\",\"manufacturer\":\"Canon\",\"currency\":\"CAD\",\"price\":\"1.08\"}
                       {\"title\":\"Fujifilm FinePix JV100 12 MP Digital Camera with 3x Optical Zoom and 2.7-Inch LCD (Black)\",\"manufacturer\":\"Fujifilm Canada\",\"currency\":\"CAD\",\"price\":\"85.87\"}
                       {\"title\":\"Fujifilm FinePix JV100 12 MP Digital Camera with 3x Optical Zoom and 2.7-Inch LCD (Black)\",\"manufacturer\":\"Fujifilm Canada\",\"currency\":\"CAD\",\"price\":\"134.99\"}
                       {\"title\":\"Canon PowerShot ELPH 100 HS (Silver)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"229.99\"}
                       {\"title\":\"Canon PowerShot ELPH 100 HS (Silver)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"314.78\"}
                       {\"title\":\"Canon PowerShot SD1300IS 12 MP Digital Camera with 4x Wide Angle Optical Image Stabilized Zoom and 2.7-Inch LCD (Silver)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"159.99\"}
                       {\"title\":\"Canon PowerShot SD1300IS 12 MP Digital Camera with 4x Wide Angle Optical Image Stabilized Zoom and 2.7-Inch LCD (Silver)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"175.13\"}
                       {\"title\":\"Panasonic Lumix DMC-TS3 12.1 MP Waterproof Digital Camera with 4.6x Optical Zoom and 2.7-Inch LCD, GPS, 3D  (Red)\",\"manufacturer\":\"Panasonic\",\"currency\":\"CAD\",\"price\":\"399.99\"}
                       {\"title\":\"PENTAX Optio RZ10 14 MP Digital Camera with 10X Optical Zoom and 2.7-inch LCD\",\"manufacturer\":\"Pentax Canada\",\"currency\":\"CAD\",\"price\":\"159.99\"}
                       {\"title\":\"Olympus Stylus Tough 3000 12 MP Digital Camera with 3.6x Wide Angle Zoom and 2.7-inch LCD (Blue)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"161.87\"}
                       {\"title\":\"Olympus Stylus Tough 3000 12 MP Digital Camera with 3.6x Wide Angle Zoom and 2.7-inch LCD (Blue)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"211.29\"}
                       {\"title\":\"Canon PowerShot ELPH 300 HS (Red)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"264.00\"}
                       {\"title\":\"Canon PowerShot ELPH 300 HS (Red)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"274.99\"}
                       {\"title\":\"Canon PowerShot ELPH 100 HS (Blue)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"209.99\"}
                       {\"title\":\"Canon PowerShot ELPH 100 HS (Blue)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"314.75\"}
                       {\"title\":\"Canon PowerShot SD1400IS 14.1 MP Digital Camera with 4x Wide Angle Optical Image Stabilized Zoom and 2.7-Inch LCD (Pink)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"168.00\"}
                       {\"title\":\"Canon PowerShot SD1400IS 14.1 MP Digital Camera with 4x Wide Angle Optical Image Stabilized Zoom and 2.7-Inch LCD (Pink)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"239.99\"}
                       {\"title\":\"Canon PowerShot SD3500IS 14.1 MP Digital Camera with 3.5-Inch Touch Panel LCD and 5x Ultra Wide Angle Optical Image Stabilized Zoom (Silver)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"279.99\"}
                       {\"title\":\"Canon PowerShot SD3500IS 14.1 MP Digital Camera with 3.5-Inch Touch Panel LCD and 5x Ultra Wide Angle Optical Image Stabilized Zoom (Silver)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"344.87\"}
                       {\"title\":\"Canon PowerShot SD1300IS 12.1 MP Digital Camera with 4x Wide Angle Optical Image Stabilized Zoom and 2.7-Inch LCD (Blue)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"159.99\"}
                       {\"title\":\"Olympus Stylus Tough 3000 12 MP Digital Camera with 3.6x Wide Angle Zoom and 2.7-inch LCD (Red)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"134.98\"}
                       {\"title\":\"Olympus Stylus Tough 3000 12 MP Digital Camera with 3.6x Wide Angle Zoom and 2.7-inch LCD (Red)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"221.30\"}
                       {\"title\":\"Olympus FE-4020 14 MP Digital Camera with 4x Wide Angle Zoom and 2.7-inch LCD (Grey)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"109.99\"}
                       {\"title\":\"Olympus FE-4020 14 MP Digital Camera with 4x Wide Angle Zoom and 2.7-inch LCD (Grey)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"122.39\"}
                       {\"title\":\"Canon PowerShot SD4000IS 10 MP CMOS Digital Camera with 3.8x Optical Zoom and f/2.0 Lens (White)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"229.99\"}
                       {\"title\":\"Canon PowerShot SD4000IS 10 MP CMOS Digital Camera with 3.8x Optical Zoom and f/2.0 Lens (White)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"372.59\"}
                       {\"title\":\"Olympus Stylus Tough 8010 14MP Digital Camera with 5x Wide Angle Zoom and 2.7 inch LCD (Silver)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"302.87\"}
                       {\"title\":\"Olympus Stylus Tough 8010 14MP Digital Camera with 5x Wide Angle Zoom and 2.7 inch LCD (Silver)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"365.95\"}
                       {\"title\":\"Nikon ML-L3 Wireless Remote Control for Nikon P7000, D3000, D40, D40x, D50, D5000, D60, D70, D7000, D70s, D80 & D90 Digital SLR Cameras\",\"manufacturer\":\"Nikon\",\"currency\":\"CAD\",\"price\":\"22.19\"}
                       {\"title\":\"Kodak EasyShare M530 12 MP Digital Camera with 3x Wide Angle Optical Zoom and 2.7 Inch LCD (Blue)\",\"manufacturer\":\"Kodak\",\"currency\":\"CAD\",\"price\":\"75.00\"}
                       {\"title\":\"Kodak EasyShare M530 12 MP Digital Camera with 3x Wide Angle Optical Zoom and 2.7 Inch LCD (Blue)\",\"manufacturer\":\"Kodak\",\"currency\":\"CAD\",\"price\":\"105.37\"}
                       {\"title\":\"Fujifilm Finepix S1800 12 Mega-Pixel, 18x Long Zoom Digital Camera\",\"manufacturer\":\"Fujifilm CA\",\"currency\":\"CAD\",\"price\":\"206.98\"}
                       {\"title\":\"Fujifilm Finepix S1800 12 Mega-Pixel, 18x Long Zoom Digital Camera\",\"manufacturer\":\"Fujifilm CA\",\"currency\":\"CAD\",\"price\":\"256.77\"}
                       {\"title\":\"Olympus T-100 12MP Digital Camera with 3x Optical Zoom and 2.4 inch LCD (Red)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"87.86\"}
                       {\"title\":\"Panasonic Lumix DMC-FS25 12MP Digital Camera with 5x MEGA Optical Image Stabilized Zoom and 3 inch LCD (Gold)\",\"manufacturer\":\"Panasonic\",\"currency\":\"CAD\",\"price\":\"179.08\"}
                       {\"title\":\"Panasonic Lumix DMC-FS25 12MP Digital Camera with 5x MEGA Optical Image Stabilized Zoom and 3 inch LCD (Gold)\",\"manufacturer\":\"Panasonic\",\"currency\":\"CAD\",\"price\":\"217.64\"}
                       {\"title\":\"Olympus Stylus Tough 3000 12 MP Digital Camera with 3.6x Wide Angle Zoom and 2.7-inch LCD (Green)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"154.98\"}
                       {\"title\":\"Olympus Stylus Tough 3000 12 MP Digital Camera with 3.6x Wide Angle Zoom and 2.7-inch LCD (Green)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"223.97\"}
                       {\"title\":\"Fujifilm FinePix HS10 10 MP CMOS Digital Camera with 30x Wide Angle Optical Zoom and 3-Inch LCD (Black)\",\"manufacturer\":\"Fujifilm Canada\",\"currency\":\"CAD\",\"price\":\"398.00\"}
                       {\"title\":\"Fujifilm FinePix HS10 10 MP CMOS Digital Camera with 30x Wide Angle Optical Zoom and 3-Inch LCD (Black)\",\"manufacturer\":\"Fujifilm Canada\",\"currency\":\"CAD\",\"price\":\"410.69\"}
                       {\"title\":\"PENTAX Optio RZ10 14 MP Digital Camera with 10X Optical Zoom and 2.7-inch LCD (Purple)\",\"manufacturer\":\"Pentax Canada\",\"currency\":\"CAD\",\"price\":\"159.99\"}
                       {\"title\":\"Canon PowerShot ELPH 100 HS (Pink)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"224.99\"}
                       {\"title\":\"Canon PowerShot ELPH 100 HS (Pink)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"314.78\"}
                       {\"title\":\"Kodak EasyShare M580 14 MP Digital Camera with 8x Wide Angle Optical Zoom and 3.0 Inch LCD (Blue)\",\"manufacturer\":\"Kodak\",\"currency\":\"CAD\",\"price\":\"129.99\"}
                       {\"title\":\"Kodak EasyShare M580 14 MP Digital Camera with 8x Wide Angle Optical Zoom and 3.0 Inch LCD (Blue)\",\"manufacturer\":\"Kodak\",\"currency\":\"CAD\",\"price\":\"157.73\"}
                       {\"title\":\"Canon PowerShot SD1300IS 12 MP Digital Camera with 4x Wide Angle Optical Image Stabilized Zoom and 2.7-Inch LCD (Pink)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"159.99\"}
                       {\"title\":\"Canon PowerShot SD1300IS 12 MP Digital Camera with 4x Wide Angle Optical Image Stabilized Zoom and 2.7-Inch LCD (Pink)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"177.24\"}
                       {\"title\":\"Canon PowerShot SD4000IS 10 MP CMOS Digital Camera with 3.8x Optical Zoom and f/2.0 Lens (Black)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"239.99\"}
                       {\"title\":\"Canon PowerShot SD4000IS 10 MP CMOS Digital Camera with 3.8x Optical Zoom and f/2.0 Lens (Black)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"372.59\"}
                       {\"title\":\"Battery EN-EL9 ENEL9 For Nikon D3X D40 D40X D60 D3000 D5000 Digital Cameras\",\"manufacturer\":\"Neewer Electronics Accessories\",\"currency\":\"CAD\",\"price\":\"3.67\"}
                       {\"title\":\"Nikon D3100 w/18-55mm VR\",\"manufacturer\":\"Nikon\",\"currency\":\"CAD\",\"price\":\"749.95\"}
                       {\"title\":\"Panasonic Lumix DMC-FH25 16.1 MP Digital Camera with x LEICA Optical Zoom and 2.7-Inch LCD (Violet)\",\"manufacturer\":\"Panasonic\",\"currency\":\"CAD\",\"price\":\"210.73\"}
                       {\"title\":\"Pentax Optio I10 12.1 MP Digital Camera with 5x Wide Angle Optical Image Stabilized Zoom and 2.7-Inch LCD (Pearl White)\",\"manufacturer\":\"Pentax Canada\",\"currency\":\"CAD\",\"price\":\"186.29\"}
                       {\"title\":\"Olympus TG-310 14 MP Digital Camera with 3.6x Optical Zoom, Waterproof, Shockproof, Freezer Proof (Blue)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"199.99\"}
                       {\"title\":\"Olympus TG-310 14 MP Digital Camera with 3.6x Optical Zoom, Waterproof, Shockproof, Freezer Proof (Blue)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"233.73\"}
                       {\"title\":\"Pentax Optio W90 12.1 MP Waterproof Digital Camera with 5x Wide Angle Zoom and 2.7-Inch LCD (Black)\",\"manufacturer\":\"Pentax Canada\",\"currency\":\"CAD\",\"price\":\"655.70\"}
                       {\"title\":\"Panasonic Lumix DMC-FP1 12.1 MP Digital Camera with 4x Optical Image Stabilized Zoom and 2.7-Inch LCD (Green)\",\"manufacturer\":\"Panasonic\",\"currency\":\"CAD\",\"price\":\"149.47\"}
                       {\"title\":\"Panasonic Lumix DMC-FP1 12.1 MP Digital Camera with 4x Optical Image Stabilized Zoom and 2.7-Inch LCD (Green)\",\"manufacturer\":\"Panasonic\",\"currency\":\"CAD\",\"price\":\"192.99\"}
                       {\"title\":\"NEW WP5300 Blue(with Mirco 2GB)Waterproof 12MP Digital Camera& Video Recorder\",\"manufacturer\":\"SVP\",\"currency\":\"CAD\",\"price\":\"64.83\"}
                       {\"title\":\"Olympus SZ-30MR 228825 16 MP Digital Camera with Super-Slim 28mm Wide-Angle 24x Optical Zoom and 3.0-Inch LCD (Silver)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"399.99\"}
                       {\"title\":\"Canon PowerShot A2200IS (Red)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"149.99\"}
                       {\"title\":\"Canon PowerShot A2200IS (Red)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"198.97\"}
                       {\"title\":\"Fujifilm FinePix JZ300 12 MP Digital Camera with 10x Wide Angle Optical Zoom and 2.7-Inch LCD (Silver)\",\"manufacturer\":\"Fujifilm Canada\",\"currency\":\"CAD\",\"price\":\"126.87\"}
                       {\"title\":\"Fujifilm FinePix JZ300 12 MP Digital Camera with 10x Wide Angle Optical Zoom and 2.7-Inch LCD (Silver)\",\"manufacturer\":\"Fujifilm Canada\",\"currency\":\"CAD\",\"price\":\"218.99\"}
                       {\"title\":\"Kodak EasyShare M580 14 MP Digital Camera with 8x Wide Angle Optical Zoom and 3.0 Inch LCD (Pink)\",\"manufacturer\":\"Kodak\",\"currency\":\"CAD\",\"price\":\"176.62\"}
                       {\"title\":\"Kodak EasyShare M580 14 MP Digital Camera with 8x Wide Angle Optical Zoom and 3.0 Inch LCD (Pink)\",\"manufacturer\":\"Kodak\",\"currency\":\"CAD\",\"price\":\"233.14\"}
                       {\"title\":\"Kodak EasyShare M550 12 MP Digital Camera with 5x Wide Angle Optical Zoom and 2.7 Inch LCD (Tan)\",\"manufacturer\":\"Kodak\",\"currency\":\"CAD\",\"price\":\"78.00\"}
                       {\"title\":\"Kodak EasyShare M550 12 MP Digital Camera with 5x Wide Angle Optical Zoom and 2.7 Inch LCD (Tan)\",\"manufacturer\":\"Kodak\",\"currency\":\"CAD\",\"price\":\"89.99\"}
                       {\"title\":\"Olympus TG-310 14 MP Digital Camera with 3.6x Optical Zoom, Waterproof, Shockproof, Freezer Proof (Red)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"199.99\"}
                       {\"title\":\"Olympus TG-310 14 MP Digital Camera with 3.6x Optical Zoom, Waterproof, Shockproof, Freezer Proof (Red)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"233.73\"}
                       {\"title\":\"Fujifilm FinePix XP10 12 MP Waterproof Digital Camera with 5x Optical Zoom and 2.7-Inch LCD (Green)\",\"manufacturer\":\"Fujifilm Canada\",\"currency\":\"CAD\",\"price\":\"169.90\"}
                       {\"title\":\"Fujifilm FinePix XP10 12 MP Waterproof Digital Camera with 5x Optical Zoom and 2.7-Inch LCD (Green)\",\"manufacturer\":\"Fujifilm Canada\",\"currency\":\"CAD\",\"price\":\"195.95\"}
                       {\"title\":\"Olympus TG-310 14 MP Digital Camera with 3.6x Optical Zoom, Waterproof, Shockproof, Freezer Proof (Silver)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"199.99\"}
                       {\"title\":\"Olympus TG-310 14 MP Digital Camera with 3.6x Optical Zoom, Waterproof, Shockproof, Freezer Proof (Silver)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"233.73\"}
                       {\"title\":\"Kodak EasyShare M550 12 MP Digital Camera with 5x Wide Angle Optical Zoom and 2.7 Inch LCD (Dark Grey)\",\"manufacturer\":\"Kodak\",\"currency\":\"CAD\",\"price\":\"71.99\"}
                       {\"title\":\"Kodak EasyShare M550 12 MP Digital Camera with 5x Wide Angle Optical Zoom and 2.7 Inch LCD (Dark Grey)\",\"manufacturer\":\"Kodak\",\"currency\":\"CAD\",\"price\":\"116.41\"}
                       {\"title\":\"Casio NP-20 Lithium-Ion battery for Exilim Series Cameras\",\"manufacturer\":\"CASIO\",\"currency\":\"CAD\",\"price\":\"10.99\"}
                       {\"title\":\"Pentax Optio E90 10 MP Digital Camera with 3x Optical Zoom and 2.7-Inch LCD (Wine Red)\",\"manufacturer\":\"Pentax Canada\",\"currency\":\"CAD\",\"price\":\"105.70\"}
                       {\"title\":\"Fujifilm FinePix F80EXR 12 MP Super CCD EXR Digital Camera with 10x Wide Angle Optical Zoom and 3.0-Inch LCD (Red)\",\"manufacturer\":\"Fujifilm Canada\",\"currency\":\"CAD\",\"price\":\"278.99\"}
                       {\"title\":\"NEW 9.0 MP High Resolution Full-Motion Digital Red Video Camcorder / STILL CAMERA\",\"manufacturer\":\"SVP\",\"currency\":\"CAD\",\"price\":\"48.87\"}
                       {\"title\":\"Fujifilm FinePix JV100 12 MP Digital Camera with 3x Optical Zoom and 2.7-Inch LCD (Pink)\",\"manufacturer\":\"Fujifilm Canada\",\"currency\":\"CAD\",\"price\":\"119.99\"}
                       {\"title\":\"Fujifilm FinePix JV100 12 MP Digital Camera with 3x Optical Zoom and 2.7-Inch LCD (Pink)\",\"manufacturer\":\"Fujifilm Canada\",\"currency\":\"CAD\",\"price\":\"156.97\"}
                       {\"title\":\"Slide and Negative Scanner\",\"manufacturer\":\"Jobar International\",\"currency\":\"CAD\",\"price\":\"48.28\"}
                       {\"title\":\"DURAGADGET Full Size Professional Tripod with Quick Release for Canon Ixus 1000HS, Ixus 300HS, Ixus 210, Ixus 200IS, Ixus 130, Ixus 120IS\",\"manufacturer\":\"DURAGADGET\",\"currency\":\"CAD\",\"price\":\"63.99\"}
                       {\"title\":\"Olympus TG-310 14 MP Digital Camera with 3.6x Optical Zoom, Waterproof, Shockproof, Freezer Proof (White)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"199.99\"}
                       {\"title\":\"Olympus TG-310 14 MP Digital Camera with 3.6x Optical Zoom, Waterproof, Shockproof, Freezer Proof (White)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"248.97\"}
                       {\"title\":\"GE C1033 10 MP Digital Camera with 3X Optical Zoom and 2.4-Inch LCD (Silver)\",\"manufacturer\":\"General Imaging\",\"currency\":\"CAD\",\"price\":\"62.92\"}
                       {\"title\":\"Panasonic Lumix DMC-FH25 16.1 MP Digital Camera with x LEICA Optical Zoom and 2.7-Inch LCD (Black)\",\"manufacturer\":\"Panasonic\",\"currency\":\"CAD\",\"price\":\"229.99\"}
                       {\"title\":\"Olympus FE-47 14 MP Digital Camera with 5x Optical Zoom and 2.7-inch LCD (Red)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"120.75\"}
                       {\"title\":\"PENTAX Optio WG-1 14 MP Rugged Waterproof Digital Camera with 5X Optical Zoom and 2.7-inch LCD (Purple)\",\"manufacturer\":\"Pentax Canada\",\"currency\":\"CAD\",\"price\":\"349.99\"}
                       {\"title\":\"Canon PowerShot A2200IS (Blue)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"144.99\"}
                       {\"title\":\"Fujifilm FinePix XP10 12 MP Waterproof Digital Camera with 5x Optical Zoom and 2.7-Inch LCD (Blue)\",\"manufacturer\":\"Fujifilm Canada\",\"currency\":\"CAD\",\"price\":\"149.00\"}
                       {\"title\":\"Fujifilm FinePix XP10 12 MP Waterproof Digital Camera with 5x Optical Zoom and 2.7-Inch LCD (Blue)\",\"manufacturer\":\"Fujifilm Canada\",\"currency\":\"CAD\",\"price\":\"171.30\"}
                       {\"title\":\"310 Digital Camera Video Mask - NOW Rated to 65 Feet!\",\"manufacturer\":\"LEI - Liquid Image\",\"currency\":\"CAD\",\"price\":\"199.99\"}
                       {\"title\":\"Lexar 16GB SDHC Platinum II Class 6 Card\",\"manufacturer\":\"LEXAR MEDIA INC\",\"currency\":\"CAD\",\"price\":\"44.53\"}
                       {\"title\":\"Olympus Stylus Tough 6020 14MP Digital Camera with 5x Wide Angle Zoom and 2.7 inch LCD (Blue)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"232.99\"}
                       {\"title\":\"Olympus Stylus Tough 6020 14MP Digital Camera with 5x Wide Angle Zoom and 2.7 inch LCD (Blue)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"241.87\"}
                       {\"title\":\"Olympus PEN E-PL1 12.3MP Live MOS Micro Four Thirds Interchangeable Lens Digital Camera with 14-42mm f/3.5-5.6 Zuiko Digital Zoom Lens (Gold)\",\"manufacturer\":\"Olympus\",\"currency\":\"CAD\",\"price\":\"439.99\"}
                       {\"title\":\"Olympus PEN E-PL1 12.3MP Live MOS Micro Four Thirds Interchangeable Lens Digital Camera with 14-42mm f/3.5-5.6 Zuiko Digital Zoom Lens (Gold)\",\"manufacturer\":\"Olympus\",\"currency\":\"CAD\",\"price\":\"598.97\"}
                       {\"title\":\"Panasonic Lumix DMC-FH3 14.1 MP Digital Camera with 5x Optical Image Stabilized Zoom and 2.7-Inch LCD (Silver)\",\"manufacturer\":\"Panasonic\",\"currency\":\"CAD\",\"price\":\"189.47\"}
                       {\"title\":\"Panasonic Lumix DMC-FH1 12.1 MP Digital Camera with 5x Optical Image Stabilized Zoom and 2.7-Inch LCD (Silver)\",\"manufacturer\":\"Panasonic\",\"currency\":\"CAD\",\"price\":\"135.23\"}
                       {\"title\":\"Fujifilm FinePix JV100 12 MP Digital Camera with 3x Optical Zoom and 2.7-Inch LCD (Silver)\",\"manufacturer\":\"Fujifilm Canada\",\"currency\":\"CAD\",\"price\":\"128.66\"}
                       {\"title\":\"Fujifilm FinePix F300EXR 12 MP Digital Camera with 15x Wide-Angle Zoom and 3.0-Inch LCD (Black)\",\"manufacturer\":\"Fujifilm CA\",\"currency\":\"CAD\",\"price\":\"330.80\"}
                       {\"title\":\"Fujifilm FinePix F300EXR 12 MP Digital Camera with 15x Wide-Angle Zoom and 3.0-Inch LCD (Black)\",\"manufacturer\":\"Fujifilm CA\",\"currency\":\"CAD\",\"price\":\"383.72\"}
                       {\"title\":\"DURAGADGET Deluxe Mini Flat folding camera / Camcorder Tripod stand for Kodak Playtouch, Playfull\",\"manufacturer\":\"DURAGADGET\",\"currency\":\"CAD\",\"price\":\"23.99\"}
                       {\"title\":\"Olympus Stylus Tough 6020 14MP Digital Camera with 5x Wide Angle Zoom and 2.7 inch LCD (Green)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"241.87\"}
                       {\"title\":\"Olympus Stylus Tough 6020 14MP Digital Camera with 5x Wide Angle Zoom and 2.7 inch LCD (Green)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"301.51\"}
                       {\"title\":\"Panasonic Lumix DMC-FP5, Slim Design, 14.1 MP Digital Camera with 4x Optical Zoom and 3.0-Inch Smart Touch LCD (Blue)\",\"manufacturer\":\"Panasonic\",\"currency\":\"CAD\",\"price\":\"199.99\"}
                       {\"title\":\"Panasonic Lumix DMC-FP3 14.1 MP Digital Camera with 4x Optical Image Stabilized Zoom and 3.0-Inch Touch-Screen LCD (Dark Blue)\",\"manufacturer\":\"Panasonic\",\"currency\":\"CAD\",\"price\":\"198.99\"}
                       {\"title\":\"Panasonic Lumix DMC-FP3 14.1 MP Digital Camera with 4x Optical Image Stabilized Zoom and 3.0-Inch Touch-Screen LCD (Dark Blue)\",\"manufacturer\":\"Panasonic\",\"currency\":\"CAD\",\"price\":\"199.99\"}
                       {\"title\":\"Fujifilm FinePix REAL 3D W3 10 MP Digital Camera with Dual 3x Optical Zoom Lenses (Black)\",\"manufacturer\":\"Fujifilm Canada\",\"currency\":\"CAD\",\"price\":\"499.99\"}
                       {\"title\":\"Fujifilm FinePix REAL 3D W3 10 MP Digital Camera with Dual 3x Optical Zoom Lenses (Black)\",\"manufacturer\":\"Fujifilm Canada\",\"currency\":\"CAD\",\"price\":\"499.99\"}
                       {\"title\":\"Kodak EasyShare M580 14 MP Digital Camera with 8x Wide Angle Optical Zoom and 3.0 Inch LCD (Purple)\",\"manufacturer\":\"Kodak\",\"currency\":\"CAD\",\"price\":\"223.15\"}
                       {\"title\":\"Panasonic Lumix DMC-FP1 12.1 MP Digital Camera with 4x Optical Image Stabilized Zoom and 2.7-Inch LCD (Pink)\",\"manufacturer\":\"Panasonic\",\"currency\":\"CAD\",\"price\":\"149.47\"}
                       {\"title\":\"Panasonic Lumix DMC-FP1 12.1 MP Digital Camera with 4x Optical Image Stabilized Zoom and 2.7-Inch LCD (Pink)\",\"manufacturer\":\"Panasonic\",\"currency\":\"CAD\",\"price\":\"164.99\"}
                       {\"title\":\"Panasonic DMC-ZS5 Black 12.1MP Digital Camera\",\"manufacturer\":\"Panasonic\",\"currency\":\"CAD\",\"price\":\"298.12\"}
                       {\"title\":\"Fujifilm FinePix F80EXR 12 MP Super CCD EXR Digital Camera with 10x Wide Angle Optical Zoom and 3.0-Inch LCD (Black)\",\"manufacturer\":\"Fujifilm Canada\",\"currency\":\"CAD\",\"price\":\"186.87\"}
                       {\"title\":\"Fujifilm FinePix F80EXR 12 MP Super CCD EXR Digital Camera with 10x Wide Angle Optical Zoom and 3.0-Inch LCD (Black)\",\"manufacturer\":\"Fujifilm Canada\",\"currency\":\"CAD\",\"price\":\"268.99\"}
                       {\"title\":\"Pentax Optio H90 12.1 MP Digital Camera with 5x Wide Angle Optical Zoom and 2.7-Inch LCD (Ceramic White)\",\"manufacturer\":\"Pentax Canada\",\"currency\":\"CAD\",\"price\":\"159.00\"}
                       {\"title\":\"Pentax Optio H90 12.1 MP Digital Camera with 5x Wide Angle Optical Zoom and 2.7-Inch LCD (Ceramic White)\",\"manufacturer\":\"Pentax Canada\",\"currency\":\"CAD\",\"price\":\"207.29\"}
                       {\"title\":\"Panasonic Lumix DMC-FP1 12.1 MP Digital Camera with 4x Optical Image Stabilized Zoom and 2.7-Inch LCD (Orange)\",\"manufacturer\":\"Panasonic\",\"currency\":\"CAD\",\"price\":\"194.99\"}
                       {\"title\":\"Olympus VG-100 14 MP 5x Optical Zoom, 720pHD movie, magic filter, AF tracking (Red)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"99.99\"}
                       {\"title\":\"Olympus VG-100 14 MP 5x Optical Zoom, 720pHD movie, magic filter, AF tracking (Red)\",\"manufacturer\":\"Olympus Canada\",\"currency\":\"CAD\",\"price\":\"131.97\"}
                       {\"title\":\"Canon PowerShot A800 (Black)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"99.99\"}
                       {\"title\":\"Canon PowerShot A800 (Black)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"131.97\"}
                       {\"title\":\"Canon PowerShot ELPH 100 HS (Grey)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"229.99\"}
                       {\"title\":\"Canon PowerShot ELPH 100 HS (Grey)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"314.78\"}
                       {\"title\":\"Kodak EasyShare M550 12 MP Digital Camera with 5x Wide Angle Optical Zoom and 2.7 Inch LCD (Purple)\",\"manufacturer\":\"Kodak\",\"currency\":\"CAD\",\"price\":\"161.56\"}
                       {\"title\":\"Canon EOS Rebel XS 10.1MP Digital SLR Camera with EF-S 18-55mm f/3.5-5.6 IS Lens (Black)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"774.71\"}
                       {\"title\":\"Canon PowerShot A800 (Silver)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"99.99\"}
                       {\"title\":\"Canon PowerShot A800 (Silver)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"152.43\"}
                       {\"title\":\"Canon PowerShot SD1300IS 12 MP Digital Camera with 4x Wide Angle Optical Image Stabilized Zoom and 2.7-Inch LCD (Green)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"242.40\"}
                       {\"title\":\"GoPro HD Surf HERO Camera\",\"manufacturer\":\"GoPro\",\"currency\":\"CAD\",\"price\":\"289.99\"}
                       {\"title\":\"Canon EOS Rebel XS 10.1 MP Digital SLR Camera with 3X Optical zoom and 2.5-Inch TFT LCD (Black)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"448.00\"}
                       {\"title\":\"NEW WP5300 Blue Waterproof 12MP Digital Video Recorder /Camera\",\"manufacturer\":\"SVP\",\"currency\":\"CAD\",\"price\":\"59.84\"}
                       {\"title\":\"Sony DSC-W310 12.1MP Digital Camera with 4x Wide Angle Zoom with Digital Steady Shot Image Stabilization and 2.7 inch LCD (Silver)\",\"manufacturer\":\"Sony\",\"currency\":\"CAD\",\"price\":\"139.99\"}
                       {\"title\":\"Olympus UZ Series 14 MP 30 X\",\"manufacturer\":\"Olympus\",\"currency\":\"CAD\",\"price\":\"257.87\"}
                       {\"title\":\"Olympus UZ Series 14 MP 30 X\",\"manufacturer\":\"Olympus\",\"currency\":\"CAD\",\"price\":\"534.93\"}
                       {\"title\":\"Samsung EC-ES27 Purple Digital Camera\",\"manufacturer\":\"Samsung\",\"currency\":\"CAD\",\"price\":\"69.99\"}
                       {\"title\":\"NEW 9.0 MP High Resolution Full-Motion Digital Black Video Camcorder / STILL CAMERA\",\"manufacturer\":\"SVP\",\"currency\":\"CAD\",\"price\":\"48.87\"}
                       {\"title\":\"Canon SX30IS 14.1MP Digital Camera with 35x Wide Angle Optical Image Stabilized Zoom and 2.7 Inch Wide LCD\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"459.99\"}
                       {\"title\":\"Canon SX30IS 14.1MP Digital Camera with 35x Wide Angle Optical Image Stabilized Zoom and 2.7 Inch Wide LCD\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"479.96\"}
                       {\"title\":\"Sony Cyber-shot DSC-H55 14.1MP Digital Camera with 10x Wide Angle Optical Zoom with SteadyShot Image Stabilization and 3.0 inch LCD (Black)\",\"manufacturer\":\"Sony\",\"currency\":\"CAD\",\"price\":\"216.87\"}
                       {\"title\":\"Kodak EasyShare M530 12 MP Digital Camera with 3x Wide Angle Optical Zoom and 2.7 Inch LCD (Red)\",\"manufacturer\":\"Kodak\",\"currency\":\"CAD\",\"price\":\"122.03\"}
                       {\"title\":\"Kodak EasyShare M530 12 MP Digital Camera with 3x Wide Angle Optical Zoom and 2.7 Inch LCD (Red)\",\"manufacturer\":\"Kodak\",\"currency\":\"CAD\",\"price\":\"160.15\"}
                       {\"title\":\"PENTAX Optio RZ10 14 MP Digital Camera with 10X Optical Zoom and 2.7-inch LCD (Green)\",\"manufacturer\":\"Pentax Canada\",\"currency\":\"CAD\",\"price\":\"159.99\"}
                       {\"title\":\"Canon PowerShot A800 (Red)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"99.99\"}
                       {\"title\":\"Canon PowerShot A800 (Red)\",\"manufacturer\":\"Canon Canada\",\"currency\":\"CAD\",\"price\":\"131.97\"}"))