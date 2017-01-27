(ns clgame.system.hand-motion
  (:require [clgame.system :as s]
            [clgame.vector :refer :all]
            [clgame.system.default :refer [register-default]]))

(def delta-time (/ 1 60.0))

(defn hand-motion [e-id [movement, {:keys [t amplitude speed] :or {t 0} :as hand-motion}] inbox]
  ; if we want the position to be A*sin(wx), we must set the speed to its derivative, i.e: A*s*cos(wx)
  (let [s (* amplitude speed (Math/cos (* speed t)))]
    {:movement (assoc-in movement [:velocity :y] s)
     :hand-motion (assoc hand-motion :t (+ t delta-time))}))

(register-default :HandMotion [:movement :hand-motion] hand-motion)
