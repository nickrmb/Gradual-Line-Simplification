package simplifier;

import distance.DistanceMeasure;
import line.PolyLine;
import util.MinNodeHeap;
import util.HeapNode;
import util.Tuple;

public class GreedySimplification implements LineSimplifier {

	@Override
	public Tuple<int[], double[]> simplify(PolyLine l, DistanceMeasure distanceMeasurement) {
		int numPointsBetween = l.length() - 2;

		int[] simplification = new int[numPointsBetween];
		double[] error = new double[numPointsBetween];
		double currentError = 0;

		// create min node heap
		MinNodeHeap minHeap = new MinNodeHeap(l, distanceMeasurement);

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
		return "Greedy";
	}

}