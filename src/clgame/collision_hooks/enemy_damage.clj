(ns clgame.collision-hooks.enemy-damage
  (:require [clgame.system.collision-logic :refer [add-hook match-tags]]))

(add-hook "enemy-damage-hook"
 (match-tags #{:player} #{:enemy})
 (fn handler [[controller] []]
   (clojure.java.shell/sh "notify-send" "Sir, you have been hit!")
   [{:controller (assoc controller :is-hit true)}
    {}])
 [:controller] []
 100)

