(ns clgame.test-scene
  (:require [clgame.gl :as gl]
            [clgame.scene :as sc]
            [clgame.entity :as e]
            [clgame.vector :refer :all]
            [clgame.spritesheet :as spr]
            [clgame.system.debug-keys]
            [clgame.system.renderer]
            [clgame.system.movement]
            [clgame.system.animation]
            [clgame.system.controller]
            [clgame.system.collision]
            [clgame.system.collision-handler]
            [clgame.system.registration :refer [add-system]]
            [clgame.game-loop :as game-loop]
            [clgame.system :as s]))

(def pacman-spritesheet
  {::spr/tiles-width 16.0
   ::spr/tiles-height 16.0})

(defn add-platform [scene]
  (sc/insert-entity scene
    :transform {:position (v2 400 20)
                :rotation 0.0
                :scale (v2 1 1)}
    :sprite    {:w 800.0 :h 40.0
                :u 0 :v 0 :tw 1 :th 1
                :texture-id 1}
    :collider  {:static true
                :w 800.0 :h 40.0}))

(defn add-player [scene]
  (sc/insert-entity scene
    :transform  {:position (v2 (rand-int 800) (rand-int 600))
                 :rotation 0.0
                 :scale (v2 1 1)}
    :sprite     (merge
                 (spr/get-sprite-uv pacman-spritesheet 0 0)
                 {:w 50.0 :h 50.0
                  :texture-id 2})
    :animation  {:animations {:walking [[5 3] [5 4] [5 5]]
                              :idle [[5 3]]}
                 :current-animation :walking
                 :frame-index 0
                 :frame-time 0.1
                 :current-frame-time 0.0
                 :spritesheet pacman-spritesheet}
    :collider   {:static false
                :w 50.0 :h 50.0}
    :controller {:jump-speed 300.0
                 :walk-acceleration 1000.0
                 :gravity -300.0
                 :brake-multiplier 0.9}
    :movement   {:velocity (v2 0 0)
                 :acceleration (v2 0 0)
                 :speed-clamp {:x [-150
                                   150]
                               :y [-200
                                   Float/POSITIVE_INFINITY]}}
    :ground-sensor {:grounded false}))


(def test-scene
  (-> (sc/mk-scene)
      (add-platform)
      (add-player)
      (add-system :Controller)
      (add-system :Movement)
      (add-system :Collision)
      (add-system :CollisionHandler)
      (add-system :GroundSensor)
      (add-system :Animation)
      (add-system :Renderer)
      (add-system :DebugKeys)
      ))

(try (game-loop/run-game test-scene)
     (catch Throwable e (do (gl/destroy-display)
                            (throw e))))


