package simplifier;

import java.util.LinkedList;
import java.util.Queue;

import distance.DistanceMeasure;
import line.PolyLine;
import util.SymmetricMatrix;
import util.Tuple;

public class MinSumSimplifier implements LineSimplifier {

	private SymmetricMatrix fromK;
	private SymmetricMatrix errorShortcut;
	private SymmetricMatrix errorSum;
	private int numPointsBetween;

	@Override
	public Tuple<int[], double[]> simplify(PolyLine l, DistanceMeasure distance) {
		numPointsBetween = l.length() - 2;

		this.errorShortcut = new SymmetricMatrix(l.length(), -1.0);
		this.errorSum = new SymmetricMatrix(l.length(), 0);
		this.fromK = new SymmetricMatrix(l.length(), -1);

		int[] simplification = new int[numPointsBetween];
		double[] error = new double[numPointsBetween];

		// iterate through all hop distances
		for (int hop = 2; hop < l.length(); hop++) {

			// iterate through all possible pair of vertices
			for (int i = 0; i < l.length() - hop; i++) {
				int j = i + hop;

				// get shortcut error
				double shortCutError = getError(i, j, l, distance);

				if (hop == 2) {
					fromK.setValue(i, j, i + 1);
					errorSum.setValue(i, j, shortCutError);
					continue;
				}

				// get minimal k
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
				fromK.setValue(i, j, curK);
			}
		}

		// Backtrack the "path" in fromK
		Queue<Tuple<Integer, Integer>> fromTo = new LinkedList<>();

		@SuppressWarnings("unchecked")
		Tuple<Integer, Integer>[] simpl = new Tuple[simplification.length];

		// add last shortcut used
		fromTo.add(new Tuple<>(0, l.length() - 1));

		// backtrack
		for (int x = numPointsBetween - 1; x >= 0; x--) {
			Tuple<Integer, Integer> cur = fromTo.remove();

			// get k
			int k = (int) fromK.getValue(cur.l, cur.r);

			simplification[x] = k;
			simpl[x] = cur;

			// see if further shortcuts were used between
			if (k - cur.l > 1) {
				fromTo.add(new Tuple<>(cur.l, k));
			}
			if (cur.r - k > 1) {
				fromTo.add(new Tuple<>(k, cur.r));
			}
		}

		// calculate corresponding error

		for (int i = 0; i < numPointsBetween; i++) {
			Tuple<Integer, Integer> cur = simpl[i];
			error[i] = getError(cur.l, cur.r, l, distance);
		}

		return new Tuple<>(simplification, error);
	}

	/**
	 * This method gets the shortcut error between two vertices, if shortcut is
	 * already calculated it is accessed in constant time
	 * 
	 * @param i The vertex where the shortcut starts
	 * @param j The vertex where the shortcut ends
	 * @param l The PolyLine 
	 * @param distance The distance measure used
	 * @return
	 */
	public double getError(int i, int j, PolyLine l, DistanceMeasure distance) {
		// check valid
		int diff = i - j;
		if (diff >= -1 && diff <= 1) {
			return 0.0;
		}

		// check if calculated
		if (errorShortcut.getValue(i, j) == -1.0) {
			
			// calculate
			double error = distance.measure(l, i, j);
			errorShortcut.setValue(i, j, error);
		}

		return errorShortcut.getValue(i, j);
	}

	@Override
	public String toString() {
		return "MinSum";
	}

}
