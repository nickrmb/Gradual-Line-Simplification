package distance;

import line.PolyLine;

public interface DistanceMeasurement {
	
	
	/**
	 * Gets the distance of a shortcut
	 * @param l The polyline
	 * @param from The index of the point where the shortcut starts
	 * @param to The index of the point where the shortcut ends
	 * @return The shortcut error
	 */
	public double distance(PolyLine l, int from, int to);
	
}


class Tuple<T, R> {
	T l;
	R r;

	Tuple(T l, R r) {
		this.l = l;
		this.r = r;
	}
}