(ns clgame.spritesheet
  (:require [clojure.spec :as spec]))

(def spritesheet
  {::tiles-width 16.0
   ::tiles-height 16.0})

(defn get-sprite-uv [{:keys [::tiles-width ::tiles-height]} i j]
  {:u (/ j (float tiles-width))
   :v (/ i (float tiles-height))
   :tw (/ 1 (float tiles-width))
   :th (/ 1 (float tiles-height))})

(spec/def ::tiles-height float?)
(spec/def ::tiles-width float?)
(spec/def ::spritesheet (spec/keys :req [::tiles-height ::tiles-width]))
