(ns clgame.collision-hooks.enemy-damage
  (:require [clgame.system.collision-logic :refer [add-hook match-tags]]
            [clgame.vector :refer :all]))

(add-hook "enemy-damage-hook"
 (match-tags #{:player} #{:enemy})
 (fn handler [[controller movement] []]
   ;;(clojure.java.shell/sh "notify-send" "Sir, you have been hit!")
   (let [already-hit? (:is-hit? controller)]
     [{:controller
       (assoc controller :is-hit? true)
       :movement
       (if (not already-hit?)
         (assoc movement :acceleration (v2 1000000.0 1000000.0))
         movement)}
     {}]))
 [:controller :movement] []
 100)

