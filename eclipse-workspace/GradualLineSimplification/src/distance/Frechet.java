package distance;

import line.PolyLine;

import java.util.ArrayList;
import java.util.List;

import line.Point;

import util.Tuple;

public class Frechet implements DistanceMeasurement {

	private static final double delta = 0.000000000000005; // room of error at testing

	@Override
	public double distance(PolyLine l, int from, int to) {
		Tuple<List<Double>, double[]> hausdorff = Hausdorff.getTypeB(l, from, to);
		List<Double> typeBErrors = hausdorff.l;
		double[] t = hausdorff.r;

		List<Double> typeCErrors = getTypeC(l, from, to, t);

		double max = 0;
		for (Double error : typeBErrors)
			max = (error > max) ? error : max;

		for (Double error : typeCErrors)
			max = (error > max) ? error : max;

		return max;
	}

	/**
	 * Gets the list of type c errors
	 * 
	 * @param l    The line
	 * @param from The start of the shortcut
	 * @param to   The end of the shortcut
	 * @param t    Array of t's where nearest Point is
	 * @return List of errors
	 */
	public static List<Double> getTypeC(PolyLine l, int from, int to, double[] t) {
		List<Double> errors = new ArrayList<>();

		Point a = l.getPoint(from);
		Point b = l.getPoint(to);

		boolean abDuplicates = false;

		if (a.squaredDistanceTo(b) == 0.0) {
			abDuplicates = true;
		}

		if (abDuplicates) {
			return errors;
		}

		// type c (new passage opens)
		for (int i = 0; i < t.length - 1; i++) {
			for (int j = i + 1; j < t.length; j++) {
				if (t[i] > t[j]) {
					Point p = l.getPoint(from + 1 + i);
					Point q = l.getPoint(from + 1 + j);

					Tuple<Point, Double> nearestCommon = nearestCommonPointOnSegmentBetween(a, b, p, q, t[i], t[j]);

					if (nearestCommon != null) {
						errors.add(p.distanceTo(nearestCommon.l));
						//System.out.println("Intersection between " + (i + from + 1) + " and " + (j + from + 1) + " at "
						//		+ nearestCommon.l + ": " + nearestCommon.r);
					}
				}
			}
		}

		return errors;
	}

	/**
	 * Gets the t and error of the nearest common Point on Segment <a,b> of a Point
	 * p and q
	 * 
	 * @param a The index of the point where the shortcut starts
	 * @param b The index of the point where the shortcut ends
	 * @param p The first point of interest
	 * @param q The second point of interest
	 * @param tp The t of the first point of interest (p)
	 * @param tq The t of the second point of interest (q)
	 * @return A tuple containing a point as first argument and t as second argument
	 *         
	 * @apiNote It has to hold that tp > tq
	 */
	private static Tuple<Point, Double> nearestCommonPointOnSegmentBetween(Point a, Point b, Point p, Point q, double tp, double tq) {
		double ax = a.getX();
		double ay = a.getY();
		double bx = b.getX();
		double by = b.getY();
		double px = p.getX();
		double py = p.getY();
		double qx = q.getX();
		double qy = q.getY();

		// calculate nearest common point
		double tBetween = (a.squaredDistanceTo(q) - a.squaredDistanceTo(p))
				/ ((ax - bx) * (px - qx) + (ay - by) * (py - qy)) / 2.0;

		// check bounds
		if (tBetween >= tp || tBetween <= tq) {
			// System.out.println("No intersection between " + (i + from + 1) + " and " + (j
			// + from + 1)
			// + " at t = " + tBetween);
			return null;
		}

		// get intersection
		Point intersection = new Point(ax + (bx - ax) * tBetween, ay + (by - ay) * tBetween);

		return new Tuple<>(intersection, tBetween);
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
	public static boolean test(PolyLine l, int from, int to, double error) {
		double lower = 0.0;
		double upper = 1.0;

		Point a = l.getPoint(from);
		Point b = l.getPoint(to);

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
			Point p = l.getPoint(i);
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

}
