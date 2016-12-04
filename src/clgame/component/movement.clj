(ns clgame.component.movement
  (:require [clgame.vector :refer :all]
            [clojure.spec :as spec]))

(spec/def ::speed-factor float?)
(spec/def ::acceleration-factor float?)
(spec/def ::max-speed float?)
(spec/def ::velocity v2?)
(spec/def ::acceleration v2?)
(spec/def ::movement (spec/keys :req-un [::speed-factor ::acceleration-factor ::max-speed ::velocity ::acceleration]))
