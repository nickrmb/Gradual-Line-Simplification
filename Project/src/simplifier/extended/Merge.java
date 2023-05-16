package simplifier.extended;

import util.Tuple;

public interface Merge {
	
	public Tuple<Double, Removal[]> merge(Removal[] seq1, Removal[] seq2, Removal r);
	
	public Tuple<Double, Removal[]> getError(Removal[] seq1, Removal[] seq2, Removal r);
	
}