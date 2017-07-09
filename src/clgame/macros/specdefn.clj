(ns clgame.macros.specdefn
  (:require [clojure.spec :as spec]
            [clojure.spec.test :as stest]
            [clgame.macros.common :refer :all]))

(spec/def ::namespaced-keyword
  (spec/and keyword? namespace))

(spec/def :mapdestruct/keys
  (spec/and vector? (spec/* (spec/or :lvalue ::lvalue
                                     :ns-keyword ::namespaced-keyword))))

(spec/def :mapdesturct/or
  (spec/map-of (spec/or :sym symbol?, :ns-key ::namespaced-keyword) any?))

(spec/def :mapdestruct/as
  symbol?)

(spec/def ::lvalue
  ;;TODO: Full destructuring syntax support.
  (spec/or
   :base symbol?
   :list-destruct (spec/and vector? (spec/cat :vals (spec/* ::lvalue)
                                              :as (spec/? (spec/cat :as-sep (is :as)
                                                                    :as symbol?))))
   :map-destruct (spec/and map? (spec/keys :opt-un [:mapdestruct/keys :mapdestruct/or :mapdestruct/as])
                           (fn destructure-map-syms [map]
                             (let [map (dissoc map :keys :or :as)]
                               (and (every? symbol? (keys map))
                                    (every? keyword? (vals map))))))))

;;(spec/valid? ::lvalue '[{a :1 b :2 :keys [x {a :a} z] :as mp} x y z & d :as lst])
;;(spec/valid? ::lvalue 'x)



(spec/def ::defn-args
  (spec/cat :args (spec/spec (spec/* (spec/cat :name (fn is-lvalue [x] (spec/valid? ::lvalue x))
                                               :spec (spec/? (spec/cat :separator (is :>) :spec spec-form?)))))
            :rest (spec/* any?)))

;;(spec/conform ::defn-args '([x :> int?, y :> #(= "hello" %)]))

(spec/def ::defn-docstring
  (spec/cat :docstring (spec/? string?)
            :rest (spec/* any?)))

(spec/def ::defn-return
  (spec/cat :return (spec/? (spec/cat :separator (is '->) :spec spec-form?))
            :rest (spec/* any?)))

;;(spec/explain-str (spec/and any? string?) 2)

(defn extract-or-generate-alias
  [lvalue]
  (cond
    (symbol? lvalue) {:lvalue lvalue
                      :alias  lvalue
                      :generated? false}
    (vector? lvalue) (let [[as-key alias] (take-last 2 lvalue)
                           aliased?       (and (= as-key :as) (symbol? alias))
                           alias          (if aliased? alias (gensym "destruct-list"))]
                       {:lvalue (if aliased? lvalue (conj lvalue :as alias))
                        :alias  alias
                        :generated? (not aliased?)})
    (map? lvalue)    (let [alias (:as lvalue)
                           generated? (not alias)
                           alias (or alias (gensym "destruct-map"))]
                       {:lvalue (assoc lvalue :as alias)
                        :alias alias
                        :generated? generated?})))

;;(extract-or-generate-alias '{:keys [x y z] x :x y :y :as wow})
;;(extract-or-generate-alias '{:keys [x y z] x :x y :y})
;;(extract-or-generate-alias '[x y :as wat])
;;(extract-or-generate-alias '[x y])
;;(extract-or-generate-alias 'x)

(defmacro defn' [name & rest]
  (let [{:keys [args rest]} (maybe-conform ::defn-args rest "Syntax error: Invalid argument form.")
        {:keys [docstring rest]} (maybe-conform ::defn-docstring rest "Syntax error: Invalid docstring.")
        {:keys [return rest]} (maybe-conform ::defn-return rest "Syntax error: Invalid return value.")
        body rest
        extracted-aliases (map #(-> % :name extract-or-generate-alias) args)
        ]
    `(do
       ;;Function definition
       (defn ~name
         ~(or docstring "")
         ~(mapv :lvalue extracted-aliases)
         (do ~@(for [[{{spec :spec} :spec} {:keys [lvalue alias generated?]}] (map vector args extracted-aliases)
                     :when spec]
                 `(maybe-conform (spec/and ~spec) ~alias (str "Error in argument " '~(if generated? lvalue alias) " of function " '~name":")))
             (let [ret# (do ~@body)
                   ret-spec?# ~(if (:spec return) true false)]
               (when ret-spec?# (maybe-conform (spec/and ~(:spec return)) ret# (str "Error in return value of function " '~name ":")))
               ret#)))

       ;;Function spec
       (spec/fdef ~name :args
                  ~(cons `spec/cat (interleave (map #(keyword (str "arg" %)) (range))
                                               (map :spec (map :spec args)))))
       )))


;; @Todo: TODO
;; * Return type instrumentation
;; - Support for rest args typing
;; - Support for defn metadata


(comment
  "Pseudo test suite"

  (defn' foo [x :> (spec/and int? even?), a-string :> string?] -> int? (str (* 2 x) " " a-string "s"))

  (foo 2 "foo") ; Return value error
  (foo 2 :foo) ;; a-string argument error

  (defn' backwards-compatible [x y]
    (* x y))

  (backwards-compatible 2 5) ;; Should work

  (defn' backwards-compatible-2 [x y & rest] -> string?
    (str x " " y " -> " rest))

  (backwards-compatible-2 1 2 3 4 5) ;; Should work


  (defn' str-first-two [[x y & rest] :> (spec/* int?)]
     (str x " " y))

  (str-first-two [1 2 3 4 5]) ;; Works as expected
  (str-first-two ["wat" "foo"]) ;; First argument fails


    )


