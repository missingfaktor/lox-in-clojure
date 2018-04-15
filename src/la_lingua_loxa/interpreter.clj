(ns la-lingua-loxa.interpreter
  (:require [akar.syntax :refer [match clauses]]
            [akar.patterns :refer [!constant]]
            [la-lingua-loxa.internal.utilities :as lu])
  (:gen-class))

(defn new-environment []
  (atom {:+            +
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
         :print        println}))

(defn resolve-symbol [symbol' environment]
  (match symbol'
         (:look-in @environment fun) fun
         :_                          (lu/fail-with (str "Could not resolve symbol! " symbol'))))

(declare interpret)

(defn lox-define [name expression environment]
  (let [evaluation-result (interpret expression environment)]
    (swap! environment assoc name evaluation-result)
    evaluation-result))

(defn lox-assign [name expression environment]
  (let [_                 (resolve-symbol name environment)
        evaluation-result (interpret expression environment)]
    (swap! environment assoc name evaluation-result)
    evaluation-result))

(defn ensuring-boolean [value]
  (if (instance? Boolean value)
    value
    (lu/fail-with "The expression does not evaluate to a boolean!")))

(defn lox-if [cond then else environment]
  (if (ensuring-boolean (interpret cond environment))
    (interpret then environment)
    (if else
      (interpret else environment))))

(defn lox-let [bindings body environment]
  (let [parse-binding
        (clauses (:seq [(:seq [:lox-symbol name]) expression]) [name expression]
                 :_                                            (lu/fail-with "Malformed let-expression!"))]

    (let [parsed-bindings (map parse-binding bindings)]
      (doseq [[name expression] parsed-bindings]
        (let [evaluation-result (interpret expression environment)]
          (swap! environment assoc name evaluation-result)))
      (interpret body environment))))

(defn interpret [lox-syntax-tree environment]
  (match lox-syntax-tree

         (:seq [:lox-number value])                       value

         (:seq [:lox-string value])                       value

         (:seq [:lox-boolean value])                      value

         (:seq [:lox-nil])                                nil

         (:seq [:lox-symbol symbol])                      (resolve-symbol symbol environment)

         (:seq [:lox-define
                (:seq [:lox-symbol name])
                expression])                              (lox-define name expression environment)

         (:seq [:lox-assign
                (:seq [:lox-symbol name])
                expression])                              (lox-assign name expression environment)

         (:seq [:lox-if cond then else])                  (lox-if cond then else environment)


         (:seq [:lox-let bindings body])                  (lox-let bindings body environment)

         (:seq [:lox-list (:seq [operator & operands])])  (apply (interpret operator environment)
                                                                 (map #(interpret % environment) operands))

         (:seq [:lox-program expressions])                (reduce (fn [_ expression] (interpret expression environment))
                                                                       nil
                                                                       expressions)

         expression                                       (lu/fail-with
                                                            (str "Could not interpret expression! " expression))))
