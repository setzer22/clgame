(ns clgame.system.default
  (:require [clojure.spec :as spec]
            [clojure.spec.test :as stest]
            [clgame.component :as c]
            [clgame.entity :as e]
            [clgame.system :as s]
            [clgame.system.registration :refer [register-system]]
            [clgame.message :as m]
            [clgame.scene :as sc]
            [clojure.set :as set]
            [clgame.message :as m]))

(defn default-system-executor
  "Returns a function that iterates over all entities having the required
   components and transforms them using iteration-fn, which takes the
   argument components in the same order as in 'components' and returns the
   new components. Will execute pre-fn on the scene after the iterations and
   post-fn after them."
  ([components iteration-fn] (default-system-executor components identity iteration-fn identity))
  ([components pre-fn iteration-fn post-fn]
   ;;TODO: Check which entities are affected by the subsystem
   #dbg (let [component-set (set components)]
     (fn [scene]
       (loop [scene (pre-fn scene)
              [e & entities] (::sc/entities scene)]
         (let [{e-components ::e/components e-id ::e/id} e]
           (cond
             (not e) (post-fn scene)
             (set/subset? component-set (set e-components))
             (let [component-data (mapv #(get-in scene [::sc/component-data % e-id])
                                        components)
                   response (iteration-fn e-id component-data (sc/get-inbox scene e-id))
                   new-component-data (dissoc response ::m/messages)
                   new-messages (::m/messages response)]
               (recur (let [s (-> scene
                                (sc/update-component-data e-id new-component-data)
                                (sc/add-messages new-messages))]
                        (when (seq (::m/messages response)) (println s))
                        s)
                      entities))
             :else (recur scene entities))))))))

(defn register-default
  ([system-name components iteration-fn]
   (register-system system-name
     (sc/add-system
      scene
      (s/mk-system system-name
                   components
                   (default-system-executor components iteration-fn))))))

