(ns clgame.message
  (:require [clgame.entity :as e]
            [clojure.spec :as spec]))

(spec/def ::id (spec/or :to-entity ::e/id
                        :to-engine #(= % ::global-msg)))
(spec/def ::from ::id)
(spec/def ::to ::id)
(spec/def ::data any?)
(spec/def ::type keyword?)
(spec/def ::message (spec/keys :req [::from ::to ::data ::type]))

(spec/fdef mk-message
           :args (spec/cat :from ::from :to ::to :data ::data :type ::type)
           :ret ::message)
(defn mk-message [from to data type]
  {::from from ::to to ::data data ::type type})
