(ns clgame.macros.state-machine
  (:require [clojure.spec :as spec]
            [clgame.macros.common :refer :all]))

(spec/def ::state-machine-body
  (spec/+
   (spec/cat :old-state #(or (and (coll? %) (every? keyword? %)) (keyword? %))
             :sep #(or (symbol? %) (keyword? %))
             :condition #(or (is :else) (list? %) (symbol? %))
             :sep2 #(or (symbol? %) (keyword? %))
             :new-state keyword?)))


(spec/conform ::state-machine-body
              '[:ready   -- grounded? -> :ready
                :ready   -- jump-key? -> :jumping
                :jumping -- still-time? -> :jumping
                :jumping -- :else -> :on-air
                :on-air  -- grounded? -> :ready
                :on-air  -- :else -> :on-air])

(def conjv (fnil conj []))

(defmacro state-cond [lines]
  `(cond
     ~@(mapcat
        (fn [{:keys [condition new-state]}]
          `(~condition
            ~new-state))
        lines)))

(defmacro state-machine
  "State machine custom switch-like statement."
  {:style/indent 1}
  [state-var & body]
  (let [lines (maybe-conform ::state-machine-body body "Wrong state machine definition.")
        lines (mapcat
               (fn [line]
                 (if (-> line :old-state coll?)
                   (mapv #(assoc line :old-state %) (:old-state line))
                   [line]))
               lines)
        states-map (reduce
                    (fn [states-map {:keys [old-state] :as line}]
                      (update states-map old-state conjv line))
                    {} lines)
        states-map (reduce
                    (fn [states-map [k v]]
                      (if (seq (filter #(= :else (:condition %)) v))
                        states-map
                        (update states-map k conj {:old-state k, :condition :else, :new-state k})))
                    states-map
                    states-map )]
    `(case ~state-var
             ~@(mapcat
                (fn [old-state]
                  `(~old-state
                    (state-cond ~(get states-map old-state))))
                (keys states-map)))))


(comment
  "Example"
  (let [grounded? false
        jump-key? true
        still-time? true]

    (state-machine jump-state
      :ready   -- grounded?   -> :ready
      :ready   -- jump-key?   -> :jumping
      :jumping -- still-time? -> :jumping
      :jumping -- :else       -> :on-air
      :on-air  -- grounded?   -> :ready
      :on-air  -- :else       -> :on-air)

    )

  (pprintexpand
   '(state-machine my-curent-state
    :ready   -- grounded? -> :ready
    :ready   -- jump-key? -> :jumping
    :jumping -- still-time? -> :jumping
    :jumping -- :else -> :on-air
    :on-air  -- grounded? -> :ready
    :on-air  -- :else -> :on-air
    [:dafuq :wat :foo]-- :else -> :wat))

  )

