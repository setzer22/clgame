(ns clgame.entity
  (:require [clojure.spec :as spec]
            [clojure.spec.test :as stest]
            [clgame.component :as c]))

(defonce entity-id-generator
  (let [counter (atom -1)]
    (fn []
      (swap! counter inc)
      (str "entity-"@counter))))


(spec/def ::id string?)
(spec/def ::components (spec/* ::c/type))
(spec/def ::entity (spec/keys :req [::id ::components]))


(spec/fdef mk-entity :args (spec/cat :components (spec/spec ::components)), :ret ::entity)
(defn mk-entity [component-types]
  {::id (entity-id-generator)
   ::components component-types})

(stest/instrument (stest/instrumentable-syms))
