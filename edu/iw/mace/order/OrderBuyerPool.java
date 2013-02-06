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

/**
 * Pool to manage buyer orders
 * 
 * Created: 20.08.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public class OrderBuyerPool extends AbstractOrderPool {

	private static final long serialVersionUID = 7968656519453782943L;

	/**
	 * Constructor
	 */
	public OrderBuyerPool() {
		super();
	}

	/**
	 * Returns a buyer order of a given index
	 * 
	 * @param index
	 *            Index
	 * @return seller order
	 */
	public OrderBuyer getBuyerOrder(int index) {
		return (OrderBuyer) getOrder(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.mace.order.AbstractOrderPool#getInstance()
	 */
	@Override
	public AbstractOrderPool getInstance() {
		return new OrderBuyerPool();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.mace.order.AbstractOrderPool#toXML(org.w3c.dom.Document, int)
	 */
	@Override
	public Element toXML(Document document, int currentTime) {
		Element buyerorders = document.createElement("buyerorders");
		for (int x = 0; x < size(); x++) {
			OrderBuyer order = getBuyerOrder(x);
			if (order.getTimestamp() == currentTime) {
				buyerorders.appendChild(order.toXML(document));
			}
		}
		return buyerorders;
	}

}
