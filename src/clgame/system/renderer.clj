(ns clgame.system.renderer
  (:require [clgame.gl :as gl]
            [clgame.system.registration :refer [register-system]]
            [clgame.system :as s]
            [clgame.scene :as sc]
            [clgame.system.default :refer [default-system-executor]]))

(defn render [e-id [{:keys [x y] :as transform}, {:keys [w h texture-id] :as sprite}] inbox]
  (gl/enable-texture)
  (gl/glBindTexture texture-id)
  (gl/center-rect x y w h)
  {})

(register-system :Renderer
  (sc/add-system
   scene
   (s/mk-system  :Renderer
                [:transform :sprite]
                (default-system-executor [:transform :sprite] render))))

