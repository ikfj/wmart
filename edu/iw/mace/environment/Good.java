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

import org.w3c.dom.Element;

import edu.iw.utils.XMLExporter;
import edu.iw.utils.patterns.Entity;

/**
 * Represents a good
 * 
 * Created: 20.08.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1 *
 */
public class Good extends Entity implements XMLExporter {

	/**
	 * Attributes of the Good
	 */
	private AttributePool attributes = null;

	/**
	 * Empty constructor
	 * 
	 * @throws MaceException
	 */
	public Good() throws MaceException {
		this("");
	}

	/**
	 * @param id
	 *            Id of the new good
	 * @throws MaceException
	 */
	public Good(String id) throws MaceException {
		super(id);
		attributes = new AttributePool();
	}

	/**
	 * Adds an attribute to the good
	 * 
	 * @param attribute
	 *            Attribute to add
	 */
	public void addAttribute(Attribute attribute) throws MaceException {
		attributes.addAttribute(attribute);
	}

	/**
	 * @param index
	 *            Index value of the requested attribute
	 * @return Attribute instance
	 */
	public Attribute getAttribute(int index) {
		return attributes.getAttribute(index);
	}

	/**
	 * @return The number of attributes of the good
	 */
	public int getNumberAttributes() {
		return attributes.size();
	}

	/**
	 * Searches for the existence of a given attribute
	 * 
	 * @return <true> if the good contains this attribute, <false> otherwise
	 */
	public boolean containsAttribute(String attributeId) {
		return attributes.contains(attributeId);
	}

	/**
	 * Generates a key containing good1 and good2 index
	 * 
	 * @param good1
	 *            Good1
	 * @param good2
	 *            Good2
	 * @return A unique key of Good1 and Good2
	 */
	public static String generateKey(Good good1, Good good2) throws NullPointerException {
		// Sort the goods
		if (good1 == null)
			throw new NullPointerException("Good1 is null");
		if (good2 == null)
			throw new NullPointerException("Good2 is null");
		if (good1.getId().compareTo(good2.getId()) < 0) {
			Good good3 = good1;
			good1 = good2;
			good2 = good3;
		} else {
		}
		// Then return both ids as a new Id
		return good1.getId() + good2.getId();
	}

	/**
	 * Clones a good
	 * 
	 * @return A cloned good
	 */
	public Good cloneGood() throws MaceException {
		Good result = new Good();
		// Copy the id
		result.setId(getId());
		// Copy the attributes
		for (int a = 0; a < getNumberAttributes(); a++) {
			result.addAttribute(getAttribute(a));
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.utils.XMLExporter#toXML(org.w3c.dom.Document)
	 */
	public Element toXML(org.w3c.dom.Document document) {
		Element element = document.createElement("good");
		element.setAttribute("id", getId());
		return element;
	}

}
