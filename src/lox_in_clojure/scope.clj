(ns lox-in-clojure.scope
  (:require [akar.syntax :refer [match clauses]]
            [akar.patterns :refer [!constant]]
            [lox-in-clojure.internal.utilities :as lu]
            [lox-in-clojure.functions :as lf])
  (:gen-class))

(defn fresh-global-scope []
  {:values    (lu/mutable-map {})
   :enclosing nil})

(defn enclosing-global-scope [scope]
  (if-let [parent (:enclosing scope)]
    (enclosing-global-scope parent)
    scope))

(defn set-in-global-scope [symbol value scope]
  (.put (:values (enclosing-global-scope scope)) symbol value))

(defn set-in-scope [symbol value scope]
  (.put (:values scope) symbol value))

(defn resolve-var-or-local [symbol scope]
  (if-let [resolved (.get (:values scope) symbol)]
    [resolved scope]
    (if-let [parent (:enclosing scope)]
      (resolve-var-or-local symbol parent)
      ::failed-to-resolve)))

(defn resolve-native-function [symbol]
  (if-let [fun (symbol lf/+native-functions+)]
    [fun nil]
    ::failed-to-resolve))

(defn resolve-symbol [symbol scope]
  (let [variable (resolve-var-or-local symbol scope)]
    (if (not= variable ::failed-to-resolve)
      variable
      (let [native-function (resolve-native-function symbol)]
        (if (not= native-function ::failed-to-resolve)
          native-function
          (lu/fail-with (str "Could not resolve symbol! " symbol)))))))

(defn with-new-scope [enclosing block]
  (block {:values (lu/mutable-map {})
          :enclosing enclosing}))
