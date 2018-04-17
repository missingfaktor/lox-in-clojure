(defproject lox-in-clojure "why-is-this-version-part-mandatory"
  :description (str
                 "A Clojure implementation of a tree-walking interpreter for Lox language"
                 "from the excellent book 'Crafting Interpreters'")
  :url "https://github.com/missingfaktor/lox-in-clojure"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [akar/akar "2.0.0"]
                 [org.blancas/kern "1.1.0"]
                 [com.bhauman/rebel-readline "0.1.2"]]
  :main ^:skip-aot lox-in-clojure.runner
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :aliases {"rebl" ["trampoline" "run" "-m" "rebel-readline.main"]})
