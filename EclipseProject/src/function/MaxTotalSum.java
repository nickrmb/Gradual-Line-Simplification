package function;

public class MaxTotalSum implements OptimizationFunction{

	@Override
	public double[] measure(int[] simplification, double[] error) {
		double[] sequence = new double[error.length];
		
		double max = 0.0;
		double cur = 0;
		for(int i = 0; i < error.length; i++) {
			max = Math.max(max, error[i]);
			cur += max;
			sequence[i] = cur;
		}
		
		return sequence;
	}
	
	@Override
	public String toString() {
		return "MaxTotalSum";
	}

}
