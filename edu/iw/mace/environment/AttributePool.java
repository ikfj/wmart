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

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.iw.utils.XMLExporter;
import edu.iw.utils.patterns.EntityPool;

/**
 * Represents a pool storing Attributes
 * 
 * Created: 31.08.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */

public class AttributePool extends EntityPool implements XMLExporter {

	private static final long serialVersionUID = -2730115240090197657L;

	/**
	 * @param id
	 *            Id value of the requested attribute
	 * @return The requested attribute instance
	 */
	public Attribute getAttribute(String id) {
		return (Attribute) getEntity(id);
	}

	/**
	 * @param attribute
	 *            Attribute instance of the requested attribute
	 * @return The requested attribute instance
	 */
	public Attribute getAttribute(Attribute attribute) {
		return (Attribute) getEntity(attribute);
	}

	/**
	 * @param index
	 *            Index value of the requested attribute
	 * @return The requested attribute instance
	 */
	public Attribute getAttribute(int index) {
		return (Attribute) getEntity(index);
	}

	/**
	 * @param a
	 */
	public void addAttribute(Attribute a) throws MaceException {
		addEntity(a);
	}

	/**
	 * Creates a new attribute and stores it in the pool
	 * 
	 * @param id
	 *            Parameter of the new attribute
	 * @return A new attribute, if the <id> attribute is not already in the
	 *         pool. If so, the existing attribute is returned.
	 */
	public Attribute createNewAttribute(String id) throws MaceException {
		if (getAttribute(id) == null) {
			Attribute a = new Attribute(id);
			addAttribute(a);
			return a;
		}
		return getAttribute(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.utils.XMLExporter#toXML(org.w3c.dom.Document)
	 */
	public Element toXML(Document document) {
		Iterator iterator = getValueIterator();
		Element attributes = document.createElement("attributes");
		while (iterator.hasNext()) {
			Attribute attribute = (Attribute) iterator.next();
			Element attributeElement = attribute.toXML(document);
			attributes.appendChild(attributeElement);
		}
		return attributes;
	}
}
