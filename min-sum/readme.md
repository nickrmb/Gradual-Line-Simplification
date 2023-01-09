# Gradual Line Simplification (min-sum Problem)

## Report

A full detailed report on Gradual Line Simplification can be found in [report.pdf](report.pdf).

## Testing

There are multiple simplifiers available:
- Exact: The exact simplifier that runs in $\mathcal{O}(n^2 (f_X(n) + n))$
- Greedy: A 4-approximation simplifier that runs in $\mathcal{O}(n (f))$
- InOrder: A simple heuristic that runs in $\mathcal{O}(n)$
- Random: A random simplification that runs in $\mathcal{O}(n)$
- Equal: Also a heuristic that runs in $\mathcal{O}(n)$

Where $f_X$ denotes the runtime function of $X$. <br>

Also multiple distance measures are available:
- Hausdorff: The [Hausdorff distance](https://en.wikipedia.org/wiki/Hausdorff_distance) with runtime $\mathcal{O}(n)$
- Frechet: The [Fréchet distance](https://en.wikipedia.org/wiki/Fr%C3%A9chet_distance) with runtime $\mathcal{O}(n^2)$
- FrechetApprox: An approximation of the Fréchet distance with runtime $\mathcal{O}(n)$

See [report.pdf](report.pdf) for full explanation on all simplifiers and distance measures.

### Simplifying

The [Simplify.jar](tests/Simplify.jar) is an executable version of [Simplify.java](src/test/Simplify.java).

Arguments must be given by `<pathToLine> <simplifier> <distanceType> <optimal:numbersIterationApprox> <optimal:removal>` where "numbersIterationApprox" is an argument how detailed the approximated fréchet distance will be, if distanceType is equal to "frechetapprox", and "removal" would print the removal sequence if given.

Possible calls could be:

    java -jar tests/Simplify.jar ../data/l1.sgpx inorder frechet
    java -jar tests/Simplify.jar ../data/l2.sgpx greedy frechetapprox 10 removal
    java -jar tests/Simplify.jar ../data/l3.sgpx exact hausdorff removal
    java -jar tests/Simplify.jar ../data/424.sgpx random frechetapprox 10
    java -jar tests/Simplify.jar ../data/3151.sgpx equal hausdorff

### Visualizer

The [Visualizer.jar](tests/Visualizer.jar) is an executable version of [Visualizer.java](src/test/Visualizer.java).

Arguments must be given by `<pathToLine> <simplifier> <distanceType> <optimal:numbersIterationApprox>`, where the same rules apply as in the Simplify section discussed.

Possible calls could be:

    java -jar tests/Visualizer.jar ../data/l1.sgpx inorder frechet
    java -jar tests/Visualizer.jar ../data/l2.sgpx greedy frechetapprox 10
    java -jar tests/Visualizer.jar ../data/l3.sgpx exact hausdorff
    java -jar tests/Visualizer.jar ../data/424.sgpx random frechetapprox 10
    java -jar tests/Visualizer.jar ../data/3151.sgpx equal hausdorff

Note: only .sgpx files are supported. See [here](../data/) to see some sample lines.
Or see [here](../util/) to learn about converting .gpx files to .sgpx files.