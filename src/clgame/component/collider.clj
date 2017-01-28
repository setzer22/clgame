(ns clgame.component.collider
  (:require [clojure.spec :as spec]
            [clgame.macros.defcomponent :refer [defcomponent]]))

(defcomponent :collider
  :static :> boolean?
  :w      :> float?
  :h      :> float)
