(ns clgame.scene
  (:require [clojure.spec :as spec]
            [clojure.spec.test :as stest]
            [clgame.component :as c]
            [clgame.entity :as e]
            [clgame.system :as s]
            [clgame.message :as m]))

(spec/def ::entities (spec/and vector? ::e/entity))
(spec/def ::component-data (spec/map-of ::c/type (spec/map-of ::e/id #(not (nil? %)))))
(spec/def ::systems (spec/and vector? ))
(spec/def ::messages (spec/map-of ::m/from ::m/message))
(spec/def ::scene (spec/keys :req [::entities ::components ::systems ::messages]))

(defn mk-scene
  ([]
   {::entities []
    ::component-data {}
    ::systems []
    ::messages {}}))

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

(spec/fdef update-component-data
           :args (spec/cat :scene ::scene :e-id ::e/id :comp-data (spec/map-of ::c/type any?))
           :ret ::scene)
(defn update-component-data [scene entity-id new-components-data]
  (reduce
   (fn [scene component]
     (assoc-in scene [::component-data component entity-id] (get new-components-data component)))
   scene
   (keys new-components-data)))

(defn get-inbox [scene id]
  (get-in scene [::messages id]))

(defn clear-inbox [scene id]
  (assoc-in scene [::messages id] []))

(def conjv (fnil conj []))

(defn add-messages [scene messages]
  (reduce (fn [scene message]
            (update-in scene [::messages (::m/to message)] conjv message))
          scene messages))



(comment

  (def test-scene (-> (mk-scene)
                      (add-entity (e/mk-entity [:transform :render])
                                  [{:x 1 :y 1}
                                   {}])))

  (get-all-entities-with test-scene :transform)

  (clear-inbox
   (add-messages test-scene [(m/mk-message "entity-42" ::m/global-msg :quit nil)
                             (m/mk-message "entity-42" ::m/global-msg :wat nil)])
   ::m/global-msg)

  (update-component-data
   (update-component-data test-scene "entity-46"  {:transform {:x "wat" :y "wat"}})
   "entity-46" {:transform {:x 1 :y 2}})

  )



(stest/instrument (stest/instrumentable-syms))



