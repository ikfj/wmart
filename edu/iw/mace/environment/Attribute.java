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
 * Represents an attribute of a good
 * 
 * Created: 31.08.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public class Attribute extends Entity implements XMLExporter {

	/**
	 * @param id
	 *            The id (name) of the attribute
	 */
	public Attribute(String id) {
		super(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.utils.XMLExporter#toXML(org.w3c.dom.Document)
	 */
	public Element toXML(org.w3c.dom.Document document) {
		Element element = document.createElement("attribute");
		element.setAttribute("id", getId());
		return element;
	}
}
