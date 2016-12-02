(ns clgame.system.collision
  (:require
   [clojure.math.combinatorics :as combinatorics]
   [clgame.system :as s]
   [clgame.system.registration :refer [register-system]]
   [clgame.entity :as e]
   [clgame.message :as m]
   [clgame.scene :as sc]
   [clojure.set :as set]))

(defn in? [seq elm]
  (some #(= elm %) seq))

(defn transform->rect [t]
  {:left (- (:x t) (/ (:w t) 2))
   :right (+ (:x t) (/ (:w t) 2))
   :top (+ (:y t) (/ (:h t) 2))
   :bottom (- (:y t) (/ (:h t) 2))})

(defn entity-intersect [t1 t2]
  (let [r1 (transform->rect t1)
        r2 (transform->rect t2)]
    (not (or (> (:left r2) (:right r1))
             (< (:right r2) (:left r1))
             (< (:top r2) (:bottom r1))
             (> (:bottom r2) (:top r1))))))


(def conjv (fnil conj []))

(defn collision-system-executor [scene]
  (let [component-set #{:transform :collider}
        collidable-entities (map ::e/id (filter #(set/subset? component-set (set (::e/components %))) (::sc/entities scene)))
        scene (assoc-in scene [::sc/system-data :Collision] [])]
    (reduce (fn [scene [e1 e2]] (if (entity-intersect (get-in scene [::sc/component-data :transform e1])
                                                      (get-in scene [::sc/component-data :transform e2]))
                                  (update-in scene [::sc/system-data :Collision] conjv [e1 e2])
                                  scene))
            scene
            (combinatorics/combinations collidable-entities 2))))

(register-system :Collision
  (sc/add-system
   scene
   (s/mk-system :Collision [:transform :collider]
                collision-system-executor)))

(comment "TEST"
         (def test-collision-scene
           (-> (sc/mk-scene)
               (sc/add-entity (e/mk-entity [:transform :collider]) [{:x 0 :y 0 :w 10 :h 10} {:static true}])
               (sc/add-entity (e/mk-entity [:transform :collider]) [{:x 0 :y 0 :w 3 :h 3} {:static true}])
               (sc/add-entity (e/mk-entity [:transform :collider]) [{:x 100 :y 100 :w 3 :h 3} {:static false}])))

         (::sc/system-data (collision-system-executor test-collision-scene))

         )

