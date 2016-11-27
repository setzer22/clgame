(ns clgame.system.collision
  (:require
   [clgame.system :as s]
   [clgame.entity :as e]
   [clgame.entity :as m]
   [clgame.scene :as sc]))

;;TODO: This

(defn in? [seq elm]
  (some #(= elm %) seq))

(comment (defn collision-system-executor
   (fn [scene]
     (let [collidable-entities (filter
                                (fn [e] (in? (::e/components e) :collider))
                                (::sc/entities scene))]
       (for [e1 collidable-entities
             e2 collidable-entities]
         ()))
     (loop [scene scene
            [e & entities] (::sc/entities scene)]
       (let [{:keys [::e/components ::e/id]} e]
         (cond
           (not e) scene
           (set/subset? component-set (set components))
           (let [component-data (mapv #(get-in scene [::sc/component-data % id])
                                      components)
                 response (iteration-fn id component-data)
                 new-component-data (dissoc response ::m/messages)
                 new-messages (::m/messages response)]
             (recur (-> scene
                        (sc/update-component-data id new-component-data)
                        (sc/add-messages new-messages))
                    entities))
           :else (recur scene entities)))))))


