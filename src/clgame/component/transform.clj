(ns clgame.component.transform
  (:require [clojure.spec :as spec]
            [clgame.vector :refer :all]
            [clgame.macros.defcomponent :refer [defcomponent]])
  (:import [clgame.vector Vector2]))

(defcomponent :transform
   :position :> v2?
   :rotation :> float?
   :scale    :> v2?)




