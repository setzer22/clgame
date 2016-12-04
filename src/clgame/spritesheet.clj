(ns clgame.spritesheet
  (:require [clojure.spec :as spec]))

(def spritesheet
  {::tiles-width 16.0
   ::tiles-height 16.0})

(defn get-sprite-uv [{:keys [::tiles-width ::tiles-height]} i j]
  {:u (/ j tiles-width)
   :v (/ i tiles-height)
   :tw (/ 1 tiles-width)
   :th (/ 1 tiles-height)})

(spec/def ::tiles-height float?)
(spec/def ::tiles-width float?)
(spec/def ::spritesheet (spec/keys :req-un [::tiles-height ::tiles-width]))
