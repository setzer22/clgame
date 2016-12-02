(ns clgame.system.controller
  (:require [clgame.system :as s]
            [clgame.scene :as sc]
            [clgame.vector :refer :all]
            [clgame.system.registration :refer [register-system]]
            [clgame.system.default :refer [default-system-executor]])
  (:import [org.lwjgl.input Keyboard Mouse]))

(defn key? [k]
  (Keyboard/isKeyDown k))

(defn it [e-id [_, {:keys [acceleration] :as movement}] inbox]
  {:movement
   (assoc movement :acceleration (normalize
                                  (v2 (cond (key? Keyboard/KEY_D) 1
                                            (key? Keyboard/KEY_A) -1
                                            :else 0)
                                      (cond (key? Keyboard/KEY_W) 1
                                            (key? Keyboard/KEY_S) -1
                                            :else 0))))})

(register-system :Controller
  (sc/add-system
   scene
   (s/mk-system :Controller
                [:controller :movement]
                (default-system-executor [:controller :movement] it))))
