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

import edu.iw.utils.patterns.AbstractFactory;
import edu.iw.utils.patterns.AbstractProduct;
import edu.iw.utils.patterns.FactoryException;

/**
 * A factory that provides different pricing schemes
 * 
 * Created: 15.09.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */

public class PricingFactory implements AbstractFactory {

	private static Logger log = Logger.getLogger(PricingFactory.class);

	public static final String[] PRICING = { "edu.iw.mace.outcome.pricing.VickreyPricingThreshold",
		"edu.iw.mace.outcome.pricing.VickreyPricingReverse",
		"edu.iw.mace.outcome.pricing.VickreyPricingFractional",
		"edu.iw.mace.outcome.pricing.VickreyPricing", "edu.iw.mace.outcome.pricing.KPrice",
		"edu.iw.mace.outcome.pricing.PricingPerColumnSingleSidedSingleAttribute",
		"edu.iw.mace.outcome.pricing.WMartPricing" };

	public static final int VICK_THRESHOLD = 0;

	public static final int VICK_REVERSE = 1;

	public static final int VICK_FRACTIONAL = 2;

	public static final int VICK = 3;

	public static final int KPRICE = 4;

	public static final int PPC_SINGLE = 5;

	public static final int WMART = 6;

	public static final String DEFAULT_PRICING = PRICING[WMART];

	private String currentValue = DEFAULT_PRICING;

	private static PricingFactory instance = null;

	/**
	 * Constructor
	 */
	private PricingFactory() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.utils.patterns.AbstractFactory#createAbstractProduct()
	 */
	public AbstractProduct createAbstractProduct() throws FactoryException {
		try {
			log.debug("Pricing: " + currentValue + " will be instantiated.");
			return (AbstractPricing) this.getClass().getClassLoader().loadClass(currentValue)
				.newInstance();
		} catch (ClassNotFoundException e) {
			throw new FactoryException(e);
		} catch (IllegalAccessException e) {
			throw new FactoryException(e);
		} catch (InstantiationException e) {
			throw new FactoryException(e);
		}
	}

	/**
	 * Returns an instance of this singleton class
	 * 
	 * @return Intsance of the factory
	 */
	public static PricingFactory instance() {
		if (instance == null) {
			instance = new PricingFactory();
		}
		return instance;
	}

	/**
	 * Sets the default pricing schema
	 */
	public void setDefault() {
		setValue(DEFAULT_PRICING);
	}

	/**
	 * @return Returns the currentPricing.
	 */
	public String getCurrentValue() {
		return currentValue;
	}

	/**
	 * @param currentPricing
	 *            The currentPricing to set.
	 */
	public void setValue(String currentPricing) {
		this.currentValue = currentPricing;
	}

}
