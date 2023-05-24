package simplifier.extended.sumsum;

import simplifier.extended.Merge;
import simplifier.extended.Removal;
import util.Tuple;

public class SumSumMergeGCE_GTD_EXACT  implements Merge {

	private SumSumMerge exact = new SumSumMerge();
	private SumSumMergeGTD gtd = new SumSumMergeGTD();

	@Override
	public Tuple<Double, Removal[]> merge(Removal[] seq1, Removal[] seq2, Removal r) {
		return exact.merge(seq1, seq2, r);
	}

	@Override
	public Tuple<Double, Removal[]> getError(Removal[] seq1, Removal[] seq2, Removal r, int i, int k, int j, double e1,
			double e2) {
		return new Tuple<>(gtd.merge(seq1, seq2, r).l, null);
	}

	@Override
	public String toString() {
		return "GCE_GTD_EXACT";
	}

}
