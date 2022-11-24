package simplification;

import distance.DistanceMeasurement;
import line.PolyLine;
import util.MinNodeHeap;
import util.HeapNode;
import util.Tuple;

public class GreedySimplification implements LineSimplifier{

	@Override
	public Tuple<int[], double[]> simplify(PolyLine l, DistanceMeasurement distanceMeasurement) {
		int numPointsBetween = l.length() - 2;
		
		int[] simplification = new int[numPointsBetween];
		double[] error = new double[numPointsBetween];
		double currentError = 0;
		
		MinNodeHeap minHeap = new MinNodeHeap(l, distanceMeasurement);
		
		
		for(int i = 0; i < numPointsBetween; i++) {
			HeapNode node = minHeap.extract();
			currentError += node.error;
			
			simplification[i] = node.index;
			error[i] = currentError;
		}
		
		return new Tuple<>(simplification, error);
		
	}
	
}