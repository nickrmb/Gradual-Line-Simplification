package simplifier;

import distance.DistanceMeasure;
import line.PolyLine;
import util.SC;
import util.SymmetricMatrix;
import util.Tuple;

public class MinMaxTotalSumSimplifier implements LineSimplifier {

	private SymmetricMatrix errorShortcut;
	private SymmetricMatrix fromK;
	private PolyLine l;
	private DistanceMeasure distance;

	@Override
	public Tuple<int[], double[]> simplify(PolyLine l, DistanceMeasure distance) {
		int n = l.length() - 2;

		this.errorShortcut = new SymmetricMatrix(l.length(), -1.0);
		SymmetricMatrix errorSum = new SymmetricMatrix(l.length(), 0);
		SymmetricMatrix errorMax = new SymmetricMatrix(l.length(), 0);
		fromK = new SymmetricMatrix(l.length(), -1);

		this.l = l;
		this.distance = distance;

		int[] simplification = new int[n];
		double[] error = new double[n];

		// iterate through all hop distances
		for (int hop = 2; hop < l.length(); hop++) {

			// iterate through all possible pair of vertices
			for (int i = 0; i < l.length() - hop; i++) {
				int j = i + hop;

				// get shortcut error
				double shortCutError = error(i, j);

				if (hop == 2) {
					fromK.setValue(i, j, i + 1);
					errorMax.setValue(i, j, shortCutError);
					errorSum.setValue(i, j, shortCutError);
					continue;
				}

				// get minimal k
				double minCost = Double.MAX_VALUE;
				double minMax = 0;
				int curK = -1;
				for (int k = i + 1; k < j; k++) {
					double max = Math.max(shortCutError, Math.max(errorMax.getValue(i, k), errorMax.getValue(k, j)));
					double dist = max + errorSum.getValue(i, k) + errorSum.getValue(k, j);
					if (dist < minCost) {
						minCost = dist;
						curK = k;
						minMax = max;
					}
				}

				errorMax.setValue(i, j, minMax);
				fromK.setValue(i, j, curK);
				errorSum.setValue(i, j, minCost);
			}
		}

		// Backtrack the minimal ordered MaxTotalSum path
		SC[] scs = momts(new SC(0, l.length() - 1));
		
		for(int i = 0; i < scs.length; i++) {
			simplification[i] = (int) fromK.getValue(scs[i].i, scs[i].j);
			error[i] = error(scs[i]);
		}
		
		return new Tuple<>(simplification, error);
	}

	private SC[] momts(SC sc) {
		SC[] scs = new SC[sc.j - sc.i - 1];
		if (sc.j - sc.i == 1)
			return scs;
		scs[scs.length - 1] = sc;

		// get k
		int k = (int) fromK.getValue(sc.i, sc.j);

		SC[] sc1 = momts(new SC(sc.i, k));
		SC[] sc2 = momts(new SC(k, sc.j));

		int cur1 = 0;
		int cur2 = 0;

		for (int i = 0; i < scs.length - 1; i++) {
			if (cur1 == sc1.length || (cur2 < sc2.length && error(sc2[cur2]) < error(sc1[cur1]))) {
				scs[i] = sc2[cur2++];
			} else {
				scs[i] = sc1[cur1++];
			}
		}

		return scs;
	}

	/**
	 * This method gets the shortcut error between two vertices, if shortcut is
	 * already calculated it is accessed in constant time
	 * 
	 * @param i        The vertex where the shortcut starts
	 * @param j        The vertex where the shortcut ends
	 * @param l        The PolyLine
	 * @param distance The distance measure used
	 * @return
	 */
	public double error(int i, int j) {
		// check valid
		int diff = i - j;
		if (diff >= -1 && diff <= 1) {
			return 0.0;
		}

		// check if calculated
		if (errorShortcut.getValue(i, j) == -1.0) {

			// calculate
			double dist = distance.measure(l, i, j);
			errorShortcut.setValue(i, j, dist);
		}

		return errorShortcut.getValue(i, j);
	}

	private double error(SC sc) {
		return error(sc.i, sc.j);
	}

	@Override
	public String toString() {
		return "MinMaxTotalSum";
	}

}