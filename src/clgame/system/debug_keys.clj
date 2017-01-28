(ns clgame.system.debug-keys
  (:require [clgame.system :as s]
            [clgame.scene :as sc]
            [clgame.entity :as e]
            [clgame.message :as m]
            [clgame.system.registration :refer [register-system]]
            [clgame.system.default :as ex :refer [default-system-executor]])
  (:import [org.lwjgl.input Keyboard Mouse]))

(defn debug-keys [e-id [_] inbox]
  (if (Keyboard/isKeyDown Keyboard/KEY_ESCAPE)
    {::m/messages [(m/mk-message e-id ::m/global-msg :quit :quit)]}
    {}))

(register-system :DebugKeys
  (-> scene
      (sc/add-entity (e/mk-entity "debug-keys-entity" [:debug-keys])
                     [nil])
      (sc/add-system
       (s/mk-system :DebugKeys
                    [:debug-keys]
                    (default-system-executor
                     [:debug-keys]
                     debug-keys)))))
