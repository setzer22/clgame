(ns clgame.component.collider
  (:require [clojure.spec :as spec]))

(spec/def ::static boolean?)
(spec/def ::w float?)
(spec/def ::h float?)
(spec/def ::collider (spec/keys :req-un [::static ::w ::h]))
