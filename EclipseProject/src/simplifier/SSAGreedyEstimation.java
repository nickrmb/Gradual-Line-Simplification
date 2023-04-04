package simplifier;

import distance.DistanceMeasure;
import line.PolyLine;
import util.SC;
import util.SymmetricMatrix;
import util.Tuple;

public class SSAGreedyEstimation implements LineSimplifier {

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

		if (simplification.length == 0)
			return new Tuple<>(simplification, error);

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
				int minK = -1;
				for (int k = i + 1; k < j; k++) {
					double cik = errorSum.getValue(i, k);
					double ckj = errorSum.getValue(k, j);
					double distSum = meanEstimationFunction(cik, ckj, i, k, j);
					if (distSum < min) {
						min = distSum;
						minK = k;
					}
				}

				errorSum.setValue(i, j, min + shortCutError);
				fromK.setValue(i, j, minK);
			}
		}

		// Backtrack the minimal ordered SumTotalSum path
		Tuple<SC[], double[]> mosas = mosas(new SC(0, l.length() - 1));

		SC[] scs = mosas.l;

		for (int i = 0; i < scs.length; i++) {
			simplification[i] = (int) fromK.getValue(scs[i].i, scs[i].j);
			error[i] = error(scs[i]);
		}

		return new Tuple<>(simplification, error);
	}

	private Tuple<SC[], double[]> mosas(SC sc) {
		SC[] scs = new SC[sc.j - sc.i - 1];
		double[] sae = new double[scs.length];

		if (scs.length == 0)
			return new Tuple<>(scs, sae);

		scs[scs.length - 1] = sc;
		sae[sae.length - 1] = error(sc);

		// get k
		int k = (int) fromK.getValue(sc.i, sc.j);

		Tuple<SC[], double[]> mosas1 = mosas(new SC(sc.i, k));
		Tuple<SC[], double[]> mosas2 = mosas(new SC(k, sc.j));

		SC[] sc1 = mosas1.l;
		SC[] sc2 = mosas2.l;

		double[] seseq1 = mosas1.r;
		double[] seseq2 = mosas2.r;

		double[][] dp = new double[seseq1.length + 1][seseq2.length + 1];

		double sum = 0;
		for (int i = 1; i <= seseq1.length; i++) {
			sum += seseq1[i - 1];
			dp[i][0] = sum;
		}
		sum = 0;
		for (int j = 1; j <= seseq2.length; j++) {
			sum += seseq2[j - 1];
			dp[0][j] = sum;
		}

		for (int i = 1; i <= seseq1.length; i++) {
			for (int j = 1; j <= seseq2.length; j++) {
				dp[i][j] = seseq1[i - 1] + seseq2[j - 1] + Math.min(dp[i - 1][j], dp[i][j - 1]);
			}
		}

		int i = seseq1.length, j = seseq2.length;

		for (int x = scs.length - 2; x >= 0; x--) {
			sae[x] = ((i > 0) ? seseq1[i - 1] : 0) + ((j > 0) ? seseq2[j - 1] : 0);

			double ci = (i > 0) ? dp[i - 1][j] : Double.POSITIVE_INFINITY;
			double cj = (j > 0) ? dp[i][j - 1] : Double.POSITIVE_INFINITY;

			if (ci < cj) {
				i--;
				scs[x] = sc1[i];
			} else {
				j--;
				scs[x] = sc2[j];
			}
		}

		return new Tuple<>(scs, sae);
	}

	private double meanEstimationFunction(double cik, double ckj, double i, double k, double j) {
		return cik * (1 + (j - k - 1) / (k - i)) + ckj * (1 + (k - i - 1) / (j - k));
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
		return "SSAGreedyEstimation";
	}
}