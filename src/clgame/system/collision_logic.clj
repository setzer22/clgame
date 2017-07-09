(ns clgame.system.collision-logic
  (:require [clojure.spec :as spec]
            [clgame.macros.specdefn :refer [defn']]
            [clgame.component :as c]
            [clgame.scene :as sc]
            [clgame.system :as s]
            [clgame.macros.common :refer [maybe-conform]]
            [clgame.system.registration :refer [register-system]]
            [clojure.set :as set]
            [clojure.tools.namespace.find :as ns-find]
            [clgame.component-ref :as cr]))

;; The collision logic system does nothing by itself, and relies on multiple
;; subsystems that hook into it. For example, a player_damage subsystem might
;; hook into the collision_logic system specifying that it wants to add some
;; custom logic to the case in which an entity with collision tags #{:player}
;; and one with collision tags #{:enemy} collide.

;; In order to specify a custom collision handler, a single predicate must be
;; provided which takes a pair of entities and returns whether they should be
;; handled. For example:

(defn example-must-handle? [collider1 collider2]
  (and (contains? (:tags collider1) :player)
       (contains? (:tags collider2) :enemy)))

;; Also, when hooking into the system, the handle function and requested
;; compoenents must be provided. The function's signature must accept two
;; arguments, which correspond to the component data for the requested
;; components.

(defn example-handler [[player-controller] []]
  [{:transform (assoc player-controller :state :hit)}
   {}])

;; The behavior of the handler function is similar to the default system
;; executor one. A pair of maps is returned specifying the components that have
;; undergone changes.

;; To add logic into the collision system, we do it using the add-hook function.
;; This is an example call:

(comment
  "Add hook example"
  (add-hook
  example-must-handle?
  example-handler
  [transform] []
  25))

;; The 25 in the call is the hook priority. Hooks are sorted according to its
;; priority and are executed in that order (lesser numbers sooner).
;; Additionally, a hook takes an identifier name. The name is used in order to
;; avoid adding duplicate hooks into the system.

;; ----------------------------------------------------------------------------
;;                             HELPER  MACROS
;; ----------------------------------------------------------------------------

(defmacro match-tags
  "Returns a collider predicate that matches on the given left and
   right sets of tags"
  [tags1 tags2]
  `(let [tags1# (if (set? ~tags1) ~tags1 (set ~tags1))
         tags2# (if (set? ~tags2) ~tags2 (set ~tags2))]
     (fn [c1# c2#]
       (and (set/subset? tags1# (:tags c1#))
            (set/subset? tags2# (:tags c2#))))))


;; ----------------------------------------------------------------------------
;;                           SYSTEM IMPLEMENTATION
;; ----------------------------------------------------------------------------

(def hooks [])

(defrecord Hook [id must-handle? handler components1 components2 priority])

(defn' mk-hook [id must-handle? :> fn? handler :> fn? components1 :> (spec/* ::c/type),
                components2 :> (spec/* ::c/type) priority :> int]
  (->Hook id must-handle? handler components1 components2 priority))

(defn insert-or-replace-hook!
  [hooks {:keys [id] :as hook}]
  (let [old-hook (first (filter #(= (:id %) id) hooks))
        new-hooks (if old-hook
                    (replace {old-hook hook} hooks)
                    (conj hooks hook))]
    (sort-by :priority new-hooks)))

(defn add-hook [id must-handle? handler components1 components2 priority]
  (alter-var-root #'hooks insert-or-replace-hook!
                  (mk-hook id must-handle? handler components1 components2 priority)))

(defn simmetry-compare [comp left-val right-val left-ret right-ret]
  (cond (comp left-val right-val) [left-ret right-ret]
        (comp right-val left-val) [right-ret left-ret]
        :else             nil))

(defn get-components-data [scene e-id components]
  (mapv #(get-in scene [::sc/component-data % e-id]) components))

(defn apply-all-hooks [scene [e1 e2]]
  (reduce
   (fn [scene hook]
     (let [collider-1 (get-in scene [::sc/component-data :collider e1])
           collider-2 (get-in scene [::sc/component-data :collider e2])
           [e-left e-right :as cmp] (simmetry-compare (:must-handle? hook)
                                                      collider-1 collider-2 e1 e2)]
       (if cmp
         (let [[left-components right-components]
               (if (= e-left e1)
                 [(:components1 hook) (:components2 hook)]
                 [(:components2 hook) (:components1 hook)])
               data-left (get-components-data scene e-left left-components)
               data-right (get-components-data scene e-right right-components)
               [left-updates right-updates] ((:handler hook) data-left data-right)]
           (-> scene
               (sc/update-component-data e-left left-updates)
               (sc/update-component-data e-right right-updates)))
         scene)))
   scene hooks))

(defn collision-logic-system-executor [scene]
  (let [collisions (get-in scene [::sc/system-data :Collision])]
    (reduce
     apply-all-hooks
     scene collisions)))

(register-system :CollisionLogic
  (sc/add-system
   scene
   (s/mk-system :CollisionLogic
                [] ; @Deprecated
                collision-logic-system-executor)))

;; Automatically load all collision hooks when this ns is loaded.
;; This populates the hooks array with all declared collision hooks.
(let [all-hook-ns (filter #(.startsWith (name %) "clgame.collision-hooks")
                                (ns-find/find-namespaces-in-dir (java.io.File. ".")))]
  (doseq [hook-ns all-hook-ns]
    (require hook-ns)))

;; ----------------------------------------------------------------------------
;;                                     TEST
;; ----------------------------------------------------------------------------

(comment
  "Testing"

  (def test-scene
    (-> (sc/mk-scene)
        (sc/insert-entity "player"
          :collider {:w 10 :h 10
                     :static false
                     :tags #{:player}})
        (sc/insert-entity "enemy"
          :collider {:w 10 :h 10
                     :static false
                     :tags #{:enemy}})
        (assoc-in [::sc/system-data :Collision] [["player" "enemy"]])))

  (clojure.inspector/inspect-tree test-scene)

  (do (def hooks [])
      (add-hook
       (fn must-handle-test?
         [c1 c2]
         (and (contains? (:tags c1) :enemy)
              (contains? (:tags c2) :player)))
       (fn [c1 c2]
         (clojure.java.shell/sh "notify-send" (str c1))
         (clojure.java.shell/sh "notify-send" (str c2))
         [{} {}])
       [:collider] [:collider]
       25))

  (collision-logic-system-executor test-scene)

  )

