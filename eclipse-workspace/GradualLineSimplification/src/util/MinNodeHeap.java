package util;

import distance.DistanceMeasurement;
import line.PolyLine;

public class MinNodeHeap {

	private PolyLine line;
	private DistanceMeasurement distance;

	private Node[] heap;
	private int length;

	public MinNodeHeap(PolyLine line, DistanceMeasurement distance) {
		length = line.length();
		this.line = line;
		this.distance = distance;

		heap = new Node[length];

		// initialize heap
		heap[0] = new Node(0);
		for (int i = 1; i < length; i++) {
			heap[i] = new Node(i);
			heap[i].left = heap[i - 1];
		}
		for (int i = 0; i < length - 1; i++) {
			heap[i].right = heap[i + 1];
		}

		// calculate errors
		for (int i = 0; i < length; i++) {
			heap[i].calculateError(line, distance);
		}

		// heapify
		for (int i = length / 2 - 1; i >= 0; i--) {
			downwardsSink(i);
		}
	}

	public Node extract() {
		Node smallest = heap[0];

		Node left = smallest.left;
		Node right = smallest.right;

		double leftBefore = left.error;
		double rightBefore = right.error;

		smallest.updateAtRemove(line, distance);

		double leftAfter = left.error;
		double rightAfter = right.error;

		if (leftAfter < leftBefore)
			upwardsSink(left.heapIndex);
		if (rightAfter < rightBefore)
			upwardsSink(right.heapIndex);

		if (leftAfter > leftBefore)
			downwardsSink(left.heapIndex);
		if (rightAfter > rightBefore)
			downwardsSink(right.heapIndex);

		length--;
		swap(0, length);
		downwardsSink(0);

		return heap[length];
	}

	//
	private void downwardsSink(int i) {
		Tuple<Integer, Integer> children = getChildrenOf(i);
		Integer i1 = children.l;
		Integer i2 = children.r;

		if (i1 == null)
			return;

		int smaller = i1;

		if (i2 != null)
			smaller = (heap[i1].compareTo(heap[i2]) <= 0) ? i1 : i2;

		if (heap[i].compareTo(heap[smaller]) <= 0) {
			return;
		}

		swap(i, smaller);
		downwardsSink(smaller);

	}

	private void upwardsSink(int i) {
		if (i >= 0 && i <= 2)
			return;

		int upwardsIndex = (i - 1) / 2;

		if (heap[i].compareTo(heap[upwardsIndex]) < 0) {
			swap(i, upwardsIndex);
			upwardsSink(upwardsIndex);
		}
	}

	private void swap(int i1, int i2) {
		Node a = heap[i1];
		heap[i1] = heap[i2];
		heap[i2] = a;

		heap[i1].heapIndex = i1;
		heap[i2].heapIndex = i2;

	}

	private Tuple<Integer, Integer> getChildrenOf(int i) {
		Integer i1 = i * 2 + 1;
		if (length <= i1)
			i1 = null;
		Integer i2 = i * 2 + 2;
		if (length <= i2) {
			i2 = null;
		}
		return new Tuple<>(i1, i2);
	}

	public boolean isSorted(int i) {
		Tuple<Integer, Integer> children = getChildrenOf(i);
		boolean childrenSorted = true;
		if (children.l != null) {
			if (heap[i].compareTo(heap[children.l]) > 0)
				return false;

			childrenSorted = isSorted(children.l);

			if (children.r != null) {
				if (heap[i].compareTo(heap[children.r]) > 0)
					return false;

				if (childrenSorted)
					childrenSorted = isSorted(children.r);

			}
		}
		return childrenSorted;
	}

	public void print(int i) {
		for (int x = i; x < length; x++) {
			System.out.print(x + ":" + heap[x].error + ", ");

		}

	}

}
