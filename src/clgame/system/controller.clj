(ns clgame.system.controller
  (:require [clgame.system :as s]
            [clgame.scene :as sc]
            [clgame.message :as m]
            [clgame.vector :refer :all]
            [clgame.system.default :refer [register-default]])
  (:import [org.lwjgl.input Keyboard Mouse]))

(defn key? [k]
  (Keyboard/isKeyDown k))

(def delta-time (/ 1.0 60.0))


(defn it [e-id [{:keys [jump-speed walk-acceleration gravity
                        brake-multiplier max-jump-time jump-state jump-time
                        last-state last-grounded]
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

        new-jump-time (+ jump-time delta-time)

        [jump-state jump-time]
        (cond
          (and (key? Keyboard/KEY_SPACE) (or (= :jumping jump-state)
                                             (= :ready jump-state))
               (< new-jump-time max-jump-time))
          ,,[:jumping new-jump-time]
          (and (key? Keyboard/KEY_SPACE) (> jump-time max-jump-time))
          ,,[:on-air new-jump-time]
          grounded
          ,,[:ready 0]
          :else
          ,,[:on-air new-jump-time])

        new-velocity (assoc velocity
                            :x (if (<= 0 (* (:x acceleration)
                                            (:x velocity)))
                                 (* brake-multiplier (:x velocity))
                                 (:x velocity))
                            :y (if (and (or (= jump-state :ready)
                                            (= jump-state :jumping))
                                        (key? Keyboard/KEY_SPACE))
                                 jump-speed (:y velocity)))

        ax  (-> new-acceleration :x)
        ax' (-> controller :last-acceleration :x)
        ax' (if ax' ax' 0) ;TODO: <-


        ;; Animation 'hard-coded' state-machine
        ;; NOTE: If I had to do a lot of those, I'd consider treating them as graphs with
        ;;       transition arcs like mecanim does.
        new-state (cond
                    ;; Just started jumping
                    (and (= :jumping jump-state) (#{:walk-right :idle-right} last-state)) :jumping-right
                    (and (= :jumping jump-state) (#{:walk-left :idle-left} last-state))   :jumping-left

                    ;; Keep jumping if we are on air and we were jumping
                    (and (= :on-air jump-state) (= :jumping-left last-state)) :jumping-left
                    (and (= :on-air jump-state) (= :jumping-right last-state)) :jumping-right

                    ;; Lands on the ground
                    (and (= last-grounded false) (= grounded true)
                         (#{:idle-left :jumping-left :walk-left} last-state))    :idle-left
                    (and (= last-grounded false) (= grounded true)
                         (#{:idle-right :jumping-right :walk-right} last-state)) :idle-right

                    ;; Stops from walking
                    (and (== ax 0) (> ax' 0) (not (#{:jumping-left :jumping-right} last-state))) :idle-right
                    (and (== ax 0) (< ax' 0) (not (#{:jumping-left :jumping-right} last-state))) :idle-left

                    ;; Starts walking
                    (and (> ax 0) (or (#{:idle-left :idle-right} last-state) (<= (* ax ax') 0))) :walk-right
                    (and (< ax 0) (or (#{:idle-left :idle-right} last-state) (<= (* ax ax') 0))) :walk-left

                    ;; Otherwise keep doing the same
                    :else                                   last-state)]
    {:movement (assoc movement :acceleration new-acceleration
                      :velocity new-velocity)
     :controller (assoc controller :last-acceleration new-acceleration
                        :jump-time jump-time
                        :jump-state jump-state
                        :last-state new-state
                        :last-grounded grounded)
     ::m/messages (if (not= new-state last-state)
                    [(m/mk-message e-id e-id new-state :change-state)])}))

(register-default :Controller [:controller :ground-sensor :movement] it)
