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

/**
 * A bean that represents a particular allocation in one time slot
 * 
 * Created: 24.08.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.0
 */
public class OutcomeSellerSlotBean {

	// Percentage (partial) allocation
	private double percentage;

	// Buyer agent
	private Agent buyerAgent;

	/**
	 * Constructor
	 * 
	 * @param buyerAgent
	 *            The buyer who is allocated
	 * @param percentage
	 *            Percentage of the resources that are allocated to the buyer
	 */
	public OutcomeSellerSlotBean(Agent buyerAgent, double percentage) {
		this.percentage = percentage;
		this.buyerAgent = buyerAgent;
	}

	/**
	 * @return Returns the buyer agent.
	 */
	public Agent getBuyerAgent() {
		return buyerAgent;
	}

	/**
	 * @param agent
	 *            The buyer agent to set.
	 */
	public void setBuyerAgent(Agent agent) {
		this.buyerAgent = agent;
	}

	/**
	 * @return Returns the percentage.
	 */
	public double getPercentage() {
		return percentage;
	}

	/**
	 * @param percentage
	 *            The percentage to set.
	 */
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

}
