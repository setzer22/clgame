(ns clgame.system.enemy-damage
  (:require [clgame.system :as s]
            [clgame.scene :as sc]
            [clgame.system.default :refer [register-default]]
            [clgame.message :as m]
            [clgame.macros.specdefn :refer [defn']]
            [clgame.component-ref :as cr]))

(defn enemy-damage [e-id [sprite animation] inbox]
  (let [maybe-new-state (::m/data (first (filter #(= :change-state (::m/type %)) inbox)))
        animation (if maybe-new-state (change-animation animation maybe-new-state)
                      animation)
        {:keys [frame-indices frame-index spritesheet] :as new-animation}
        (pass-time animation delta-time)]
    {:animation new-animation
     :sprite (merge sprite (get-tex-coords new-animation))}))

(register-default :EnemyDamage [(cr/mk-ref ::cr/self :controller)] enemy-damage)
