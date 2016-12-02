(ns clgame.vector)

(defprotocol Vector2Protocol
  (+v [v1 v2] "Vector addition")
  (-v [v] [v1 v2] "Vector subtraction")
  (cross [v1 v2] "Cross product")
  (dot [v1 v2] "Dot product")
  (*v [v1 c] "Constant by vector")
  (vdiv [v1 c] "Constant by vector division") ;TODO: better name?
  (magnitude [v1] "Cross product")
  (normalize [v] "Unit length"))

(defrecord Vector2 [^float x ^float y]
  Vector2Protocol
  (+v  [{x1 :x y1 :y :as  v} {x2 :x y2 :y}] (Vector2. (+ x1 x2) (+ y1 y2)))
  (-v  [{x :x y :y}] (Vector2. (- x) (- y)))
  (-v  [{x1 :x y1 :y} {x2 :x y2 :y}] (Vector2. (- x1 x2) (- y1 y2)))
  (cross  [{x1 :x y1 :y} {x2 :x y2 :y}] (+ (* x1 x2) (* y1 y2)))
  (dot  [ v1  v2] (throw (Exception. "Not implemented!")))
  (*v  [{x :x y :y} c] (Vector2. (* c x) (* c y)))
  (vdiv [{x :x y :y} c] (Vector2. (/ x c) (/ y c)))
  (magnitude [{x :x y :y}] (Math/sqrt (+ (* x x) (* y y))))
  (normalize [{:keys [x y] :as v}] (if (== 0 x y)
                                     v
                                     (vdiv v (magnitude v)))))

(defn v2 [x y]
  (Vector2. (float x) (float y)))

