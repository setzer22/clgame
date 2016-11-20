(ns clgame.dsl
  (:require [clojure.spec :as spec]))

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


(spec/def ::state (spec/and vector? (spec/* keyword?)))
(spec/def ::components
  (spec/cat
   :section-name #(= :components %)
   :data (spec/* (spec/cat :name symbol?
                     :data (spec/keys :req-un [::state])))))

(spec/def ::required-components
  (spec/and vector? (spec/* symbol?)))
(spec/def ::fn symbol?)
(spec/def ::iterator-fn symbol?)
(spec/def ::systems
  (spec/cat
   :section-name #(= % :systems)
   :data (spec/*
          (spec/cat :name symbol?
                    :data (spec/and
                           (spec/or :with-iterator (spec/keys :req-un [::required-components ::iterator-fn])
                                    :with-fn (spec/keys :req-un [::required-components ::fn]))
                           #(not (and (contains? % :iterator-fn)
                                      (contains? % :fn))))))))

(spec/def ::entities
  (spec/cat
   :section-name #(= % :entities)
   :data (spec/* (spec/cat :name symbol?
                     :data (spec/map-of symbol? map?)))))

(spec/def ::game
  (spec/cat
   :components (spec/spec ::components)
   :systems (spec/spec ::systems)
   :entities (spec/spec ::entities)))


(spec/explain ::game '(
  (:components
   transform {:state [:x :y :w :h]}
   render {:state []}
   move {:state []})
  (:systems
   movement {:required-components [transform move]
             :fn move}
   renderer {:required-components [transform render]
             :iterator-fn render-system})
  (:entities
   player {transform {:x 400 :y 50
                      :w 30 :h 30}
           render {}
           move {}}
   floor {transform {:x 400 :y 10
                     :w 800 :h 20}
          render {}})))
