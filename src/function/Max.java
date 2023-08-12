package function;

public class Max implements ObjectiveFunction {

	@Override
	public double[] measure(int[] simplification, double[] error) {
		double[] sequence = new double[error.length];
		if(sequence.length == 0) return sequence;
		sequence[0] = error[0];
		for(int i = 1; i < sequence.length; i++) {
			sequence[i] = Math.max(sequence[i-1], error[i]);
		}
		return sequence;
	}
	
	@Override
	public String toString() {
		return "Max";
	}

}
