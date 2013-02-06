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
 * Represents dependencies constraints of two goods in a bundle
 * 
 * Created: 03.07.2005
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public class DependenciesBundle extends HashMap<String, HashMap<String, Good>> {

	private static final long serialVersionUID = -7108199466079787512L;

	/**
	 * Add a new dependence constraint for two goods, i.e. these two goods have
	 * to be allocated from the same source
	 * 
	 * @param source
	 *            Good 1
	 * @param dest
	 *            Good 2
	 */
	public void add(Good source, Good dest) {

		if (containsKey(source.getId())) {
			HashMap otherGood = get(source.getId());
			if (otherGood.containsKey(dest.getId())) {
				return;
			}
		}
		if (containsKey(dest.getId())) {
			HashMap otherGood = get(dest.getId());
			if (otherGood.containsKey(source.getId())) {
				return;
			}
		}

		// Source -> Dest sichern
		HashMap<String, Good> goodDependencies = get(source.getId());
		if (goodDependencies == null) {
			goodDependencies = new HashMap<String, Good>();
			put(source.getId(), goodDependencies);
		}
		goodDependencies.put(dest.getId(), dest);
	}

	/**
	 * Checks if these two goods depend on each other, i.e. have to be allocated
	 * from the same seller
	 * 
	 * @param source
	 *            Good 1
	 * @param dest
	 *            Good 2
	 * @return <true> if the two goods depend on each other, <false> if not
	 */
	public boolean depends(Good source, Good dest) {
		if (containsKey(source.getId()) == false) {
			if (containsKey(dest.getId()) == false) {
				return false;
			}
			HashMap goodDependencies = get(dest.getId());
			if (goodDependencies.containsKey(source.getId())) {
				return true;
			}
		} else {
			HashMap goodDependencies = get(source.getId());
			if (goodDependencies.containsKey(dest.getId())) {
				return true;
			}
		}
		return false;
	}
}
