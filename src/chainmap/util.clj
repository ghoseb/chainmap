(ns ^{:doc "ChainMap util functions."
      :author "Baishampayan Ghose <b.ghose@infinitelybeta.com>"}
  chainmap.util)


;;; Thank you Christophe
(defn scaffold
  "Print the ancestor method signatures of a given interface."
  [iface]
  (doseq [[iface methods] (->> iface
                               .getMethods
                               (map #(vector (.getName (.getDeclaringClass %))
                                             (symbol (.getName %))
                                             (count (.getParameterTypes %))))
                               (group-by first))]
    (println (str "  " iface))
    (doseq [[_ name argcount] methods]
      (println
       (str "    "
            (list name (into ['this] (take argcount (repeatedly gensym)))))))))
