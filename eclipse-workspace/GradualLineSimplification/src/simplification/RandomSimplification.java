package simplification;

import java.util.Random;

import distance.DistanceMeasurement;
import line.PolyLine;
import util.Tuple;
import util.Util;

public class RandomSimplification implements LineSimplifier {

	@Override
	public Tuple<int[], double[]> simplify(PolyLine l, DistanceMeasurement distanceMeasurement) {
		int length = l.length();
		int numPointsBetween = length - 2;

		int[] simplification = new int[numPointsBetween];

		// create in order
		for (int i = 0; i < numPointsBetween; i++) {
			simplification[i] = i + 1;
		}

		// random permutation
		Random random = new Random();
		for (int i = numPointsBetween - 1; i > 0; i--) {
			int j = random.nextInt(i + 1);

			int a = simplification[i];
			simplification[i] = simplification[j];
			simplification[j] = a;
		}

		return new Tuple<>(simplification, Util.errorFromSimplification(simplification, l, distanceMeasurement));
	}
	
	@Override
	public String toString() {
		return "Random";
	}

}
