(ns sortable-challenge.utils)

(defmulti recursively-vectorize #(cond
                                  (sequential? %) :seq
                                  (map? %) :map
                                  (set? %) :set
                                  :else :default))

(defmethod recursively-vectorize :seq [data] (mapv recursively-vectorize data))
(defmethod recursively-vectorize :map [data] (into {} (for [key-val data] (recursively-vectorize key-val))))
(defmethod recursively-vectorize :set [data] (into #{} (for [element data] (recursively-vectorize element))))
(defmethod recursively-vectorize :default [data] data)

(defmacro ->console [var]
  (list 'do `(println (quote ~var) ": " (recursively-vectorize ~var)) var))