(ns clgame.system.forall-collisions
  (:require [clojure.spec :as spec]
            [clojure.spec.test :as stest]
            [clgame.component :as c]
            [clgame.entity :as e]
            [clgame.system :as s]
            [clgame.system.registration :refer [register-system]]
            [clgame.message :as m]
            [clgame.scene :as sc]
            [clojure.set :as set]
            [clgame.vector :refer :all]
            [clojure.inspector]
            [clgame.message :as m]))

(defn get-collider [scene id]
  (get-in scene [::sc/component-data :collider id]))

(defn get-data [scene components id]
  (mapv #(get-in scene [::sc/component-data % id]) components))

(defn collisions-system-executor
  "Returns a function that iterates over all entity pairs which have
   collided during this frame and, for those entity pairs having
   the requested sets of collision tags, calls the handle-collision
   function. The handle-collision function takes two entities and
   must return the changes in both."
  [tags1 tags2 components handle-collision-fn]
  ;;...
  (let [tags1 (set tags1)
        tags2 (set tags2)]
    (fn collisions-system-executor-fn [scene]
      (let [collisions (-> scene ::sc/system-data :Collision)]
        (reduce
         (fn wat [scene [e1 e2]]
           (let [e1-tags (:tags (get-collider scene e1))
                 e2-tags (:tags (get-collider scene e2))
                 [new-data-e1 new-data-e2 :as result]
                 (cond
                   (and (set/subset? tags1 e1-tags)
                        (set/subset? tags2 e2-tags))
                   (handle-collision-fn (get-data scene components e1)
                                        (get-data scene components e2))
                   (and (set/subset? tags1 e2-tags)
                        (set/subset? tags2 e1-tags))
                   (handle-collision-fn (get-data scene components e1)
                                        (get-data scene components e2))
                   :else nil)]
             (if result
               (-> scene
                  (sc/update-component-data e1 new-data-e1)
                  (sc/update-component-data e2 new-data-e2))
               scene)))
         scene
         collisions)))))

(defn register-collision-executor
  [system-name tags1 tags2 components function]
  (register-system system-name
    (sc/add-system
     scene
     (s/mk-system system-name
                  components
                  (collisions-system-executor tags1 tags2 components function)))))

(comment
  (def test-scene
    (-> (sc/mk-scene)
        (sc/insert-entity "player"
          :transform {:position (v2 10 10)
                      :rotation 0.0
                      :scale (v2 1 1)}
          :collider {:tags #{:player}
                     :static true
                     :w 1 :h 1})

        (sc/insert-entity "enemy"
          :transform {:position (v2 10 10)
                      :rotation 0.0
                      :scale (v2 1 1)}
          :collider {:tags #{:enemy}
                     :static true
                     :w 0.5 :h 0.5})))

  (def executor (collisions-system-executor #{:player} #{:enemy} [:transform]
                                            (fn [t1 t2]
                                              (clojure.java.shell/sh "notify-send" "wat")
                                              [{} {}])))

  (-> test-scene
      clgame.system.collision/collision-system-executor
      executor) ;; A notification should be sent
 )



