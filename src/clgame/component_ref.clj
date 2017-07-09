(ns clgame.component-ref
  (:require [clojure.spec :as spec]
            [clgame.macros.specdefn :refer [defn']]
            [clgame.entity :as e]
            [clgame.component :as c]))

(spec/def ::id (spec/or :id ::e/id :self #(= % ::self)))
(spec/def ::comp ::c/type)
(spec/def ::component-ref (spec/keys :req [::id ::comp]))

(defn' mk-ref [id :> ::id, comp :> ::comp] :> ::component-ref
  {::id id ::comp comp})

(defn' parse-ref-name [ref-name :> keyword?] :> ::component-ref
  (let [[id comp] (clojure.string/split (name ref-name) #":")]
    (if comp
      {::id id
       ::comp (keyword comp)}
      {::id ::self
       ::comp (keyword id)})))

;;(defn' unparse-ref-name [{:keys [id comp]} :> ::component-ref] :> keyword?
  ;;(keyword (str id ":" comp)))
