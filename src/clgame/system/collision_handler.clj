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

(def abs #(Math/abs %))

(defmacro side
  "..."
  {:style/indent 2}
  [s d & {:keys [top bottom left right]}]
  `(let [{dx# :x dy# :y} (-v ~s ~d)]
     (cond (and (>= (abs dx#) (abs dy#)) (>= dx# 0)) ~left
           (and (>= (abs dx#) (abs dy#)) (< dx# 0)) ~right
           (and (< (abs dx#) (abs dy#)) (>= dy# 0)) ~bottom
           (and (< (abs dx#) (abs dy#)) (< dy# 0)) ~top)))

(defmacro half [x]
  `(/ ~x 2))

(defn push-vector [t1 t2]
  "The minimum push vector for intersecting rectangles t1 and t2.
   Assumes t1 is static and t2 dynamic."
  (let [s (v2 (:x t1) (:y t1))
        m (v2 (:x t2) (:y t2))
        [sw mw sh mh] [(half (:w t1)) (half (:w t2)) (half (:h t1)) (half (:h t2))]
        t (-v m s)
        push-x (+ mw sw (- (abs (:x t))))
        push-y (+ mh sh (- (abs (:y t))))]
    (if (<= push-y push-x)
      (cond
        (pos? (:y t)) (v2 0 push-y)
        :else         (v2 0 (- push-y)))
      (cond
        (pos? (:x t)) (v2 push-x 0)
        :else         (v2 (- push-x) 0)))))

;(defn dynamic-collision [a b])

;(defn handle-collision [[t1 c1 :as e1] [t2 c2 :as e2]]
  ;(cond
    ;(and (:static c1) (not (:static c2))) (static-collision e1 e2)
    ;(and (not (:static c1)) (:static c2)) (static-collision e2 e1)
    ;(and (not (:static c1)) (not (:static c2))) (dynamic-collision e1 e2)
    ;:else [e1 e2]))


(defn static-collision [t1 t2]
  (let [{:keys [x y]} (+v (v2 (:x t2) (:y t2)) (push-vector t1 t2))]
    (assoc t2 :x x :y y)))

(defn collision-handler-executor [scene]
  (reduce
   (fn [scene [e1 e2]]
     (let [c1 (get-in scene [::sc/component-data :collider e1])
           c2 (get-in scene [::sc/component-data :collider e2])
           t1 (get-in scene [::sc/component-data :transform e1])
           t2 (get-in scene [::sc/component-data :transform e2])]
       (cond
         (and (:static c1) (not (:static c2))) (assoc-in scene [::sc/component-data :transform e2]
                                                         (static-collision t1 t2))
         (and (not (:static c1)) (:static c2)) (assoc-in scene [::sc/component-data :transform e1]
                                                         (static-collision t2 t1))
         :else scene)))
   scene
   (-> scene ::sc/system-data :Collision)))

(register-system :CollisionHandler
  (sc/add-system
   scene
   (s/mk-system :CollisionHandler [:transform :collider]
                collision-handler-executor)))

(comment
  (def test-scene
    (-> (sc/mk-scene)
        (sc/add-entity (e/mk-entity [:transform :collider])
                       [{:x 0 :y 0 :w 10 :h 2}
                        {:static true}])
        (sc/add-entity (e/mk-entity [:transform :collider])
                       [{:x 3 :y 2 :w 3 :h 3}
                        {:static false}])))

  (require 'clgame.system.collision)

  (collision-handler-executor
   (clgame.system.collision/collision-system-executor test-scene) )

  )

