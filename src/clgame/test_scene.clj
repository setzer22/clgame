(ns clgame.test-scene
  (:require [clgame.gl :as gl]
            [clgame.scene :as sc]
            [clgame.entity :as e]
            [clgame.vector :refer :all]
            [clgame.system.debug-keys]
            [clgame.system.renderer]
            [clgame.system.movement]
            [clgame.system.controller]
            [clgame.system.collision]
            [clgame.system.collision-handler]
            [clgame.system.registration :refer [add-system]]
            [clgame.game-loop :as game-loop]
            [clgame.system :as s]))

(defn add-random-entity [scene]
  (sc/add-entity scene
                 (e/mk-entity [:transform :sprite :collider])
                 [{:x (rand-int 800)
                   :y (rand-int 600)}
                  {:w 50 :h 50
                   :texture-id 1}
                  {:static true
                   :w 50 :h 50}]))

(defn add-moving-entity [scene]
  (sc/add-entity scene
                 (e/mk-entity [:transform :sprite :collider :controller :movement])
                 [{:x (rand-int 800)
                   :y (rand-int 600)}
                  {:w 50 :h 50
                   :texture-id 1}
                  {:static false
                   :w 50 :h 50}
                  {}
                  {:speed-factor 50
                   :acceleration-factor 1000
                   :max-speed 100
                   :velocity (v2 0 0)
                   :acceleration (v2 0 0)}
                  ]))

(def test-scene
  (-> (sc/mk-scene)
      (add-moving-entity)
      (add-random-entity)
      (add-random-entity)
      (add-random-entity)
      (add-random-entity)
      (add-random-entity)
      (add-random-entity)
      (add-system :Movement)
      (add-system :Controller)
      (add-system :Collision)
      (add-system :CollisionHandler)
      (add-system :Renderer)
      (add-system :DebugKeys)))


(try (game-loop/run-game test-scene)
     (catch Throwable e (do (gl/destroy-display)
                            (throw e))))


