(ns clgame.test-scene
  (:require [clgame.gl :as gl]
            [clgame.scene :as sc]
            [clgame.entity :as e]
            [clgame.component :as c]
            [clgame.system :as s]
            [clgame.system-functions :as sys])
  (:import [org.lwjgl Sys]
           [org.lwjgl.input Keyboard Mouse]))

(defn render [[{:keys [x y w h] :as transform}, _]]
  (gl/glColor3f 0.0 0.0 0.0)
  (gl/center-rect x y w h)
  [transform, _])

(defn move [[transform]]
  (let [moved (cond
                (Keyboard/isKeyDown Keyboard/KEY_W) (update transform :y inc)
                (Keyboard/isKeyDown Keyboard/KEY_A) (update transform :x dec)
                (Keyboard/isKeyDown Keyboard/KEY_S) (update transform :y #(+ % 2))
                (Keyboard/isKeyDown Keyboard/KEY_D) (update transform :x inc)
                :else transform)]
    [moved]))

(defn add-random-entity [scene]
  (sc/add-entity scene
                 (e/mk-entity [:transform :render])
                 [{:x (rand-int 800)
                   :y (rand-int 600)
                   :w 50
                   :h 50}
                  {}]))

(def render-executor
  (sys/default-system-executor
   [:transform :render]
   render))

(def test-scene
  (-> (sc/mk-scene)
      (add-random-entity)
      (add-random-entity)
      (add-random-entity)
      (add-random-entity)
      (add-random-entity)
      (add-random-entity)
      (sc/add-system
       (s/mk-system ::Renderer
                    [:transform :render]
                    render-executor))
      (sc/add-system
       (s/mk-system ::Movement
                    [:transform]
                    (sys/default-system-executor [:transform] move)))))

(defn run-game [scene]
  (gl/init-display 800 600)
  (gl/glOrtho 0 800 0 600 -1 1)
  (loop [scene scene]
    (gl/sync-to-display 60)
    (gl/glClear 0.0 1.0 0.0 1.0)
    (let [new-scene
          (reduce (fn [scene {system-fn ::s/fn}]
                    (system-fn scene))
                  scene
                  (::sc/systems scene))]
      (gl/update-display)
      (recur new-scene))))

(try (run-game test-scene)
     (catch Throwable e (do (gl/destroy-display)
                            (if (and (.getCause e)
                                     (not (instance? (.getCause e) java.lang.ThreadDeath)))
                              (throw e)))))



