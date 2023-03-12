package simplifier;

import java.util.LinkedList;
import java.util.Queue;

import distance.DistanceMeasure;
import line.PolyLine;
import util.SymmetricMatrix;
import util.Tuple;

public class MinMaxSimplifier implements LineSimplifier{

	private SymmetricMatrix fromK;
	private SymmetricMatrix errorShortcut;
	private SymmetricMatrix errorMax;
	private int numPointsBetween;

	@Override
	public Tuple<int[], double[]> simplify(PolyLine l, DistanceMeasure distanceMeasure) {
		numPointsBetween = l.length() - 2;

		this.errorShortcut = new SymmetricMatrix(l.length(), -1.0);
		this.errorMax = new SymmetricMatrix(l.length(), 0);
		this.fromK = new SymmetricMatrix(l.length(), -1);

		int[] simplification = new int[numPointsBetween];
		double[] error = new double[numPointsBetween];

		// iterate through all hop distances
		for (int hop = 2; hop < l.length(); hop++) {

			// iterate through all possible pair of vertices
			for (int i = 0; i < l.length() - hop; i++) {
				int j = i + hop;

				// get shortcut error
				double shortCutError = getError(i, j, l, distanceMeasure);

				if (hop == 2) {
					fromK.setValue(i, j, i + 1);
					errorMax.setValue(i, j, shortCutError);
					continue;
				}

				// get minimal k
				double min = Double.MAX_VALUE;
				int curK = -1;
				for (int k = i + 1; k < j; k++) {
					double dist = Math.max(errorMax.getValue(i, k), errorMax.getValue(k, j));
					if (dist < min) {
						min = dist;
						curK = k;
					}
				}

				errorMax.setValue(i, j, Math.max(min, shortCutError));
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
			error[i] = getError(cur.l, cur.r, l, distanceMeasure);
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
	 * @param distanceMeasure The distance measure used
	 * @return
	 */
	public double getError(int i, int j, PolyLine l, DistanceMeasure distanceMeasure) {
		// check valid
		int diff = i - j;
		if (diff >= -1 && diff <= 1) {
			return 0.0;
		}

		// check if calculated
		if (errorShortcut.getValue(i, j) == -1.0) {
			
			// calculate
			double distance = distanceMeasure.distance(l, i, j);
			errorShortcut.setValue(i, j, distance);
		}

		return errorShortcut.getValue(i, j);
	}

	@Override
	public String toString() {
		return "MinMax";
	}
	
}
