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

(defn ^:private resolve-symbol [symbol' environment]
  (match symbol'
         [(lu/in-map @environment) fun] fun
         :_                             (lu/fail-with (str "Could not resolve symbol! " symbol'))))

(declare interpret)

(defn ^:private lox-define [operands environment]
  (match operands
         (:seq [{:node :lox-symbol :value name'} value]) (do
                                                           (swap! environment assoc name' (interpret value environment))
                                                           nil)
         :_                                              (lu/fail-with "Malformed definition!")))

(defn interpret [lox-syntax-tree environment]
  (match lox-syntax-tree

         {:node (:or :lox-number
                     :lox-string
                     :lox-boolean)
          :value value}                           value

         {:node :lox-nil}                         nil

         {:node :lox-symbol
          :value symbol}                          (resolve-symbol symbol environment)

         {:node :lox-list
          :elements (:seq [{:node :lox-symbol
                            :value :define}
                           & operands])}          (lox-define operands environment)

         {:node :lox-list
          :elements (:seq [operator & operands])} (apply (interpret operator environment)
                                                         (map #(interpret % environment) operands))

         expression                               (lu/fail-with (str "Could not interpret expression! " expression))))
