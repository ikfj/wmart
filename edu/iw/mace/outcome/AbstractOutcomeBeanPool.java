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
package edu.iw.mace.outcome;

import java.util.Iterator;

import edu.iw.utils.patterns.EntityPool;

/**
 * An Abstract class for an outcome pool
 * 
 * Created: 08.10.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.0
 */
public abstract class AbstractOutcomeBeanPool extends EntityPool {

	/**
	 * Empty constructor
	 */
	public AbstractOutcomeBeanPool() {
		super();
	}

	/**
	 * Computes the sum of the utilities in this pool
	 * 
	 * @return Utilities
	 */
	public double computeUtility() {
		double utility = 0.0d;
		Iterator iterator = getValueIterator();
		while (iterator.hasNext()) {
			AbstractOutcomeBean bean = (AbstractOutcomeBean) iterator.next();
			utility += bean.computeUtility();
		}
		return utility;
	}

	/**
	 * Computes the average utility
	 * 
	 * @return Average utility
	 */
	public abstract double computeAverageUtility();

	/**
	 * Computes the sum of the prices in this pool
	 * 
	 * @return Prices
	 */
	public double computePrices() {
		double prices = 0.0d;
		Iterator iterator = getValueIterator();
		while (iterator.hasNext()) {
			AbstractOutcomeBean bean = (AbstractOutcomeBean) iterator.next();
			prices += bean.getPrice();
		}
		return prices;
	}
}