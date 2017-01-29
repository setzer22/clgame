(ns clgame.component.controller
  (:require [clojure.spec :as spec]
            [clgame.macros.defcomponent :refer [defcomponent]]))

(defcomponent :controller
  :jump-speed        :> float?
  :walk-acceleration :> float?
  :gravity           :> float?
  :brake-multiplier  :> float?
  :max-jump-time     :> float?
  :jump-state        :> keyword?
  :jump-time         :> float?
  :has-control?      :> boolean?
  :is-hit?           :> boolean?)
