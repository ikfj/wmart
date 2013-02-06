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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import edu.iw.mace.environment.Agent;
import edu.iw.mace.environment.Bundle;
import edu.iw.mace.environment.BundlePool;
import edu.iw.mace.environment.MaceException;

/**
 * Stores and manages a set of seller outcome beans
 * 
 * Created: 21.08.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.0
 */

public class OutcomeSellerBeanPool extends AbstractOutcomeBeanPool {

	private static final long serialVersionUID = 5631328395612265421L;

	private static Logger log = Logger.getLogger(OutcomeSellerBeanPool.class);

	/**
	 * Empty constructor
	 */
	public OutcomeSellerBeanPool() {
		super();
	}

	/**
	 * Adds a new outcome seller bean to this pool
	 * 
	 * @param bean
	 *            The bean to add
	 */
	public void addBean(OutcomeSellerBean bean) throws MaceException {
		if (bean.getBundle().getId().equals(""))
			throw new MaceException("No bundle Id found");
		if (bean.getAgent().getId().equals(""))
			throw new MaceException("No agent Id found");
		put(bean.getAgent().getId() + bean.getBundle().getId(), bean);
	}

	/**
	 * Returns a bean from this pool
	 * 
	 * @param agent
	 *            The seller to look for ...
	 * @param bundle
	 *            The bundle to look for ...
	 * @return OutcomeSellerBean
	 */
	public OutcomeSellerBean getBean(Agent agent, Bundle bundle) {
		if (contains(agent.getId() + bundle.getId()) == false) {
			return null;
		}
		return (OutcomeSellerBean) getEntity(agent.getId() + bundle.getId());
	}

	/**
	 * Returns all Seller Beans for a specific buyer
	 * 
	 * @param buyerAgent
	 *            The buyer agent to look for ...
	 * @return ArrayList An ArrayList containing all seller beans for that
	 *         allocate to the particular buyer
	 */
	public ArrayList<OutcomeSellerBean> getAllocatedBeans(Agent buyerAgent) {
		ArrayList<OutcomeSellerBean> result = new ArrayList<OutcomeSellerBean>();
		Iterator iterator = getValueIterator();
		while (iterator.hasNext()) {
			OutcomeSellerBean sellerBean = (OutcomeSellerBean) iterator.next();
			// Get all allocated Slots of this seller
			for (Iterator<Integer> slotIterator = sellerBean.getSlotsKeySet()
					.iterator(); slotIterator.hasNext();) {
				if (sellerBean.hasAllocatedSlots(buyerAgent, slotIterator
						.next().intValue())) {
					// yes, add to the list
					if (result.contains(sellerBean) == false) {
						result.add(sellerBean);
					}
				}
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.mace.outcome.AbstractOutcomeBeanPool#computeAverageUtility()
	 */
	@Override
	public double computeAverageUtility() {
		/* Compute number of different agents */
		HashMap uniqueAgents = getUniqueSellers();
		if (uniqueAgents.size() == 0) {
			return 0;
		}
		return computeUtility() / uniqueAgents.size();
	}

	/**
	 * Returns a set containing unique sellers in this pool
	 * 
	 * @return HashMap containing unique sellers (key: agentId)
	 */
	private HashMap<String, Agent> getUniqueSellers() {
		HashMap<String, Agent> result = new HashMap<String, Agent>();
		Iterator iterator = getValueIterator();
		while (iterator.hasNext()) {
			OutcomeSellerBean bean = (OutcomeSellerBean) iterator.next();
			if (bean.getAgent().getId().equals("")) {
				log.error("getUniqueSellers: No agent name found for seller");
			}
			if (!result.containsKey(bean.getAgent().getId())) {
				result.put(bean.getAgent().getId(), bean.getAgent());
			}
		}
		return result;
	}

	/**
	 * Returns the number of (unique) sellers in this pool
	 * 
	 * @return Number of sellers
	 */
	public int getNumberOfSellers() {
		return getUniqueSellers().size();
	}

	/**
	 * @return the number of allocations in this pool
	 */
	public int getNumberAllocations() {
		int result = 0;
		Iterator iterator = getValueIterator();
		while (iterator.hasNext()) {
			OutcomeSellerBean bean = (OutcomeSellerBean) iterator.next();
			if (bean.hasAllocatedSlots()) {
				result += 1;
			}
		}
		return result;
	}

	/**
	 * Generates a list of outcome beans that coorespond to a given bundle pool
	 * and a given agent, i.e. selects the relevant beans for the bundles in the
	 * given pool
	 * 
	 * @param sellerAgent
	 *            The agent to look for ...
	 * @param bundlePool
	 *            The bundle pool
	 * @return A list of AllocationSellerBeans of the agent
	 */
	public ArrayList<OutcomeSellerBean> getBeanList(Agent sellerAgent,
			BundlePool bundlePool) {
		ArrayList<OutcomeSellerBean> result = new ArrayList<OutcomeSellerBean>();
		for (int a = 0; a < bundlePool.size(); a++) {
			Bundle bundle = bundlePool.getBundle(a);
			OutcomeSellerBean bean = getBean(sellerAgent, bundle);
			if (bean != null) {
				result.add(bean);
			}
		}
		return result;
	}

}