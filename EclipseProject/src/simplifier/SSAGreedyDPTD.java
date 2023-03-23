package simplifier;

import distance.DistanceMeasure;
import line.PolyLine;
import util.GenericSymmetricMatrix;
import util.SC;
import util.SymmetricMatrix;
import util.Tuple;

public class SSAGreedyDPTD implements LineSimplifier {

	private SymmetricMatrix errorShortcut;
	private int numPointsBetween;

	@Override
	public Tuple<int[], double[]> simplify(PolyLine l, DistanceMeasure distance) {
		numPointsBetween = l.length() - 2;

		this.errorShortcut = new SymmetricMatrix(l.length(), -1.0);
		GenericSymmetricMatrix scseq = new GenericSymmetricMatrix(l.length(), null);
		GenericSymmetricMatrix saeseq = new GenericSymmetricMatrix(l.length(), null);
		SymmetricMatrix fromK = new SymmetricMatrix(l.length(), 0);

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
					fromK.setValue(i, j, i + 1);
					continue;
				}

				// get minimal k
				double min = Double.MAX_VALUE;
				Tuple<SC[], Tuple<Double, double[]>> opt = null;
				int minK = -1;
				for (int k = i + 1; k < j; k++) {
					SC[] scseq1 = (SC[]) scseq.getValue(i, k);
					double[] saeseq1 = (double[]) saeseq.getValue(i, k);
					SC[] scseq2 = (SC[]) scseq.getValue(k, j);
					double[] saeseq2 = (double[]) saeseq.getValue(k, j);

					Tuple<SC[], Tuple<Double, double[]>> mosas = mosasGreedyTD(scseq1, saeseq1, scseq2, saeseq2, i, j,
							shortCutError, l, distance);

					double distSum = mosas.r.l;
					if (distSum < min) {
						min = distSum;
						minK = k;
						opt = mosas;
					}
				}

				double[] active = opt.r.r;

				scseq.setValue(i, j, opt.l);
				saeseq.setValue(i, j, active);
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

	private Tuple<SC[], Tuple<Double, double[]>> mosasGreedyTD(SC[] scseq1, double[] saeseq1, SC[] scseq2,
			double[] saeseq2, int a, int b, double shortCutError, PolyLine l, DistanceMeasure distance) {

		SC[] scs = new SC[scseq1.length + scseq2.length + 1];
		double[] ae = new double[scs.length];

		int i = saeseq1.length - 1, j = saeseq2.length - 1;

		double sum = shortCutError;
		for (int x = scs.length - 2; x >= 0; x--) {
			ae[x] = (i >= 0 ? saeseq1[i] : 0) + (j >= 0 ? saeseq2[j] : 0);
			sum += ae[x];
			if (i == -1) {
				scs[x] = scseq2[j];
				j--;
				continue;
			}
			if (j == -1) {
				scs[x] = scseq1[i];
				i--;
				continue;
			}

			double ci = saeseq1[i];
//			double cbi = (i == 0) ? 0 : saeseq1[i - 1];
			double cj = saeseq2[j];
//			double cbj = (j == 0) ? 0 : saeseq2[j - 1];

//			double c1 = cbi + cj;
//			double c2 = cbj + ci;

//			if (c1 < c2) {
			if (ci > cj) {
				scs[x] = scseq1[i];
				i--;
			} else {
				scs[x] = scseq2[j];
				j--;
			}

		}

		scs[scs.length - 1] = new SC(a, b);
		ae[ae.length - 1] = shortCutError;

		return new Tuple<>(scs, new Tuple<>(sum, ae));
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
		return "SSAGreedyDPTD";
	}

}