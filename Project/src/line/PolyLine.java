package line;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.zip.DataFormatException;

public class PolyLine {

	private Vertex[] vertices;
	private boolean[] isRemoved;
	private int active;
	private String name;

	public PolyLine(Vertex[] vertices) {
		if (vertices.length <= 1)
			throw new IllegalArgumentException("Line must have at least 2 Points");
		this.vertices = vertices;

		active = vertices.length;
		isRemoved = new boolean[active];
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * Remove a point
	 * 
	 * @param i the index of the point
	 */
	public void remove(int i) {
		if (i == 0 || i == vertices.length - 1) {
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
	 * Add a point (opposite of remove function)
	 * 
	 * @param i the index of the point
	 */
	public void add(int i) {
		if (!isRemoved[i]) {
			throw new IllegalStateException("Can not ad a Point that is already added");
		}

		// add
		isRemoved[i] = false;
		active++;
	}

	/**
	 * Gets a Point at an index
	 * 
	 * @param i The index of the point
	 * @return points[i]
	 */
	public Vertex getPoint(int i) {
		return vertices[i];
	}

	/**
	 * Gets whether a point is active or not
	 * 
	 * @param i The index of the point
	 * @return true if not removed, else false
	 */
	public boolean isActive(int i) {
		return !isRemoved[i];
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
		active = vertices.length;
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
		if (!f.exists())
			throw new IOException("File not found: " + f.getAbsolutePath());

		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader(f));

		int numberPoints = Integer.parseInt(reader.readLine());
		Vertex[] points = new Vertex[numberPoints];

		double sx = 0, sy = 0;

		for (int i = 0; i < numberPoints; i++) {
			String line = reader.readLine();
			String[] split = line.split(",");

			if (split.length != 2) {
				throw new DataFormatException("Bad file format (line: " + (i + 1) + " in " + f.getPath() + ")");
			}

			double x = Double.parseDouble(split[0]);
			double y = Double.parseDouble(split[1]);

			if (i == 0) {
				sx = x;
				sy = y;
			}

			points[i] = new Vertex(x - sx, y - sy);
		}

		PolyLine line = new PolyLine(points);
		line.setName(f.getName());

		return line;
	}

	/**
	 * Gets the number of points in the line
	 * 
	 * @return number points
	 */
	public int length() {
		return vertices.length;
	}

	@Override
	public String toString() {
		String str = "[";
		for (int i = 0; i < vertices.length; i++) {
			str += vertices[i].toString();
			if (i != vertices.length - 1) {
				str += ", ";
			}
		}
		return str + "]";
	}

}
