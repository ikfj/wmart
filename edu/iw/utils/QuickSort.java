/****************************************************************************
 $Workfile: QuickSort.java $
 $Revision: 1.1.2.2 $
 $Modtime:: $
 $Copyright:

 Copyright (c) 1997 Novell, Inc.  All Rights Reserved.

 THIS WORK IS  SUBJECT  TO  U.S.  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 WITHOUT THE PRIOR WRITTEN CONSENT OF NOVELL, INC. ANY USE OR EXPLOITATION
 OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 CRIMINAL AND CIVIL LIABILITY.$

 ***************************************************************************/

package edu.iw.utils;

import java.util.Vector;

/**
 * @author Novell, Inc. All Rights Reserved.
 * 
 */
public class QuickSort {

	// Sorts entire array
	@SuppressWarnings("unchecked")
	public static void sort(Vector array) {
		sort(array, 0, array.size() - 1, false);
	}

	// Sorts entire array
	@SuppressWarnings("unchecked")
	public static void sortDescending(Vector array) {
		sort(array, 0, array.size() - 1, true);
	}

	// Sorts partial array
	private static void sort(Vector<Object> array, int start, int end,
			boolean descending) {
		int p;
		if (end > start) {
			p = partition(array, start, end, descending);
			sort(array, start, p - 1, descending);
			sort(array, p + 1, end, descending);
		}
	}

	@SuppressWarnings("unchecked")
	protected static int compare(Comparable a, Comparable b, boolean descending) {
		if (descending == true) {
			return -a.compareTo(b);
		}
		return a.compareTo(b);
	}

	protected static int partition(Vector<Object> array, int start, int end,
			boolean descending) {
		int left, right;
		Comparable partitionElement;

		// Arbitrary partition start...there are better ways...
		partitionElement = (Comparable) array.elementAt(end);

		left = start - 1;
		right = end;
		for (;;) {
			while (compare(partitionElement, (Comparable) array
					.elementAt(++left), descending) == 1) {
				if (left == end)
					break;
			}
			while (compare(partitionElement, (Comparable) array
					.elementAt(--right), descending) == -1) {
				if (right == start)
					break;
			}
			if (left >= right)
				break;
			swap(array, left, right);
		}
		swap(array, left, end);

		return left;
	}

	protected static void swap(Vector<Object> array, int i, int j) {
		Object temp;
		temp = array.elementAt(i);
		array.setElementAt(array.elementAt(j), i);
		array.setElementAt(temp, j);
	}
}
