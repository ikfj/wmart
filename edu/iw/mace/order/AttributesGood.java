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

package edu.iw.mace.order;

import java.util.HashMap;

import edu.iw.mace.environment.Attribute;

/**
 * Represents an attribute characteristics for a good
 * 
 * Created: 31.08.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public class AttributesGood extends HashMap<String, Double> {

	private static final long serialVersionUID = -7222653545655785603L;

	/**
	 * Adds an attribute characteristic
	 * 
	 * @param id
	 *            Id of the attribute
	 * @param value
	 *            Value of the atribute
	 */
	public void addAttribute(String id, double value) {
		// if the key is already in the pool, remove it
		if (containsKey(id)) {
			remove(id);
		}
		put(id, new Double(value));
	}

	/**
	 * Adds an attribute characteristic
	 * 
	 * @param attribute
	 *            Attribute
	 * @param value
	 *            Value of the atribute
	 */
	public void addAttribute(Attribute attribute, double value) {
		if (containsKey(attribute.getId())) {
			remove(attribute.getId());
		}
		put(attribute.getId(), new Double(value));
	}

	/**
	 * Returns the (stored) attribute value of a given attribute
	 * 
	 * @param id
	 *            Id of the attribute
	 * @return Attribute value (0, if no attribute value was found)
	 */
	public double getAttributeValue(String id) {
		if (containsKey(id)) {
			return get(id).doubleValue();
		} else {
			return 0;
		}
	}

	/**
	 * Returns the (stored) attribute value of a given attribute
	 * 
	 * @param atribute
	 *            Attribute
	 * @return Attribute value (0, if no attribute value was found)
	 */
	public double getAttributeValue(Attribute atribute) {
		return getAttributeValue(atribute.getId());
	}

}
