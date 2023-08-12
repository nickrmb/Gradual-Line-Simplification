## Disclaimer

This is my Bachelor Thesis, commited at the University of Konstanz.

# Gradual Line Simplification

Simplifying polygonal lines is an important task faced
in many different applications. There already exist
many progressive line simplification algorithms that
simplify a polygonal line into different level of details.

<img src="misc/simplification.png" width=100%>

Here, we deal with another approach of
simplifying, so called gradual line simplification (GLS). It
involves repeatedly
removing a single point until we are left with the first and last point. Each removal then forms a
new simplification.

There are multiple optimization goals that can be considered.

