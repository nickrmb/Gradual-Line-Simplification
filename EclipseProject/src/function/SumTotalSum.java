package function;

public class SumTotalSum implements OptimizationFunction {

	@Override
	public double[] measure(int[] simplification, double[] error) {
		return new WeightedSum().measure(simplification, error);
	}

}
