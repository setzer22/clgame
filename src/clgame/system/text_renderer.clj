(ns clgame.system.text-renderer
  (:require [clgame.gl :as gl]
            [clgame.font :as font]
            [clgame.system :as s]
            [clgame.scene :as sc]
            [clgame.system.default :refer [register-default]]))

(defn render [e-id [{:keys [position] :as transform}, text] inbox]
  (font/draw-string (:x position) (:y position) (:string text))
  (gl/glBindTexture texture-id)
  {})

(register-default :Renderer [:transform :text] render)
