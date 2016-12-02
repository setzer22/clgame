(ns clgame.system
  (:require [clgame.component :as c]))

(defn mk-system [name required-components system-fn]
  {::name name
   ::components required-components
   ::fn system-fn})



