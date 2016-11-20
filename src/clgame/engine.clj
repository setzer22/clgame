(ns clgame.engine
  (:require [clgame.gl :as gl])
  (:import [org.lwjgl Sys]
           [org.lwjgl.input Keyboard Mouse]))

(defn update-components [state entity component-ids component-new-data]
  (loop [[c-id & c-ids] component-ids
         [c-data & c-datas] component-new-data
         state state]
    (if c-id
      (recur c-ids
             c-datas
             (assoc-in state [:component-state c-id entity] c-data))
      state)))

(defn get-all-entities-with [state components]
  (into []
        (comp
         (filter
          (fn [[id e-components]]
            (every? (fn [comp]
                      (some #(= % comp) e-components))
                    components)))
         (map first))
        (:entities state)))

(defn default-execute-system [state system]
  ;;TODO: Check which entities are affected by the subsystem
  (let [system-fn (get-in state [:systems system :fn])
        component-ids (get-in state [:systems system :components])
        entities (get-all-entities-with state component-ids)
        components (mapv #(get-in state [:component-state %]) component-ids)]
    (loop [state state
           [e & es] entities]
      (let [next-components (apply system-fn (mapv e components))
            updated-state (update-components state e component-ids next-components)]
        (if (seq es)
          (recur updated-state
                 es)
          updated-state)))))

(defn execute-system [state system]
  (let [iterator-fn (get-in state [:systems system :iterator-fn])]
    (if iterator-fn
      (iterator-fn state)
      (default-execute-system state system))))

(defn render [{:keys [x y w h] :as transform}]
  (gl/glColor3f 0.0 0.0 0.0)
  (gl/center-rect x y w h)
  [transform])

(defn render-system [state]
  (let [entities (get-all-entities-with state [:render :transform])
        transforms (get-in state [:component-state :transform])]
    (gl/glClear 0.0 1.0 1.0 1.0)
    (doseq [e entities]
      (render (get transforms e)))
    (gl/update-display)
    state))

(defn move [transform _]
  (let [moved (cond
                 (Keyboard/isKeyDown Keyboard/KEY_W) (update transform :y inc)
                 (Keyboard/isKeyDown Keyboard/KEY_A) (update transform :x dec)
                 (Keyboard/isKeyDown Keyboard/KEY_S) (update transform :y #(+ % 2))
                 (Keyboard/isKeyDown Keyboard/KEY_D) (update transform :x inc)
                 :else transform)]
    [moved _]))

(def state {:entities {:floor [:transform :render]
                       :player [:transform :render :move]}
            :systems {:renderer {:components [:transform :render]
                                 :iterator-fn render-system}
                      :movement {:components [:transform :move]
                                 :fn move}}
            :scene [:movement :gravity :renderer]
            :component-state {:transform {:floor {:x 400 :y 10
                                                  :w 800 :h 20}
                                          :player {:x 400 :y 50
                                                   :w 30 :h 30}}}})

(defn add-test-entity [state]
  (let [id (keyword (str (rand-int 100000)))]
    (-> state
       (assoc-in [:entities id]
                 [:transform :render :move :gravity])
       (assoc-in [:component-state :transform id]
                 {:x (rand-int 800)
                  :y (rand-int 600)
                  :w (rand-int 50)
                  :h (rand-int 50)}))))

(def big-state
  (nth (iterate add-test-entity state) 100))

(defn game-loop []
  (gl/init-display 800 600)
  (gl/glOrtho 0 800 0 600 -1 1)
  (loop [state big-state]
    (gl/sync-to-display 60)
    (let [new-state
          (loop [[s-id & s-ids] (:scene state)
                 state state]
            (let [new-state (execute-system state s-id)]
              (if s-ids
                (recur s-ids new-state)
                new-state)))]
      (recur new-state))))

(defn start-game []
  (def game (future (game-loop))))

(defn stop-game []
  (future-cancel game)
  (gl/destroy-display))

(comment
  (stop-game)

  (start-game)

)


