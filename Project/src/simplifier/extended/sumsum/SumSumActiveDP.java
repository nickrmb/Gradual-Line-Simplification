package simplifier.extended.sumsum;

import simplifier.DynamicProgramSimplifier;
import simplifier.extended.Merge;
import simplifier.extended.Removal;
import util.SC;
import util.SymmetricMatrix;
import util.Tuple;

public class SumSumActiveDP extends DynamicProgramSimplifier {

	private Merge merge;
	private Merge exact;

	private int length;
	private Removal[][] seq;
	private Removal[] cur = null;
	private double curError = Double.POSITIVE_INFINITY;
	private SymmetricMatrix fromK;

	public SumSumActiveDP(Merge m) {
		merge = m;
	}

	@Override
	public void onSetup(int length) {
		this.length = length;
		seq = new Removal[gauss(length)][];
		fromK = new SymmetricMatrix(length, 0);
	}

	@Override
	public void onMinFound(int i, int j, int minK) {
		if (cur == null) {
			Removal r = new Removal(minK, new SC(i, j), getShortcutDistance(i, j));
			Tuple<Double, Removal[]> merge = this.merge.merge(getRemoval(i, minK), getRemoval(minK, j), r);
			cur = merge.r;
			if (merge.r != null && merge.l != null) {
				updateError(i, j, merge.l);
			}
		}
		setRemoval(i, j, cur);
		fromK.setValue(i, j, minK);
		
		cur = null;
		curError = Double.POSITIVE_INFINITY;
	}

	@Override
	public double getError(int i, int j, int k, double error_ik, double error_kj, double shortcutDistance) {
		Removal r = new Removal(k, new SC(i, j), shortcutDistance);
		Tuple<Double, Removal[]> error = merge.getError(getRemoval(i, k), getRemoval(k, j), r, i, k, j, error_ik,
				error_kj);
//		System.out.println("---(" + i + "," + k + "," + j + ")---");
//		System.out.println("Input: " + fromRemovalToString(getRemoval(i, k)) + " ; " + fromRemovalToString(getRemoval(k, j)) + " ; " + r.getIdx());
//		System.out.println("Output: " + fromRemovalToString(error.r));
		if (error.l < curError) {
			cur = error.r;
			curError = error.l;
		}
		return error.l;
	}

	@Override
	public int[] backtrackRemovalSeq() {
		Removal[] removal = getRemoval(0, length - 1);
		if (removal == null) {
			exact = new SumSumMerge();
			removal = backtrack(new SC(0, length - 1));
			setRemoval(0, length - 1, removal);
		}
		int[] seq = new int[removal.length];
		for (int i = 0; i < seq.length; i++) {
			seq[i] = removal[i].getIdx();
		}
		return seq;
	}

	private Removal[] backtrack(SC sc) {
		if (sc.i + 1 >= sc.j) {
			return new Removal[0];
		}

		int k = (int) fromK.getValue(sc.i, sc.j);
		Removal r = new Removal(k, sc, getShortcutDistance(sc));
		Removal[] r1 = backtrack(new SC(sc.i, k));
		Removal[] r2 = backtrack(new SC(k, sc.j));
		return exact.merge(r1, r2, r).r;
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
	}

	@Override
	public String toString() {
		return "SumSumActiveDP-" + merge.toString();
	}

//	private String fromRemovalToString(Removal[] arr) {
//		String str = "[";
//		if (arr != null) {
//			for (int i = 0; i < arr.length; i++) {
//				str += arr[i].getIdx();
//				if(i != arr.length - 1) {
//					str += ", ";
//				}
//			}
//		}
//		str += "]";
//		return str;
//	}

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
