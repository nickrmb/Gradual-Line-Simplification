package util;

import distance.Hausdorff;
import line.Point;
import line.PolyLine;

public class Test {

	public static void main(String[] args) {
		HeapNode n1 = new HeapNode(0);
		n1.error = 1;
		HeapNode n2 = new HeapNode(1);
		n2.error = 2;
		System.out.println(n1.compareTo(n2));
		

		Point[] points = { new Point(-3, 1), new Point(0, 0), new Point(-1, 1), new Point(1, 2), new Point(2, -1),
				new Point(-1, -1), new Point(3, -2), new Point(11, -1), new Point(11, 1), new Point(8, 1),
				new Point(9, 2), new Point(10, 0), new Point(14, 1) };
		PolyLine l = new PolyLine(points);
		
		MinNodeHeap heap = new MinNodeHeap(l, new Hausdorff());

		System.out.println(heap.isSorted(0));
		
		for(int i = 0; i < points.length - 2; i++) {
			System.out.println(heap.extract().error);
			System.out.println(heap.isSorted(0));
		}
		
		
		
	}

}
