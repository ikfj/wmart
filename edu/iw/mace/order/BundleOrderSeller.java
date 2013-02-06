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

import edu.iw.mace.environment.MaceException;

/**
 * Represents a bundle order for a seller Created: 20.08.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public class BundleOrderSeller extends AbstractBundleOrder {

	private OrderSeller sellerOrder = null;

	/**
	 * Constructor
	 * 
	 * @param sellerOrder
	 *            parent order to set
	 */
	public BundleOrderSeller(OrderSeller sellerOrder) throws MaceException {
		super(sellerOrder);
		this.sellerOrder = sellerOrder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.mace.order.AbstractBundleOrder#toXMLSpecific(org.w3c.dom.Document)
	 */
	@Override
	public Element toXMLSpecific(Document document) {
		return null;
	}

	/**
	 * @return Returns the gridSellerOrder.
	 */
	public OrderSeller getSellerOrder() {
		return sellerOrder;
	}

	/**
	 * @param gridSellerOrder
	 *            The gridSellerOrder to set.
	 */
	public void setSellerOrder(OrderSeller gridSellerOrder) {
		this.sellerOrder = gridSellerOrder;
	}

}
