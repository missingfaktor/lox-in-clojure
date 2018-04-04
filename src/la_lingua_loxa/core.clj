(ns la-lingua-loxa.core
  (:require [akar.syntax :refer [match]])
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

(defn -main [& args]
  (println "args " args)
  (match (seq args)
         (:or nil (:seq [])) (run-prompt)
         (:seq [file])       (run-file file)
         :_                  (println "Usage la-lingua-loxa [file]")))

