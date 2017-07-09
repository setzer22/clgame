(ns clgame.system.ground-detector
  (:require [clgame.system.registration :refer [register-system]]
            [clgame.scene :as sc]
            [clgame.entity :as e]
            [clgame.system :as s]
            [clojure.set :as set]
            [clgame.component-ref :as cr]))

(defn get-position [scene e]
  (get-in scene [::sc/component-data :transform e :position]))

(defn ground-sensor-executor [scene]
  (let [entities-with-sensor (into #{}
                                   (comp
                                    (filter #(.contains (::e/components %) :ground-sensor))
                                    (map ::e/id))
                                   (::sc/entities scene))]
    (let [[scene dirty]
          (reduce
           (fn [[scene dirty] [e1 e2]]
             (cond
               (and (entities-with-sensor e1)
                    (> (:y (get-position scene e1)) (:y (get-position scene e2))))
               ,,[(sc/update-component-data scene e1 {:ground-sensor {:grounded true}}) (conj dirty e1)]
               (and (entities-with-sensor e2)
                    (> (:y (get-position scene e2)) (:y (get-position scene e1))))
               ,,[(sc/update-component-data scene e2 {:ground-sensor {:grounded true}}) (conj dirty e2)]
               :else [scene dirty]))
           [scene #{}]
           (-> scene ::sc/system-data :Collision))

          clean-scene (reduce
                       (fn [scene e]
                         (sc/update-component-data scene e {:ground-sensor {:grounded false}}))
                       scene
                       (set/difference entities-with-sensor dirty))]
      clean-scene)))


(register-system :GroundSensor
  (sc/add-system
   scene
   (s/mk-system :GroundSensor
                [] ; @Deprecated
                ground-sensor-executor)))

