(ns lox-in-clojure.internal.utilities
  (:require [akar.syntax :refer [match]]
            [clojure.pprint :refer [pprint]])
  (:gen-class))

(defn report [value & {:keys [color header]}]
  (println (str (match color
                       :black  "\u001B[30m"
                       :red    "\u001B[31m"
                       :green  "\u001B[32m"
                       :yellow "\u001B[33m"
                       :blue   "\u001B[34m"
                       :purple "\u001B[35m"
                       :cyan   "\u001B[36m"
                       :white  "\u001B[37m")
                (if header (str header \newline \newline) "")
                (with-out-str (pprint value))
                "\u001B[0m")))

(defn fail-with [message]
  (throw (RuntimeException. ^String message)))

(defn string-within-range [start end]
  (apply str (map char (range (int start) (inc (int end))))))
