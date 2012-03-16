# chainmap

A custom Map-like datastructure where multiple maps can be linked in O(1) time and can be treated as a single unit.
When looking up keys in a ChainMap the individual maps are searched in succession.

Useful when you need to quickly create multiple contexts without the O(n) overhead of using `merge`.

*Caveat* - The current implementation is not performant.

## Usage

    (ns foo
      (:use [chainmap.core :only [chainmap]]))

    (chainmap {:x 1} {:x 10 :y 20} {:x 100 :y 200 :z 300})
    ;=> #ChainMap<({:x 1} {:y 20, :x 10} {:z 300, :y 200, :x 100})>

    (def cm (chainmap {:x 1} {:x 10, :y 20} {:x 100, :y 200, :z 300}))
    ;=> #'user/cm

    (maps cm)
    ;=> ({:x 1} {:y 20, :x 10} {:z 300, :y 200, :x 100})

    (get cm :z)
    ;=> 300

    (map cm [:x :y :z])
    ;=> (1 20 300)

    (parents cm)
    ;=> #ChainMap<({:y 20, :x 10} {:z 300, :y 200, :x 100})>

    (add-child cm)
    ;=> #ChainMap<({} {:x 1} {:y 20, :x 10} {:z 300, :y 200, :x 100})>

    (add-child cm {:x 42})
    ;=> #ChainMap<({:x 42} {:x 1} {:y 20, :x 10} {:z 300, :y 200, :x 100})>

## Acknowledgements

Based on the ChainMap class from Python collections library.

## License

Copyright (C) 2011 Baishampayan Ghose

Distributed under the Eclipse Public License, the same as Clojure.
