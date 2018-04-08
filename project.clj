(defproject la-lingua-loxa "why-is-this-version-part-mandatory"
  :description (str
                 "A Clojure implementation of a tree-walking interpreter for Lox language"
                 "from the excellent book 'Crafting Interpreters'")
  :url "https://github.com/missingfaktor/la-lingua-loxa"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [akar/akar-core "1.0.0"]
                 [org.blancas/kern "1.1.0"]
                 [com.bhauman/rebel-readline "0.1.2"]]
  :plugins [[lein-gorilla "0.4.0"]]
  :main ^:skip-aot la-lingua-loxa.runner
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :aliases {"rebl" ["trampoline" "run" "-m" "rebel-readline.main"]})
