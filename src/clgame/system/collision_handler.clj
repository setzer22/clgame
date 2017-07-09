(ns clgame.system.collision-handler
  (:require
   [clgame.system :as s]
   [clgame.system.registration :refer [register-system]]
   [clgame.entity :as e]
   [clgame.vector :refer :all]
   [clgame.message :as m]
   [clgame.scene :as sc]
   [clojure.set :as set]
   [clgame.component-ref :as cr]))

(def abs #(Math/abs %))

(defmacro half [x]
  `(/ ~x 2))

(defn push-vector [p1 c1 p2 c2]
  "The minimum push vector for intersecting colliders."
  (let [s (v2 (:x p1) (:y p1))
        m (v2 (:x p2) (:y p2))
        [sw mw sh mh] [(half (:w c1)) (half (:w c2)) (half (:h c1)) (half (:h c2))]
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

(defn static-collision [p1 c1 p2 c2]
  (let [{:keys [x y]} (+v (v2 (:x p2) (:y p2)) (push-vector p1 c1 p2 c2))]
    (assoc p2 :x x :y y)))

(defn collision-handler-executor [scene]
  (reduce
   (fn [scene [e1 e2]]
     (let [c1 (get-in scene [::sc/component-data :collider e1])
           c2 (get-in scene [::sc/component-data :collider e2])
           p1 (get-in scene [::sc/component-data :transform e1 :position]) ;TODO: Handle rotation and scale...
           p2 (get-in scene [::sc/component-data :transform e2 :position])]
       (cond
         (and (:static c1) (not (:static c2))) (assoc-in scene [::sc/component-data :transform e2 :position]
                                                         (static-collision p1 c1 p2 c2))
         (and (not (:static c1)) (:static c2)) (assoc-in scene [::sc/component-data :transform e1 :position]
                                                         (static-collision p2 c2 p1 c1))
         :else scene)))
   scene
   (-> scene ::sc/system-data :Collision)))

(register-system :CollisionHandler
  (sc/add-system
   scene
   (s/mk-system :CollisionHandler
                [] ; @Deprecated
                collision-handler-executor)))

