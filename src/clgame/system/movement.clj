(ns clgame.system.movement
  (:require [clgame.system :as s]
            [clgame.scene :as sc]
            [clgame.system.registration :refer [register-system]]
            [clgame.system.default :refer [default-system-executor]])
  (:import [org.lwjgl.input Keyboard Mouse]))

(defn move [e-id [transform]]
  (let [moved (cond
                (Keyboard/isKeyDown Keyboard/KEY_W) (update transform :y inc)
                (Keyboard/isKeyDown Keyboard/KEY_A) (update transform :x dec)
                (Keyboard/isKeyDown Keyboard/KEY_S) (update transform :y dec)
                (Keyboard/isKeyDown Keyboard/KEY_D) (update transform :x inc)
                :else transform)]
    {:transform moved}))

(register-system :Movement
  (sc/add-system
   scene
   (s/mk-system :Movement
                [:transform]
                (default-system-executor [:transform] move))))
