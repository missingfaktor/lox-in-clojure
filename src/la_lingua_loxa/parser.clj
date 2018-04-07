(ns la-lingua-loxa.parser
  (:require [blancas.kern.core :as k])
  (:gen-class))

(def ^:private whitespace?
  (k/many k/white-space))

(def ^:private lox-symbol
  (k/bind [sym (apply k/<|> (map k/sym* [\+ \- \* \/]))]
    (k/return {:node  :lox-symbol
               :value (keyword (str sym))})))

(def ^:private lox-number
  (k/bind [num (k/<|> k/dec-num k/float-num)]
    (k/return {:node  :lox-number
               :value num})))

(def ^:private lox-atom
  (k/bind [value (k/<|> lox-symbol lox-number)]
    (k/return {:node  :lox-atom
               :value value})))

(declare lox-expression)

(def ^:private lox-invocation
  (k/bind [_ (k/sym* \()
           _ whitespace?
           operator (k/fwd lox-expression)
           _ whitespace?
           operands (k/end-by whitespace? (k/fwd lox-expression))
           _ (k/sym* \))]
    (k/return {:node     :lox-invocation
               :operator operator
               :operands operands})))

(def ^:private lox-expression
  (k/bind [value (k/<|> lox-atom lox-invocation)]
    (k/return {:node  :lox-expression
               :value value})))

(defn parse [lox-source]
  (k/parse lox-expression lox-source))
