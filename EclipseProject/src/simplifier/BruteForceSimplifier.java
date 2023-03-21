package simplifier;

import java.util.Arrays;

import distance.DistanceMeasure;
import function.OptimizationFunction;
import function.Sum;
import function.SumMaxActive;
import function.SumMaxTotal;
import function.SumSumActive;
import function.SumSumTotal;
import line.PolyLine;
import util.Tuple;
import util.Util;

public class BruteForceSimplifier implements LineSimplifier {

	private OptimizationFunction function = new SumSumActive();
	private PolyLine l;
	private DistanceMeasure distance;

	@Override
	public Tuple<int[], double[]> simplify(PolyLine l, DistanceMeasure distance) {
		this.l = l;
		this.distance = distance;
		int[] permutations = new int[l.length() - 2];
		for (int i = 0; i < permutations.length; i++) {
			permutations[i] = i + 1;
		}
		int[] simplification = recursive(permutations.length, permutations).l;
		return new Tuple<>(simplification, Util.errorFromSimplification(simplification, l, distance));
	}

	private Tuple<int[], Double> recursive(int n, int[] array) {
		if (n == 1) {
			double[] error = function.measure(array, Util.errorFromSimplification(array, l, distance));
			return new Tuple<>(Arrays.copyOf(array, array.length), error[error.length - 1]);
		} else {
			Tuple<int[], Double> min = null;
			for (int i = 0; i < n - 1; i++) {
				Tuple<int[], Double> rec = recursive(n - 1, array);
				min = (min == null) ? rec : ((rec.r < min.r) ? rec : min);
				if (n % 2 == 0) {
					swap(array, i, n - 1);
				} else {
					swap(array, 0, n - 1);
				}
			}
			Tuple<int[], Double> rec = recursive(n - 1, array);
			min = (min == null) ? rec : ((rec.r < min.r) ? rec : min);
			return min;
		}
	}

	@Override
	public String toString() {
		return "Bruteforce";
	}

	private static void swap(int[] array, int a, int b) {
		int tmp = array[a];
		array[a] = array[b];
		array[b] = tmp;
	}

}
