package simplifier;

import distance.DistanceMeasure;
import line.PolyLine;
import util.GenericSymmetricMatrix;
import util.SC;
import util.SymmetricMatrix;
import util.Tuple;

public class SSAGreedyDPBU implements LineSimplifier {

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

					Tuple<SC[], Tuple<Double, double[]>> mosas = mosasGreedyBU(scseq1, saeseq1, scseq2, saeseq2, i, j,
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

	private Tuple<SC[], Tuple<Double, double[]>> mosasGreedyBU(SC[] scseq1, double[] saeseq1, SC[] scseq2,
			double[] saeseq2, int a, int b, double shortCutError, PolyLine l, DistanceMeasure distance) {

		SC[] scs = new SC[scseq1.length + scseq2.length + 1];
		double[] ae = new double[scs.length];

		int i = -1, j = -1;

		double sum = 0;
		for (int x = 0; x < scs.length - 1; x++) {

			if (i == saeseq1.length - 1) {
				j++;
				scs[x] = scseq2[j];
			} else if (j == saeseq2.length - 1) {
				i++;
				scs[x] = scseq1[i];
			} else {
				double ci = saeseq1[i + 1] + ((j == -1) ? 0 : saeseq2[j]);
				double cj = saeseq2[j + 1] + ((i == -1) ? 0 : saeseq1[i]);

				if (ci < cj) {
					i++;
					scs[x] = scseq1[i];
				} else {
					j++;
					scs[x] = scseq2[j];
				}
			}

			ae[x] = ((i == -1) ? 0 : saeseq1[i]) + ((j == -1) ? 0 : saeseq2[j]);
			sum += ae[x];
		}
		
		sum += shortCutError;
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
		return "SSAGreedyDPBU";
	}

}