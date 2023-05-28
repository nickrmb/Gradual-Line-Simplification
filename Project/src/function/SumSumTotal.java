package function;

public class SumSumTotal implements ObjectiveFunction {

	private ObjectiveFunction weighted = new WeightedSum();

	@Override
	public double[] measure(int[] simplification, double[] error) {
		return weighted.measure(simplification, error);
	}
	
	@Override
	public String toString() {
		return "SumSumTotal";
	}

}
