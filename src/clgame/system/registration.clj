(ns clgame.system.registration
  (:require [clgame.scene :as sc]
            [clgame.entity :as e]
            [clojure.spec :as spec]))

(defn add-system [ ] nil)

(defmulti add-system
  "Returns a new scene with the component of given type registered."
  (fn [scene type] type))

(defmacro register-system
  "Registers system-name as a subsystem. Body is the body of a function
   which takes the only parameter scene and returns a new scene with the
   system added to it."
  {:style/indent 1}
  [system-name & body]
  `(defmethod clgame.system.registration/add-system ~system-name
     [~'scene, ~'this-system-name]
     ~@body))

(register-system :default
  (throw (Exception. (str "Subsystem " this-system-name " is not registered."))))
