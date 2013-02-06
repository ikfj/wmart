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
package edu.iw.mace.order.splitting;

import org.apache.log4j.Logger;

import edu.iw.utils.patterns.AbstractFactory;
import edu.iw.utils.patterns.AbstractProduct;
import edu.iw.utils.patterns.FactoryException;

/**
 * A factory that provides a set of splitting algorithms
 * 
 * Created: 26.11.2005
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 */

public class SplitterFactory implements AbstractFactory {

	private static Logger log = Logger.getLogger(SplitterFactory.class);

	private String currentValue = SPLITTER[0];

	public static final String[] SPLITTER = {
			"edu.iw.mace.order.splitting.NoSplit",
			"edu.iw.mace.order.splitting.SimpleGraphSplitter",
			"edu.iw.mace.order.splitting.ExtendedGraphSplitter",
			"edu.iw.mace.order.splitting.RandomSplitter",
			"edu.iw.mace.order.splitting.MaceSplitter" };

	public static final int NO_SPLIT = 0;

	public static final int SIMPLE_GRAPH_SEPARATOR = 1;

	public static final int EXTENDED_SIMPLE_GRAPH_SEPARATOR = 2;

	public static final int RANDOM = 3;

	public static final int MACE_SPLITTER = 4;

	private static SplitterFactory instance = null;

	private SplitterFactory() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.utils.patterns.AbstractFactory#createAbstractProduct()
	 */
	public AbstractProduct createAbstractProduct() throws FactoryException {
		try {
			log.debug("Splitting: " + currentValue + " will be instantiated.");
			return (Splitter) this.getClass().getClassLoader().loadClass(
					currentValue).newInstance();
		} catch (ClassNotFoundException e) {
			throw new FactoryException(e);
		} catch (IllegalAccessException e) {
			throw new FactoryException(e);
		} catch (InstantiationException e) {
			throw new FactoryException(e);
		}
	}

	/**
	 * Generates an instance of the factory
	 * 
	 * @return Instance of the factory
	 */
	public static SplitterFactory instance() {
		if (instance == null) {
			instance = new SplitterFactory();
		}
		return instance;
	}

	/**
	 * @return Returns the currentValue.
	 */
	public String getCurrentValue() {
		return currentValue;
	}

	/**
	 * @return Model value as int
	 */
	public int getValueAsInt() {
		for (int a = 0; a < SPLITTER.length; a++) {
			if (SPLITTER[a].equals(getCurrentValue())) {
				return a;
			}
		}
		return -1;
	}

	/**
	 * @param currentValue
	 *            The currentValue to set.
	 */
	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}

}
