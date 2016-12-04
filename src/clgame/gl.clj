(ns clgame.gl
  (:import [java.util.concurrent Executors]
           [org.lwjgl.opengl Display DisplayMode GL11 GL12])
  (:require [clojure.java.io :as io]
            [clgame.texture :as texture]
            [clgame.gl :as gl])
  (:gen-class))

(def opengl-executor (Executors/newSingleThreadExecutor))

(defmacro gl [& body]
  `(deref (.submit opengl-executor (fn [] ~@body))))

(comment (defmacro gl [& body]
   `(do ~@body)))

(defn load-texture [path]
  "Loads a texture and returns its texture-id"
  ;TODO: Memoize this
  (let [f (io/file path)
        {:keys [data width height]} (texture/load-texture f)
        tex-id (gl (GL11/glGenTextures))]
    ; Idiomatic clojure here
    (gl
      (GL11/glBindTexture GL11/GL_TEXTURE_2D tex-id)
      (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_WRAP_S, GL12/GL_CLAMP_TO_EDGE)
      (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_WRAP_T, GL12/GL_CLAMP_TO_EDGE)
      (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_MIN_FILTER, GL11/GL_NEAREST)
      (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_MAG_FILTER, GL11/GL_NEAREST)
      (GL11/glTexImage2D GL11/GL_TEXTURE_2D 0 GL11/GL_RGBA8 width height 0 GL11/GL_RGBA GL11/GL_UNSIGNED_BYTE data))
    tex-id))

(defn glClear [r g b a]
  (gl (GL11/glClearColor r g b a)
      (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT))))

(defn glColor3f [r g b]
  (gl (GL11/glColor3f r g b)))

(defn update-display []
  (gl (Display/update)))

(defn init-display [width height]
  (gl (Display/setDisplayMode (DisplayMode. width height))
      (Display/create)))

(defn sync-to-display [fps]
  (Display/sync fps))

(defn destroy-display []
  (gl (Display/destroy)))

(defn quad [x1 y1 u1 v1 x2 y2 u2 v2 x3 y3 u3 v3 x4 y4 u4 v4]
  (gl (GL11/glBegin GL11/GL_QUADS)
        (GL11/glTexCoord2f u1 v1) (GL11/glVertex3f x1 y1 0)
        (GL11/glTexCoord2f u2 v2) (GL11/glVertex3f x2 y2 0)
        (GL11/glTexCoord2f u3 v3) (GL11/glVertex3f x3 y3 0)
        (GL11/glTexCoord2f u4 v4) (GL11/glVertex3f x4 y4 0)
      (GL11/glEnd)))

(defn rect [^double x ^double y ^double w ^double h]
  (quad x       y       0.0 0.0
        (+ x w) y       1.0 0.0
        (+ x w) (+ y h) 1.0 1.0
        x       (+ y h) 0.0 1.0))

(defn center-rect [^double x ^double y ^double w ^double h]
  (let [w2 (/ w 2)
        h2 (/ h 2)]
    (quad (- x w2) (- y h2) 0.0 0.0
          (+ x w2) (- y h2) 1.0 0.0
          (+ x w2) (+ y h2) 1.0 1.0
          (- x w2) (+ y h2) 0.0 1.0)))

(defn center-rect-uv [{:keys [x y w h u v tw th]}]
  (let [w2 (/ w 2)
        h2 (/ h 2)]
    (quad (- x w2) (- y h2) u        v
          (+ x w2) (- y h2) (+ u tw) v
          (+ x w2) (+ y h2) (+ u tw) (+ v th)
          (- x w2) (+ y h2) u        (+ v th))))

(defn glBindTexture [tid]
  (comment (gl (GL11/glBindTexture GL11/GL_TEXTURE_2D tid))))

(defn glOrtho [left right bottom top znear zfar]
  (gl (GL11/glMatrixMode GL11/GL_PROJECTION)
      (GL11/glLoadIdentity)
      (GL11/glOrtho left right bottom top znear zfar)
      (GL11/glMatrixMode GL11/GL_MODELVIEW)))

(defn enable-texture []
  (gl (GL11/glEnable GL11/GL_TEXTURE_2D)))

(defmacro cxu [& body]
  `(do (glClear 0.0 0.5 1.0 1.0)
       ~@body
       (update-display)))
