(ns clgame.macros.common
  (:require [clojure.spec :as spec]))

(defn spec-form? [x]
  (or (symbol? x) ;; Function ref
      (list? x) ;; Lambda predicate
      (keyword? x)))

(defn is [x]
  #(= x %))

(defn maybe-conform [spec val msg]
  (let [conformed (spec/conform (spec/and any? spec) val)]
    (if (spec/invalid? conformed)
      (throw (Exception. (str msg "\n\n**SPEC INFO**:\n" (spec/explain-str spec val))))
      conformed)))

(defn pprintexpand
  [form]
  (binding [clojure.pprint/*print-suppress-namespaces* true]
    (clojure.pprint/pprint (macroexpand-1 form))))

(defn pprintexpand-all
  [form]
  (binding [clojure.pprint/*print-suppress-namespaces* true]
    (clojure.pprint/pprint (macroexpand (macroexpand-1 form)))))
