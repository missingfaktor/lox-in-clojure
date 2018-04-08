(ns la-lingua-loxa.interpreter
  (:require [akar.syntax :refer [match]]
            [la-lingua-loxa.internal.utilities :as lu])
  (:gen-class))

(defn ^:private resolve-symbol [symbol]
  (match symbol
         :+ +
         :- -
         :* *
         :/ /
         :_ (lu/fail-with (str "Could not resolve symbol! " symbol))))

(defn interpret [lox-syntax-tree]
  (match lox-syntax-tree

         {:node :lox-number
          :value value}                           value

         {:node :lox-symbol
          :value symbol}                          (resolve-symbol symbol)

         {:node :lox-list
          :elements (:seq [operator & operands])} (apply (interpret operator) (map interpret operands))

         expression                               (lu/fail-with (str "Could not interpret expression! " expression))))
