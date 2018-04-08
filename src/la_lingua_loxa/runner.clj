(ns la-lingua-loxa.runner
  (:require [akar.syntax :refer [match]]
            [clojure.string :refer [lower-case]]
            [la-lingua-loxa.parser :as lp]
            [la-lingua-loxa.interpreter :as li]
            [la-lingua-loxa.internal.utilities :as lu]
            [clojure.pprint :refer [pprint]])
  (:gen-class))

(defn run [lox-source]
  (let [parse-result (lp/parse lox-source)
        _            (if (:error parse-result)
                       (lu/fail-with (str "Parsing failure! " (:error parse-result))))
        syntax-tree  (:value parse-result)
        _            (lu/print-colored syntax-tree :yellow)
        value        (li/interpret syntax-tree)]
    (lu/print-colored value :green)))

(defn run-prompt []
  (loop []
    (print "lox> ")
    (flush)
    (try
      (run (read-line))
      (catch Exception ex
        (lu/print-colored ex :red)))
    (recur)))

(defn run-file [file]
  (run (slurp file)))

(defn -main [& args]
  (match (seq args)
         (:or nil (:seq []))                (run-prompt)
         (:seq [(:view lower-case "help")]) (lu/print-colored "Usage la-lingua-loxa [file]" :green)
         (:seq [file])                      (run-file file)
         :_                                 (lu/print-colored "Usage la-lingua-loxa [file]" :red)))
