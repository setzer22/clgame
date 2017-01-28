(ns clgame.entity
  (:require [clojure.spec :as spec]
            [clojure.spec.test :as stest]
            [clgame.component :as c]
            [clgame.macros.specdefn :refer [defn']]))

(defonce entity-id-generator
  (let [counter (atom -1)]
    (fn []
      (swap! counter inc)
      (str "entity-"@counter))))

(spec/def ::id string?)
(spec/def ::components (spec/spec (spec/* ::c/type)))
(spec/def ::entity (spec/keys :req [::id ::components]))

(defn' mk-entity [component-types :> ::components] -> ::entity
  {::id (entity-id-generator)
   ::components component-types})

