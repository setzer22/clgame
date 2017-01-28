(ns clgame.test-scene
  (:require [clgame.utils]
            [clgame.gl :as gl]
            [clgame.scene :as sc]
            [clgame.entity :as e]
            [clgame.vector :refer :all]
            [clgame.spritesheet :as spr]
            [clgame.system.debug-keys]
            [clgame.system.renderer]
            [clgame.system.movement]
            [clgame.system.animation]
            [clgame.system.controller]
            [clgame.system.ground-detector]
            [clgame.system.hand-motion]
            [clgame.system.collision]
            [clgame.system.collision-handler]
            [clgame.system.registration :refer [add-system]]
            [clgame.game-loop :as game-loop]
            [clgame.system :as s]))

(def pacman-spritesheet
  {::spr/tiles-width 7.0
   ::spr/tiles-height 7.0})

(def hand-spritesheet
  {::spr/tiles-width 1.0
   ::spr/tiles-height 2.0})

(defn add-platform [scene]
  (sc/insert-entity scene
    :transform {:position (v2 400 20)
                :rotation 0.0
                :scale (v2 1 1)}
    :sprite    {:w 800.0 :h 40.0
                :u 0 :v 0 :tw 1 :th 1
                :texture-id 4}
    :collider  {:static true
                :w 800.0 :h 40.0}))

(defn add-obstacle [scene x y w h]
  (sc/insert-entity scene
    :transform {:position (v2 x y)
                :rotation 0.0
                :scale (v2 1 1)}
    :sprite    {:w w :h h
                :u 0 :v 0 :tw 1 :th 1
                :texture-id 4}
    :collider  {:static true
                :w w :h h}))

(defn add-hand [scene x y w h]
  (sc/insert-entity scene
    :transform {:position (v2 x y)
                :rotation 0.0
                :scale (v2 1 1)}
    :sprite    (merge
                (spr/get-sprite-uv hand-spritesheet 1 0)
                {:w w :h h
                 :texture-id 5})
    :collider  {:static true
                :w w :h h}
    :hand-motion {:t 0.0
                  :amplitude 10.0
                  :speed 5.0}
    :hand-ai {}
    :movement {:velocity (v2 0 0)
               :acceleration (v2 0 0)
               :speed-clamp {:x [-150
                                 150]
                             :y [-200
                                 Float/POSITIVE_INFINITY]}}))


(defn add-player [scene]
  (sc/insert-entity scene
    :transform  {:position (v2 (rand-int 800) (rand-int 600))
                 :rotation 0.0
                 :scale (v2 1 1)}
    :sprite     (merge
                 (spr/get-sprite-uv pacman-spritesheet 0 0)
                 {:w 50.0 :h 50.0
                  :texture-id 3})
    :animation  {:animations {:walk-down  [[0 0] [0 1] [0 2]]
                              :walk-up    [[0 3] [0 4] [0 5]]
                              :walk-right [[1 0] [1 1] [1 2]]
                              :walk-left  [[1 3] [1 4] [1 5]]
                              :egg-break  [[4 0] [4 1] [4 2] [4 3] [4 4] [4 5]]
                              :eat        [[3 4] [3 5]]
                              :idle-left  [[1 3]]
                              :idle-right [[1 0]]
                              :walking    [[1 0] [1 1] [1 2]]
                              :jumping-left [[1 0] [0 0] [1 3] [0 3]]
                              :jumping-right [[1 3] [0 0] [1 0] [0 3]]}
                 :animation-speed-overrides {:jumping-left 2.0
                                             :jumping-right 1.5}
                 :current-animation :idle-right
                 :frame-index 0
                 :frame-time 0.1
                 :current-frame-time 0.0
                 :spritesheet pacman-spritesheet}
    :collider   {:static false
                :w 50.0 :h 50.0}
    :controller {:jump-speed 300.0
                 :walk-acceleration 1000.0
                 :gravity -600.0
                 :brake-multiplier 0.9
                 :jump-state :on-air
                 :jump-time 0.0
                 :max-jump-time 0.2}
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
      (add-hand 400 70 50.0 50.0)
      (add-obstacle 700.0 50.0 150.0 150.0)
      (add-system :Controller)
      (add-system :Movement)
      (add-system :HandMotion)
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


