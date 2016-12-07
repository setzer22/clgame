(ns clgame.system.controller
  (:require [clgame.system :as s]
            [clgame.scene :as sc]
            [clgame.message :as m]
            [clgame.vector :refer :all]
            [clgame.system.default :refer [register-default]])
  (:import [org.lwjgl.input Keyboard Mouse]))

(defn key? [k]
  (Keyboard/isKeyDown k))

(defn it [e-id [{:keys [jump-speed walk-acceleration gravity
                        brake-multiplier]
                 :as controller},
                {:keys [grounded] :as ground-sensor},
                {:keys [acceleration velocity] :as movement}]
          inbox]
  (let [new-acceleration
        (v2 (* walk-acceleration
               (cond (key? Keyboard/KEY_D) 1
                   (key? Keyboard/KEY_A) -1
                   :else 0))
            gravity)

        new-velocity (assoc velocity
                            :x (if (<= 0 (* (:x acceleration)
                                            (:x velocity)))
                                 (* brake-multiplier (:x velocity))
                                 (:x velocity))
                            :y (if (and grounded (key? Keyboard/KEY_SPACE))
                                 jump-speed (:y velocity)))]
    {:movement (assoc movement :acceleration new-acceleration
                      :velocity new-velocity)
     :controller (assoc controller :last-acceleration new-acceleration)
     ::m/messages (if (not= new-acceleration (:last-acceleration controller))
                    (if (== (:x new-acceleration) 0)
                      [(m/mk-message e-id e-id :idle :change-state)]
                      [(m/mk-message e-id e-id :walking :change-state)]))}))

(register-default :Controller [:controller :ground-sensor :movement] it)
