(ns la-lingua-loxa.parser
  (:require [blancas.kern.core :as k]
            [blancas.kern.lexer.basic :as kl]
            [la-lingua-loxa.internal.utilities :as lu])
  (:gen-class))

(def ^:private whitespace?
  (k/many k/white-space))

(def ^:private lox-symbol-start
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

(def ^:private lox-symbol
  (k/bind [[head tail] (k/<*> lox-symbol-start
                              (k/many (k/<|> lox-symbol-start k/digit)))]
    (k/return {:node  :lox-symbol
               :value (keyword (apply str head tail))})))

(def ^:private lox-number
  (k/bind [num (k/<|> k/dec-num k/float-num)]
    (k/return {:node  :lox-number
               :value num})))

(def ^:private lox-string
  (k/bind [string kl/string-lit]
    (k/return {:node :lox-string
               :value string})))

(def ^:private lox-nil
  (k/bind [_ (k/token* "nil")]
    (k/return {:node :lox-nil})))

(def ^:private lox-boolean
  (k/bind [value (k/<|> (k/token* "true") (k/token* "false"))]
    (k/return {:node :lox-boolean
               :value (Boolean/parseBoolean value)})))

(def ^:private lox-atom
  (k/<|> lox-nil lox-boolean lox-symbol lox-number lox-string))

(declare lox-expression)

(def ^:private lox-list
  (k/bind [_        (k/sym* \()
           _        whitespace?
           elements (k/end-by whitespace? (k/fwd lox-expression))
           _        (k/sym* \))]
    (k/return {:node     :lox-list
               :elements elements})))

(def ^:private lox-expression
  (k/<|> lox-atom lox-list))

(defn parse [lox-source]
  (k/parse lox-expression lox-source))
