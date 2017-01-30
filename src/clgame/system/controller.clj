(ns clgame.system.controller
  (:require [clgame.system :as s]
            [clgame.scene :as sc]
            [clgame.message :as m]
            [clgame.vector :refer :all]
            [clgame.system.default :refer [register-default]]
            [clgame.macros.state-machine :refer [state-machine]])
  (:import [org.lwjgl.input Keyboard Mouse]))

(defn key? [k]
  (Keyboard/isKeyDown k))

(def delta-time (/ 1.0 60.0))

(defmacro state-or [state states]
  `(or ~@(map
          (fn [kw]
            `(= ~state ~kw))
          states)))

(defn it [e-id [{:keys [jump-speed walk-acceleration gravity
                        brake-multiplier max-jump-time jump-state jump-time
                        last-state last-grounded has-control? is-hit?
                        hit-timer max-hit-timer]
                 :as   controller},
                {:keys [grounded] :as ground-sensor},
                {:keys [acceleration velocity] :as movement}]
          inbox]
  ;; @Cleanup ;; @Cleanup ;; @Cleanup ;; @Cleanup ;; @Cleanup ;; @Cleanup ;; @Cleanup
  (let [;; Input query at the beginning
        ;; @Cleanup
        left-key?  (if has-control? (key? Keyboard/KEY_A) false)
        up-key?    (if has-control? (key? Keyboard/KEY_W) false)
        down-key?  (if has-control? (key? Keyboard/KEY_S) false)
        right-key? (if has-control? (key? Keyboard/KEY_D) false)
        jump-key?  (if has-control? (key? Keyboard/KEY_SPACE) false)

        ;; -------------------------------------------------
        ;; Compute new acceleration, velocity and jump state
        ;; -------------------------------------------------

        new-acceleration
        (v2 (* walk-acceleration
               (cond right-key? 1
                     left-key?  -1
                     :else      0))
            gravity)

        new-jump-time (+ jump-time delta-time)
        still-time? (< new-jump-time max-jump-time)

        new-jump-state
        (state-machine jump-state
          :ready   -- grounded    -> :ready
          :ready   -- jump-key?   -> :jumping
          :ready   -- :else       -> :on-air
          :jumping -- (and jump-key? still-time?) -> :jumping
          :jumping -- :else       -> :on-air
          :on-air  -- grounded    -> :ready
          :on-air  -- :else       -> :on-air)

        jump-time (if (= new-jump-state :ready) 0 new-jump-time)
        jump-state new-jump-state

        new-velocity
        (v2 (if (<= 0 (* (:x acceleration) (:x velocity)))
              (* brake-multiplier (:x velocity))
              (:x velocity))
            (if (and (state-or jump-state [:ready :jumping])
                     jump-key?)
              jump-speed (:y velocity)))

        ;; Animation 'hard-coded' state-machine
        ;; NOTE: If I had to do a lot of those, I'd consider treating them as graphs with
        ;;       transition arcs like mecanim does.
        ax  (-> new-acceleration :x)
        ax' (-> controller :last-acceleration :x)
        ax' (if ax' ax' 0)

        new-state
        (state-machine last-state
          ;; Started Jumping
          [:walk-right :idle-right] -- (= :jumping jump-state) -> :jumping-right
          [:walk-left :idle-left] -- (= :jumping jump-state) -> :jumping-left

          ;; Landing
          [:idle-left :jumping-left :walk-left] -- (and (not last-grounded) grounded) -> :idle-left
          [:idle-right :jumping-right :walk-right] -- (and (not last-grounded) grounded) -> :idle-right

          ;; Stop from walking
          [:idle-left :walk-left :idle-right :walk-right] -- (and (== ax 0) (> ax' 0)) -> :idle-right
          [:idle-left :walk-left :idle-right :walk-right] -- (and (== ax 0) (< ax' 0)) -> :idle-left

          ;; Start walking
          [:idle-left :idle-right] -- (and (> ax 0) (<= (* ax ax') 0)) -> :walk-right
          [:idle-left :idle-right] -- (and (< ax 0) (<= (* ax ax') 0)) -> :walk-left)]

    {:movement    (assoc movement :acceleration new-acceleration
                         :velocity new-velocity)
     :controller  (assoc controller :last-acceleration new-acceleration
                         :jump-time jump-time
                         :jump-state jump-state
                         :last-state new-state
                         :last-grounded grounded)
     ::m/messages (if (not= new-state last-state)
                    [(m/mk-message e-id e-id new-state :change-state)])}))

(register-default :Controller [:controller :ground-sensor :movement] it)
