package simplifier;

import distance.DistanceMeasure;
import line.PolyLine;
import util.SymmetricMatrix;
import util.Tuple;

public class MinMaxActiveSumSimplifier implements LineSimplifier {

	private SymmetricMatrix fromK;
	private SymmetricMatrix errorShortcut;
	private SymmetricMatrix errorSum;
	private int numPointsBetween;
	private PolyLine l;
	private DistanceMeasure distance;

	@Override
	public Tuple<int[], double[]> simplify(PolyLine l, DistanceMeasure distance) {
		numPointsBetween = l.length() - 2;
		
		this.l = l;
		this.distance = distance;

		this.errorShortcut = new SymmetricMatrix(l.length(), -1.0);
		this.errorSum = new SymmetricMatrix(l.length(), 0);
		this.fromK = new SymmetricMatrix(l.length(), -1);

		int[] simplification = new int[numPointsBetween];
		double[] error = new double[numPointsBetween];
		
		if(simplification.length == 0) return new Tuple<>(simplification, error);

		// iterate through all hop distances
		for (int hop = 2; hop < l.length(); hop++) {

			// iterate through all possible pair of vertices
			for (int i = 0; i < l.length() - hop; i++) {
				int j = i + hop;

				// get shortcut error
				double shortCutError = error(i, j);

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

		// Backtrack the minimal ordered MaxActiveSum path
		SC[] scs = momas(new SC(0, l.length() - 1));
		
		for(int i = 0; i < scs.length; i++) {
			simplification[i] = (int) fromK.getValue(scs[i].i, scs[i].j);
			error[i] = error(scs[i]);
		}

		// calculate corresponding error

		return new Tuple<>(simplification, error);
	}
	
	private SC[] momas(SC sc) {
		SC[] scs = new SC[sc.j - sc.i - 1];
		if(sc.j - sc.i == 1) return scs;
		scs[scs.length - 1] = sc;

		// get k
		int k = (int) fromK.getValue(sc.i, sc.j);

		SC[] sc1 = momas(new SC(sc.i, k));
		SC[] sc2 = momas(new SC(k, sc.j));
		
		int active1 = sc1.length - 1;
		int active2 = sc2.length - 1;
		
		for(int i = scs.length - 2; i >= 0; i--) {
			if(active1 < 0 || (active2 >= 0 && error(sc2[active2]) > error(sc1[active1]))) {
				scs[i] = sc2[active2--];
			} else {
				scs[i] = sc1[active1--];
			}
		}
		
		return scs;
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
	public double error(int i, int j) {
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
	
	private double error(SC sc) {
		return error(sc.i, sc.j);
	}
	
	@Override
	public String toString() {
		return "MinMaxActiveSum";
	}

}

class Tree {
	Tree l, r;
	SC sc;
	
}

class SC {
	int i, j;

	SC() {
	}

	SC(int i, int j) {
		this.i = i;
		this.j = j;
	}
}
