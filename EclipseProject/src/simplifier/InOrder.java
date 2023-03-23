package simplifier;

import distance.DistanceMeasure;
import line.PolyLine;
import util.Tuple;

public class InOrder implements LineSimplifier {
	
	
	@Override
	public Tuple<int[], double[]> simplify(PolyLine l, DistanceMeasure distance) {
		int length = l.length();
		int numPointsBetween = length - 2;

		int[] simplification = new int[numPointsBetween];

		// create in order
		for (int i = 0; i < numPointsBetween; i++) {
			simplification[i] = i + 1;
		}

		return new Tuple<>(simplification, null);
	}
	
	@Override
	public String toString() {
		return "InOrder";
	}
	
}
