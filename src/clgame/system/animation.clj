(ns clgame.system.animation
  (:require [clgame.gl :as gl]
            [clgame.spritesheet :as spr]
            [clgame.system :as s]
            [clgame.scene :as sc]
            [clgame.system.default :refer [register-default]]))

(def delta-time (/ 1 60.0))

(defn pass-time [{:keys [frame-index frame-indices current-frame-time frame-time] :as animation} delta-time]
  (let [new-frame-time (+ current-frame-time delta-time)
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

(defn animation [e-id [sprite animation] inbox]
  (let [{:keys [frame-indices frame-index spritesheet] :as new-animation}
        (pass-time animation delta-time)
        [i j] (frame-indices frame-index)]
    {:animation new-animation
     :sprite (merge sprite (spr/get-sprite-uv spritesheet i j))}))

(register-default :Animation [:sprite :animation] animation)
