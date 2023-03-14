package function;

public interface OptimizationFunction {
	
	
	/**
	 * @param simplification
	 * @param error
	 * @return
	 */
	public double[] measure(int[] simplification, double[] error);

}
