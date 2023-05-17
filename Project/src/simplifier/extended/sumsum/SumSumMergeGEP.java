package simplifier.extended.sumsum;

import simplifier.extended.Merge;
import simplifier.extended.Removal;
import util.Tuple;

public class SumSumMergeGEP implements Merge {

	private SumSumMerge exact = new SumSumMerge();

	@Override
	public Tuple<Double, Removal[]> merge(Removal[] seq1, Removal[] seq2, Removal r) {
		return exact.merge(seq1, seq2, r);
	}

	@Override
	public Tuple<Double, Removal[]> getError(Removal[] seq1, Removal[] seq2, Removal r, int i, int k, int j, double e1,
			double e2) {
		return new Tuple<>(e1 * (1 + (j - k - 1) / (k - i)) + e2 * (1 + (k - i - 1) / (j - k)), null);
	}

	@Override
	public String toString() {
		return "GEP";
	}

}
