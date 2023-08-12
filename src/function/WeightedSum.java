package function;

public class WeightedSum implements ObjectiveFunction {

	double[] weight;
	
	@Override
	public double[] measure(int[] simplification, double[] error) {
		double[] sequence = new double[error.length];
		
		double cur = 0.0;
		if(weight == null) {
			for(int i = 0; i < error.length; i++) {
				cur += (error.length - i) * error[i];
				sequence[i] = cur;
			}
		} else {
			for(int i = 0; i < error.length; i++) {
				cur += weight[i] * error[i];
				sequence[i] = cur;
			}
		}
		
		return sequence;
	}
	
	public void setWeight(double[] weight) {
		this.weight = weight;
	}
	
	@Override
	public String toString() {
		return "SumWeighted";
	}

}
