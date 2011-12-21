(ns chainmap.test.core
  (:refer-clojure :exclude [parents])
  (:use [chainmap.core])
  (:use [clojure.test]))

(def local {:x 1})
(def prod {:x 10 :y 20})
(def default {:x 100 :y 200 :z 300})
(def cm (chainmap local prod default))

(deftest test-chainmap-proto
  (are [x y] (= x y)
       '() (maps (chainmap))
       (list local prod default) (maps cm)
       (apply chainmap (list prod default)) (parents cm)
       (list {} local prod default) (maps (add-child cm))))

(deftest test-chainmap-impl
  (are [x y] (= x y)
       1 (:x cm)
       20 (:y cm)
       300 (:z cm)
       1 (cm :x)
       '(:z :y :x) (keys cm)
       '(300 20 1) (vals cm)
       42 (:z (assoc cm :z 42))
       '([:z 300] [:y 20] [:x 1]) (seq cm)
       10 (:x (dissoc cm :x))
       100 (:x (dissoc (parents cm) :x))
       true (contains? cm :y)
       [:y 20] (find cm :y)
       '([:y 20] [:x 1]) (filter (fn [[k v]] (< v 100)) cm)
       (chainmap {:x 1 :y 20}) (merge {:x 1 :y 2} {:y 20})
       (chainmap {:x 1} {:y 2} {:z 3}) (chainmap {:x 1} {:y 2} {:z 3})
       cm (reduce add-child (empty cm) (reverse (maps cm)))
       "#ChainMap<({:x 2} {:y 2, :x 1} {:z 2, :y 1, :x 0})>" (pr-str (chainmap {:x 2} {:x 1 :y 2} {:x 0 :y 1 :z 2}))))


(comment

(let [N (iterate inc 0)
      m1 (doall (zipmap (take 100 N) (repeat 1)))
      m2 (doall (zipmap (take 100 (drop 50 N)) (repeat 2)))
      m3 (doall (zipmap (take 100 (drop 100 N)) (repeat 3)))
      m4 (doall (zipmap (take 100 (drop 150 N)) (repeat 4)))
      m5 (doall (zipmap (take 100 (drop 200 N)) (repeat 5)))
      ms [m1 m2 m3 m4 m5]
      rms (doall (reverse ms))
      ks (doall (take 100 (map first (partition 3 N))))]
  
  (time (dotimes [_ 100]
          (dotimes [_ 100]
            (let [c (apply chainmap ms)]
              (doall (map c ks))))))

  (time (dotimes [_ 100]
          (dotimes [_ 100]
            (let [x (apply merge rms)]
              (doall (map x ks)))))))
)
