package simplification;

import distance.DistanceMeasurement;
import line.PolyLine;

public interface Simplifier {
	
	public int[] simplify(PolyLine l, DistanceMeasurement distance);

}