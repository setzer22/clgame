(ns clgame.system.movement
  (:require [clgame.system :as s]
            [clgame.scene :as sc]
            [clgame.vector :refer :all]
            [clgame.system.registration :refer [register-system]]
            [clgame.system.default :refer [default-system-executor]]
            [clgame.component-ref :as cr])
  (:import [org.lwjgl.input Keyboard Mouse]))

(def delta-time (/ 1 60)); TODO !!

(defn clamp-vector [v
                    {[^float min-x ^float max-x] :x
                     [^float min-y ^float max-y] :y}]
  (cond-> v
    (> (:x v) max-x) (assoc :x max-x)
    (> (:y v) max-y) (assoc :y max-y)
    (< (:x v) min-x) (assoc :x min-x)
    (< (:y v) min-y) (assoc :y min-y)))

(defn move [e-id [{:keys[position] :as transform} {:keys [velocity acceleration speed-clamp] :as movement}] inbox]
  (let [vel (clamp-vector
             (+v velocity (*v acceleration delta-time))
             speed-clamp)
        new-pos (+v position (*v vel delta-time))]
    {:transform (assoc transform :position new-pos)
     :movement (assoc movement :velocity vel)}))

(register-system :Movement
  (sc/add-system
   scene
   (s/mk-system :Movement
                [] ; @Deprecated
                (default-system-executor [(cr/mk-ref ::cr/self :transform) (cr/mk-ref ::cr/self :movement)] move))))
