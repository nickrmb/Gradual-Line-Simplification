package simplifier;

import java.util.Random;

import distance.DistanceMeasure;
import line.PolyLine;
import util.Tuple;

public class Rnd implements LineSimplifier {

	@Override
	public Tuple<int[], double[]> simplify(PolyLine l, DistanceMeasure distance) {
		int length = l.length();
		int numPointsBetween = length - 2;

		int[] simplification = new int[numPointsBetween];

		// create in order
		for (int i = 0; i < numPointsBetween; i++) {
			simplification[i] = i + 1;
		}

		// random permutation (fisher-yates shuffle)
		Random random = new Random();
		for (int i = numPointsBetween - 1; i > 0; i--) {
			int j = random.nextInt(i + 1);

			int a = simplification[i];
			simplification[i] = simplification[j];
			simplification[j] = a;
		}

		return new Tuple<>(simplification, null);
	}
	
	@Override
	public String toString() {
		return "Random";
	}

}
