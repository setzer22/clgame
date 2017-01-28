(ns clgame.component.animation
  (:require [clgame.spritesheet :as spr]
            [clojure.spec :as spec]
            [clgame.macros.defcomponent :refer [defcomponent]]))

(defcomponent :animation
  :animations   :> (spec/map-of keyword? (spec/* ::frame-coords))

  :animation-speed-overrides :> (spec/map-of keyword? :conform/float)

  :current-animation  :> keyword?
  :frame-index        :> int?
  :frame-time         :> float?
  :current-frame-time :> float?
  :spritesheet        :> ::spr/spritesheet)


