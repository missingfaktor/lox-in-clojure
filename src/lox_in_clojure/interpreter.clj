(ns lox-in-clojure.interpreter
  (:require [akar.syntax :refer [match clauses]]
            [akar.patterns :refer [!constant]]
            [lox-in-clojure.internal.utilities :as lu]
            [lox-in-clojure.scope :as sc])
  (:gen-class))

(declare interpret)

(defn lox-resolve [name scope]
  (let [[value _] (sc/resolve-symbol name scope)]
    value))

(defn lox-define [name expression scope]
  (let [evaluation-result (interpret expression scope)]
    (sc/set-in-global-scope name evaluation-result scope)
    evaluation-result))

(defn lox-assign [name expression scope]
  (let [[_ scope-resolved-in] (sc/resolve-symbol name scope)
        evaluation-result     (interpret expression scope)]
    (sc/set-in-scope name evaluation-result scope-resolved-in)
    evaluation-result))

(defn ensuring-boolean [value]
  (if (instance? Boolean value)
    value
    (lu/fail-with "The expression does not evaluate to a boolean!")))

(defn lox-if [cond then else scope]
  (if (ensuring-boolean (interpret cond scope))
    (interpret then scope)
    (if else
      (interpret else scope))))

(defn lox-let [bindings body scope]
  (let [parse-binding
        (clauses (:seq [(:seq [:lox-symbol name]) expression]) [name expression]
                 :_                                            (lu/fail-with "Malformed let-expression!"))]
    (sc/with-new-scope scope
                       (fn [new-scope]
                         (let [parsed-bindings (map parse-binding bindings)]
                           (doseq [[name expression] parsed-bindings]
                             (let [evaluation-result (interpret expression new-scope)]
                               (sc/set-in-scope name evaluation-result new-scope)))
                           (interpret body new-scope))))))

(defn lox-while [cond body scope]
  (loop []
    (if (ensuring-boolean (interpret cond scope))
      (do
        (interpret body scope)
        (recur)))))

(defn interpret [lox-syntax-tree environment]
  (match lox-syntax-tree

         (:seq [:lox-number value])                       value

         (:seq [:lox-string value])                       value

         (:seq [:lox-boolean value])                      value

         (:seq [:lox-nil])                                nil

         (:seq [:lox-symbol name])                        (lox-resolve name environment)

         (:seq [:lox-define
                (:seq [:lox-symbol name])
                expression])                              (lox-define name expression environment)

         (:seq [:lox-assign
                (:seq [:lox-symbol name])
                expression])                              (lox-assign name expression environment)

         (:seq [:lox-if cond then else])                  (lox-if cond then else environment)


         (:seq [:lox-let bindings body])                  (lox-let bindings body environment)

         (:seq [:lox-while cond body])                    (lox-while cond body environment)

         (:seq [:lox-list (:seq [operator & operands])])  (apply (interpret operator environment)
                                                                 (map #(interpret % environment) operands))

         (:seq [:lox-program expressions])                (reduce (fn [_ expression] (interpret expression environment))
                                                                       nil
                                                                       expressions)

         expression                                       (lu/fail-with
                                                            (str "Could not interpret expression! " expression))))
