(ns clgame.font
  (:require [clgame.gl :as gl :refer :all]
            [clgame.global-config :as config :refer [config]])
  (:import [org.lwjgl.opengl Display DisplayMode GL11 GL12]))

"Uncomment to display some awfully colored test text on the screen"
(comment
  (use 'clgame.gl)
  (destroy-display)
  (def font (java.awt.Font. "Sans" java.awt.Font/BOLD, 24))
  (do (init-display 800 600)
      (glClear 1.0 0.5 0.5 0.5)
      (gl (GL11/glEnable GL11/GL_TEXTURE_2D)
          (GL11/glEnable GL11/GL_BLEND)
          (GL11/glBlendFunc GL11/GL_SRC_ALPHA GL11/GL_ONE_MINUS_SRC_ALPHA))
      (glOrtho 0 800 600 0 -1 1)
      (update-display))
  (def ufont (gl (org.newdawn.slick.TrueTypeFont. font true)))
  (glClear 1.0 0.5 0.5 0.5)
  (gl (.drawString ufont 30 20 "Test Text" org.newdawn.slick.Color/green))
  (update-display))

(def font nil)

(defn init-font []
  (let [awt-font (java.awt.Font. "Sans" java.awt.Font/BOLD 24)
        slick-font (gl (org.newdawn.slick.TrueTypeFont. font true))]
    (alter-var-root font (constantly slick-font))))

(defn draw-string [x y string]
  (gl (GL11/glEnable GL11/GL_TEXTURE_2D)
      (GL11/glEnable GL11/GL_BLEND)
      (GL11/glBlendFunc GL11/GL_SRC_ALPHA GL11/GL_ONE_MINUS_SRC_ALPHA))
  (glOrtho 0 (-> config :screen :width) (-> config :screen :height) 0 -1 1) ; This flips the projection matrix.
  (.drawString font x y string org.newdawn.slick.Color/blue)
  (glOrtho 0 (-> config :screen :width) 0 (-> config :screen :height) -1 1))


