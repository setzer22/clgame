(ns clgame.system.controller
  (:require [clgame.system :as s]
            [clgame.scene :as sc]
            [clgame.message :as m]
            [clgame.vector :refer :all]
            [clgame.system.default :refer [register-default]])
  (:import [org.lwjgl.input Keyboard Mouse]))

(defn key? [k]
  (Keyboard/isKeyDown k))

(defn it [e-id [controller, {:keys [acceleration] :as movement}] inbox]
  (let [new-acceleration
        (normalize
         (v2 (cond (key? Keyboard/KEY_D) 1
                   (key? Keyboard/KEY_A) -1
                   :else 0)
             (cond (key? Keyboard/KEY_W) 1
                   (key? Keyboard/KEY_S) -1
                   :else 0)))]
    {:movement (assoc movement :acceleration new-acceleration)
     :controller (assoc controller :last-acceleration new-acceleration)
     ::m/messages [(if (not= new-acceleration (:last-acceleration controller))
                     (if (= new-acceleration (v2 0 0))
                       (do (println "idle") (m/mk-message e-id e-id :idle :change-state))
                       (do (println "walking") (m/mk-message e-id e-id :walking :change-state))))]}))

(register-default :Controller [:controller :movement] it)
