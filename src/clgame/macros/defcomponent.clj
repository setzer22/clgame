(ns clgame.macros.defcomponent
  (:require [clojure.spec :as spec :refer [cat * and or]]
            [clgame.macros.common :refer :all]))

(spec/def ::defcomponent
  (cat
   :component-name keyword?
   :fields (* (cat :name keyword? :separator (is :>) :spec spec-form?))))

(defmacro defcomponent [& body]
  (let [{:keys [component-name fields]} (maybe-conform ::defcomponent body "Incorrect syntax for defcomponent form:")]
    `(do ~@(for [{field-name :name, spec :spec} fields]
             `(spec/def ~(keyword (str *ns*) (name field-name))
                ~spec))
         (spec/def ~(keyword (str *ns*) (name component-name))
           (spec/keys :req-un ~(mapv (fn [field]
                                       (keyword (str *ns*) (name (:name field))))
                                     fields))))))

(comment
  ;; Call example
  (pprintexpand
   '(defcomponent :transform
      :position :> v2?
      :rotation :> v2?
      :scale    :> float?))

  ;; Gets compiled to
  (do
    (def :clgame.macros.defcomponent/position v2?)
    (def :clgame.macros.defcomponent/rotation v2?)
    (def :clgame.macros.defcomponent/scale float?)
    (def
      :clgame.macros.defcomponent/transform
      (keys
       :req-un
       [:clgame.macros.defcomponent/position
        :clgame.macros.defcomponent/rotation
        :clgame.macros.defcomponent/scale]))))
