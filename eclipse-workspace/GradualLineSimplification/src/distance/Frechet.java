package distance;

import line.Line;

public class Frechet implements DistanceMeasurement{

	@Override
	public double distance(Line l, int a, int b) {
		// TODO: Implement Frechet Distance
		return 0;
	}
	
	private boolean test(Line l, int a, int b, double error) {
		
		return false;
	}
	
	
	@Override
	public String toString() {
		return "Frechet";
	}
	
	
}
