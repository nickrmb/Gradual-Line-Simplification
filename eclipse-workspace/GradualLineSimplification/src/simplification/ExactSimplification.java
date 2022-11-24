package simplification;

import java.util.LinkedList;
import java.util.Queue;

import distance.DistanceMeasurement;
import line.PolyLine;
import util.SymmetricMatrix;
import util.Tuple;

public class ExactSimplification implements LineSimplifier {

	private SymmetricMatrix fromK;
	private SymmetricMatrix errorShortcut;
	private SymmetricMatrix errorSum;
	private int numPointsBetween;

	@Override
	public Tuple<int[], double[]> simplify(PolyLine l, DistanceMeasurement distanceMeasurement) {
		numPointsBetween = l.length() - 2;

		this.errorShortcut = new SymmetricMatrix(l.length(), -1.0);
		this.errorSum = new SymmetricMatrix(l.length(), 0);
		this.fromK = new SymmetricMatrix(l.length(), -1);

		int[] simplification = new int[numPointsBetween];
		double[] error = new double[numPointsBetween];

		for (int hop = 2; hop < l.length(); hop++) {
			System.out.println(hop);
			for (int i = 0; i < l.length() - hop; i++) {
				int j = i + hop;

				double shortCutError = getError(i, j, l, distanceMeasurement);

				if (hop == 2) {
					fromK.setValue(i, j, i + 1);
					// System.out.println(i + " " + j + " set to " + (i+1));
					errorSum.setValue(i, j, shortCutError);
					continue;
				}

				double min = Double.MAX_VALUE;
				int curK = -1;
				for (int k = i + 1; k < j; k++) {
					double distSum = errorSum.getValue(i, k) + errorSum.getValue(k, j);
					if (distSum < min) {
						min = distSum;
						curK = k;
					}
				}

				errorSum.setValue(i, j, min + shortCutError);
				// System.out.println(i + " " + j + " set to " + (curK));
				fromK.setValue(i, j, curK);
			}
		}

		Queue<Tuple<Integer, Integer>> fromTo = new LinkedList<>();

		@SuppressWarnings("unchecked")
		Tuple<Integer, Integer>[] simpl = new Tuple[simplification.length];

		fromTo.add(new Tuple<>(0, l.length() - 1));

		for (int x = numPointsBetween - 1; x >= 0; x--) {
			Tuple<Integer, Integer> cur = fromTo.remove();

			int k = (int) fromK.getValue(cur.l, cur.r);

			simplification[x] = k;
			simpl[x] = cur;

			if (k - cur.l > 1) {
				fromTo.add(new Tuple<>(cur.l, k));
			}
			if (cur.r - k > 1) {
				fromTo.add(new Tuple<>(k, cur.r));
			}
		}

		double err = 0.0;
		for (int i = 0; i < numPointsBetween; i++) {
			Tuple<Integer, Integer> cur = simpl[i];
			err += getError(cur.l, cur.r, l, distanceMeasurement);
			error[i] = err;
		}

		return new Tuple<>(simplification, error);
	}

	public double getError(int i, int j, PolyLine l, DistanceMeasurement distanceMeasurement) {
		int diff = i - j;
		if (diff >= -1 && diff <= 1) {
			return 0.0;
		}

		if (errorShortcut.getValue(i, j) == -1.0) {

			double distance = distanceMeasurement.distance(l, i, j);
			errorShortcut.setValue(i, j, distance);
		}

		return errorShortcut.getValue(i, j);
	}

}
