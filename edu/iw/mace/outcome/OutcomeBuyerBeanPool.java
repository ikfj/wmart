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
import edu.iw.mace.environment.MaceException;

/**
 * Stores and manages a set of buyer outcome beans
 * 
 * Created: 21.08.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.0
 */
public class OutcomeBuyerBeanPool extends AbstractOutcomeBeanPool {

	private static final long serialVersionUID = 4852420627537616373L;

	/**
	 * Empty constructor
	 */
	public OutcomeBuyerBeanPool() {
		super();
	}

	/**
	 * Adds a new buyer bean to the pool
	 * 
	 * @param bean
	 *            Buyer bean to add
	 */
	public void addBean(OutcomeBuyerBean bean) throws MaceException {
		put(bean.getAgent().getId(), bean);
	}

	/**
	 * Gets the buyer bean for a given agent
	 * 
	 * @param agent
	 *            Agent
	 * @return Buyer bean
	 */
	public OutcomeBuyerBean getBean(Agent agent) {
		return (OutcomeBuyerBean) getEntity(agent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.mace.outcome.AbstractOutcomeBeanPool#computeAverageUtility()
	 */
	@Override
	public double computeAverageUtility() {
		if (size() == 0) {
			return 0;
		}
		return computeUtility() / size();
	}
}