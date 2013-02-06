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

package edu.iw.mace.order;

import java.util.HashMap;

import edu.iw.mace.environment.Good;

/**
 * Represents divisiblity (max-split) constraints of a good
 * 
 * Created: 03.07.2005
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public class DivisibilityBundle extends HashMap<String, Integer> {

	private static final long serialVersionUID = -6788776033973769110L;

	/**
	 * Set a new max divisibility constraint for a given good
	 * 
	 * @param good
	 *            The good
	 * @param maxDivisibility
	 *            Maximum divisibility (co-allocation) of this goods
	 */
	public void addDivisibility(Good good, int maxDivisibility) {
		put(good.getId(), new Integer(maxDivisibility));
	}

	/**
	 * Returns the divisibility constraint of a given goods
	 * 
	 * @param good
	 *            Given good
	 * @return Max-Divisibility
	 */
	public int getDivisibility(Good good) {
		Integer value = get(good.getId());
		if (value == null) {
			return 0;
		} else {
			return value.intValue();
		}
	}
}
