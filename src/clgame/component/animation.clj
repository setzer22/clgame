(ns clgame.component.animation
  (:require [clgame.spritesheet :as spr]
            [clojure.spec :as spec]))

(spec/def ::frame-coords (spec/and vector? (spec/tuple int? int?)))
(spec/def ::frame-indices (spec/and vector? (spec/* ::frame-coords)))
(spec/def ::frame-index int?)
(spec/def ::frame-time float?)
(spec/def ::current-frame-time float?)
(spec/def ::spritesheet ::spr/spritesheet)
(spec/def ::animation (spec/keys :req-un [::frame-indices ::frame-index ::frame-time ::current-frame-time ::spritesheet]))
