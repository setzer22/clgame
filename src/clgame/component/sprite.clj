(ns clgame.component.sprite
  (:require [clojure.spec :as spec]))

(spec/def ::tw :conform/float)
(spec/def ::th :conform/float)
(spec/def ::u :conform/float)
(spec/def ::v :conform/float)
(spec/def ::w :conform/float)
(spec/def ::h :conform/float)
(spec/def ::sprite (spec/keys :req-un [::tw ::th ::u ::v ::w ::h]))
