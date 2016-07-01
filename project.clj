(defproject sortable-challenge "0.9.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :resource-paths ["resources"]
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [prismatic/schema "1.1.2"]
                 [org.clojure/core.async "0.2.385"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/math.combinatorics "0.1.3"]]
  :aot :all
  :main sortable-challenge.core)
