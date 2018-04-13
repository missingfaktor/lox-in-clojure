(ns la-lingua-loxa.runner
  (:require [akar.syntax :refer [match]]
            [clojure.string :refer [lower-case]]
            [la-lingua-loxa.parser :as lp]
            [la-lingua-loxa.interpreter :as li]
            [la-lingua-loxa.internal.utilities :as lu]
            [clojure.pprint :refer [pprint]]
            [akar-exceptions.core :refer [attempt !ex]])
  (:gen-class))

(defn run [lox-source environment]
  (let [parse-result (lp/parse lox-source)
        _            (if (:error parse-result)
                       (lu/fail-with (str "Parsing failure! " (:error parse-result))))
        syntax-tree  (:value parse-result)
        _            (lu/report syntax-tree :header "# Syntax tree:" :color :yellow)
        _            (lu/report (keys @environment) :header "# Environment:" :color :blue)
        value        (li/interpret syntax-tree environment)]
    (lu/report value :header "# Value:" :color :green)))

(defn run-prompt []
  (let [environment (li/new-environment)]
    (loop []
      (print "lox> ")
      (flush)
      (attempt (run (read-line) environment)
               :on-error ([(!ex Exception) ex] (lu/report (.getMessage ex) :color :red)))
      (recur))))

(defn run-file [file]
  (run (slurp file) (li/new-environment)))

(defn -main [& args]
  (match (seq args)
         (:or nil (:seq []))                (run-prompt)
         (:seq [(:view lower-case "help")]) (lu/report "Usage la-lingua-loxa [file]" :color :green)
         (:seq [file])                      (run-file file)
         :_                                 (lu/report "Usage la-lingua-loxa [file]" :color :red)))
