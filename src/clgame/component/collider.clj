(ns clgame.component.collider
  (:require [clojure.spec :as spec]
            [clgame.macros.defcomponent :refer [defcomponent]]))

(defcomponent :collider
  :static :> boolean?
  :tags   :> (spec/and set? (spec/* keyword?))
  :w      :> float?
  :h      :> float)
