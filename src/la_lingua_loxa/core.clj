(ns la-lingua-loxa.core
  (:require [akar.syntax :refer [match]]
            [clojure.string :refer [lower-case]]
            [blancas.kern.core :as k]
            [blancas.kern.lexer.basic :as kl]
            [clojure.pprint :refer [pprint]])
  (:gen-class))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; S-Expressions parser

(def whitespace?
  (k/many k/white-space))

(def lox-symbol
  (k/bind [sym (apply k/<|> (map k/sym* [\+ \- \* \/]))]
    (k/return {:node :lox-symbol
               :value (keyword (str sym))})))

(def lox-number
  (k/bind [num (k/<|> k/dec-num k/float-num)]
    (k/return {:node :lox-number
               :value num})))

(def lox-atom
  (k/bind [value (k/<|> lox-symbol lox-number)]
    (k/return {:node :lox-atom
               :value value})))

(declare lox-expression)

(def lox-invocation
  (k/bind [_ (k/sym* \()
           _ whitespace?
           operator (k/fwd lox-expression)
           _ whitespace?
           operands (k/end-by whitespace? (k/fwd lox-expression))
           _ (k/sym* \))]
    (k/return {:node :lox-invocation
               :operator operator
               :operands operands})))

(def lox-expression
  (k/bind [value (k/<|> lox-atom lox-invocation)]
    (k/return {:node :lox-expression
               :value value})))

(defn parse-lox-expression [lox-source]
  (k/parse lox-expression lox-source))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Runner

(defn run [lox-source]
  (pprint (parse-lox-expression lox-source)))

(defn run-prompt []
  (loop []
    (print "lox> ")
    (flush)
    (run (read-line))
    (recur)))

(defn run-file [file]
  (run (slurp file)))

(defn ansi [text color]
  (str (match color
              :black  "\u001B[30m"
              :red    "\u001B[31m"
              :green  "\u001B[32m"
              :yellow "\u001B[33m"
              :blue   "\u001B[34m"
              :purple "\u001B[35m"
              :cyan   "\u001B[36m"
              :white  "\u001B[37m")
       text
       "\u001B[0m"))

(defn -main [& args]
  (match (seq args)
         (:or nil (:seq []))                (run-prompt)
         (:seq [(:view lower-case "help")]) (println (ansi "Usage la-lingua-loxa [file]" :green))
         (:seq [file])                      (run-file file)
         :_                                 (println (ansi "Usage la-lingua-loxa [file]" :red))))
