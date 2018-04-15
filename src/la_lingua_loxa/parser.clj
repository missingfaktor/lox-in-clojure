(ns la-lingua-loxa.parser
  (:require [blancas.kern.core :as k]
            [blancas.kern.lexer.basic :as kl]
            [la-lingua-loxa.internal.utilities :as lu])
  (:gen-class))

(def whitespace?
  (k/many k/white-space))

(def whitespace
  (k/many1 k/white-space))

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
    (let [symbol (keyword (apply str head tail))]
      (if (#{:define :assign :if :let} symbol)
        (k/fail (str symbol " is a reserved word! It cannot be used as a symbol."))
        (k/return [:lox-symbol (keyword (apply str head tail))])))))

(def lox-number
  (k/bind [num (k/<|> kl/dec-lit kl/float-lit)]
    (k/return [:lox-number num])))

(def lox-string
  (k/bind [string kl/string-lit]
    (k/return [:lox-string string])))

(def lox-nil
  (k/bind [_ (k/token* "nil")]
    (k/return [:lox-nil])))

(def lox-boolean
  (k/bind [value (k/<|> (k/token* "true") (k/token* "false"))]
    (k/return [:lox-boolean (Boolean/parseBoolean value)])))

(def lox-atom
  (k/<|> lox-number lox-string lox-nil lox-boolean lox-symbol))

(declare lox-expression)

(defn bracketed [parser]
  (k/<:> (k/bind [_      (k/sym* \()
                  _      whitespace?
                  parsed parser
                  _      whitespace?
                  _      (k/sym* \))]
           (k/return parsed))))

(def lox-define
  (bracketed (k/bind [_          (k/token* "define")
                      _          whitespace
                      name       lox-symbol
                      _          whitespace
                      expression (k/fwd lox-expression)]
               (k/return [:lox-define name expression]))))

(def lox-assign
  (bracketed (k/bind [_          (k/token* "assign")
                      _          whitespace
                      name       lox-symbol
                      _          whitespace
                      expression (k/fwd lox-expression)]
               (k/return [:lox-assign name expression]))))

(def lox-if
  (bracketed (k/bind [_    (k/token* "if")
                      _    whitespace
                      cond (k/fwd lox-expression)
                      _    (k/optional whitespace)
                      then (k/fwd lox-expression)
                      _    (k/optional whitespace)
                      else (k/optional (k/fwd lox-expression))]
               (k/return [:lox-if cond then else]))))

(def lox-binding
  (bracketed (k/bind [symbol     lox-symbol
                      _          whitespace
                      expression (k/fwd lox-expression)]
               (k/return [symbol expression]))))

(def lox-let
  (bracketed (k/bind [_        (k/token* "let")
                      _        whitespace?
                      bindings (bracketed (k/sep-end-by whitespace? lox-binding))
                      _        whitespace?
                      body     (k/fwd lox-expression)]
               (k/return [:lox-let bindings body]))))

(def lox-list
  (k/bind [elements (bracketed (k/sep-end-by whitespace? (k/fwd lox-expression)))]
    (k/return [:lox-list elements])))

(def lox-expression
  (k/<|> lox-atom lox-define lox-assign lox-if lox-let lox-list))

(def lox-program
  (k/bind [expressions (k/<*> (k/sep-end-by whitespace? lox-expression) (k/skip k/eof))]
    (k/return [:lox-program (mapcat identity expressions)])))

(defn parse [lox-source]
  (k/parse lox-program lox-source))
