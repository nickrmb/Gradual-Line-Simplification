package simplifier.extended.summax;

import simplifier.extended.Removal;
import util.Tuple;
import simplifier.extended.Merge;

public class SumMaxMerge implements Merge {
	
	@Override
	public Tuple<Double,Removal[]> merge(Removal[] seq1, Removal[] seq2, Removal r) {
		Removal[] r1 = seq1 == null ? new Removal[] {} : seq1;
		Removal[] r2 = seq2 == null ? new Removal[] {} : seq2;

		Removal[] seq = new Removal[r1.length + r2.length + 1];
		seq[seq.length - 1] = r;

		int i = r1.length - 1, j = r2.length - 1;
		int idx = seq.length - 2;

		while (i >= 0 || j >= 0) {
			double ci = i < 0 ? Double.NEGATIVE_INFINITY : r1[i].getError();
			double cj = j < 0 ? Double.NEGATIVE_INFINITY : r2[j].getError();
			if (ci >= cj) {
				seq[idx--] = r1[i--];
			} else {
				seq[idx--] = r2[j--];
			}
		}
		return new Tuple<>(null,seq);
	}

	@Override
	public Tuple<Double, Removal[]> getError(Removal[] seq1, Removal[] seq2, Removal r) {
		return null;
	}

}
