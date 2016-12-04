(ns clgame.spritesheet)

(def spritesheet
  {::tiles-width 16.0
   ::tiles-height 16.0})

(defn get-sprite-uv [{:keys [::tiles-width ::tiles-height]} i j]
  {:u (/ j tiles-width)
   :v (/ i tiles-height)
   :tw (/ 1 tiles-width)
   :th (/ 1 tiles-height)})
