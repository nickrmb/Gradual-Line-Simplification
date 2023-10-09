## Disclaimer

This is my Bachelor Thesis, commited at the University of Konstanz.

# Gradual Line Simplification

Simplifying polygonal lines is an important task faced
in many different applications.
There exist many progressive line simplification algorithms that simplify a polygonal line into different level of details.

<img src="misc/simplification.png" width=100%>

Here, we deal with another approach of simplifying, so called gradual line simplification (GLS).
It involves repeatedly removing a single point until we are left with the first and last point.
Each removal then forms a
new simplification.

# Usage

The [simplifier.jar](simplifier.jar) file is able to simplify a line (.sgpx format - see [util](/util) for more info) using a simplification algorithm and a geometric distance metric.

    java -jar .\simplifier.jar <pathToLine> <simplifier> <distanceMetric>

## Simplifiers 

There are multiple optimization goals that can be considered (see section ).
Therefore, 


