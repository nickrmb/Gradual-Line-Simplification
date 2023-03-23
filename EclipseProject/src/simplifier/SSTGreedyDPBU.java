package simplifier;

import distance.DistanceMeasure;
import line.PolyLine;
import util.GenericSymmetricMatrix;
import util.SC;
import util.SymmetricMatrix;
import util.Tuple;

public class SSTGreedyDPBU implements LineSimplifier {

	private SymmetricMatrix errorShortcut;
	private int numPointsBetween;

	@Override
	public Tuple<int[], double[]> simplify(PolyLine l, DistanceMeasure distance) {
		numPointsBetween = l.length() - 2;

		this.errorShortcut = new SymmetricMatrix(l.length(), -1.0);
		GenericSymmetricMatrix scseq = new GenericSymmetricMatrix(l.length(), null);
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
					continue;
				}

				if (hop == 2) {
					scseq.setValue(i, j, new SC[] { new SC(i, j) });
					fromK.setValue(i, j, i + 1);
					continue;
				}

				// get minimal k
				double min = Double.MAX_VALUE;
				SC[] opt = null;
				int minK = -1;
				for (int k = i + 1; k < j; k++) {
					SC[] scseq1 = (SC[]) scseq.getValue(i, k);
					SC[] scseq2 = (SC[]) scseq.getValue(k, j);
					Tuple<SC[], Double> mosts = mostsGreedy(scseq1, scseq2, i, j, shortCutError, l, distance);

					double distSum = mosts.r;
					if (distSum < min) {
						min = distSum;
						minK = k;
						opt = mosts.l;
					}
				}

//				for (int x = 0; x < opt.l.length; x++) {
//					System.out.println("\t" + opt.l[x].i + " " + opt.l[x].j);
//				}

				scseq.setValue(i, j, opt);
				fromK.setValue(i, j, minK);

			}
		}

		SC[] sequence = (SC[]) scseq.getValue(0, l.length() - 1);

		// backtrack
		for (int i = 0; i < simplification.length; i++) {
			// System.out.println(sequence[i].i + " " + sequence[i].j);
			simplification[i] = (int) fromK.getValue(sequence[i].i, sequence[i].j);
			error[i] = getError(sequence[i].i, sequence[i].j, l, distance);
		}

		return new Tuple<>(simplification, error);
	}

	private Tuple<SC[], Double> mostsGreedy(SC[] scseq1, SC[] scseq2, int a, int b, double shortCutError, PolyLine l, DistanceMeasure distance) {

		SC[] scs = new SC[scseq1.length + scseq2.length + 1];

		double[] seseq1 = new double[scseq1.length];
		double[] seseq2 = new double[scseq2.length];
		
		double sum = 0;
		for(int i = 0; i < seseq1.length; i++) {
			sum += getError(scseq1[i].i, scseq1[i].j, l, distance);
			seseq1[i] = sum;
		}
		sum = 0;
		for(int i = 0; i < seseq2.length; i++) {
			sum += getError(scseq2[i].i, scseq2[i].j, l, distance);
			seseq2[i] = sum;
		}
		
		int i = -1, j = -1;
		double cur = 0;
		sum = 0;
		
		for (int x = 0; x < scs.length - 1; x++) {

			if (i == seseq1.length - 1) {
				j++;
				scs[x] = scseq2[j];
			} else if (j == seseq2.length - 1) {
				i++;
				scs[x] = scseq1[i];
			} else {
				double ci = seseq1[i + 1] + ((j == -1) ? 0 : seseq2[j]);
				double cj = seseq2[j + 1] + ((i == -1) ? 0 : seseq1[i]);

				if (ci < cj) {
					i++;
					scs[x] = scseq1[i];
				} else {
					j++;
					scs[x] = scseq2[j];
				}
			}
			
			cur += ((i == -1) ? 0 : seseq1[i]) + ((j == -1) ? 0 : seseq2[j]);
			sum += cur;
		}

		scs[scs.length - 1] = new SC(a, b);

		return new Tuple<>(scs, sum);
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
		return "SSTGreedyDPBU";
	}

}
