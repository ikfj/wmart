/*
 * jCase - Java Combinatorial Auction Simulator 
 * Copyright (C) 2004-2006 Bjoern Schnizler, University of Karlsruhe (TH)
 * http://www.iw.uni-karlsruhe.de/jcase
 *
 * Parts of this work are funded by the European Union
 * under the IST project CATNETS (http://www.catnets.org/)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or any later version.
 * See the GNU General Public License for more details.
 *
 * This code comes WITHOUT ANY WARRANTY
 */
/*
 * Created on 04.07.2005 
 */
package edu.iw.utils;

/**
 * A class that provides some Math utils
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 */
public class MathUtils {

	public static final int INF_NUMBER = 1000000000;
	public static final double INF_NUMBER_DOUBLE = 1000000000;

	public static final double SMALL_NUMBER = 0.01;

	/**
	 * Compares a to b if they are equal within a range
	 * 
	 * @param a
	 *            Parameter A
	 * @param b
	 *            Parameter B
	 * @param range
	 *            The rangte
	 * @return <true> if A and B are equal within the given range, <false>
	 *         otherwise
	 */
	public static boolean compare(double a, double b, double range) {
		if (a == b) {
			return true;
		}
		if (a > b) {
			if (b + range >= a) {
				return true;
			} else {
				return false;
			}
		} else {
			if (a + range >= b) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Checks if a value is between two parameters with respect to a little
	 * error (epsilon)
	 * 
	 * @param valueToCompare
	 *            The value to check [e.g. 0.5]
	 * @param leftBoundary
	 *            The left boundary [e.g. 0]
	 * @param rightBoundary
	 *            The right boundary [e.g. 1]
	 * @param epsilon
	 *            The error epsilon [e.g 0.01]
	 * @return <true> if the value is between the parameters with respect to
	 *         epsilon, <false> otherwise
	 */
	public static boolean between(double valueToCompare, double leftBoundary,
			double rightBoundary, double epsilon) {
		if (valueToCompare == leftBoundary || valueToCompare == rightBoundary)
			return true;
		if (valueToCompare >= leftBoundary && valueToCompare <= rightBoundary)
			return true;
		if (valueToCompare + epsilon < leftBoundary)
			return false;
		if (valueToCompare > rightBoundary + epsilon)
			return false;
		return true;
	}

}