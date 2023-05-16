package function;

public class Sum implements OptimizationFunction {

	@Override
	public double[] measure(int[] simplification, double[] error) {
		double[] sequence = new double[error.length];
		double cur = 0;
		for (int i = 0; i < sequence.length; i++) {
			cur += error[i];
			sequence[i] = cur;
		}
		return sequence;
	}
	
	@Override
	public String toString() {
		return "Sum";
	}
	
}
