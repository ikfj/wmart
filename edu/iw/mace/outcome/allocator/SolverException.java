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
 * This class represents a solver exception.
 * 
 * Created: 16.09.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.0
 */

public class SolverException extends Exception {

	private static final long serialVersionUID = -3670882078119770285L;

	/**
	 * Empty Constructor
	 */
	public SolverException() {
		super();
	}

	/**
	 * @param message
	 *            Message of the exception
	 */
	public SolverException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 *            Exception
	 */
	public SolverException(Throwable cause) {
		super(cause);
	}

}
