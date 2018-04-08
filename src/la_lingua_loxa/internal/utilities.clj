(ns la-lingua-loxa.internal.utilities
  (:require [akar.syntax :refer [match]]
            [clojure.pprint :refer [pprint]])
  (:gen-class))

(defn print-colored [value color]
  (println (str (match color
                       :black  "\u001B[30m"
                       :red    "\u001B[31m"
                       :green  "\u001B[32m"
                       :yellow "\u001B[33m"
                       :blue   "\u001B[34m"
                       :purple "\u001B[35m"
                       :cyan   "\u001B[36m"
                       :white  "\u001B[37m")
                (with-out-str (pprint value))
                "\u001B[0m")))

(defn fail-with [message]
  (throw (RuntimeException. ^String message)))
