(ns clgame.system.hand-anim
  (:require [clgame.macros.defsystem :refer [defsystem]]
            [clgame.message :as m]))

(defsystem :HandAnim [:animation :movement :hand-anim]
  (let [dir (if (neg? (-> movement :velocity :x))
              :left :right)]
    {::m/messages [(m/mk-message e-id e-id dir :change-state)]}))
