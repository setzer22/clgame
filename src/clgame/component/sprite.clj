(ns clgame.component.sprite
  (:require [clojure.spec :as spec]))

(spec/def ::tx float?)
(spec/def ::ty float?)
(spec/def ::u float?)
(spec/def ::v float?)
(spec/def ::w float?)
(spec/def ::h float?)
(spec/def ::sprite (spec/keys :req-un [::tx ::ty ::u ::v ::w ::h]))
