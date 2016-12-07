(ns clgame.component.movement
  (:require [clgame.utils]
            [clgame.vector :refer :all]
            [clojure.spec :as spec]))

(spec/def ::velocity v2?)
(spec/def ::acceleration v2?)
(spec/def :speed-clamp/x (spec/and vector?
                                   (spec/tuple :conform/float
                                               :conform/float)
                                   (fn [[a b]] (< a b))))
(spec/def :speed-clamp/y :speed-clamp/x)
(spec/def ::speed-clamp (spec/keys :req-un [:speed-clamp/y
                                            :speed-clamp/x]))
(spec/def ::movement (spec/keys :req-un [::velocity ::acceleration
                                         ::speed-clamp]))
