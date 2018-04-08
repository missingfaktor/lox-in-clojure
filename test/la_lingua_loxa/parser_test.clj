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
                                          :value 1.2})))
