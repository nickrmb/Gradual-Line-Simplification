## Disclaimer

This is my Bachelor Thesis, commited at the University of Konstanz.

# Gradual Line Simplification

Simplifying polygonal lines is an important task faced
in many different applications. There already exist
many progressive line simplification algorithms that
simplify a polygonal line into different level of details.

<img src="misc/simplification.png" width=100%>

In this work we deal with another approach of
simplifying, so called gradual line simplification. It
asks for simplifications such that we repeatedly remove one vertex of the line in each simplification step,
until we removed all vertices except the first and last
one.

There are multiple optimization goals that can be considered.

