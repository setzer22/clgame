(ns clgame.system.collision
  (:require
   [clojure.math.combinatorics :as combinatorics]
   [clgame.system :as s]
   [clgame.system.registration :refer [register-system]]
   [clgame.entity :as e]
   [clgame.vector  :refer :all]
   [clgame.message :as m]
   [clgame.scene :as sc]
   [clojure.set :as set]))

(defn in? [seq elm]
  (some #(= elm %) seq))

(defn entity-intersect? [[{x1 :x y1 :y} {h1 :h w1 :w}]
                         [{x2 :x y2 :y} {h2 :h w2 :w}]]
  (let [T (-v (v2 x1 y1) (v2 x2 y2))]
    (not (or (> (Math/abs (:x T)) (+ (/ w1 2) (/ w2 2)))
             (> (Math/abs (:y T)) (+ (/ h1 2) (/ h2 2)))))))

(def conjv (fnil conj []))

(defn get-collider-info [scene e]
  [(get-in scene [::sc/component-data :transform e :position]) ;TODO Handle rotation and scale
   (get-in scene [::sc/component-data :collider e])])

(defn collision-system-executor [scene]
  (let [component-set #{:transform :collider}
        collidable-entities (into []
                             (comp (filter #(set/subset? component-set (set (::e/components %))))
                                   (map ::e/id))
                             (::sc/entities scene))
        collisions (persistent!
                    (reduce
                     (fn [collisions [e1 e2]]
                       (if (entity-intersect? (get-collider-info scene e1)
                                              (get-collider-info scene e2))
                         (conj! collisions [e1 e2])
                         collisions))
                     (transient [])
                     (combinatorics/combinations collidable-entities 2)))]
    (assoc-in scene [::sc/system-data :Collision] collisions)))

(register-system :Collision
  (sc/add-system
   scene
   (s/mk-system :Collision [:transform :collider]
                collision-system-executor)))

(comment "TEST"
         (def test-collision-scene ;TODO: Outdated
           (-> (sc/mk-scene)
               (sc/add-entity (e/mk-entity "e1" [:transform :collider]) [{:x 0 :y 0 :w 10 :h 10} {:static true}])
               (sc/add-entity (e/mk-entity "e2" [:transform :collider]) [{:x 0 :y 0 :w 3 :h 3} {:static true}])
               (sc/add-entity (e/mk-entity "e3" [:transform :collider]) [{:x 100 :y 100 :w 3 :h 3} {:static false}])))

         (::sc/system-data (collision-system-executor test-collision-scene))

         )

