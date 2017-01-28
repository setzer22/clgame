(ns clgame.component.sprite
  (:require [clojure.spec :as spec]
            [clgame.macros.defcomponent :refer [defcomponent]]))

(defcomponent :sprite
  :tw :> :conform/float
  :th :> :conform/float
  :u  :> :conform/float
  :v  :> :conform/float
  :w  :> :conform/float
  :h  :> :conform/float)
