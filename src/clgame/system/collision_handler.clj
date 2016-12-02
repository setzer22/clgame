(ns clgame.system.collision-handler
  (:require
   [clgame.system :as s]
   [clgame.system.registration :refer [register-system]]
   [clgame.entity :as e]
   [clgame.vector :refer :all]
   [clgame.message :as m]
   [clgame.scene :as sc]
   [clojure.set :as set]))

(defn posz? [x] (>= x 0))

(defn rotate [{:keys [x y]} theta]
  (let [c (Math/cos theta)
        s (Math/sin theta)]
    (v2 (- (* x c) (* y s))
        (+ (* x s) (* y c)))))

(defn quadrant-with-rotation [center p rotation]
  (let [v' (-v p center)
        {:keys [x y]} (rotate v' (- rotation))]
    (cond (and (posz? x) (posz? y)) 1
          (and (neg? x) (posz? y)) 2
          (and (neg? x) (neg? y)) 3
          (and (posz? x) (neg? y)) 4
          :else (throw (Exception. "wat?")))))

(defn static-collision [[t1 c1 :as static] [t2 c2 :as moving]]
  (let [c1 (v2 (:x t1) (:x t2))
        c2 (v2 (:x t1) (:x t2))
        q (quadrant-with-rotation c1 c2 (/ Math/PI 4))]
    ))

(defn dynamic-collision [a b])

(defn handle-collision [[t1 c1 :as e1] [t2 c2 :as e2]]
  (cond
    (and (:static c1) (not (:static c2))) (static-collision e1 e2)
    (and (not (:static c1)) (:static c2)) (static-collision e2 e1)
    (and (not (:static c1)) (not (:static c2))) (dynamic-collision e1 e2)
    :else [e1 e2]))
