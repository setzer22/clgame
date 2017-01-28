(ns clgame.component.movement
  (:require [clgame.utils]
            [clgame.vector :refer :all]
            [clojure.spec :as spec]
            [clgame.macros.defcomponent :refer [defcomponent]]))

(spec/def :speed-clamp/x (spec/and vector?
                                   (spec/tuple :conform/float
                                               :conform/float)
                                   (fn [[a b]] (< a b))))
(spec/def :speed-clamp/y :speed-clamp/x)

(defcomponent :movement
  :velocity     :> v2?
  :acceleration :> v2?
  :speed-clamp  :> (spec/keys :req-un [:speed-clamp/y
                                       :speed-clamp/x]))
