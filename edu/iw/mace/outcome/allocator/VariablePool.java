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

import java.util.HashMap;

import edu.harvard.econcs.jopt.solver.mip.Variable;

/**
 * A utility class for managing the variable arrays for the JOPT package. This
 * class allows to generate and access different variable arrays.
 * 
 * Created: 30.03.2006
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @author Ikki Fujiwara (NII)
 * @version 1.0
 */

public class VariablePool {

	private HashMap<String, Variable> values = null;

	/**
	 * Empty constructor
	 */
	public VariablePool() {
		values = new HashMap<String, Variable>();
	}

	/**
	 * Add a variable with two parameters that form the "key" of this variable
	 * 
	 * @param variable
	 *            Variable
	 * @param a
	 *            Key 1
	 */
	public void add(Variable variable, int a) {
		values.put("" + a, variable);
	}

	/**
	 * Add a variable with two parameters that form the "key" of this variable
	 * 
	 * @param variable
	 *            Variable
	 * @param a
	 *            Key 1
	 * @param b
	 *            Key 2
	 */
	public void add(Variable variable, int a, int b) {
		values.put("" + a + "-" + b, variable);
	}
	
	/**
	 * Add a variable with three parameters that form the "key" of this variable
	 * 
	 * @param variable
	 *            Variable
	 * @param a
	 *            Key 1
	 * @param b
	 *            Key 2
	 * @param c
	 *            Key 3
	 */
	public void add(Variable variable, int a, int b, int c) {
		values.put("" + a + "-" + b + "-" + c, variable);
	}

	/**
	 * Add a variable with four parameters that form the "key" of this variable
	 * 
	 * @param variable
	 *            Variable
	 * @param a
	 *            Key 1
	 * @param b
	 *            Key 2
	 * @param c
	 *            Key 3
	 * @param d
	 *            Key 3
	 */
	public void add(Variable variable, int a, int b, int c, int d) {
		values.put("" + a + "-" + b + "-" + c + "-" + d, variable);
	}

	/**
	 * Returns a variable that is identified by two keys
	 * 
	 * @param a
	 *            Key 1
	 * @return The variable object
	 */
	public Variable get(int a) {
		return values.get("" + a);
	}


	/**
	 * Returns a variable that is identified by two keys
	 * 
	 * @param a
	 *            Key 1
	 * @param b
	 *            Key 2
	 * @return The variable object
	 */
	public Variable get(int a, int b) {
		return values.get("" + a + "-" + b);
	}
	
	/**
	 * Returns a variable that is identified by three keys
	 * 
	 * @param a
	 *            Key 1
	 * @param b
	 *            Key 2
	 * @param c
	 *            Key 3
	 * @return The variable object
	 */
	public Variable get(int a, int b, int c) {
		return values.get("" + a + "-" + b + "-" + c);
	}

	/**
	 * Returns a variable that is identified by four keys
	 * 
	 * @param a
	 *            Key 1
	 * @param b
	 *            Key 2
	 * @param c
	 *            Key 3
	 * @param d
	 *            Key 4
	 * @return The variable object
	 */
	public Variable get(int a, int b, int c, int d) {
		return values.get("" + a + "-" + b + "-" + c + "-" + d);
	}
}
