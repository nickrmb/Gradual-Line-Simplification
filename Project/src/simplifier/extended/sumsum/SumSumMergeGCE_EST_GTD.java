package simplifier.extended.sumsum;

import simplifier.extended.Merge;
import simplifier.extended.Removal;
import util.Tuple;
import util.Util;

public class SumSumMergeGCE_EST_GTD implements Merge {

	private SumSumMergeGTD gtd = new SumSumMergeGTD();

	@Override
	public Tuple<Double, Removal[]> merge(Removal[] seq1, Removal[] seq2, Removal r) {
		return gtd.merge(seq1, seq2, r);
	}

	@Override
	public Tuple<Double, Removal[]> getError(Removal[] seq1, Removal[] seq2, Removal r, int i, int k, int j, double e1,
			double e2) {
//		double err = gtd.merge(seq1, seq2, r).l / (Util.sumSumMergeEstimation5(i, k, j, e1, e2) + r.getError());
//		if (!Double.isInfinite(err) && err != 1) {
//			System.out.println(gtd.merge(seq1, seq2, r).l/ (Util.sumSumMergeEstimation3(i, k, j, e1, e2) + r.getError()) + " " + err);
//			System.out.print(err + " " + e1 + " " + e2  + " -> seq1: ");
//			if (seq1 == null) {
//				System.out.print("null ");
//			} else {
//				for (Removal re : seq1) {
//					System.out.print(re.getError() + " ");
//				}
//			}
//			System.out.print("; seq2: ");
//			if (seq2 == null) {
//				System.out.print("null ");
//			} else {
//				for (Removal re : seq2) {
//					System.out.print(re.getError() + " ");
//				}
//			}
//			System.out.println();
//		}
		return new Tuple<>(Util.sumSumMergeEstimation(i, k, j, e1, e2) + r.getError(), null);
	}

	@Override
	public String toString() {
		return "GCE_EST_GTD";
	}

}
