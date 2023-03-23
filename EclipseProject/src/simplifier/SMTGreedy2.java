package simplifier;

import distance.DistanceMeasure;
import line.PolyLine;
import util.GenericSymmetricMatrix;
import util.SC;
import util.SymmetricMatrix;
import util.Tuple;

public class SMTGreedy2 implements LineSimplifier {

	private SymmetricMatrix errorShortcut;
	private PolyLine l;
	private DistanceMeasure distance;

	@Override
	public Tuple<int[], double[]> simplify(PolyLine l, DistanceMeasure distance) {
		int n = l.length() - 2;

		this.errorShortcut = new SymmetricMatrix(l.length(), -1.0);
		SymmetricMatrix errorSum = new SymmetricMatrix(l.length(), 0);
		SymmetricMatrix fromK = new SymmetricMatrix(l.length(), -1);
		GenericSymmetricMatrix mseq = new GenericSymmetricMatrix(l.length(), null);

		this.l = l;
		this.distance = distance;

		int[] simplification = new int[n];
		double[] error = new double[n];

		// iterate through all hop distances
		for (int hop = 1; hop < l.length(); hop++) {
			
			//System.out.println(hop);

			// iterate through all possible pair of vertices
			for (int i = 0; i < l.length() - hop; i++) {
				int j = i + hop;

				// get shortcut error
				double shortCutError = error(i, j);

				if (hop == 1) {
					errorSum.setValue(i, j, 0);
					mseq.setValue(i, j, new SC[] {});
					continue;
				}

				if (hop == 2) {
					fromK.setValue(i, j, i + 1);
					errorSum.setValue(i, j, shortCutError);
					mseq.setValue(i, j, new SC[] { new SC(i, j) });
					continue;
				}

				// get minimal k
				double minCost = Double.MAX_VALUE;
				SC[] minSeq = null;
				int curK = -1;
				for (int k = i + 1; k < j; k++) {
					Tuple<SC[], Double> momts = momts(new SC(i, j), (SC[]) mseq.getValue(i, k),
							(SC[]) mseq.getValue(k, j));
					if (momts.r < minCost) {
						minCost = momts.r;
						curK = k;
						minSeq = momts.l;
					}
				}

				fromK.setValue(i, j, curK);
				errorSum.setValue(i, j, minCost);
				mseq.setValue(i, j, minSeq);
			}
		}

		// Backtrack the minimal ordered MaxTotalSum path
		SC[] scs = (SC[]) mseq.getValue(0, l.length() - 1);

		for (int i = 0; i < scs.length; i++) {
			simplification[i] = (int) fromK.getValue(scs[i].i, scs[i].j);
			error[i] = error(scs[i]);
		}
		//System.out.println(errorSum.getValue(0, l.length()-1));

		return new Tuple<>(simplification, error);
	}

	private Tuple<SC[], Double> momts(SC sc, SC[] mseq1, SC[] mseq2) {
		SC[] scs = new SC[mseq1.length + mseq2.length + 1];

		double max = 0;
		double err = 0;

		int cur1 = 0;
		int cur2 = 0;

		double err1 = (cur1 < mseq1.length) ? error(mseq1[cur1]) : Double.POSITIVE_INFINITY;
		double err2 = (cur2 < mseq2.length) ? error(mseq2[cur2]) : Double.POSITIVE_INFINITY;

		for (int i = 0; i < scs.length - 1; i++) {
			if (err1 < err2) {
				scs[i] = mseq1[cur1++];
				max = Math.max(err1, max);
				err1 = (cur1 < mseq1.length) ? error(mseq1[cur1]) : Double.POSITIVE_INFINITY;
			} else {
				scs[i] = mseq2[cur2++];
				max = Math.max(err2, max);
				err2 = (cur2 < mseq2.length) ? error(mseq2[cur2]) : Double.POSITIVE_INFINITY;
			}
			err += max;
		}

		scs[scs.length - 1] = sc;
		max = Math.max(error(sc), max);
		err += max;

		return new Tuple<>(scs, err);
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
		return "SMTGreedy2";
	}

}