(ns la-lingua-loxa.parser-test
  (:require [clojure.test :refer :all]
            [la-lingua-loxa.parser :as lp]
            [blancas.kern.core :as k]
            [la-lingua-loxa.internal.utilities :as lu]))

(defn should-parse-to [parser text expected]
  (let [parse-result (k/parse parser text)]
    (if (:error parse-result)
      (lu/fail-with (str "Parsing failure! " (:error parse-result)))
      (is (= expected (:value parse-result))))))

(deftest parser-test
  (testing "Parsing integers"
    (should-parse-to lp/lox-number "1" {:node  :lox-number
                                        :value 1}))

  (testing "Parsing real numbers"
    (should-parse-to lp/lox-number "1.2" {:node  :lox-number
                                          :value 1.2}))

  (testing "Parsing nil"
    (should-parse-to lp/lox-nil "nil" {:node :lox-nil}))

  (testing "Parsing booleans"
    (should-parse-to lp/lox-boolean "true" {:node  :lox-boolean
                                            :value true})
    (should-parse-to lp/lox-boolean "false" {:node  :lox-boolean
                                             :value false}))

  (testing "Parsing strings"
    (should-parse-to lp/lox-string "\"Hello\"" {:node  :lox-string
                                                :value "Hello"}))

  (testing "Parsing symbols"
    (should-parse-to lp/lox-symbol "+" {:node  :lox-symbol
                                        :value :+})
    (should-parse-to lp/lox-symbol "+1" {:node  :lox-symbol
                                         :value :+1})
    (should-parse-to lp/lox-symbol "concat" {:node  :lox-symbol
                                             :value :concat})
    (should-parse-to lp/lox-symbol "," {:node  :lox-symbol
                                        :value (keyword ",")})
    (should-parse-to lp/lox-symbol "++11++" {:node  :lox-symbol
                                             :value :++11++}))

  (testing "Parsing lists"
    (should-parse-to lp/lox-list "()" {:node     :lox-list
                                       :elements '()})
    (should-parse-to lp/lox-list "(x)" {:node     :lox-list
                                        :elements '({:node  :lox-symbol
                                                     :value :x})})
    (should-parse-to lp/lox-list "(x y)" {:node     :lox-list
                                          :elements '({:node  :lox-symbol
                                                       :value :x}
                                                       {:node  :lox-symbol
                                                        :value :y})})
    (should-parse-to lp/lox-list "( x y)" {:node     :lox-list
                                           :elements '({:node  :lox-symbol
                                                        :value :x}
                                                        {:node  :lox-symbol
                                                         :value :y})})
    (should-parse-to lp/lox-list "(x y  )" {:node     :lox-list
                                            :elements '({:node  :lox-symbol
                                                         :value :x}
                                                         {:node  :lox-symbol
                                                          :value :y})})
    (should-parse-to lp/lox-list "( x y  )" {:node     :lox-list
                                             :elements '({:node  :lox-symbol
                                                          :value :x}
                                                          {:node  :lox-symbol
                                                           :value :y})})
    (should-parse-to lp/lox-list "(+ 1 2)" {:node     :lox-list
                                            :elements '({:node  :lox-symbol
                                                         :value :+}
                                                         {:node  :lox-number
                                                          :value 1}
                                                         {:node  :lox-number
                                                          :value 2})})))
