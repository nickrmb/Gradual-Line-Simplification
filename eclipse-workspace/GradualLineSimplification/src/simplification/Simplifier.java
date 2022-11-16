package simplification;

import distance.DistanceMeasurement;
import line.Line;

public interface Simplifier {
	
	public int[] simplify(Line l, DistanceMeasurement distance);

}