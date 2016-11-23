(ns clgame.system-functions
  (:require [clojure.spec :as spec]
            [clojure.spec.test :as stest]
            [clgame.component :as c]
            [clgame.entity :as e]
            [clgame.system :as s]
            [clgame.scene :as sc]
            [clojure.set :as set]))

(defn default-system-executor
  "Returns a function that iterates over all entities having the required
   components and transforms them using iteration-fn, which takes the
   argument components in the same order as in 'components' and returns the
   new components"
  [components iteration-fn]
  ;;TODO: Check which entities are affected by the subsystem
  (let [component-set (set components)]
    (fn [scene]
      (loop [scene scene
             [e & entities] (::sc/entities scene)]
         (let [{:keys [::e/components ::e/id]} e]
          (cond
           (not e) scene
           (set/subset? component-set (set components))
           (let [component-data (mapv #(get-in scene [::sc/component-data % id])
                                      components)
                 new-component-data (iteration-fn component-data)]
             (recur (sc/update-component-data scene id components new-component-data)
                    entities))
           :else (recur scene entities)))))))


