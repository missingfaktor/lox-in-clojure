(ns lox-in-clojure.runner
  (:require [akar.syntax :refer [match]]
            [clojure.string :refer [lower-case]]
            [lox-in-clojure.parser :as lp]
            [lox-in-clojure.interpreter :as li]
            [lox-in-clojure.internal.utilities :as lu]
            [lox-in-clojure.scope :as sc]
            [clojure.pprint :refer [pprint]]
            [akar-exceptions.core :refer [attempt !ex]])
  (:gen-class))

(defn run [lox-source scope]
  (let [parse-result (lp/parse lox-source)
        _            (if (:error parse-result)
                       (lu/fail-with (str "Parsing failure! " (:error parse-result))))
        syntax-tree  (:value parse-result)
        _            (lu/report syntax-tree :header "# Syntax tree:" :color :yellow)
        _            (lu/report scope :header "# Scope:" :color :blue)
        value        (li/interpret syntax-tree scope)]
    (lu/report value :header "# Value:" :color :green)))

(defn run-prompt []
  (let [scope (sc/fresh-global-scope)]
    (loop []
      (print "lox> ")
      (flush)
      (attempt (run (read-line) scope)
               :on-error ([(!ex Exception) ex] (lu/report (.getMessage ex) :color :red)))
      (recur))))

(defn run-file [file]
  (run (slurp file) (sc/fresh-global-scope)))

(defn -main [& args]
  (match (seq args)
         (:or nil (:seq []))                (run-prompt)
         (:seq [(:view lower-case "help")]) (lu/report "Usage: lox [file]" :color :green)
         (:seq [file])                      (run-file file)
         :_                                 (lu/report "Usage: lox [file]" :color :red)))
