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
package edu.iw.mace.outcome;

import edu.iw.mace.environment.Agent;
import edu.iw.mace.environment.Bundle;
import edu.iw.mace.environment.MaceException;
import edu.iw.mace.order.AbstractBundleOrder;
import edu.iw.utils.RandomGUID;
import edu.iw.utils.patterns.Entity;

/**
 * Abstract class for an outcome bean, i.e. a bean that encapsulates an agent,
 * the price and the order
 * 
 * Created: 25.09.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.0
 */
public abstract class AbstractOutcomeBean extends Entity {

	protected AbstractBundleOrder order = null;

	protected Agent agent = null;

	protected float price = 0.0f;

	protected float bid = 0.0f;

	protected float valuation = 0.0f;

	private Bundle bundle = null;

	// In which orderbook was the order?
	private int orderBookNumber = -1;

	/**
	 * Constructur
	 * 
	 * @param order
	 *            Order corresponding with this outcome bean
	 */
	public AbstractOutcomeBean(AbstractBundleOrder order) throws MaceException {
		this.agent = order.getAbstractOrder().getAgent();
		if (agent == null)
			throw new MaceException("Agent is null");
		this.bundle = order.getBundle();
		this.order = order;
		setId(agent.getId());
		RandomGUID guid = new RandomGUID();
		setId(guid.toString());
	}

	/**
	 * Compute the utility an agent has achieved with his outcome
	 * 
	 * @return the agent's utility
	 */
	public abstract double computeUtility();

	/**
	 * @return Returns the bundle.
	 */
	public Bundle getBundle() {
		return bundle;
	}

	/**
	 * @param bundle
	 *            The bundle to set.
	 */
	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}

	/**
	 * @return Returns the price.
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            The price to set.
	 */
	public void setPrice(double price) {
		this.price = (float) price;
	}

	/**
	 * @return Returns the bid.
	 */
	public double getBid() {
		return bid;
	}

	/**
	 * @param bid
	 *            The bid to set.
	 */
	public void setBid(double bid) {
		this.bid = (float) bid;
	}

	/**
	 * @return Returns the order.
	 */
	public AbstractBundleOrder getOrder() {
		return order;
	}

	/**
	 * @param order
	 *            The order to set.
	 */
	public void setOrder(AbstractBundleOrder order) {
		this.order = order;
		setId(order.getAbstractOrder().getAgent().getId());
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
		setId(agent.getId());
	}

	/**
	 * @return Returns the valuation.
	 */
	public double getValuation() {
		return valuation;
	}

	/**
	 * @param valuation
	 *            The valuation to set.
	 */
	public void setValuation(double valuation) {
		this.valuation = (float) valuation;
	}

	/**
	 * @return Returns the orderBookNumber.
	 */
	public int getOrderBookNumber() {
		return orderBookNumber;
	}

	/**
	 * @param orderBookNumber
	 *            The orderBookNumber to set.
	 */
	public void setOrderBookNumber(int orderBookNumber) {
		this.orderBookNumber = orderBookNumber;
	}
}
