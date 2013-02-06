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

/**
 * A Splitting exception
 * 
 * Created 03.07.2006
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 */

public class SplittingException extends Exception {

	private static final long serialVersionUID = 6002327437404949793L;

	/**
	 * Constructor
	 * 
	 * @param message
	 *            Message of the exception
	 */
	public SplittingException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param e
	 *            Exception
	 */
	public SplittingException(Exception e) {
		super(e);
	}
}
