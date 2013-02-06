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
 * Represents an agent
 * 
 * Created: 20.08.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public class Agent extends Entity implements XMLExporter {

	public static final String AGENT_BUYER = "buyer";

	public static final String AGENT_SELLER = "seller";

	/**
	 * Denotes the type of the agent (seller or buyer)
	 */
	private String type = "";

	/**
	 * @param type
	 *            Type of the Agent (buyer or seller)
	 */
	public Agent(String type) {
		this.type = type;
	}

	/**
	 * @param id
	 *            Name (id) of the agent
	 * @param type
	 *            Type of the Agent (buyer or seller)
	 */
	public Agent(String id, String type) {
		super(id);
		this.type = type;
	}

	/**
	 * @return <true>, if agent is a buyer, <false> if agent is a seller
	 */
	public boolean isBuyer() {
		if (type.equals(AGENT_BUYER)) {
			return true;
		}
		return false;
	}

	/**
	 * @return <true>, if agent is a seller, <false> if agent is a buyer
	 */
	public boolean isSeller() {
		if (type.equals(AGENT_SELLER)) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.utils.XMLExporter#toXML(org.w3c.dom.Document)
	 */
	public Element toXML(org.w3c.dom.Document document) {
		Element element = document.createElement("agent");
		element.setAttribute("id", getId());
		return element;
	}
}
