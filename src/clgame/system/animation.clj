(ns clgame.system.animation
  (:require [clgame.gl :as gl]
            [clgame.spritesheet :as spr]
            [clgame.system :as s]
            [clgame.scene :as sc]
            [clgame.system.default :refer [register-default]]
            [clgame.message :as m]
            [clgame.component-ref :as cr]))

(def delta-time (/ 1 60.0))

(defn pass-time [{:keys [frame-index current-animation current-frame-time frame-time] :as animation} delta-time]
  (let [frame-indices (-> animation :animations current-animation)
        new-frame-time (+ current-frame-time delta-time)
        frame-time (/ frame-time
                      (get-in animation [:animation-speed-overrides current-animation] 1.0))
        [elapsed-frames new-frame-time]
        (loop [elapsed-frames 0
               new-frame-time new-frame-time]
          (if (>= new-frame-time frame-time)
            (recur (inc elapsed-frames) (- new-frame-time frame-time))
            [elapsed-frames new-frame-time]))
        new-frame-index (mod (+ frame-index elapsed-frames) (count frame-indices))]
    (assoc animation
           :frame-index new-frame-index
           :current-frame-time new-frame-time)))

(defn get-tex-coords [{:keys [current-animation frame-index] :as animation}]
  (let [[i j] (get-in animation [:animations current-animation frame-index])]
    (spr/get-sprite-uv (:spritesheet animation) i j)))

(defn change-animation [animation new-animation]
  (assoc animation
         :current-animation new-animation
         :current-frame-time 0.0
         :frame-index 0))

(defn animation [e-id [sprite animation] inbox]
  (let [maybe-new-state (::m/data (first (filter #(= :change-state (::m/type %)) inbox)))
        animation (if maybe-new-state (change-animation animation maybe-new-state)
                      animation)
        {:keys [frame-indices frame-index spritesheet] :as new-animation}
        (pass-time animation delta-time)]
    {:animation new-animation
     :sprite (merge sprite (get-tex-coords new-animation))}))

(register-default :Animation [(cr/mk-ref ::cr/self :sprite) (cr/mk-ref ::cr/self :animation)] animation)
