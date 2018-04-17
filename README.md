# lox-in-clojure

[![Build Status](https://travis-ci.org/missingfaktor/lox-in-clojure.svg?branch=master)](https://travis-ci.org/missingfaktor/lox-in-clojure)

This is a Clojure implementation of a tree-walking interpreter for Lox language from [Bob Nystorm](https://twitter.com/munificentbob)'s excellent book ["Crafting Interpreters"](http://craftinginterpreters.com/).

# Running

If you wish to play with this project, simply run `lein run` from within the project directory, and you will be greeted with a barebone Lox REPL.

# Deviations

Since the only aim of this project is learning, I am deviating from the book in a number of ways: experimenting with things not covered in the book, making different choices with syntax and semantics, dropping bits I am not particularly interested in learning. A few of these deviations:

- I didn't hand-roll a scanner and a parser. I used a parser combinator library instead.
- I picked Lisp syntax for my version of Lox.
