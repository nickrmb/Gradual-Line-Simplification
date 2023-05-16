package simplifier.extended.sumsum;

import simplifier.DynamicProgramSimplifier;
import simplifier.extended.Removal;
import util.SC;
import util.SymmetricMatrix;
import util.Tuple;

public class SumSumTotalDP extends DynamicProgramSimplifier {

	private SumSumMerge ssm = new SumSumMerge();

	private int length;
	private Removal[][] seq;
	private Removal[][] cur;
	private SymmetricMatrix totalSum;

	@Override
	public void onSetup(int length) {
		this.length = length;
		seq = new Removal[gauss(length)][];
		totalSum = new SymmetricMatrix(length, 0);
	}

	@Override
	public void onMinFound(int i, int j, int minK) {
		if(cur[minK] == null) {
			Removal r = new Removal(minK, new SC(i, j), totalSum.getValue(i, minK) + totalSum.getValue(minK, j) + getShortcutDistance(i, j));
			cur[minK] = ssm.merge(getRemoval(i, minK), getRemoval(minK, j), r).r;
		}
		setRemoval(i, j, cur[minK]);
		totalSum.setValue(i, j, totalSum.getValue(i, minK) + totalSum.getValue(minK, j) + getShortcutDistance(i, j));
	}

	@Override
	public double getError(int i, int j, int k, double error_ik, double error_kj, double shortcutDistance) {
		Removal r = new Removal(k, new SC(i, j), totalSum.getValue(i, k) + totalSum.getValue(k, j) + shortcutDistance);
		Tuple<Double, Removal[]> error = ssm.getError(getRemoval(i, k), getRemoval(k, j), r);
		cur[k] = error.r;
		return error.l;
	}

	@Override
	public int[] backtrackRemovalSeq() {
		Removal[] removal = getRemoval(0, length - 1);
		int[] seq = new int[removal.length];
		for (int i = 0; i < seq.length; i++) {
			seq[i] = removal[i].getIdx();
		}
		return seq;
	}

	@Override
	public SC[] backtrackShortcutSeq() {
		Removal[] removal = getRemoval(0, length - 1);
		SC[] seq = new SC[removal.length];
		for (int i = 0; i < seq.length; i++) {
			seq[i] = removal[i].getShortcut();
		}
		return seq;
	}

	@Override
	public void onNewHop() {
		cur = new Removal[length][];
	}

	@Override
	public String toString() {
		return "SumSumTotalDP";
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Gets value at some 2d index
	 * 
	 * @param i
	 * @param j
	 * @return
	 */
	public Removal[] getRemoval(int i, int j) {
		return seq[indexOf(i, j)];
	}

	/**
	 * Sets the value at some 2d index
	 * 
	 * @param i
	 * @param j
	 * @param value
	 */
	public void setRemoval(int i, int j, Removal[] value) {
		seq[indexOf(i, j)] = value;
	}

	/**
	 * Projects the 2d index to a 1d index
	 * 
	 * @param i
	 * @param j
	 * @return
	 */
	private int indexOf(int i, int j) {
		if (i > j) {
			int a = i;
			i = j;
			j = a;
		}

		int index = seq.length - gauss(length - i) + j - i;

		return index;
	}

	/**
	 * Gauss sum formula
	 * 
	 * @param n
	 * @return
	 */
	private int gauss(int n) {
		return n * (n + 1) / 2;
	}
}