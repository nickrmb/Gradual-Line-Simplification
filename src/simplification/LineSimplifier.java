package simplification;

import distance.DistanceMeasurement;
import line.PolyLine;
import util.Tuple;

public interface LineSimplifier {

	/**
	 * Simplifies a Polyline and returns its simlification and error
	 * 
	 * @param l        The Polyline
	 * @param distance The distance measurement
	 * @return A tuple consisting the simplification sequence and the corresponding
	 *         error summer error according to the distance measure
	 */
	public Tuple<int[], double[]> simplify(PolyLine l, DistanceMeasurement distanceMeasurement);

}