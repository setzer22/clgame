(ns clgame.component
  (:require [clojure.spec :as spec]
            [clojure.spec.test :as stest]
            [clgame.macros.specdefn :refer [defn']]))

(spec/def ::type keyword?)
(spec/def ::schema (spec/and vector? (spec/* keyword?)))
(spec/def ::component (spec/keys :req [::type ::schema]))

(defn' mk-component [type :> ::type schema :> ::schema] -> ::component
  {::type type
   ::schema schema})

