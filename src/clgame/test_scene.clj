(ns clgame.test-scene
  (:require [clgame.gl :as gl]
            [clgame.scene :as sc]
            [clgame.entity :as e]
            [clgame.system.registration :refer [add-system]]
            [clgame.game-loop :as game-loop]))

(defn add-random-entity [scene]
  (sc/add-entity scene
                 (e/mk-entity [:transform :render])
                 [{:x (rand-int 800)
                   :y (rand-int 600)
                   :w 50
                   :h 50}
                  {:texture-id 1}]))

(def test-scene
  (-> (sc/mk-scene)
      (add-random-entity)
      (add-random-entity)
      (add-random-entity)
      (add-random-entity)
      (add-random-entity)
      (add-random-entity)
      (add-system :Renderer)
      (add-system :Movement)
      (add-system :DebugKeys)))

(try (game-loop/run-game test-scene)
     (catch Throwable e (do (gl/destroy-display)
                            (throw e))))




