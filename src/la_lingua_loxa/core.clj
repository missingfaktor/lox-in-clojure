(ns la-lingua-loxa.core
  (:require [akar.syntax :refer [match]]
            [clojure.string :refer [lower-case]])
  (:gen-class))

(def token-types-groupings
  {:single-character-tokens     [:left-paren :right-paren :left-brace :right-brace
                                 :comma :dot :minus :plus :semicolon :slash :star]

   :one-or-two-character-tokens [:bang :bang-equal
                                 :equal :equal-equal
                                 :greater :greater-equal
                                 :less :less-equal]

   :literals                    [:identifier :string :number]

   :keywords                    [:and :class :else :false :fun :for :if :nil :or
                                 :print :return :super :this :true :var :while]

   :standalone                  [:eof]})

(def token-types
  (-> token-types-groupings vals flatten set))

(defn scan-tokens [lox-text]
  [])

(defn run [lox-text]
  (let [tokens (scan-tokens lox-text)]
    (doseq [token tokens]
      (println token))))

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
