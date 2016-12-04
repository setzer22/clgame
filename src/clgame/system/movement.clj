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

(defn move [e-id [{:keys[position] :as transform} {:keys [velocity acceleration acceleration-factor max-speed] :as movement}] inbox]
  (let [vel (clamp-to-length (+v velocity (*v (*v acceleration delta-time) acceleration-factor))
                           max-speed)
        new-pos (+v position (*v vel delta-time))]
    {:transform (assoc transform :position new-pos)
     :movement (assoc movement :velocity vel)}))

(register-system :Movement
  (sc/add-system
   scene
   (s/mk-system :Movement
                [:transform :movement]
                (default-system-executor [:transform :movement] move))))
