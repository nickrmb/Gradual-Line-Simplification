package simplifier.extended.sumsum;

import simplifier.extended.Merge;
import simplifier.extended.Removal;
import util.Tuple;

public class SumSumMergeGTD implements Merge {

	@Override
	public Tuple<Double, Removal[]> merge(Removal[] seq1, Removal[] seq2, Removal r) {

		Removal[] r1 = seq1 == null ? new Removal[] {} : seq1;
		Removal[] r2 = seq2 == null ? new Removal[] {} : seq2;

		Removal[] removal = new Removal[r1.length + r2.length + 1];

		int i = r1.length - 1, j = r2.length - 1;

		double error = 0;

		for (int x = removal.length - 2; x >= 0; x--) {
			double ci = (i >= 0) ? r1[i].getError() : 0;
			double cj = (j >= 0) ? r2[j].getError() : 0;

			error += ci + cj;

			if (ci >= cj) {
				Removal cur = r1[i];
				removal[x] = new Removal(cur.getIdx(), cur.getShortcut(), ci + cj);
				i--;
			} else {
				Removal cur = r2[j];
				removal[x] = new Removal(cur.getIdx(), cur.getShortcut(), ci + cj);
				j--;
			}
		}

		removal[removal.length - 1] = r;
		error += r.getError();

		return new Tuple<>(error, removal);
	}

	@Override
	public Tuple<Double, Removal[]> getError(Removal[] seq1, Removal[] seq2, Removal r, int i, int k, int j, double e1,
			double e2) {
		return merge(seq1, seq2, r);
	}

	@Override
	public String toString() {
		return "GTD";
	}

}