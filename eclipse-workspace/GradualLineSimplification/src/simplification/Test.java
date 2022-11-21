package simplification;

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

import distance.Hausdorff;
import line.Point;
import line.PolyLine;
import line.Visualizer;
import util.Tuple;

public class Test {

	public static void main(String[] args) throws NumberFormatException, IOException, DataFormatException {

		Point[] points = { new Point(-3, 1), new Point(0, 0), new Point(-1, 1), new Point(1, 2), new Point(2, -1),
				new Point(-1, -1), new Point(3, -2), new Point(11, -1), new Point(11, 1), new Point(8, 1),
				new Point(9, 2), new Point(10, 0), new Point(14, 1) };
		PolyLine l = new PolyLine(points);
		//PolyLine l = PolyLine.readLine(new File("/Users/nick/Documents/University/Bachelor Project/gradual-line-simplification/python-workspace/data/5377790.sgpx"));

		
		GreedySimplification simpl = new GreedySimplification();
		
		Tuple<int[], double[]> solution = simpl.simplify(l, new Hausdorff());
		
		System.out.println(solution.r[solution.r.length - 1]);
		
		new Visualizer(l, solution.l, solution.r);
	}

}
