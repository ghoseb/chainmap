(ns ^{:doc "ChainMap implementation. Inspired by Python."
      :author "Baishampayan Ghose <b.ghose@infinitelybeta.com>"}
  chainmap.core
  (:refer-clojure :exclude [parents]))


(defprotocol PChainMap
  (parents [this] "Return a new ChainMap with all but the first map.")
  (add-child [this] [this child] "Return a new ChainMap with a  map prepended.")
  (maps [this] "Return the maps enclosed in the ChainMap."))


(deftype ChainMap [ms]
  PChainMap
  (parents [_]
    (ChainMap. (or (next ms) '())))
  (add-child [_ child]
    (ChainMap. (cons child ms)))
  (add-child [this]
    (.add-child this {}))
  (maps [_]
    ms)

  clojure.lang.Seqable
  (seq [_]
    (seq (apply merge (reverse ms))))
  
  clojure.lang.IPersistentMap
  (without [_ k]
    (ChainMap. (cons (dissoc (first ms) k) (rest ms))))
  (assocEx [this k v]
    (if (contains? (first ms) k)
      (throw (IllegalArgumentException. (format "Key %s already present in latest context %s." k (first ms))))
      (assoc this k v)))

  clojure.lang.Associative
  (assoc [_ k v]
    (let [ps (rest ms)
          curr (assoc (first ms) k v)]
      (ChainMap. (cons curr ps))))  
  (containsKey [_ k]
    (some #(contains? % k) ms))
  (entryAt [_ k]
    (some #(find % k) ms))

  clojure.lang.IPersistentCollection
  (cons [this x]
    (if-let [[k v] (and (vector? x) x)]
      (assoc this k v)
      (reduce (fn [m [k v]] (assoc m k v)) this x)))
  (empty [_]
    (ChainMap. '()))
  (equiv [this x]
    (boolean
     (or
      (identical? this x)
      (if (identical? (class this) (class x))
        (= (maps this) (maps x))
        (= (apply merge (reverse ms)) x)))))

  clojure.lang.ILookup
  (valAt [_ k not-found]
    (loop [[fst & more :as ms] ms]
      (if (seq ms)
        (if (contains? fst k)
          (fst k)
          (recur more))
        not-found)))
  (valAt [this k]
    (.valAt this k nil))

  clojure.lang.Counted
  (count [_]
    (count (first ms)))

  clojure.lang.IFn
  (invoke [this k]
    (.valAt this k))
  
  Object
  (toString [_]
    (str "#ChainMap<" ms ">"))
  (hashCode [_]
    (.hashCode ms))
  (equals [_ x]
    (.equals (apply merge (reverse ms)) x)))


(defmethod clojure.core/print-method ChainMap
  [chainmap writer]
  (.write writer (str "#ChainMap<" (maps chainmap) ">")))


(defn chainmap
  [& ms]
  (let [ms (or ms '())]
    (ChainMap. ms)))
