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
 * Represents a bundle order for a buyer
 * 
 * Created: 20.08.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public class BundleOrderBuyer extends AbstractBundleOrder {

	private int slots=1;

	private OrderBuyer orderBuyer = null;

	private DivisibilityBundle divisibilityBundle = null;

	private DependenciesBundle dependenciesBundle = null;

	/**
	 * Constructor
	 * 
	 * @param orderBuyer
	 *            Parent order
	 */
	public BundleOrderBuyer(OrderBuyer orderBuyer) throws MaceException {
		super(orderBuyer);
		this.orderBuyer = orderBuyer;
		divisibilityBundle = new DivisibilityBundle();
		dependenciesBundle = new DependenciesBundle();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.mace.order.AbstractBundleOrder#toXMLSpecific(org.w3c.dom.Document)
	 */
	@Override
	public Element toXMLSpecific(Document document) {
		Element element = document.createElement("slots");
		element.setAttribute("value", String.valueOf(slots));
		return element;
	}

	/**
	 * @return Returns the slots.
	 */
	public int getSlots() {
		return slots;
	}

	/**
	 * @param slots
	 *            The slots to set.
	 */
	public void setSlots(int slots) {
		this.slots = slots;
	}

	/**
	 * @return Returns the gridBuyerOrder.
	 */
	public OrderBuyer getOrderBuyer() {
		return orderBuyer;
	}

	/**
	 * @param gridBuyerOrder
	 *            The gridBuyerOrder to set.
	 */
	public void setOrderBuyer(OrderBuyer gridBuyerOrder) {
		this.orderBuyer = gridBuyerOrder;
	}

	/**
	 * @return Returns the divisibilityBundle.
	 */
	public DivisibilityBundle getDivisibilityBundle() {
		return divisibilityBundle;
	}

	/**
	 * @param divisibilityBundle
	 *            The divisibilityBundle to set.
	 */
	public void setDivisibilityBundle(DivisibilityBundle divisibilityBundle) {
		this.divisibilityBundle = divisibilityBundle;
	}

	/**
	 * @return Returns the dependenciesBundle.
	 */
	public DependenciesBundle getDependenciesBundle() {
		return dependenciesBundle;
	}

	/**
	 * @param dependenciesBundle
	 *            The dependenciesBundle to set.
	 */
	public void setDependenciesBundle(DependenciesBundle dependenciesBundle) {
		this.dependenciesBundle = dependenciesBundle;
	}
}
