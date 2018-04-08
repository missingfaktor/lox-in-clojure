(ns la-lingua-loxa.parser
  (:require [blancas.kern.core :as k]
            [blancas.kern.lexer.basic :as kl]
            [la-lingua-loxa.internal.utilities :as lu])
  (:gen-class))

(def whitespace?
  (k/many k/white-space))

(def lox-symbol-start
  (k/<|> (k/one-of* (lu/string-within-range \a \z))
         (k/one-of* (lu/string-within-range \A \Z))
         (k/sym* \+)
         (k/sym* \-)
         (k/sym* \*)
         (k/sym* \/)
         (k/sym* \.)
         (k/sym* \,)
         (k/sym* \=)
         (k/sym* \<)
         (k/sym* \>)
         (k/sym* \!)))

(def lox-symbol
  (k/bind [[head tail] (k/<*> lox-symbol-start
                              (k/many (k/<|> lox-symbol-start k/digit)))]
    (k/return {:node  :lox-symbol
               :value (keyword (apply str head tail))})))

(def lox-number
  (k/bind [num (k/<|> kl/dec-lit kl/float-lit)]
    (k/return {:node  :lox-number
               :value num})))

(def lox-string
  (k/bind [string kl/string-lit]
    (k/return {:node :lox-string
               :value string})))

(def lox-nil
  (k/bind [_ (k/token* "nil")]
    (k/return {:node :lox-nil})))

(def lox-boolean
  (k/bind [value (k/<|> (k/token* "true") (k/token* "false"))]
    (k/return {:node :lox-boolean
               :value (Boolean/parseBoolean value)})))

(def lox-atom
  (k/<|> lox-nil lox-boolean lox-symbol lox-number lox-string))

(declare lox-expression)

(def lox-list
  (k/bind [_        (k/sym* \()
           _        whitespace?
           elements (k/sep-end-by whitespace? (k/fwd lox-expression))
           _        whitespace?
           _        (k/sym* \))]
    (k/return {:node     :lox-list
               :elements elements})))

(def lox-expression
  (k/<|> lox-atom lox-list))

(defn parse [lox-source]
  (k/parse lox-expression lox-source))
