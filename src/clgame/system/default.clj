(ns clgame.system.default
  (:require [clgame.macros.specdefn :refer [defn']]
            [clojure.spec :as spec]
            [clojure.spec.test :as stest]
            [clgame.component-ref :as cr]
            [clgame.component :as c]
            [clgame.entity :as e]
            [clgame.system :as s]
            [clgame.system.registration :refer [register-system]]
            [clgame.message :as m]
            [clgame.scene :as sc]
            [clojure.set :as set]
            [clgame.message :as m]))

(defn default-system-executor
  "TODO @Obsolete. Returns a function that iterates over all entities having the required
   components and transforms them using iteration-fn, which takes the
   argument components in the same order as in 'components' and returns the
   new components. Will execute pre-fn on the scene after the iterations and
   post-fn after them."
  ([component-refs iteration-fn] (default-system-executor component-refs identity iteration-fn identity))
  ([component-refs pre-fn iteration-fn post-fn]
   ;;TODO: Index which entities are affected by the subsystem
   (let [component-set (set (map ::cr/comp (filter #(= (::cr/id %) ::cr/self) component-refs)))]
     (fn [scene]
       (loop [scene (pre-fn scene)
              [e & entities] (::sc/entities scene)]
         (let [{e-components ::e/components e-id ::e/id} e]
           (cond
             (not e) (post-fn scene)
             (set/subset? component-set (set e-components))
             (let [component-data (mapv (partial sc/get-component scene)
                                        (map
                                         #(if (= (::cr/id %) ::cr/self)
                                            (cr/mk-ref e-id (::cr/comp %))
                                            %)
                                         component-refs))
                   response (iteration-fn e-id component-data (sc/get-inbox scene e-id))
                   new-component-data (dissoc response ::m/messages)
                   new-messages (::m/messages response)]
               (recur (-> scene
                          (sc/update-component-data e-id new-component-data)
                          (sc/add-messages new-messages))
                      entities))
             :else (recur scene entities))))))))

(defn' register-default
   [system-name :> ::s/name
    component-refs :> (spec/coll-of ::cr/component-ref)
    iteration-fn]
   (register-system system-name
     (sc/add-system
      scene
      (s/mk-system system-name
                   [] ;;TODO: @Deprecated?
                   (default-system-executor component-refs iteration-fn)))))

