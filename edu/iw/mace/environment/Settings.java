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
package edu.iw.mace.environment;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import edu.iw.mace.order.splitting.SplitterFactory;
import edu.iw.mace.outcome.allocator.SolverSettingsSingleton;
import edu.iw.mace.outcome.allocator.WinnerDeterminationFactory;
import edu.iw.utils.patterns.FactoryException;

/**
 * Controls the settings of the auction simulator
 * 
 * Created: 06.11.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @author Ikki Fujiwara, NII
 * @version 1.01
 */
public class Settings {

	public static String PROPERTIES_FILE = "market_central.properties";

	private static Logger log = Logger.getLogger(Settings.class);

	private boolean deleteOrderBookPerTick = false;

	private boolean deleteEnvironmentPerTick = false;

	private boolean forcePriceAsInt = false;

	private boolean forceQualityAsInt = false;

	private PropertiesConfiguration config = null;

	private int solver = 0;

	private int model = 0;

	private boolean findDisjunctiveSets = false;

	/**
	 * Constructor
	 */
	public Settings() throws FactoryException, MaceException {

		try {
			config = new PropertiesConfiguration(PROPERTIES_FILE);
		} catch (ConfigurationException e) {
			log.error(e.fillInStackTrace());
		}
		config.setAutoSave(true);

		initProperties();
	}

	/**
	 * Initializes the properties
	 */
	private void initProperties() {
		// Splitter
		int splitValue = config.getInt("resource.orderbook.split");
		if (splitValue >= 0) {
			SplitterFactory.instance().setCurrentValue(
					SplitterFactory.SPLITTER[splitValue]);
		}
		// Find disjunctive order sets
		boolean findSets = config
				.getBoolean("resource.orderbook.finddisjunctivesets");
		setFindDisjunctiveSets(findSets);

		// Solver
		int solverValue = config.getInt("resource.allocator.solver");
		if (solverValue >= 0) {
			solver = solverValue;
			SolverSettingsSingleton.instance().setSolver(solver);
		}
		int time = config.getInt("resource.allocator.timelimit");
		if (time >= 0) {
			SolverSettingsSingleton.instance().setTimeLimit(time);
		}
		String solverHost = config.getString("resource.allocator.solver.remote.host");
		if (!solverHost.equals("")) {
			SolverSettingsSingleton.instance().setHost(solverHost);
		}
		int port = config.getInt("resource.allocator.solver.remote.port");
		if (port > 0) {
			SolverSettingsSingleton.instance().setPort(port);
		}
		// Model
		// int modelValue = config.getInt("resource.allocator.model");
		// if (modelValue >= 0) {
		// model = modelValue;
		// WinnerDeterminationFactory.instance().setValue(
		// WinnerDeterminationFactory.MODELS[model]);
		// }

	}

	/**
	 * @return Returns the deleteEnvironmentPerTick.
	 */
	public boolean isDeleteEnvironmentPerTick() {
		return deleteEnvironmentPerTick;
	}

	/**
	 * @param deleteEnvironmentPerTick
	 *            The deleteEnvironmentPerTick to set.
	 */
	public void setDeleteEnvironmentPerTick(boolean deleteEnvironmentPerTick) {
		this.deleteEnvironmentPerTick = deleteEnvironmentPerTick;
	}

	/**
	 * @return Returns the deleteOrderBookPerTick.
	 */
	public boolean isDeleteOrderBookPerTick() {
		return deleteOrderBookPerTick;
	}

	/**
	 * @param deleteOrderBookPerTick
	 *            The deleteOrderBookPerTick to set.
	 */
	public void setDeleteOrderBookPerTick(boolean deleteOrderBookPerTick) {
		this.deleteOrderBookPerTick = deleteOrderBookPerTick;
	}

	/**
	 * @return Returns the model.
	 */
	public int getModel() {
		return model;
	}

	/**
	 * @param model
	 *            The model to set.
	 */
	public void setModel(int model) {
		this.model = model;
	}

	/**
	 * @return Returns the solver.
	 */
	public int getSolver() {
		return solver;
	}

	/**
	 * @param solver
	 *            The solver to set.
	 */
	public void setSolver(int solver) {
		this.solver = solver;
	}

	/**
	 * @return Returns the config.
	 */
	public PropertiesConfiguration getConfig() {
		return config;
	}

	/**
	 * @param config
	 *            The config to set.
	 */
	public void setConfig(PropertiesConfiguration config) {
		this.config = config;
	}

	/**
	 * @return Returns the findDisjunctiveSets.
	 */
	public boolean isFindDisjunctiveSets() {
		return findDisjunctiveSets;
	}

	/**
	 * @param findDisjunctiveSets
	 *            The findDisjunctiveSets to set.
	 */
	public void setFindDisjunctiveSets(boolean findDisjunctiveSets) {
		this.findDisjunctiveSets = findDisjunctiveSets;
	}

	/**
	 * @return the forcePriceAsInt
	 */
	public boolean isForcePriceAsInt() {
		return forcePriceAsInt;
	}

	/**
	 * @param forcePriceAsInt
	 *            the forcePriceAsInt to set
	 */
	public void setForcePriceAsInt(boolean forcePriceAsInt) {
		this.forcePriceAsInt = forcePriceAsInt;
	}

	/**
	 * @return the forceQualityAsInt
	 */
	public boolean isForceQualityAsInt() {
		return forceQualityAsInt;
	}

	/**
	 * @param forceQualityAsInt
	 *            the forceQualityAsInt to set
	 */
	public void setForceQualityAsInt(boolean forceQualityAsInt) {
		this.forceQualityAsInt = forceQualityAsInt;
	}

	@Override
	public String toString() {
		return "Settings"
		 + "{ PROPERTIES_FILE=\"" + PROPERTIES_FILE + "\""
		 + ", solver=" + getSolver()
		 + ", model=" + getModel()
		 + ", deleteOrderBookPerTick=" + isDeleteOrderBookPerTick()
		 + ", deleteEnvironmentPerTick=" + isDeleteEnvironmentPerTick()
		 + ", forcePriceAsInt=" + isForcePriceAsInt()
		 + ", forceQualityAsInt=" + isForceQualityAsInt()
		 + ", findDisjunctiveSets=" + isFindDisjunctiveSets()
		 + "}";
	}

	public static void main(String args[]) {
		Settings settings = null;
		try {
			settings = new Settings();
		} catch (FactoryException e) {
			e.printStackTrace();
		} catch (MaceException e) {
			e.printStackTrace();
		}

		System.out.println(settings);
	}

}