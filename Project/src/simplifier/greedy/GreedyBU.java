package simplifier.greedy;

import distance.DistanceMeasure;
import line.PolyLine;
import simplifier.LineSimplifier;
import util.MinNodeHeap;
import util.HeapNode;
import util.Tuple;

public class GreedyBU implements LineSimplifier {

	@Override
	public Tuple<int[], double[]> simplify(PolyLine l, DistanceMeasure distance) {
		int numPointsBetween = l.length() - 2;

		int[] simplification = new int[numPointsBetween];
		double[] error = new double[numPointsBetween];

		// create min node heap
		MinNodeHeap minHeap = new MinNodeHeap(l, distance);

		// for every node between
		for (int i = 0; i < numPointsBetween; i++) {
			// extract
			HeapNode node = minHeap.extract();

			simplification[i] = node.index;
			error[i] = node.error;
		}

		return new Tuple<>(simplification, error);

	}
	
	@Override
	public String toString() {
		return "GreedyBU";
	}

}