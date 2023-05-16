package simplifier.simple;

import java.util.LinkedList;
import java.util.Queue;

import simplifier.DynamicProgramSimplifier;
import util.SC;
import util.SymmetricMatrix;

public abstract class SimpleDP extends DynamicProgramSimplifier{

	private SymmetricMatrix fromK;
	private SC[] scSeq;
	
	@Override
	public void onSetup(int length) {
		fromK = new SymmetricMatrix(length, 0);
	}

	@Override
	public void onMinFound(int i, int j, int minK) {
		fromK.setValue(i, j, minK);
	}

	@Override
	public int[] backtrackRemovalSeq() {
		int[] removalSeq = new int[numPointsBetween];
		
		// Backtrack the "path" in fromK
		Queue<SC> fromTo = new LinkedList<>();

		// shortcut Seq
		scSeq = new SC[numPointsBetween];

		// add last shortcut used
		fromTo.add(new SC(0, numPointsBetween + 1));

		// backtrack
		for (int x = numPointsBetween - 1; x >= 0; x--) {
			SC cur = fromTo.remove();

			// get k
			int k = (int) fromK.getValue(cur.i, cur.j);
			removalSeq[x] = k;
			scSeq[x] = cur;

			// see if further shortcuts were used between
			if (k - cur.i > 1) {
				fromTo.add(new SC(cur.i, k));
			}
			if (cur.j - k > 1) {
				fromTo.add(new SC(k, cur.j));
			}
		}
		return removalSeq;
	}

	@Override
	public SC[] backtrackShortcutSeq() {
		return scSeq;
	}
	
	@Override
	public void onNewHop() {
	}

}
