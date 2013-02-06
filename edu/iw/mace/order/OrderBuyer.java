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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.iw.mace.environment.Agent;
import edu.iw.mace.environment.MaceException;

/**
 * Represents an order of a buyer
 * 
 * Created: 20.08.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1 *
 */
public class OrderBuyer extends AbstractOrder {

	/**
	 * Constructur
	 * 
	 * @param agent
	 *            The buyer agent
	 */
	public OrderBuyer(Agent agent) throws MaceException {
		super(agent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.utils.XMLExporter#toXML(org.w3c.dom.Document)
	 */
	@Override
	public Element toXML(Document document) {
		Element element = document.createElement("buyerorder");
		element.setAttribute("agent", getAgent().getId());
		for (int x = 0; x < bundleOrders.size(); x++) {
			BundleOrderBuyer bundleOrder = (BundleOrderBuyer) bundleOrders
					.values().toArray()[x];
			element.appendChild(bundleOrder.toXML(document));
		}
		return element;
	}
}
