(ns clgame.game-loop
  (:require [clgame.global-config :refer [config]]
            [clgame.system :as s]
            [clgame.scene :as sc]
            [clgame.message :as m]
            [clgame.gl :as gl]))

(defn run-game [scene]
  (gl/init-display (-> config :window :width) (-> config :window :height))
  (gl/glOrtho 0 (-> config :screen :width) 0 (-> config :screen :height) -1 1)
  (gl/load-texture "/home/josep/Repositories/clgame/resources/pacman.png")
  (gl/load-texture "/home/josep/Repositories/clgame/resources/spritesheet.png")
  (gl/load-texture "/home/josep/Repositories/clgame/resources/penguin_sprite.png")
  (gl/load-texture "/home/josep/Repositories/clgame/resources/ice-ground.png")
  (with-local-vars
    [running true]
    (loop [scene scene]
      (gl/sync-to-display 60)
      (gl/glClear 0.0 1.0 0.0 1.0)
      (let [new-scene
            (reduce (fn [scene {system-fn ::s/fn}]
                      (system-fn scene))
                    scene
                    (::sc/systems scene))
            __ (doseq [m (sc/get-inbox new-scene ::m/global-msg)]
                 (if (= (::m/type m) :quit)
                   (var-set running false)))
            new-scene (sc/clear-all-messages new-scene)]
        (gl/update-display)
        (if @running
          (recur new-scene)
          (gl/destroy-display))))))
