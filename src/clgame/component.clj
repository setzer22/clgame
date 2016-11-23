(ns clgame.component
  (:require [clojure.spec :as spec]
            [clojure.spec.test :as stest]))

(spec/def ::type keyword?)
(spec/def ::schema (spec/and vector? (spec/* keyword?)))
(spec/def ::component (spec/keys :req [::type ::schema]))

(spec/fdef mk-component
           :args (spec/cat :type ::type :schema ::schema)
           :ret ::component)
(defn mk-component [type schema]
  {::type type
   ::schema schema})

(stest/instrument (stest/instrumentable-syms))
