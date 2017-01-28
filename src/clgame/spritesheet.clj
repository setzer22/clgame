(ns clgame.spritesheet
  (:require [clojure.spec :as spec]
            [clgame.macros.specdefn :refer [defn']]))

(def spritesheet
  {::tiles-width 16.0
   ::tiles-height 16.0})

(spec/def ::tiles-height float?)
(spec/def ::tiles-width float?)
(spec/def ::spritesheet (spec/keys :req [::tiles-height ::tiles-width]))

(defn' get-sprite-uv [{:keys [::tiles-width ::tiles-height]} :> ::spritesheet
                      i :> number? j :> number?]
  {:u (/ j (float tiles-width))
   :v (/ i (float tiles-height))
   :tw (/ 1 (float tiles-width))
   :th (/ 1 (float tiles-height))})

