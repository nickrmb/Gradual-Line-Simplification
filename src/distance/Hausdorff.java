package distance;

import java.util.ArrayList;
import java.util.List;

import line.Point;
import line.PolyLine;
import util.Tuple;

public class Hausdorff implements DistanceMeasurement {

	@Override
	public double distance(PolyLine l, int from, int to) {
		Tuple<List<Double>, double[]> hausdorff = getTypeB(l, from, to);
		List<Double> errors = hausdorff.l;

		double max = 0;
		for (Double error : errors)
			max = (error > max) ? error : max;

		return max;
	}

	/**
	 * Gets the list of possible hausdorff errors
	 * 
	 * @param l    The line
	 * @param from The start of the shortcut
	 * @param to   The end of the shortcut
	 * @return List of errors
	 */
	public static Tuple<List<Double>, double[]> getTypeB(PolyLine l, int from, int to) {
		List<Double> errors = new ArrayList<Double>();
		double[] t = new double[to - from - 1]; // arrays holding which t is next to each point, t in [0;1]

		Point a = l.getPoint(from);
		Point b = l.getPoint(to);

		boolean abDuplicates = false;

		if (a.squaredDistanceTo(b) == 0.0) {
			abDuplicates = true;
		}

		// type b (shortest distance to line)
		for (int i = from + 1; i < to; i++) {
			Point p = l.getPoint(i);

			if (abDuplicates) {
				errors.add(a.distanceTo(p));
				continue;
			}

			Tuple<Point, Double> nearest = nearestPointOnSegment(a, b, p);

			t[i - from - 1] = nearest.r;
			errors.add(p.distanceTo(nearest.l));

			//System.out.println("Direct of " + i + " at " + nearest.l + "	: " + nearest.r);
		}

		return new Tuple<>(errors, t);
	}

	/**
	 * Gets the t and error of the nearest Point on Segment <a,b> of a Point p
	 * 
	 * @param a The Point where the segment starts
	 * @param b The Point where the segment ends
	 * @param p The Point of interest
	 * @return A tuple containing t as first argument and the error as second
	 *         argument
	 */
	private static Tuple<Point, Double> nearestPointOnSegment(Point a, Point b, Point p) {
		double ax = a.getX();
		double ay = a.getY();
		double bx = b.getX();
		double by = b.getY();
		double px = p.getX();
		double py = p.getY();

		double pt = ((px - ax) * (bx - ax) + (py - ay) * (by - ay)) / a.squaredDistanceTo(b);
		Point nearestPoint;
		// check bounds
		if (pt <= 0.0) {
			pt = 0.0;
			nearestPoint = a;
		} else if (pt >= 1.0) {
			pt = 1.0;
			nearestPoint = b;
		} else {
			nearestPoint = new Point(ax + (bx - ax) * pt, ay + (by - ay) * pt);
		}

		return new Tuple<>(nearestPoint, pt);
	}
	
	@Override
	public String toString() {
		return "Hausdorff";
	}

}
