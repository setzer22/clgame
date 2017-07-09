(ns clgame.scene
  (:require [clojure.spec :as spec]
            [clojure.spec.test :as stest]
            [clgame.component :as c]
            [clgame.entity :as e]
            [clgame.system :as s]
            [clgame.message :as m]
            [clgame.scene :as sc]
            [clgame.macros.specdefn :refer [defn']]
            [clgame.component-ref :as cr]))

(spec/def ::entities (spec/and vector? (spec/* ::e/entity)))
(spec/def ::component-data (spec/map-of ::c/type (spec/map-of ::e/id any?)))
(spec/def ::systems (spec/and vector? )) ;TODO: System spec
(spec/def ::messages (spec/map-of ::m/from (spec/* ::m/message)))
(spec/def ::system-data (spec/map-of ::s/name any?))
(spec/def ::scene (spec/keys :req [::entities ::component-data ::system-data ::systems ::messages]))


(defn' mk-scene [] -> ::scene
  {::entities []
   ::component-data {}
   ::systems []
   ::messages {}
   ::system-data {}})

(defn' get-component-spec [component-id :> ::c/type] -> keyword?
  (keyword (str "clgame.component." (name component-id)) (name component-id)))
(defn' get-component-ns [component-id :> ::c/type] -> symbol?
  (symbol (str "clgame.component." (name component-id))))

(defn' get-component [scene, ref :> ::cr/component-ref]
  "Given a component reference, returns the corresponding component data"
  (get-in scene [::component-data (::cr/comp ref) (::cr/id ref)]))

(defn' add-entity [scene :> ::scene, entity :> ::e/entity, components-data :> (spec/* any?)] -> ::scene
  (as-> scene s
    (update s ::entities conj entity)
    (reduce (fn [s [component-id component-data]]
              ;; Component data validation
              (try
                (require (get-component-ns component-id))
                (when (spec/invalid? (spec/conform (get-component-spec component-id) component-data))
                  (throw (Exception. (str "Can't add " entity " to the scene because data for " component-id " has wrong format:\n\n"
                                          (spec/explain-str (get-component-spec component-id) component-data)))))
                (catch Exception e
                  (if (or (.startsWith (.getMessage e) "Unable to resolve spec")
                          (.startsWith (.getMessage e) "Could not locate"))
                    (println "[WARNING]: No validation data for " component-id "\n\n" (.getMessage e))
                    (throw e))))
              ;; Adding the component
              (let [path [::component-data component-id (::e/id entity)]]
                (assoc-in s path component-data)))
            s
            (map vector
                 (::e/components entity)
                 components-data))))

(defn insert-entity
  "..."
  {:style/indent 1}
  [scene id & {:as components}]
  (let [component-types (into [] (keys components))]
    (add-entity scene (e/mk-entity id component-types)
                (map components component-types))))

(defn' add-system [scene :> ::scene system :> ::s/system] -> ::scene
  (update scene ::systems conj system))

;(spec/fdef update-component-data
           ;:args (spec/cat :scene ::scene :e-id ::e/id :comp-data (spec/map-of ::c/type any?))
           ;:ret ::scene)
(defn' update-component-data [scene :> ::scene, entity-id :> ::e/id,
                             new-components-data :> (spec/map-of ::c/type any?)] -> ::scene
  (reduce
   (fn [scene component]
     (assoc-in scene [::component-data component entity-id] (get new-components-data component)))
   scene
   (keys new-components-data)))

(defn' get-inbox [scene :> ::scene, id :> ::m/to] -> (spec/nilable (spec/* ::m/message))
  (get-in scene [::messages id]))

(defn' clear-inbox [scene :> ::scene, id :> ::e/id] -> ::scene
  (assoc-in scene [::messages id] []))

(defn' clear-all-messages [scene :> ::scene] -> ::scene
  (assoc scene ::messages {}))

(def conjv (fnil conj []))

(defn' add-messages [scene :> ::scene messages :> (spec/* ::m/message)] -> ::scene
  (reduce (fn [scene message]
            (update-in scene [::messages (::m/to message)] conjv message))
          scene
          messages))

(comment

  (def test-scene (-> (mk-scene)
                      (add-entity (e/mk-entity "test" [:transform :render])
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

