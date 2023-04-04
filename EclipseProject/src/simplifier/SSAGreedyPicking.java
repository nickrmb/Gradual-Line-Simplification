package simplifier;

import distance.DistanceMeasure;
import line.PolyLine;
import util.GenericSymmetricMatrix;
import util.SC;
import util.SymmetricMatrix;
import util.Tuple;

public class SSAGreedyPicking implements LineSimplifier {

	private SymmetricMatrix errorShortcut;
	private int numPointsBetween;

	@Override
	public Tuple<int[], double[]> simplify(PolyLine l, DistanceMeasure distance) {
		numPointsBetween = l.length() - 2;

		this.errorShortcut = new SymmetricMatrix(l.length(), -1.0);
		GenericSymmetricMatrix scseq = new GenericSymmetricMatrix(l.length(), null);
		GenericSymmetricMatrix saeseq = new GenericSymmetricMatrix(l.length(), null);
		SymmetricMatrix fromK = new SymmetricMatrix(l.length(), 0);
		SymmetricMatrix ssa = new SymmetricMatrix(l.length(), 0);

		int[] simplification = new int[numPointsBetween];
		double[] error = new double[numPointsBetween];

		// iterate through all hop distances
		for (int hop = 1; hop < l.length(); hop++) {

//			System.out.println(hop);

			// iterate through all possible pair of vertices
			for (int i = 0; i < l.length() - hop; i++) {
				int j = i + hop;

				// get shortcut error
				double shortCutError = getError(i, j, l, distance);

				if (hop == 1) {
					scseq.setValue(i, j, new SC[] {});
					saeseq.setValue(i, j, new double[] {});
					continue;
				}

				if (hop == 2) {
					scseq.setValue(i, j, new SC[] { new SC(i, j) });
					saeseq.setValue(i, j, new double[] { shortCutError });
					ssa.setValue(i, j, shortCutError);
					fromK.setValue(i, j, i + 1);
					continue;
				}

				// get minimal k
				double min = Double.MAX_VALUE;
				int minK = -1;
				for (int k = i + 1; k < j; k++) {

					double distSum = meanEstimationFunction(ssa.getValue(i, k), ssa.getValue(k, j), i, k, j);
					if (distSum < min) {
						min = distSum;
						minK = k;
					}
				}

				SC[] scseq1 = (SC[]) scseq.getValue(i, minK);
				double[] saeseq1 = (double[]) saeseq.getValue(i, minK);
				SC[] scseq2 = (SC[]) scseq.getValue(minK, j);
				double[] saeseq2 = (double[]) saeseq.getValue(minK, j);
				Tuple<SC[], Tuple<Double, double[]>> mosas = mosas(scseq1, saeseq1, scseq2, saeseq2, i, j,
						shortCutError, l, distance);

//				System.out.println("Estimation: " + (min) + " ; Actual: " + (mosas.r.l - shortCutError));
				
				double[] active = mosas.r.r;

				scseq.setValue(i, j, mosas.l);
				saeseq.setValue(i, j, active);
				ssa.setValue(i, j, mosas.r.l);
				fromK.setValue(i, j, minK);

			}
		}

		SC[] sequence = (SC[]) scseq.getValue(0, l.length() - 1);

		// backtrack
		for (int i = 0; i < simplification.length; i++) {
			simplification[i] = (int) fromK.getValue(sequence[i].i, sequence[i].j);
			error[i] = getError(sequence[i].i, sequence[i].j, l, distance);
		}

		return new Tuple<>(simplification, error);
	}

	private Tuple<SC[], Tuple<Double, double[]>> mosas(SC[] scseq1, double[] saeseq1, SC[] scseq2, double[] saeseq2,
			int a, int b, double shortCutError, PolyLine l, DistanceMeasure distance) {

		SC[] scs = new SC[scseq1.length + scseq2.length + 1];
		double[] ae = new double[scs.length];

		double[][] dp = new double[saeseq1.length + 1][saeseq2.length + 1];

		double sum = 0;
		for (int i = 1; i <= saeseq1.length; i++) {
			sum += saeseq1[i - 1];
			dp[i][0] = sum;
		}
		sum = 0;
		for (int j = 1; j <= saeseq2.length; j++) {
			sum += saeseq2[j - 1];
			dp[0][j] = sum;
		}

		for (int i = 1; i <= saeseq1.length; i++) {
			for (int j = 1; j <= saeseq2.length; j++) {
				dp[i][j] = saeseq1[i - 1] + saeseq2[j - 1] + Math.min(dp[i - 1][j], dp[i][j - 1]);
			}
		}

		int i = saeseq1.length, j = saeseq2.length;
		double total = shortCutError;

		for (int x = scs.length - 2; x >= 0; x--) {
			total += (i > 0 ? saeseq1[i - 1] : 0) + (j > 0 ? saeseq2[j - 1] : 0);
			ae[x] = ((i > 0) ? saeseq1[i - 1] : 0) + ((j > 0) ? saeseq2[j - 1] : 0);
//			System.out.print(((i > 0) ? saeseq1[i - 1] : 0) + " " + ((j > 0) ? saeseq2[j - 1] : 0) + " ");
//			System.out.println(se[x]);

			double ci = (i > 0) ? dp[i - 1][j] : Double.POSITIVE_INFINITY;
			double cj = (j > 0) ? dp[i][j - 1] : Double.POSITIVE_INFINITY;

			if (ci < cj) {
				i--;
				scs[x] = scseq1[i];
			} else {
				j--;
				scs[x] = scseq2[j];
			}
		}

		scs[scs.length - 1] = new SC(a, b);
		ae[ae.length - 1] = shortCutError;

//		System.out.println();
//		System.out.println(a + " " + (scseq1.length == 0 ? a + 1 : scseq1[scseq1.length - 1].j) + " " + b);
//		System.out.print("\t|0");
//		for (int x = 0; x < saeseq1.length; x++) {
//			//System.out.print("\t" + round(saeseq1[x]));
//			System.out.print("\t" + round(saeseq1[x]));
//		}
//		System.out.print(
//				"\n-------------------------------------------------------------------------------------------------\n");
//		for (int x = 0; x <= saeseq2.length; x++) {
//			for (int y = 0; y <= saeseq1.length + 1; y++) {
//				if (y == 0) {
//					//System.out.print(round((x == 0) ? 0 : saeseq2[x - 1]) + "\t|");
//					System.out.print(((x == 0) ? 0 : round(saeseq2[x-1])) + "\t|");
//				} else {
//					System.out.print(round(dp[y - 1][x]) + "\t");
//				}
//			}
//			System.out.print("\n");
//		}
//		for(i = 0; i < ae.length; i++) {
//			System.out.print(round(ae[i]));
//			if(i != ae.length - 1)
//				System.out.print(", ");
//		}
//		System.out.println();
		//System.out.println("total: " + total);

		return new Tuple<>(scs, new Tuple<>(total, ae));
	}

	private double meanEstimationFunction(double cik, double ckj, double i, double k, double j) {
		return cik * (1 + (j - k - 1) / (k - i)) + ckj * (1 + (k - i - 1) / (j - k));
	}

//	private double round(double d) {
//		return Math.round(d * 100) / 100.0;
//	}

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
		return "SSAGreedyPicking";
	}

}