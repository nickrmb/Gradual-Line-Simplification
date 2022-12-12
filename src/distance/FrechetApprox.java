package distance;

import line.Point;
import line.PolyLine;

public class FrechetApprox implements DistanceMeasurement {

	private double delta = 0.0000005;
	private int iterations = 20;

	public FrechetApprox() {
	}

	public FrechetApprox(double delta) {
		this.delta = delta;
		iterations = -1;
	}

	public FrechetApprox(int maxIterations) {
		this.iterations = maxIterations;
	}

	@Override
	public double distance(PolyLine l, int from, int to) {
		double upperBound = upperBound(l, from, to);
		// double lowerBound = new Hausdorff().distance(l, from, to); // better?
		double lowerBound = 0;
		if (iterations != -1)
			return intervalSearchIterations(l, from, to, upperBound, lowerBound);
		return intervalSearchDelta(l, from, to, upperBound, lowerBound);
	}

	public static double upperBound(PolyLine l, int from, int to) {
		double upper1 = maxDistancePointLine(l, from + 1, to - 1, l.getPoint(from));
		double upper2 = maxDistancePointLine(l, from + 1, to - 1, l.getPoint(to));
		return Math.max(upper1, upper2);
	}

	public static double maxDistancePointLine(PolyLine l, int from, int to, Point point) {
		double distance = 0.0;
		for (int i = from; i <= to; i++) {
			Point cur = l.getPoint(i);
			distance = Math.max(distance, point.distanceTo(cur));
		}
		return distance;
	}

	public double intervalSearchDelta(PolyLine l, int from, int to, double upper, double lower) {
		while (upper - lower > delta) {
			double mid = (upper + lower) / 2.0;
			if (Frechet.test(l, from, to, mid, 0.0)) {
				upper = mid;
			} else {
				lower = mid;
			}
		}
		return upper;
	}

	public double intervalSearchIterations(PolyLine l, int from, int to, double upper, double lower) {
		for (int i = 0; i < iterations; i++) {
			double mid = (upper + lower) / 2.0;
			//System.out.println(upper + " " + lower + " " + mid);
			// System.out.println(i);
			if (Frechet.test(l, from, to, mid, 0.0)) {
				upper = mid;
			} else {
				lower = mid;
			}
		}
		return upper;
	}
	
	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	@Override
	public String toString() {
		return "FrechetApprox";
	}

}
