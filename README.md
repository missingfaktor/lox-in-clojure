# la-lingua-loxa

This is a Clojure implementation of a tree-walking interpreter for Lox language from [Bob Nystorm](https://twitter.com/munificentbob)'s excellent book ["Crafting Interpreters"](http://craftinginterpreters.com/).

# Running

If you wish to play with this project, simply run `lein run` from within the project directory, and you will be greeted with a barebone Lox REPL.

# Deviations

Since the only aim of this project is learning, I am deviating from the book in a number of ways: experimenting with things not covered in the book, making different choices with syntax and semantics, dropping bits I am not particularly interested in learning. A few of these deviations:

- I didn't hand-roll a scanner and a parser. I used a parser combinator library instead.
- I picked Lisp syntax for my version of Lox.

# Why is this project named in Italian?

When I created this repository, I had just finished binging on [Lemony Snicket's A Series of Unfortunate Events](https://en.wikipedia.org/wiki/A_Series_of_Unfortunate_Events_(TV_series)), and it left me thinking about Italian food. That's all. :spaghetti:
