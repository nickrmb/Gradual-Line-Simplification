package distance;

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

import line.*;

public class Test {

	public static void main(String[] args) throws NumberFormatException, IOException, DataFormatException {
		Point[] points = { new Point(-3, 1), new Point(0, 0), new Point(-1, 1), new Point(1, 2), new Point(2, -1),
				new Point(-1, -1), new Point(3, -2), new Point(11, -1), new Point(11, 1), new Point(8, 1),
				new Point(9, 2), new Point(10, 0), new Point(14, 1) };
		PolyLine l = PolyLine.readLine(new File("/Users/nick/Documents/University/Bachelor Project/gradual-line-simplification/python-workspace/data/5377791.sgpx"));

		DistanceMeasurement[] distanceMeasurements = { new Hausdorff(), new Frechet() };

		double distance = distanceMeasurements[1].distance(l, 0, 3336);

		System.out.println(distance);

		System.out.println(Frechet.test(l, 1, 11, 2.0155644370746373));
		
		}

}
