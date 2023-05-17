package simplifier.extended.sumsum;

import simplifier.extended.Merge;
import simplifier.extended.Removal;
import util.Tuple;

public class SumSumMergeGBU implements Merge {

	@Override
	public Tuple<Double, Removal[]> merge(Removal[] seq1, Removal[] seq2, Removal r) {

		Removal[] r1 = seq1 == null ? new Removal[] {} : seq1;
		Removal[] r2 = seq2 == null ? new Removal[] {} : seq2;

		Removal[] removal = new Removal[r1.length + r2.length + 1];

		int i = -1, j = -1;

		double error = 0;

		for (int x = removal.length - 2; x >= 0; x--) {

			double cip = i >= 0 && i < r1.length ? r1[i].getError() : 0;
			double cjp = j >= 0 && i < r2.length ? r2[j].getError() : 0;
			double cia = i + 1 < r1.length ? r1[i + 1].getError() : Double.POSITIVE_INFINITY;
			double cja = j + 1 < r2.length ? r2[j + 1].getError() : Double.POSITIVE_INFINITY;

			double ci = cia + cjp;
			double cj = cja + cip;

			if (ci <= cj) {
				i++;
				Removal cur = r1[i];
				removal[x] = new Removal(cur.getIdx(), cur.getShortcut(), ci);
				error += ci;
			} else {
				j++;
				Removal cur = r2[j];
				removal[x] = new Removal(cur.getIdx(), cur.getShortcut(), cj);
				error += cj;
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
		return "GBU";
	}

}