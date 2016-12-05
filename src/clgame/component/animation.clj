(ns clgame.component.animation
  (:require [clgame.spritesheet :as spr]
            [clojure.spec :as spec]))

(spec/def ::frame-coords (spec/and vector? (spec/tuple int? int?)))
(spec/def ::animations (spec/map-of keyword? (spec/* ::frame-coords)))
(spec/def ::current-animation keyword?)
(spec/def ::frame-index int?)
(spec/def ::frame-time float?)
(spec/def ::current-frame-time float?)
(spec/def ::spritesheet ::spr/spritesheet)
(spec/def ::animation
  (spec/keys
   :req-un
   [::frame-index ::frame-time
    ::current-frame-time ::spritesheet
    ::animations ::current-animation]))

