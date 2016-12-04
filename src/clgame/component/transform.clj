(ns clgame.component.transform
  (:require [clojure.spec :as spec]
            [clgame.vector :refer :all])
  (:import [clgame.vector Vector2]))

(spec/def ::position v2?)
(spec/def ::rotation float?)
(spec/def ::scale v2?)
(spec/def ::transform
  (spec/keys :req-un [::position ::rotation ::scale]))

(comment "Example"
         {::position (v2 0.0 0.0)
          ::rotation 0.0
          ::scale (v2 1.0 1.0)})

