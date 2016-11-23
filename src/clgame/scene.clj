(ns clgame.scene
  (:require [clojure.spec :as spec]
            [clojure.spec.test :as stest]
            [clgame.component :as c]
            [clgame.entity :as e]
            [clgame.system :as s]))

(spec/def ::entities (spec/and vector? ::e/entity))
(spec/def ::component-data (spec/map-of ::c/type (spec/map-of ::e/id #(not (nil? %)))))
(spec/def ::systems (spec/and vector? ))
(spec/def ::scene (spec/keys :req [::entities ::components ::systems]))

(defn mk-scene
  ([]
   {::entities []
    ::component-data {}
    ::systems []}))

(defn add-entity [scene entity components-data]
  (as-> scene s
      (update s ::entities conj entity)
      (reduce (fn [s [component-id component-data]]
                ;TODO: Component data valiation
                (let [path [::component-data component-id (::e/id entity)]]
                  (assoc-in s path component-data)))
              s
              (map vector
                     (::e/components entity)
                     components-data))))

(defn add-system [scene system]
  (update scene ::systems conj system))

(defn update-component-data [scene entity-id components new-components-data]
  (loop [scene scene
         [c & components] components
         [c-d & components-data] new-components-data]
    (if c
      (recur (assoc-in scene [::component-data c entity-id] c-d)
             components
             components-data)
      scene)))

(comment

  (def test-scene (-> (mk-scene)
                      (add-entity (e/mk-entity [:transform :render])
                                  [{:x 1 :y 1}
                                   {}])))

  (update-component-data test-scene "entity-46" [:render] [{:x "wat" :y "pedo"}])

  )



