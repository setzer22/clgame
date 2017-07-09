(ns clgame.spectrum-test
  (:require [clgame.macros.specdefn :refer [defn']]
            [spectrum.check :as check]))

(comment 
  (defn' f [x :> ::int, y :> ::int] :> ::int
    (+ x y))

  (spec/def ::a string?)

  (spec/def ::b int?)

  (spec/def ::foo
    (spec/keys :req [::a ::b]))

  (spec/conform ::foo {::a "foo", ::b 12})

  (spec/fdef mk-foo
             :args (spec/cat :a string?, :b int?)
             :ret ::foo)

  (defn mk-foo [a b]
    {::a a ::b b})

  (f :c 3))
