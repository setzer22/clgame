(ns clgame.core
  (:require [clgame.gl :as gl]
            [com.rpl.specter :as s])
  (:import [org.lwjgl Sys]
           [org.lwjgl.input Keyboard Mouse])
  (:gen-class))

(set! *warn-on-reflection* true)
(set! *unchecked-math* true)
(alter-var-root #'*out* (constantly *out*))

(defrecord Entity [x y size])

(defn entity [x y size]
  (Entity. x y size))

(defn random-entity-coords []
  [(rand-int 800) (rand-int 600) (rand-int 70)])

(defn make-initial-state [] 
  {:timestamp 0
   :entities (map #(apply entity %) (take 10 (repeatedly random-entity-coords)))
   :running true})

;TODO: Too slow
(let [keyboard-keys (map #(.getName %) (filter #(re-matches #"KEY_.*" (.getName %)) (.getDeclaredFields Keyboard )))]
  (defn input [] 
    (let [key-symbol (fn [k] (symbol (str "org.lwjgl.input.Keyboard/" k)))
          key-name   (fn [k] (keyword (clojure.string/lower-case (clojure.string/replace k #"KEY_" ""))))]
      (try 
        (into {} (map #(vector (key-name %) (Keyboard/isKeyDown (eval (key-symbol %)))) keyboard-keys))
        (catch Exception e {})))))

(defprotocol Vector 
  (v+ [a b])
  (v- [a b])
  (v* [c v])
  (z? [v])
  ;(cross [a b])
  (lerp [a b t])
  (dot [a b]))

(defrecord Vector2 [x y]
  Vector
  (v+ [{vx :x vy :y} {wx :x wy :y}] (Vector2. (+ vx wx) (+ vy wy)))
  (v- [{vx :x vy :y} {wx :x wy :y}] (Vector2. (- vx wx) (- vy wy)))
  (v* [{vx :x vy :y} c] (Vector2. (* c vx) (* c vy)))
  (z? [{vx :x vy :y}] (= vx vy 0))
  ;(cross [{:x vx :y vy} {:x wx :y wy}] (Vector2. (- vx wx) (- vy wy)))
  (lerp [v1 v2 alpha] (v+ (v* v1 alpha) (v* v2 alpha)))
  (dot [{vx :x vy :y} {wx :x wy :y}] (+ (* vx vy) (* wx wy))))

(defrecord MoveEquation [t0 r0 v])

(defn vec2 [x y] (Vector2. x y))
(defn v [x y] (Vector2. x y));TODO Remove this, just for testing
(defn move-eq [t0 r0 v] (MoveEquation. t0 r0 v))

(defn moving? [entity]
  (contains? entity :move-eq))

(defn not-moving? [entity] 
  (not (moving? entity)))

(defn elapsed [timestamp entity]
  (when (moving? entity)
    (- timestamp (get-in entity [:move-eq :t0]))))

(defn pos [{:keys [r0 t0 v]} t]
  (let [delta (- t t0)]
    (v+ r0 (v* v delta))))

(defn move [entity t]
  (if (moving? entity)
    (dissoc 
      (let [{:keys [x y]} (pos (:move-eq entity) t)]
        (assoc entity :x x, :y y)) 
      :move-eq)
    entity))

(defn input-dir [input]
  (vec2 
    (cond (:left input) -1
          (:right input) 1
          :else 0)
    (cond (:up input) 1
          (:down input) -1
          :else 0)))

(def state {:timestamp 0, 
            :entities [],
            :running true})

(defn pure-tick [state timestamp input]
  (s/transform [:entities s/FIRST #(or (not-moving? %) (>= (elapsed timestamp %) 1000))] 
               (fn [{:keys [x y] :as hero}]
                 (let [dir (input-dir input)]
                   (if (not (z? dir))
                     (let [{:keys [x y] :as moved-hero} (move hero timestamp)] 
                       (assoc moved-hero :move-eq (move-eq timestamp (vec2 x y) (v* dir 0.06))))
                     (dissoc (move hero timestamp) :move-eq hero))))
               state))

(defn tick [state timestamp]
  (let [input (input)] 
    (if (:escape input) 
      (assoc state :running false)
      (pure-tick state timestamp input))))

;TODO Timer resolution
(defn game-loop []
  (time (loop [state (make-initial-state)
         delta 0
         timestamp (Sys/getTime)]
    (def state state)
    (gl/sync-to-display 30)
    (when (:running state)
      (let [new-timestamp (Sys/getTime)] 
        (recur (assoc (tick state timestamp) :timestamp timestamp)
               (- new-timestamp timestamp)
               new-timestamp))))))

(defn render [state]
  (gl/cxu 
      (gl/glColor3f 0.5 1.0 0.5)
      (doseq [e (:entities state)] 
        (if (moving? e)
          (do
            (let [{size :size, eq :move-eq} e 
                  {:keys [x y]} (pos eq (Sys/getTime))] 
              (gl/center-rect x y size size)))
          (gl/center-rect (:x e) (:y e) (:size e) (:size e))))))

(defn render-loop []
  (gl/init-display 800 600)
  (gl/glOrtho 0 800 0 600 -1 1)
  (loop [] 
    (if (:running state)
      (do (try (render state)) ; Render is already locked at 60FPS
          (recur))
      (gl/destroy-display))))

(defn main- []
  (future (game-loop))
  (future (render-loop)))

(defn reload []
  (gl/destroy-display)
  (main-))
