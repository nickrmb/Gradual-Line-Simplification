# Gradual Line Simplification

Simplifying polygonal lines is an important task faced
in many different applications. There already exist
many progressive line simplification algorithms that
simplify a polygonal line into different level of details.

In this work we will deal with another approach of
simplifying, so called gradual line simplification. It
asks for simplifications such that we repeatedly re-
move one vertex of the line in each simplification step,
until we removed all vertices except the first and last
one.

There are multiple optimization goals that can be considered.

## Min Sum

In the min sum problem we are interested in finding simplifications such that the summation of the error created in each simplification step is minimalized. See [min-sum/](min-sum/) to see the work dedicated to the min sum optimization goal.