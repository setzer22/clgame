(ns clgame.system.movement
  (:require [clgame.system :as s]
            [clgame.scene :as sc]
            [clgame.vector :refer :all]
            [clgame.system.registration :refer [register-system]]
            [clgame.system.default :refer [default-system-executor]])
  (:import [org.lwjgl.input Keyboard Mouse]))

(def delta-time (/ 1 60)); TODO !!

(defn clamp-to-length [v max-length]
  (let [l (magnitude v)]
    (if (> l max-length)
      (*v (normalize v) max-length)
      v)))

(defn move [e-id [transform {:keys [velocity acceleration acceleration-factor max-speed] :as movement}] inbox]
  (let [v (clamp-to-length (+v velocity (*v (*v acceleration delta-time) acceleration-factor))
                           max-speed)
        px (+ (:x transform) (* delta-time (:x v)))
        py (+ (:y transform) (* delta-time (:y v)))]
    {:transform (assoc transform :x px :y py)
     :movement (assoc movement :velocity v)}))

(register-system :Movement
  (sc/add-system
   scene
   (s/mk-system :Movement
                [:transform :movement]
                (default-system-executor [:transform :movement] move))))
