(ns la-lingua-loxa.parser-test
  (:require [clojure.test :refer :all]
            [la-lingua-loxa.parser :as lp]
            [blancas.kern.core :as k]
            [la-lingua-loxa.internal.utilities :as lu]))

(defn should-parse-to [text expected]
  (let [parse-result (k/parse lp/lox-expression text)]
    (if (:error parse-result)
      (lu/fail-with (str "Parsing failure! " (:error parse-result)))
      (is (= expected (:value parse-result))))))

(deftest parser-test
  (testing "Parsing integers"
    (should-parse-to "1" {:node  :lox-number
                          :value 1}))

  (testing "Parsing real numbers"
    (should-parse-to "1.2" {:node  :lox-number
                            :value 1.2}))

  (testing "Parsing nil"
    (should-parse-to "nil" {:node :lox-nil}))

  (testing "Parsing booleans"
    (should-parse-to "true" {:node  :lox-boolean
                             :value true})
    (should-parse-to "false" {:node  :lox-boolean
                              :value false}))

  (testing "Parsing strings"
    (should-parse-to "\"Hello\"" {:node  :lox-string
                                  :value "Hello"}))

  (testing "Parsing symbols"
    (should-parse-to "+" {:node  :lox-symbol
                          :value :+})
    (should-parse-to "+1" {:node  :lox-symbol
                           :value :+1})
    (should-parse-to "concat" {:node  :lox-symbol
                               :value :concat})
    (should-parse-to "," {:node  :lox-symbol
                          :value (keyword ",")})
    (should-parse-to "++11++" {:node  :lox-symbol
                               :value :++11++}))

  (testing "Parsing lists"
    (should-parse-to "()" {:node     :lox-list
                           :elements '()})
    (should-parse-to "(x)" {:node     :lox-list
                            :elements '({:node  :lox-symbol
                                         :value :x})})
    (should-parse-to "(x y)" {:node     :lox-list
                              :elements '({:node  :lox-symbol
                                           :value :x}
                                           {:node  :lox-symbol
                                            :value :y})})
    (should-parse-to "( x y)" {:node     :lox-list
                               :elements '({:node  :lox-symbol
                                            :value :x}
                                            {:node  :lox-symbol
                                             :value :y})})
    (should-parse-to "(x y  )" {:node     :lox-list
                                :elements '({:node  :lox-symbol
                                             :value :x}
                                             {:node  :lox-symbol
                                              :value :y})})
    (should-parse-to "( x y  )" {:node     :lox-list
                                 :elements '({:node  :lox-symbol
                                              :value :x}
                                              {:node  :lox-symbol
                                               :value :y})})
    (should-parse-to "(+ 1 2)" {:node     :lox-list
                                :elements '({:node  :lox-symbol
                                             :value :+}
                                             {:node  :lox-number
                                              :value 1}
                                             {:node  :lox-number
                                              :value 2})})))
