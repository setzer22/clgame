(ns clgame.macros
  (:require [clojure.spec :as spec]
            [clojure.spec.test :as stest]))

(defn is [x]
  #(= x %))

(defn spec-form? [x]
  (or (symbol? x) ;; Function ref
      (list? x) ;; Lambda predicate
      (keyword? x)
      ))

(spec/def ::defn-args
  (spec/cat :args (spec/spec (spec/* (spec/cat :name symbol? :separator (is :>) :spec spec-form?)))
            :rest (spec/* any?)))

(spec/conform ::defn-args '[x :> int?, y :> #(= "hello" %)])

(spec/def ::defn-docstring
  (spec/cat :docstring (spec/? string?)
            :rest (spec/* any?)))

(spec/def ::defn-return
  (spec/cat :return (spec/? (spec/cat :separator (is '->) :return-type spec-form?))
            :rest (spec/* any?)))

(spec/explain-str (spec/and any? string?) 2)

(defn maybe-conform [spec val msg]
  ;; FIXME @Hack: When clojure.spec fixes the bug about explain-str failing
  ;;   to explain function predicates, remove the (spec/and any? ...) wrap.
  (let [conformed (spec/conform (spec/and any? spec) val)]
    (if (spec/invalid? conformed)
      (throw (Exception. (str msg "\n\n**SPEC INFO**:\n" (spec/explain-str spec val))))
      conformed)))

;(maybe-conform ::defn-args '([x :> int?, y :> #(= "hello" %)]) "Error in the argument form.")
;(maybe-conform ::defn-docstring '("wat" (* 2 x)) "Error")
;(maybe-conform ::defn-return '((* x 2)) "Error in the argument form.")

(defmacro defn' [name & rest]
  (let [{:keys [args rest]} (maybe-conform ::defn-args rest "Syntax error: Invalid argument form.")
        {:keys [docstring rest]} (maybe-conform ::defn-docstring rest "Syntax error: Invalid docstring.")
        {:keys [return rest]} (maybe-conform ::defn-return rest "Syntax error: Invalid return value.")
        argument-names (map #(str (:name %)) args)
        body rest]
    `(do
       ;;Function definition
       (defn ~name
         ~(or docstring "")
         ~(mapv :name args)
         (do (doseq [[{name# :name spec# :spec} name-str#] ~(mapv vector args argument-names)]
               (maybe-conform (spec/and spec#) name# (str "Error in argument " name-str#)))
             (let [ret# (do ~@body)]
               ret#)))

       ;;Function spec
       (spec/fdef ~name :args
                  ~(cons `spec/cat (interleave (map #(keyword (str "arg" %)) (range))
                                                 (map :spec args))))
       )))



(comment
  (spec/def ::wat (spec/cat :1 int?
                            :2 (spec/* string?)
                            :3 (spec/* keyword?)))
  (defn' foo [x :> (spec/and int? even?), a-string :> ::wat] -> int? (str (* 2 x) " " a-string "s")) 
    )

