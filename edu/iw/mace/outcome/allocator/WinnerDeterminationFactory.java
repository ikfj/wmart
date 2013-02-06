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

package edu.iw.mace.outcome.allocator;

import org.apache.log4j.Logger;

import edu.iw.utils.patterns.AbstractFactory;
import edu.iw.utils.patterns.AbstractProduct;
import edu.iw.utils.patterns.FactoryException;

/**
 * A factory that provides different winner determination models
 * 
 * Created: 15.09.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @author Ikki Fujiwara, NII
 * @version 1.2
 */

public class WinnerDeterminationFactory implements AbstractFactory {

	private static Logger log = Logger.getLogger(WinnerDeterminationFactory.class);

	public static final int MACE = 0;
	public static final int COMBINATORIAL_AUCTION = 1;
	public static final int COMBINATORIAL_EXCHANGE = 2;
	public static final int CATNETS_RESOURCE_MARKET = 3;
	public static final int WMART = 4;

	public static final String[] MODELS = { "edu.iw.mace.outcome.allocator.Mace",
		"edu.iw.mace.outcome.allocator.CombinatorialAuction", "edu.iw.mace.outcome.allocator.CombinatorialExchange",
		"edu.iw.mace.outcome.allocator.CatnetsResourceMarket", "edu.iw.mace.outcome.allocator.WMartWinnerDetermination" };

	public static final String DEFAULT_MODEL = MODELS[4];

	private String currentModel = DEFAULT_MODEL;

	private static WinnerDeterminationFactory instance = null;

	/**
	 * Private constructor
	 */
	private WinnerDeterminationFactory() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.utils.patterns.AbstractFactory#createAbstractProduct()
	 */
	public AbstractProduct createAbstractProduct() throws FactoryException {
		log.debug("Winner Determination Model  " + currentModel + " will be instantiated.");
		try {
			return (AbstractProduct) this.getClass().getClassLoader().loadClass(currentModel).newInstance();
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
	public static WinnerDeterminationFactory instance() {
		if (instance == null) {
			instance = new WinnerDeterminationFactory();
		}
		return instance;
	}

	/**
	 * @return <true> if the current model supports slots, otherwise <false>
	 */
	public boolean hasSlots() {
		if (getValueAsInt() == MACE || getValueAsInt() == WMART)
			return true;
		return false;
	}

	/**
	 * @return Returns the winner determination model as a string
	 */
	public String getValue() {
		return currentModel;
	}

	/**
	 * @return Returns the winner determination model as an int value
	 */
	public int getValueAsInt() {
		for (int a = 0; a < MODELS.length; a++) {
			if (MODELS[a].equals(getValue())) {
				return a;
			}
		}
		return -1;
	}

	/**
	 * @param currentSolver
	 *            The current model to set.
	 */
	public void setValue(String currentSolver) {
		this.currentModel = currentSolver;
	}

}