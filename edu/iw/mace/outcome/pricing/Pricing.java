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
package edu.iw.mace.outcome.pricing;

import org.apache.log4j.Logger;

import edu.iw.mace.environment.MaceException;
import edu.iw.mace.environment.Market;
import edu.iw.utils.patterns.FactoryException;

/**
 * This class manages the pricing process
 * 
 * Created: 25.09.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public class Pricing {

	private static Logger log = Logger.getLogger(Pricing.class);

	/**
	 * Empty constructor
	 */
	public Pricing() {
		super();
	}

	/**
	 * For a given market instance, this class computes prices. It should be
	 * called directly after the winner determination problem is solved.
	 * 
	 * @param market
	 *            Market instance
	 * @throws MaceException
	 */
	public static void computePrices(Market market) throws MaceException {
		try {
			AbstractPricing pricing = (AbstractPricing) market
					.getPricingFactory().createAbstractProduct();
			log.debug("Computing prices");
			pricing.pricing(market);
		} catch (FactoryException e) {
			throw new MaceException(e);
		}
	}
}