(ns clgame.system
  (:require [clojure.spec :as spec]
            [clgame.component :as c]
            [clgame.macros.specdefn :refer [defn']]))


(spec/def ::name any?)
(spec/def ::components (spec/* ::c/type))
(spec/def ::fn fn?)
(spec/def ::system
  (spec/keys :req [::name ::components ::fn]))

(defn' mk-system [name :> ::name required-components :> ::components system-fn :> ::fn] -> ::system
  {::name name
   ::components required-components
   ::fn system-fn})



