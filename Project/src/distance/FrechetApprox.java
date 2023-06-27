package distance;

import line.Vertex;
import line.PolyLine;

public class FrechetApprox implements DistanceMeasure {

	private double delta = 0.0000005;
	private int iterations = 10;

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
	public double measure(PolyLine l, int from, int to) {
		// get an upper bound
		double upperBound = upperBound(l, from, to);

		// get a lower bound
		double lowerBound = 0;
		// double lowerBound = new Hausdorff().distance(l, from, to); // alternative

		// perform interval search
		if (iterations != -1)
			return intervalSearchIterations(l, from, to, upperBound, lowerBound);
		return intervalSearchDelta(l, from, to, upperBound, lowerBound);
	}

	/**
	 * Creates an upper bound for the frechet distance for a shortcut
	 * 
	 * @param l    The polygonal line
	 * @param from The start of the shortcut
	 * @param to   The end of the shortcut
	 * @return the upper bound
	 */
	public static double upperBound(PolyLine l, int from, int to) {
		double upper1 = maxDistanceLinePoint(l, from + 1, to - 1, l.getPoint(from));
		double upper2 = maxDistanceLinePoint(l, from + 1, to - 1, l.getPoint(to));
		return Math.min(upper1, upper2);
	}

	/**
	 * Gets the maximum euclidean distance between a line and a Point
	 * 
	 * @param l     The polygonal line
	 * @param from  The start of the subline
	 * @param to    The end of the shubline
	 * @param point The point
	 * @return maximum euclidean distance line point
	 */
	public static double maxDistanceLinePoint(PolyLine l, int from, int to, Vertex point) {
		double distance = 0.0;
		for (int i = from; i <= to; i++) {
			Vertex cur = l.getPoint(i);
			distance = Math.max(distance, point.distanceTo(cur));
		}
		return distance;
	}

	/**
	 * Performs an interval search to find the approximated frechet distance until
	 * the error is under delta
	 * 
	 * @param l     The polyline
	 * @param from  The start of the shortcut
	 * @param to    The end of the shortcut
	 * @param upper The upper bound of the distance
	 * @param lower The lower bound of the distance
	 * @return the approximated value
	 */
	private double intervalSearchDelta(PolyLine l, int from, int to, double upper, double lower) {
		while (upper - lower > delta) {
			double mid = (upper + lower) / 2.0;
			if (test(l, from, to, mid, 0.0)) {
				upper = mid;
			} else {
				lower = mid;
			}
		}
		return upper;
	}

	/**
	 * Performs an interval search to find the approximated frechet distance until
	 * the number of iterations is reached
	 * 
	 * @param l     The polyline
	 * @param from  The start of the shortcut
	 * @param to    The end of the shortcut
	 * @param upper The upper bound
	 * @param lower The lower bound
	 * @return approximated value
	 */
	private double intervalSearchIterations(PolyLine l, int from, int to, double upper, double lower) {
		for (int i = 0; i < iterations; i++) {
			double mid = (upper + lower) / 2.0;
			if (test(l, from, to, mid, 0.0)) {
				upper = mid;
			} else {
				lower = mid;
			}
		}
		return upper;
	}

	/**
	 * Tests whether an error is valid
	 * 
	 * @param l     The PolyLine
	 * @param from  The start of the shortcut
	 * @param to    The end of the shortcut
	 * @param error The error
	 * @return true if valid, else false
	 */
	public static boolean test(PolyLine l, int from, int to, double error, double delta) {
		double lower = 0.0;
		double upper = 1.0;

		Vertex a = l.getPoint(from);
		Vertex b = l.getPoint(to);

		double ax = a.getX();
		double ay = a.getY();
		double bx = b.getX();
		double by = b.getY();

		boolean abDuplicates = false;

		if (a.squaredDistanceTo(b) == 0.0) {
			abDuplicates = true;
		}

		// go through all points in between
		for (int i = from + 1; i < to; i++) {
			Vertex p = l.getPoint(i);
			double px = p.getX();
			double py = p.getY();

			if (abDuplicates) {
				if (a.distanceTo(p) > error) {
					return false;
				}
				continue;
			}

			// calculate free space

			// quadratic formula
			double qa = a.squaredDistanceTo(b);
			double qb = 2 * ((px - ax) * (ax - bx) + (py - ay) * (ay - by));
			double qc = p.squaredDistanceTo(a) - error * error;

			double qd = qb * qb - 4.0 * qa * qc;

			// new free space
			double newUpper, newLower;
			if (qd < -delta) { // less than approx 0
				// not in range
				return false;
			} else if (qd >= -delta && qd <= delta) { // approx 0
				// exactly one intersection

				// calculate new free space
				newUpper = -qb / (2.0 * qa);
				newLower = newUpper;
			} else { // greater than approx 0
				// two intersections

				// calculate new free space
				double root = Math.sqrt(qd);
				newUpper = (-qb + root) / (2.0 * qa);
				newLower = (-qb - root) / (2.0 * qa);

			}

			// check intersection validity
			if ((newUpper < 0.0 && newLower < 0.0) || (newUpper > 1.0 && newLower > 1.0)) {
				return false;
			}

			// update free space
			upper = Math.min(newUpper, 1.0);
			lower = Math.max(lower, newLower);

			// check free space validity
			if (lower - upper > delta) {
				return false;
			}

		}

		return true;
	}

	/**
	 * Sets the number of iterations
	 * 
	 * @param iterations
	 */
	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	@Override
	public String toString() {
		return "FrechetApprox";
	}

}
