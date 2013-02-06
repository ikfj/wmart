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

/**
 * This class manages the solver settings, i.e. which solver engine is used for
 * the winner determination model and which parameters are applied
 * 
 * @author Ikki Fujiwara, NII
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */

public class SolverSettingsSingleton {

	public static final int LOCAL_DEFAULT = 0;
	public static final int LOCAL_LPSOLVE51 = 1;
	public static final int LOCAL_LPSOLVE55 = 2;
	public static final int LOCAL_CPLEX = 4;
	public static final int REMOTE = 10;
	
	private int timeLimit = 1500;
	private int solver = 0;
	private String host = "";
	private int port = 0;

	private static SolverSettingsSingleton instance = null;

	/**
	 * Private constructor
	 */
	private SolverSettingsSingleton() {

	}

	/**
	 * Returns an instance of this singleton class
	 * 
	 * @return Instance of the solver settings object
	 */
	public static SolverSettingsSingleton instance() {
		if (instance == null) {
			instance = new SolverSettingsSingleton();
		}
		return instance;
	}

	/**
	 * @return Returns the value.
	 */
	public int getSolver() {
		return solver;
	}

	/**
	 * @param value
	 *            The value to set.
	 */
	public void setSolver(int value) {
		this.solver = value;
	}

	/**
	 * @return Returns the timeLimit.
	 */
	public int getTimeLimit() {
		return timeLimit;
	}

	/**
	 * @param timeLimit
	 *            The timeLimit to set.
	 */
	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

}
