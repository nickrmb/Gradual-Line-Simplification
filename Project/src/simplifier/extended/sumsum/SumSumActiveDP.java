package simplifier.extended.sumsum;

import simplifier.DynamicProgramSimplifier;
import simplifier.extended.Removal;
import util.SC;
import util.Tuple;

public class SumSumActiveDP extends DynamicProgramSimplifier {

	private SumSumMerge ssm = new SumSumMerge();
	
	private int length;
	private Removal[][] seq;
	private Removal[][] cur;
	
	@Override
	public void onSetup(int length) {
		this.length = length;
		seq = new Removal[gauss(length)][];
	}

	@Override
	public void onMinFound(int i, int j, int minK) {
		setRemoval(i, j, cur[minK]);
	}

	@Override
	public double getError(int i, int j, int k, double error_ik, double error_kj, double shortcutDistance) {
		Removal r = new Removal(k, new SC(i,j), shortcutDistance);
		Tuple<Double,Removal[]> merged = ssm.merge(getRemoval(i, k), getRemoval(k, j), r);
		cur[k] = merged.r;
		return merged.l;
	}

	@Override
	public int[] backtrackRemovalSeq() {
		Removal[] removal = getRemoval(0, length - 1);
		int[] seq = new int[removal.length];
		for(int i = 0; i < seq.length; i++) {
			seq[i] = removal[i].getIdx();
		}
		return seq;
	}

	@Override
	public SC[] backtrackShortcutSeq() {
		Removal[] removal = getRemoval(0, length - 1);
		SC[] seq = new SC[removal.length];
		for(int i = 0; i < seq.length; i++) {
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
		return "SumSumActiveDP";
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
