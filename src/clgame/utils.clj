(ns clgame.utils
  (:require [clojure.spec :as spec]
            [clojure.walk :as walk]))

(defn x-float? [x]
  (try (float x)
    (catch java.lang.ClassCastException e :clojure.spec/invalid)))
(defn x-int? [x]
  (try (int x)
       (catch java.lang.ClassCastException e :clojure.spec/invalid)))

(spec/def :conform/float (spec/conformer x-float?))
(spec/def :conform/int (spec/conformer x-int?))

