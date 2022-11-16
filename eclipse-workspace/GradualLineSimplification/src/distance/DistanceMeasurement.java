package distance;

import line.Line;

public interface DistanceMeasurement {
	
	/**
	 * Calculates some distance measure for a shortcut from Point a to Point b in Line l
	 * @param l The line
	 * @param a Start point of shortcut
	 * @param b End point of shortcut
	 * @return The distance
	 */
	public double distance(Line l, int a, int b);
	
}
