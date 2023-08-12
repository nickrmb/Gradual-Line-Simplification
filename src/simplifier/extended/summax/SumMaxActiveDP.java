package simplifier.extended.summax;

import simplifier.DynamicProgramSimplifier;
import simplifier.extended.Removal;
import util.SC;
import util.SymmetricMatrix;

public class SumMaxActiveDP extends DynamicProgramSimplifier{

	private SymmetricMatrix fromK;
	private SumMaxMerge smm = new SumMaxMerge();
	private Removal[] result;
	
	@Override
	public void onSetup(int length) {
		fromK = new SymmetricMatrix(length, 0);
	}

	@Override
	public void onMinFound(int i, int j, int minK) {
		fromK.setValue(i, j, minK);
	}

	@Override
	public double getError(int i, int j, int k, double error_ik, double error_kj, double shortcutDistance) {
		return error_ik + error_kj + shortcutDistance;
	}

	@Override
	public int[] backtrackRemovalSeq() {
		result = backtrack(new SC(0, numPointsBetween + 1));
		int[] removals = new int[result.length];
		for(int i = 0; i < removals.length; i++) {
			removals[i] = result[i].getIdx();
		}
		return removals;
	}
	
	private Removal[] backtrack(SC sc) {
		if(sc.i + 1 >= sc.j) {
			return new Removal[0];
		}
		
		int k = (int) fromK.getValue(sc.i, sc.j);
		Removal r = new Removal(k, sc, getShortcutDistance(sc));
		Removal[] r1 = backtrack(new SC(sc.i,k));
		Removal[] r2 = backtrack(new SC(k,sc.j));
		return smm.merge(r1, r2, r).r;
	}

	@Override
	public SC[] backtrackShortcutSeq() {
		SC[] shortcuts = new SC[result.length];
		for(int i = 0; i < shortcuts.length; i++) {
			shortcuts[i] = result[i].getShortcut();
		}
		return shortcuts;
	}

	@Override
	public void onNewHop() {
	}
	
	@Override
	public String toString() {
		return "MinSMA";
	}

}
