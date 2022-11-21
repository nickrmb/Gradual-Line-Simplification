package simplification;

import distance.Hausdorff;
import line.Point;
import line.PolyLine;
import util.Tuple;

public class Test {

	public static void main(String[] args) {

		Point[] points = { new Point(-3, 1), new Point(0, 0), new Point(-1, 1), new Point(1, 2), new Point(2, -1),
				new Point(-1, -1), new Point(3, -2), new Point(11, -1), new Point(11, 1), new Point(8, 1),
				new Point(9, 2), new Point(10, 0), new Point(14, 1) };
		PolyLine l = new PolyLine(points);
		
		GreedySimplification simpl = new GreedySimplification();
		
		Tuple<int[], double[]> solution = simpl.simplify(l, new Hausdorff());
		
		System.out.println(solution.r[solution.r.length - 1]);
	}

}
