(ns clgame.system.controller
  (:require [clgame.system :as s]
            [clgame.scene :as sc]
            [clgame.vector :refer :all]
            [clgame.system.default :refer [register-default]])
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

(register-default :Controller [:controller :movement] it)
