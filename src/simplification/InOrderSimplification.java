package simplification;

import distance.DistanceMeasurement;
import line.PolyLine;
import util.Tuple;
import util.Util;

public class InOrderSimplification implements LineSimplifier {
	
	
	@Override
	public Tuple<int[], double[]> simplify(PolyLine l, DistanceMeasurement distanceMeasurement) {
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
