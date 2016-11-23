(ns clgame.system
  (:require [clgame.component :as c]))

(defn mk-system [name required-components system-fn]
  {::name name
   ::components required-components
   ::fn system-fn})


;;;;TODO: Default system function, parametrized by the iteration
;; signature: (defn default-system-fn [components, iteration-fn])
;; -> The default system fn performs the iterations implicitly and
;;    you only provide the single iteration fn.
;;;;TODO: Mechanism to register new systems:
;; We need something to register new subsystems and easily use them.
;; We will se what, though, with some more engine usage. For now,
;; systems will get declared here as defs For now,
