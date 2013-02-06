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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.iw.mace.environment.Agent;
import edu.iw.mace.environment.Bundle;
import edu.iw.mace.environment.MaceException;
import edu.iw.utils.XMLExporter;
import edu.iw.utils.patterns.Entity;

/**
 * This class represents an abstract order. An order comprises a set of bundle
 * orders. Dependening on the underlying winner determination model, an abstract
 * order can be a set of OR or XOR concatanted atomic bids.
 * 
 * Created: 20.08.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public abstract class AbstractOrder extends Entity implements
		XMLExporter {

	protected Agent agent = null;

	protected HashMap<String, AbstractBundleOrder> bundleOrders;

	protected int timestamp = 0;

	/**
	 * Constructor of the class
	 * 
	 * @param agent
	 *            Owner agent of this order
	 */
	public AbstractOrder(Agent agent) throws MaceException {
		super();
		// Bundle orders are stored in a HashMap
		bundleOrders = new HashMap<String, AbstractBundleOrder>();
		if (agent == null)
			throw new MaceException("Agent is null");
		this.agent = agent;
		generateNewId();
	}

	/**
	 * Adds a bundle order to this order
	 * 
	 * @param bundleOrder
	 *            The bundle order to add
	 */
	public void addBundleOrder(AbstractBundleOrder bundleOrder) {
		bundleOrders.put(bundleOrder.getId(), bundleOrder);
	}

	/**
	 * Removes a bundle order from this order
	 * 
	 * @param bundleOrder
	 *            Bundle order to remove
	 */
	public void removeBundleOrder(AbstractBundleOrder bundleOrder) {
		bundleOrders.remove(bundleOrder.getId());
	}

	/**
	 * Returns all bundle orders in this order for a given bundle
	 * 
	 * @param bundle
	 *            Bundle to search for
	 * @return ArrayList containing all bundle orders for the given bundle
	 */
	public ArrayList<AbstractBundleOrder> getBundleOrders(Bundle bundle) {
		ArrayList<AbstractBundleOrder> result = new ArrayList<AbstractBundleOrder>();
		for (Iterator<AbstractBundleOrder> bundleIterator = bundleOrders
				.values().iterator(); bundleIterator.hasNext();) {
			AbstractBundleOrder bundleOrder = (AbstractBundleOrder) bundleIterator
					.next();
			if (bundleOrder.getBundle().equals(bundle)) {
				result.add(bundleOrder);
			}
		}
		return result;
	}

	/**
	 * Computes the minimal early time range of all bundle orders in this order
	 * 
	 * @return Minimal early time slot
	 */
	public int getMinEarly() throws OrderException {
		int result = 10000;
		for (Iterator<AbstractBundleOrder> bundleIterator = bundleOrders
				.values().iterator(); bundleIterator.hasNext();) {
			AbstractBundleOrder bundleOrder = (AbstractBundleOrder) bundleIterator
					.next();
			if (bundleOrder.getEarly() < result) {
				result = bundleOrder.getEarly();
			}
		}
		if (result == 10000) {
			throw new OrderException(
					"No min early computed. No bundle found in this order");
		}
		return result;
	}

	/**
	 * Computes the maximal latest time range of all bundle orders in this order
	 * 
	 * @return Maximal latest time slot
	 */
	public int getMaxLate() throws OrderException {
		int result = -1;
		for (Iterator<AbstractBundleOrder> bundleIterator = bundleOrders
				.values().iterator(); bundleIterator.hasNext();) {
			AbstractBundleOrder bundleOrder = (AbstractBundleOrder) bundleIterator
					.next();
			if (bundleOrder.getLatest() > result)
				result = bundleOrder.getLatest();
		}
		if (result == -1) {
			throw new OrderException(
					"No max late computed. No bundle found in this order with a size of "
							+ this.getBundleOrders().size());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.utils.XMLExporter#toXML(org.w3c.dom.Document)
	 */
	public abstract Element toXML(Document document);

	/**
	 * @param gridBundleOrder
	 *            The gridBundleOrder to set.
	 */
	public void setBundleOrders(
			HashMap<String, AbstractBundleOrder> gridBundleOrder) {
		this.bundleOrders = gridBundleOrder;
	}

	/**
	 * @return Returns the timestamp.
	 */
	public int getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            The timestamp to set.
	 */
	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return Returns the agent.
	 */
	public Agent getAgent() {
		return agent;
	}

	/**
	 * @param agent
	 *            The agent to set.
	 */
	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	/**
	 * @return Returns the gridBundleOrder.
	 */
	public HashMap<String, AbstractBundleOrder> getBundleOrders() {
		return bundleOrders;
	}

}