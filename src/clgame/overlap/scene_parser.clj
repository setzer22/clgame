(ns clgame.overlap.scene-parser
  (:require [clojure.data.json :as json]
            [clgame.scene :as sc]
            [clojure.java.io :as io]))


(def test-scene-path "/home/josep/Repositories/clgame/overlap2d/clgame/scenes/MainScene.dt")
(def images-path "/home/josep/Repositories/clgame/overlap2d/clgame/assets/orig/images")

(defn json->scene [path]
  (let [scene (sc/mk-scene)
        jscene (clojure.walk/keywordize-keys (json/read-str (slurp path)))
        images (-> jscene :composite :sImages)
        image-folder (io/file images-path)]
    (reduce
     (fn [scene image]
       (io/file image-folder (str (:imageName image) ".png")); TODO: Hardcoded format
       (-> scene
           (sc/insert-entity scene
             :transform (merge {:x 0 :y 0} (select-keys image [:x :y]))
             :sprite {:w :h})
           )
       )
     scene images)))


(json->scene test-scene-path)
