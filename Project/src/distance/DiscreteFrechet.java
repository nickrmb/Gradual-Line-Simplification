package distance;

import line.PolyLine;
import line.Vertex;

public class DiscreteFrechet implements DistanceMeasure {

	@Override
	public double measure(PolyLine l, int from, int to) {
		if (to < from) {
			int tmp = to;
			to = from;
			from = tmp;
		}
		if (to - from < 2) {
			return 0;
		}

		double[][] dp = new double[2][to - from];
		dp[0][0] = 0.0;
		dp[1][0] = l.getPoint(to).distanceTo(l.getPoint(from));

		for (int i = 0; i < 2; i++) {
			Vertex vSeg = l.getPoint((i == 0) ? from : to);
			for (int j = 1; j < to - from; j++) {
				Vertex vOther = l.getPoint(from + j);
				double dist = vSeg.distanceTo(vOther);
				double before1 = (i == 0) ? Double.MAX_VALUE : dp[0][j];
				double before2 = dp[i][j - 1];
				dp[i][j] = Math.max(dist, Math.min(before1, before2));
			}
		}
		

		return dp[1][to - from - 1];
	}
	
	@Override
	public String toString() {
		return "DiscreteFrechet";
	}

}
