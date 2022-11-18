package line;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.IllegalFormatException;
import java.util.zip.DataFormatException;

import javax.annotation.processing.FilerException;

public class PolyLine {

	private Point[] points;
	private boolean[] isRemoved;
	private int active;

	public PolyLine(Point[] points) {
		if (points.length <= 1)
			throw new IllegalArgumentException("Line must have at least 2 Points");
		this.points = points;

		active = points.length;
		isRemoved = new boolean[active];
	}

	/**
	 * Remove a point
	 * 
	 * @param i the index of the point
	 */
	public void remove(int i) {
		if (i == 0 || i == points.length - 1) {
			throw new IllegalArgumentException("Cannot remove first or last point");
		}
		if (isRemoved[i]) {
			throw new IllegalStateException("Can not remove a Point that is already removed");
		}

		// remove
		isRemoved[i] = true;
		active--;
	}

	/**
	 * Gets a Point at an index
	 * 
	 * @param i The index of the point
	 * @return points[i]
	 */
	public Point getPoint(int i) {
		return points[i];
	}

	/**
	 * Gets the number of non-removed (active) points
	 * 
	 * @return number of active points
	 */
	public int getActive() {
		return active;
	}

	/**
	 * Resets the line. Activates all Points; Resets active counter.
	 */
	public void reset() {
		active = points.length;
		isRemoved = new boolean[active];
	}

	/**
	 * Reads a Line from a file
	 * 
	 * @param f File to be read from (in sgpx format)
	 * @return Line from file
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws DataFormatException
	 */
	public static PolyLine readLine(File f) throws NumberFormatException, IOException, DataFormatException {
		BufferedReader reader = new BufferedReader(new FileReader(f));

		int numberPoints = Integer.parseInt(reader.readLine());
		Point[] points = new Point[numberPoints];
		
		double sx = 0, sy = 0;

		for (int i = 0; i < numberPoints; i++) {
			String line = reader.readLine();
			String[] split = line.split(",");

			if (split.length != 2) {
				throw new DataFormatException("Bad file format (line: " + (i + 1) + " in " + f.getPath() + ")");
			}

			double x = Double.parseDouble(split[0]);
			double y = Double.parseDouble(split[1]);
			
			if(i == 0) {
				sx = x;
				sy = y;
			}
			

			points[i] = new Point(x - sx, y - sy);
		}

		return new PolyLine(points);
	}

}