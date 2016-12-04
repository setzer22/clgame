(ns clgame.system.renderer
  (:require [clgame.gl :as gl]
            [clgame.system :as s]
            [clgame.scene :as sc]
            [clgame.system.default :refer [register-default]]))

(defn render [e-id [transform, {:keys [texture-id] :as sprite}] inbox]
  (gl/enable-texture)
  (gl/glBindTexture texture-id)
  (gl/center-rect-uv (merge (:position transform) sprite))
  {})

(register-default :Renderer [:transform :sprite] render)

