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
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.iw.mace.environment.Agent;
import edu.iw.mace.environment.Attribute;
import edu.iw.mace.environment.Bundle;
import edu.iw.mace.environment.Good;
import edu.iw.mace.environment.MaceException;
import edu.iw.utils.patterns.EntityPool;

/**
 * Manages orders An order comprises several bundleOrders
 * 
 * Created: 20.08.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public abstract class AbstractOrderPool extends EntityPool {

	private static Logger log = Logger.getLogger(AbstractOrderPool.class);

	/**
	 * Empty constructur
	 */
	public AbstractOrderPool() {
		super();
	}

	/**
	 * Returns an order
	 * 
	 * @param index
	 * @return An order at the given index
	 */
	public AbstractOrder getOrder(int index) {
		return (AbstractOrder) getEntity(index);
	}

	/**
	 * Adds an order to the current pool
	 * 
	 * @param order
	 *            Order to add
	 */
	public void addOrder(AbstractOrder order) throws MaceException {
		addEntity(order);
	}

	/**
	 * Returns the number of bids (not orders) of this pool
	 * 
	 * @return Number of bids contained in this pool
	 */
	public int computeNumberBids() {
		int numberBids = 0;
		int size = size();
		for (int x = 0; x < size; x++) {
			numberBids += getOrder(x).getBundleOrders().size();
		}
		return numberBids;
	}

	/**
	 * Returns the order of a given agent
	 * 
	 * @param agent
	 *            Given agent
	 * @return Order if the agent has an order in the pool, <null> if no order
	 *         is found
	 */
	public AbstractOrder getOrder(Agent agent) {
		int size = size();
		for (int x = 0; x < size; x++) {
			if (getOrder(x).getAgent().equals(agent)) {
				return getOrder(x);
			}
		}
		return null;
	}

	/**
	 * Checks, if an agent has an order in the pool
	 * 
	 * @param agent
	 *            Agent to search for
	 * @return <true> if the agent has an order, <false> if not
	 */
	public boolean hasOrder(Agent agent) {
		if (getOrder(agent) != null) {
			return true;
		}
		return false;
	}

	/**
	 * Checks, if an agent has an order in the pool containing a bundle with a
	 * specific good at a given time slot
	 * 
	 * @param agent
	 *            Agent to search for
	 * @param good
	 *            Good to search for
	 * @param time
	 *            Current time slot
	 * @return <true> if the agent has an order, <false> if not
	 */
	public boolean hasOrder(Agent agent, Good good, int time) {
		AbstractOrder order = getOrder(agent);
		if (order == null) {
			return false;
		}
		for (Iterator<AbstractBundleOrder> bundleIterator = order
				.getBundleOrders().values().iterator(); bundleIterator
				.hasNext();) {
			AbstractBundleOrder bundleOrder = (AbstractBundleOrder) bundleIterator
					.next();
			if (bundleOrder.getBundle().contains(good)) {
				if (bundleOrder.getEarly() <= time
						&& bundleOrder.getLatest() >= time) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns all Bundle orders of a given bundle and agent
	 * 
	 * @param bundle
	 *            Bundle to look for
	 * @param agent
	 *            Agent to look for
	 * @return Arraylist containing all relevant orders
	 */
	private ArrayList<AbstractBundleOrder> getBundleOrderList(Bundle bundle,
			Agent agent) {
		int size = size();
		for (int x = 0; x < size; x++) {
			AbstractOrder order = getOrder(x);
			if (order.getAgent().equals(agent)) {
				// Get all orders for a given bundle
				ArrayList<AbstractBundleOrder> bundleOrderList = order
						.getBundleOrders(bundle);
				if (bundleOrderList != null && bundleOrderList.size() > 0) {
					return bundleOrderList;
				}
			}
		}
		// If nothing is found, return an empty list
		return new ArrayList<AbstractBundleOrder>();
	}

	/**
	 * Returns an orderpool of all orders by a given agent
	 * 
	 * @param agent
	 *            Agent to search for
	 * @return An orderpool containing orders from an agent
	 */
	public AbstractOrderPool getOrderPool(Agent agent) throws MaceException {
		AbstractOrderPool result = getInstance();
		int size = size();
		for (int x = 0; x < size; x++) {
			AbstractOrder order = getOrder(x);
			if (order.getAgent().equals(agent)) {
				result.addOrder(order);
			}
		}
		return result;
	}

	/**
	 * Removes all orders from an agent
	 * 
	 * @param agent
	 *            Agent
	 * @return <true> if orders were deleted, <false> otherwise
	 */
	public boolean removeOrders(Agent agent) {
		Vector<AbstractOrder> toRemove = new Vector<AbstractOrder>();
		for (int a = 0; a < size(); a++) {
			if (getOrder(a).getAgent().equals(agent)) {
				toRemove.add(getOrder(a));
			}
		}
		for (int a = 0; a < toRemove.size(); a++) {
			removeEntity(toRemove.get(a));
		}
		if (toRemove.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the first bundle order of a given agent
	 * 
	 * @param bundle
	 *            Bundle to look for
	 * @param agent
	 *            Agent
	 * @return Bundleorder
	 */
	public AbstractBundleOrder getFirstBundleOrder(Bundle bundle, Agent agent)
			throws OrderException {
		ArrayList result = getBundleOrderList(bundle, agent);
		if (result.size() == 0) {
			return null;
		}

		if (result.size() > 1) {
			throw new OrderException("Found " + result.size()
					+ " bundles for bundleOrder search with " + bundle.getId()
					+ " for agent " + agent.getId());
		}
		return (AbstractBundleOrder) result.get(0);
	}

	/**
	 * Sums up all valuations of the orders contained in this pool
	 * 
	 * @return Sum of all valuations
	 */
	public double computeValuations() {
		double result = 0.0d;
		AbstractOrder order = null;
		AbstractBundleOrder bundleOrder = null;
		int size = size();
		for (int a = 0; a < size; a++) {
			order = getOrder(a);
			Iterator bundleOrderIterator = order.getBundleOrders().values()
					.iterator();
			while (bundleOrderIterator.hasNext()) {
				bundleOrder = (AbstractBundleOrder) bundleOrderIterator.next();
				result += bundleOrder.getValuation();
			}
		}
		return result;
	}

	/**
	 * Sums up all bids (values) of the orders contained in this pool
	 * 
	 * @return Sum of all valuations
	 */
	public double computeBids() {
		double result = 0.0d;
		AbstractOrder order = null;
		AbstractBundleOrder bundleOrder = null;
		int size = size();
		for (int a = 0; a < size; a++) {
			order = getOrder(a);
			Iterator bundleOrderIterator = order.getBundleOrders().values()
					.iterator();
			while (bundleOrderIterator.hasNext()) {
				bundleOrder = (AbstractBundleOrder) bundleOrderIterator.next();
				result += bundleOrder.getBid();
			}
		}
		if (result == 0) {
			log.warn("Sum of bids = 0 ");
		}
		return result;
	}
	
	/**
	 * Function which computes the maximum value of a good/attribute combination
	 * in an order
	 * 
	 * @param good
	 *            Good
	 * @param attribute
	 *            Attribute
	 * @return Maximum Value
	 */
	public double getMaxAttributeValue(Good good, Attribute attribute) {
		double value = -1;
		Iterator valueIterator = getValueIterator();
		while (valueIterator.hasNext()) {
			AbstractOrder order = (AbstractOrder) valueIterator.next();
			Iterator bundleIterator = order.getBundleOrders().values()
					.iterator();
			while (bundleIterator.hasNext()) {
				AbstractBundleOrder bundleOrder = (AbstractBundleOrder) bundleIterator
						.next();
				if (bundleOrder.getBundle().contains(good)) {
					double bundleValue = bundleOrder.getQuality().getQuality(
							good, attribute.getId());
					if (bundleValue > value) {
						value = bundleValue;
					}
				}
			}
		}
		return value;
	}

	/**
	 * Returns an instance of the orderpool
	 * 
	 * @return Instance of the order pool
	 */
	public abstract AbstractOrderPool getInstance();

	/**
	 * Exports the orderbook for a given time
	 * 
	 * @param document
	 *            Document root
	 * @param currentTime
	 *            Time
	 * @return XML Element containing all relevant information
	 */
	public abstract Element toXML(Document document, int currentTime);

}