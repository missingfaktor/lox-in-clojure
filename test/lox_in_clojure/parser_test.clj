(ns lox-in-clojure.parser-test
  (:require [clojure.test :refer :all]
            [lox-in-clojure.parser :as lp]
            [blancas.kern.core :as k]
            [lox-in-clojure.internal.utilities :as lu]))

(defn should-parse-to [text expected]
  (let [parse-result (k/parse lp/lox-expression text)]
    (if (:error parse-result)
      (lu/fail-with (str "Parsing failure! " (:error parse-result)))
      (is (= expected (:value parse-result))))))

(deftest parser-test
  (testing "Parsing integers"
    (should-parse-to "1" [:lox-number 1]))

  (testing "Parsing real numbers"
    (should-parse-to "1.2" [:lox-number 1.2]))

  (testing "Parsing nil"
    (should-parse-to "nil" [:lox-nil]))

  (testing "Parsing booleans"
    (should-parse-to "true" [:lox-boolean true])
    (should-parse-to "false" [:lox-boolean false]))

  (testing "Parsing strings"
    (should-parse-to "\"Hello\"" [:lox-string "Hello"]))

  (testing "Parsing symbols"
    (should-parse-to "+" [:lox-symbol :+])
    (should-parse-to "concat" [:lox-symbol :concat])
    (should-parse-to "," [:lox-symbol (keyword ",")])
    (should-parse-to "++11++" [:lox-symbol :++11++]))

  (testing "Parsing lists"
    (should-parse-to "()" [:lox-list []])
    (should-parse-to "(x)" [:lox-list [[:lox-symbol :x]]])
    (should-parse-to "(x y)" [:lox-list [[:lox-symbol :x]
                                         [:lox-symbol :y]]])
    (should-parse-to "( x y)" [:lox-list [[:lox-symbol :x]
                                          [:lox-symbol :y]]])
    (should-parse-to "(x y  )" [:lox-list [[:lox-symbol :x]
                                           [:lox-symbol :y]]])
    (should-parse-to "( x y  )" [:lox-list [[:lox-symbol :x]
                                            [:lox-symbol :y]]])
    (should-parse-to "(+ 1 2)" [:lox-list [[:lox-symbol :+]
                                           [:lox-number 1]
                                           [:lox-number 2]]])))
