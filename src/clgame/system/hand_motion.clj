(ns clgame.system.hand-motion
  (:require [clgame.macros.defsystem :refer [defsystem]]
            [clgame.vector :as v :refer :all]))

(defmacro nif [expr pos zero neg]
  `(let [result# ~expr]
     (cond
       (pos? result#) ~pos
       (zero? result#) ~zero
       (neg? result#) ~neg)))

(defsystem :HandMotion [:movement :hand-motion :transform :player:transform]
  (let [sy (* (hand-motion :amplitude) (hand-motion :speed)
             (Math/cos (* (hand-motion :speed) (hand-motion :t))))
        dir (+v (-v (player:transform :position)
                    (transform :position))
                (v2 0 sy))]
    {:movement (-> movement
                   (assoc :velocity dir)) 
     :hand-motion (assoc hand-motion :t (+ (hand-motion :t) delta-time))}))
