package distance;

import line.PolyLine;
import java.util.List;
import line.Vertex;
import util.Tuple;

public class Frechet implements DistanceMeasure {

	public static final double DELTA = 0.000000000000005; // room of error in testing

	@Override
	public double measure(PolyLine l, int from, int to) {
		Tuple<List<Double>, double[]> hausdorff = Hausdorff.getTypeB(l, from, to);
		List<Double> typeBErrors = hausdorff.l;
		double[] t = hausdorff.r;

		double max = getMaxTypeC(l, from, to, t);
		;
		for (Double error : typeBErrors)
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
	public static double getMaxTypeC(PolyLine l, int from, int to, double[] t) {

		Vertex a = l.getPoint(from);
		Vertex b = l.getPoint(to);

		if (a.squaredDistanceTo(b) == 0.0) {
			return 0;
		}

		double max = 0.0;

		// type c (new passage opens)
		for (int i = 0; i < t.length - 1; i++) {
			for (int j = i + 1; j < t.length; j++) {
				if (t[i] > t[j]) {
					Vertex p = l.getPoint(from + 1 + i);
					Vertex q = l.getPoint(from + 1 + j);

					Tuple<Vertex, Double> nearestCommon = nearestCommonPointOnSegmentBetween(a, b, p, q, t[i], t[j]);

					if (nearestCommon != null) {
						double d = (p.distanceTo(nearestCommon.l));
						max = (max < d) ? d : max;
					}
				}
			}
		}

		return max;
	}

	/**
	 * Gets the t and error of the nearest common Point on Segment <a,b> of a Point
	 * p and q
	 * 
	 * @param a  The index of the point where the shortcut starts
	 * @param b  The index of the point where the shortcut ends
	 * @param p  The first point of interest
	 * @param q  The second point of interest
	 * @param tp The nearest t of the first point of interest (p)
	 * @param tq The nearest t of the second point of interest (q)
	 * @return A tuple containing a point as first argument and t as second argument
	 * 
	 * @apiNote It has to hold that tp > tq
	 */
	private static Tuple<Vertex, Double> nearestCommonPointOnSegmentBetween(Vertex a, Vertex b, Vertex p, Vertex q,
			double tp, double tq) {
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
			return null;
		}

		// get intersection
		Vertex intersection = new Vertex(ax + (bx - ax) * tBetween, ay + (by - ay) * tBetween);

		return new Tuple<>(intersection, tBetween);
	}

	@Override
	public String toString() {
		return "Frechet";
	}

}
