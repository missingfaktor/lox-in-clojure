(ns la-lingua-loxa.interpreter
  (:require [akar.syntax :refer [match]]
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
         [(lu/!in-map @environment) fun] fun
         :_                              (lu/fail-with (str "Could not resolve symbol! " symbol'))))

(declare interpret)

(defn lox-define [operands environment]
  (match operands
         (:seq [{:node :lox-symbol :value name'} value]) (let [evaluation-result (interpret value environment)]
                                                           (swap! environment assoc name' evaluation-result)
                                                           evaluation-result)
         :_                                              (lu/fail-with "Malformed definition!")))

(defn lox-assign [operands environment]
  (match operands
         (:seq [{:node :lox-symbol :value name'} value]) (let [_ (resolve-symbol name' environment)
                                                               evaluation-result (interpret value environment)]
                                                           (swap! environment assoc name' evaluation-result)
                                                           evaluation-result)
         :_                                              (lu/fail-with "Malformed assignment expression!")))

(defn ensuring-boolean [value]
  (if (instance? Boolean value)
    value
    (lu/fail-with "The expression does not evaluate to a boolean!")))

(defn lox-if [operands environment]
  (match operands
         (:seq [condition then else]) (if (ensuring-boolean (interpret condition environment))
                                        (interpret then environment)
                                        (interpret else environment))
         (:seq [condition then])      (if (ensuring-boolean (interpret condition environment))
                                        (interpret then environment))
         :_                           (lu/fail-with "Malformed if-expression!")))

(defn interpret [lox-syntax-tree environment]
  (match lox-syntax-tree

         {:node  (:or :lox-number
                      :lox-string
                      :lox-boolean)
          :value value}                           value

         {:node :lox-nil}                         nil

         {:node :lox-symbol
          :value symbol}                          (resolve-symbol symbol environment)

         {:node     :lox-list
          :elements (:seq [{:node  :lox-symbol
                            :value :define}
                           & operands])}          (lox-define operands environment)

         {:node     :lox-list
          :elements (:seq [{:node  :lox-symbol
                            :value :set}
                           & operands])}          (lox-assign operands environment)

         {:node     :lox-list
          :elements (:seq [{:node  :lox-symbol
                            :value :if}
                           & operands])}          (lox-if operands environment)

         {:node     :lox-list
          :elements (:seq [operator & operands])} (apply (interpret operator environment)
                                                         (map #(interpret % environment) operands))

         {:node        :lox-program
          :expressions expressions}               (reduce (fn [_ expression] (interpret expression environment))
                                                          nil
                                                          expressions)

         expression                               (lu/fail-with
                                                    (str "Could not interpret expression! " expression))))
