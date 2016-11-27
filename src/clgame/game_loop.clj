(ns clgame.game-loop
  (:require [clgame.system :as s]
            [clgame.scene :as sc]
            [clgame.message :as m]
            [clgame.gl :as gl]))

(defn run-game [scene]
  (gl/init-display 800 600)
  (gl/glOrtho 0 800 0 600 -1 1)
  (gl/load-texture "/home/josep/Repositories/clgame/resources/pacman.png")
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
            new-scene (sc/clear-inbox new-scene ::m/global-msg)]
        (gl/update-display)
        (if @running
          (recur new-scene)
          (gl/destroy-display))))))
