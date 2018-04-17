(ns lox-in-clojure.scope
  (:require [akar.syntax :refer [match clauses]]
            [akar.patterns :refer [!constant]]
            [lox-in-clojure.internal.utilities :as lu])
  (:gen-class))

(defn fresh-global-scope []
  {:values    (lu/mutable-map {:+            +
                               :-            -
                               :*            *
                               :/            /
                               (keyword ",") str
                               :<            <
                               :<=           <=
                               :>            >
                               :>=           >=
                               :=            =
                               :not=         not=
                               :print        println})
   :enclosing nil})

(defn enclosing-global-scope [scope]
  (if-let [parent (:enclosing scope)]
    (enclosing-global-scope parent)
    scope))

(defn set-in-global-scope [symbol value scope]
  (.put (:values (enclosing-global-scope scope)) symbol value))

(defn set-in-scope [symbol value scope]
  (.put (:values scope) symbol value))

(defn resolve-symbol [symbol scope]
  (if-let [resolved (.get (:values scope) symbol)]
    [resolved scope]
    (if-let [parent (:enclosing scope)]
      (resolve-symbol symbol parent)
      (lu/fail-with (str "Could not resolve symbol! " symbol)))))

(defn with-new-scope [enclosing block]
  (block {:values (lu/mutable-map {})
          :enclosing enclosing}))
