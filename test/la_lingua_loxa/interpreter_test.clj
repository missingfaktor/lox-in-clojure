(ns la-lingua-loxa.interpreter-test
  (:require [clojure.test :refer :all]
            [la-lingua-loxa.interpreter :as li]
            [la-lingua-loxa.parser :as lp]))

(defn interpret [source-line]
  (li/interpret (:value (lp/parse source-line)) (li/new-environment)))

(deftest interpreter-test
  (testing "Atomic values"
    (is (= (interpret "1") 1))
    (is (= (interpret "1.2") 1.2))
    (is (= (interpret "nil") nil))
    (is (= (interpret "true") true))
    (is (= (interpret "false") false))
    (is (= (interpret "\"Hello\"") "Hello")))

  (testing "Symbol definition and resolution"
    (is (= (interpret "(define x 11)")
           11))
    (is (= (interpret "(define y 12)
                       y")
           12))
    (is (= (interpret "(define y 12)
                       y")
           12))
    (is (= (interpret "(define y 12)
                       (set y 21)
                       y")
           21)))

  (testing "Symbol resolution failure"
    (is (thrown? RuntimeException (interpret "y"))))

  (testing "Reassignment to undeclared variable failure"
    (is (thrown? RuntimeException (interpret "(set y 11)"))))

  (testing "Numeric operations"
    (= (interpret "(define y 12)
                   (* y y)")
       144)
    (= (interpret "(define x 9)
                   (define z 2)
                   (+ x z 1)")
       12)))
