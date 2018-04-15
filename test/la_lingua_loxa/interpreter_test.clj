(ns la-lingua-loxa.interpreter-test
  (:require [clojure.test :refer :all]
            [la-lingua-loxa.interpreter :as li]
            [la-lingua-loxa.parser :as lp]))

(defn interpret [source-line]
  (li/interpret (:value (lp/parse source-line)) (li/clone-global-environment)))

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
                       (assign y 21)
                       y")
           21)))

  (testing "Symbol resolution failure"
    (is (thrown? RuntimeException (interpret "y"))))

  (testing "Reassignment to undeclared variable failure"
    (is (thrown? RuntimeException (interpret "(assign y 11)"))))

  (testing "Numeric operations"
    (is (= (interpret "(define y 12)
                       (* y y)")
           144))
    (is (= (interpret "(define x 9)
                       (define z 2)
                       (+ x z 1)")
           12)))

  (testing "Let bindings"
    (is (= (interpret "(let () 1)")
           1))
    (is (= (interpret "(let ((x 11)) x)")
           11))
    (is (= (interpret "(let ((b 9) (c b)) (+ b c))")
           18)))

  (testing "if"
    (is (= (interpret "(if (= 1 1) 5)")
           5))
    (is (= (interpret "(if (= 1 1) 5 6)")
           5))
    (is (= (interpret "(if (not= 1 1) 5)")
           nil))
    (is (= (interpret "(if (not= 1 1) 5 6)")
           6))))
