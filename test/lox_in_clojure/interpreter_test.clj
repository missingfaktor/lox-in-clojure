(ns lox-in-clojure.interpreter-test
  (:require [clojure.test :refer :all]
            [lox-in-clojure.interpreter :as li]
            [lox-in-clojure.scope :as sc]
            [lox-in-clojure.parser :as lp]))

(defn interpret [source-line]
  (li/interpret (:value (lp/parse source-line)) (sc/fresh-global-scope)))

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
           18))
    (is (= (interpret "(define x 9)
                       (let ((x 1)) x)")
           1))
    (is (= (interpret "(define x 9)
                       (let ((x 1)) x)
                       x")
           9)))

  (testing "if"
    (is (= (interpret "(if (= 1 1) 5)")
           5))
    (is (= (interpret "(if (= 1 1) 5 6)")
           5))
    (is (= (interpret "(if (not= 1 1) 5)")
           nil))
    (is (= (interpret "(if (not= 1 1) 5 6)")
           6)))

  (testing "while"
    (is (= (interpret "(let ((i 5) (dummy (while (> i 2) (assign i (- i 1))))) i)"))
        2))

  (testing "native function invocations"
    (is (= (interpret "(+ 1 2)")
           3))
    (is (thrown? RuntimeException (interpret "(1 2)")))))
