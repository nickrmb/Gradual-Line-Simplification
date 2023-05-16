package simplifier.extended.sumsum;

import simplifier.extended.Merge;
import simplifier.extended.Removal;
import util.Tuple;

public class SumSumMerge implements Merge {

	@Override
	public Tuple<Double, Removal[]> merge(Removal[] seq1, Removal[] seq2, Removal r) {

		Removal[] r1 = seq1 == null ? new Removal[] {} : seq1;
		Removal[] r2 = seq2 == null ? new Removal[] {} : seq2;

		double[][] dp = new double[r1.length + 1][r2.length + 1];
		dp[0][0] = 0.0;

		double sum = 0;
		for (int i = 1; i <= r1.length; i++) {
			sum += r1[i - 1].getError();
			dp[i][0] = sum;
		}
		sum = 0;
		for (int j = 1; j <= r2.length; j++) {
			sum += r2[j - 1].getError();
			dp[0][j] = sum;
		}

		for (int i = 1; i <= r1.length; i++) {
			for (int j = 1; j <= r2.length; j++) {
				dp[i][j] = r1[i - 1].getError() + r2[j - 1].getError() + Math.min(dp[i - 1][j], dp[i][j - 1]);
			}
		}

		Removal[] removal = new Removal[r1.length + r2.length + 1];

		int i = r1.length, j = r2.length;

		double error = 0;

		for (int x = removal.length - 2; x >= 0; x--) {
			double ci = (i > 0) ? dp[i - 1][j] : Double.POSITIVE_INFINITY;
			double cj = (j > 0) ? dp[i][j - 1] : Double.POSITIVE_INFINITY;

			double cellError = (i > 0 ? r1[i - 1].getError() : 0) + (j > 0 ? r2[j - 1].getError() : 0);

			error += cellError;

			if (ci < cj) {
				i--;
				Removal cur = r1[i];
				removal[x] = new Removal(cur.getIdx(), cur.getShortcut(), cellError);
			} else {
				j--;
				Removal cur = r2[j];
				removal[x] = new Removal(cur.getIdx(), cur.getShortcut(), cellError);
			}
		}

		removal[removal.length - 1] = r;
		error += r.getError();

		return new Tuple<>(error, removal);
	}

	@Override
	public Tuple<Double, Removal[]> getError(Removal[] seq1, Removal[] seq2, Removal r) {
		return merge(seq1, seq2, r);
	}

}
