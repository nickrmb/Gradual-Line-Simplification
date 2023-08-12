package simplifier.extended.sumsum;

import simplifier.extended.Merge;
import simplifier.extended.Removal;
import util.Tuple;
import util.Util;

public class SumSumMergeGCE_EST_GBU implements Merge {

	private SumSumMergeGBU gbu = new SumSumMergeGBU();

	@Override
	public Tuple<Double, Removal[]> merge(Removal[] seq1, Removal[] seq2, Removal r) {
		return gbu.merge(seq1, seq2, r);
	}

	@Override
	public Tuple<Double, Removal[]> getError(Removal[] seq1, Removal[] seq2, Removal r, int i, int k, int j, double e1,
			double e2) {
		return new Tuple<>(Util.sumSumMergeEstimation(i, k, j, e1, e2) + r.getError(), null);
	}

	@Override
	public String toString() {
		return "GCE_EST_GBU";
	}

}