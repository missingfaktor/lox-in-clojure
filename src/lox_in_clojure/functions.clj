(ns lox-in-clojure.functions)

(defn create-lox-native-function [name clojure-function]
  {:type    :lox-callable
   :subtype :native-function
   :name    name
   :clojure clojure-function})

(def +native-functions+
  (into {}
        (map (fn [[name clojure-function]] [name (create-lox-native-function name clojure-function)])
             [[:+ +]
              [:- -]
              [:* *]
              [:/ /]
              [(keyword ",") str]
              [:< <]
              [:<= <=]
              [:> >]
              [:>= >=]
              [:= =]
              [:not= not=]
              [:print println]])))
