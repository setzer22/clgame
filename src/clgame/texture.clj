(ns clgame.texture
  (:import [java.awt.image BufferedImage]
           [java.nio ByteBuffer]
           [javax.imageio ImageIO])
  (:require [clojure.java.io :as io])
  (:gen-class))

(defmacro sub-byte [b x]
  `(unchecked-byte (-> ~x
    (bit-shift-left (* 8 ~b))
    (bit-shift-right 24))))

(defn bufferedimage->bytebuffer [^BufferedImage img]
  (binding [*unchecked-math* true] 
    (let [w (.getWidth img)
          h (.getHeight img)
          ^bytes arr (make-array Byte/TYPE (* 4 w h))]
      (loop [i 0]
          (let [img-i (mod i w)
                img-j (quot i w)
                value (.getRGB img img-i img-j)]
            (aset arr (* i 4)       (sub-byte 1 value))
            (aset arr (+ 1 (* i 4)) (sub-byte 2 value))
            (aset arr (+ 2 (* i 4)) (sub-byte 3 value))
            (aset arr (+ 3 (* i 4)) (sub-byte 0 value))
            (when (< (+ i 1) (* w h)) (recur (+ i 1)))))
      (cast ByteBuffer (-> (ByteBuffer/allocateDirect (count arr))
                           (.put arr)
                           (.flip))))))

(defn load-texture [file]
  (let [buff-img (ImageIO/read file)
        img-data (bufferedimage->bytebuffer buff-img)]
    {:data img-data
     :width (.getWidth buff-img)
     :height (.getHeight buff-img)}))
