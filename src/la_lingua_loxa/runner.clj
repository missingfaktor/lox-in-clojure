(ns la-lingua-loxa.runner
  (:require [akar.syntax :refer [match]]
            [clojure.string :refer [lower-case]]
            [la-lingua-loxa.parser :as lp]
            [la-lingua-loxa.internal.utilities :as lu]
            [clojure.pprint :refer [pprint]])
  (:gen-class))

(defn run [lox-source]
  (lu/print-colored (lp/parse lox-source) :yellow))

(defn run-prompt []
  (loop []
    (print "lox> ")
    (flush)
    (run (read-line))
    (recur)))

(defn run-file [file]
  (run (slurp file)))

(defn -main [& args]
  (match (seq args)
         (:or nil (:seq []))                (run-prompt)
         (:seq [(:view lower-case "help")]) (lu/print-colored "Usage la-lingua-loxa [file]" :green)
         (:seq [file])                      (run-file file)
         :_                                 (lu/print-colored "Usage la-lingua-loxa [file]" :red)))
