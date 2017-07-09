(ns clgame.macros.defsystem
  (:require [clgame.entity :as e]
            [clgame.macros.specdefn :refer [defn']]
            [clgame.component :as c]
            [clgame.message :as m]
            [clgame.system.default :refer [register-default]]
            [clojure.spec :as spec]
            [clgame.scene :as sc]
            [clgame.component-ref :as cr]))

(defmacro defsystem [system-name components & fn-body]
  (let [system-fn (gensym "system-fn")]
    `(do
       (let [~'delta-time (/ 1 60)]
         (defn' ~system-fn [~'e-id :> ::e/id
                            ~(mapv #(-> % name symbol) components)
                            ;; TODO: Allow typing destructure args
                            ;;[~@(mapcat #(vector
                                         ;;(-> % name symbol)
                                         ;;:> ::c/type
                                       ;;components
                            ~'inbox] :> map? ;; TODO: not just map...
          ~@fn-body))
       (register-default ~system-name ~(mapv cr/parse-ref-name components) ~system-fn)
       ~(str "Compiled system " system-name))))

