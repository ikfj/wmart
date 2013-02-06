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

import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

import edu.iw.mace.environment.Agent;
import edu.iw.mace.environment.Bundle;
import edu.iw.mace.environment.Environment;
import edu.iw.mace.environment.MaceException;
import edu.iw.mace.environment.Market;
import edu.iw.mace.order.AbstractBundleOrder;
import edu.iw.mace.order.BundleOrderBuyer;
import edu.iw.mace.order.OrderBuyer;
import edu.iw.mace.order.OrderBuyerPool;
import edu.iw.mace.order.OrderSeller;
import edu.iw.mace.order.OrderSellerPool;
import edu.iw.mace.outcome.allocator.AbstractWinnerDetermination;
import edu.iw.mace.outcome.allocator.Allocator;
import edu.iw.mace.outcome.allocator.SolverException;
import edu.iw.mace.outcome.pricing.Pricing;
import edu.iw.utils.patterns.FactoryException;
import edu.iw.utils.patterns.ObserverSubject;

/**
 * This class manages the outcome, i.e. starts the computing allocations and
 * prices and stores all "outcome" information
 * 
 * Created: 21.08.2004
 * 
 * @author Bjˆrn Schnizler, University of Karlsruhe (TH)
 * @version 1.0
 */
public class Outcome extends ObserverSubject {

	private static Logger log = Logger.getLogger(Outcome.class);

	// Welfare
	private double welfare;

	// Runtime
	private double runtime;

	// Stores all buyer allocations
	private OutcomeBuyerBeanPool allocationBuyerBeanPool = null;

	// Stores all seller allocations
	private OutcomeSellerBeanPool allocationSellerBeanPool = null;

	// Reference to the market
	private Market market = null;

	// Reference to the current solver
	private AbstractWinnerDetermination currentSolver = null;

	/**
	 * Constructor of the outcome class
	 * 
	 * @param market
	 *            Reference to the market instance
	 */
	public Outcome(Market market) {
		super();
		this.market = market;
		allocationBuyerBeanPool = new OutcomeBuyerBeanPool();
		allocationSellerBeanPool = new OutcomeSellerBeanPool();
	}

	/**
	 * Computes an allocation, i.e. solves the winner determination problem
	 * 
	 * @throws SolverException
	 * @throws MaceException
	 * @throws LoggingException
	 * @throws FactoryException
	 */
	public void computeAllocation() throws SolverException, MaceException,
			FactoryException {

		Environment environment = market.getEnvironment();
		if (environment != null) {
			log.debug("Computing allocation");
			try {
				Allocator.computeAllocation(market, market
						.getOrderBookManagement().getDataModels());
			} catch (FactoryException e) {
				throw new MaceException(e);
			}
			log.debug("Logging RuntimeMessage");
		} else {
			throw new MaceException("Environment was null");
		}
	}

	/**
	 * Computes an outcome, i.e. solves the winner determination problem and
	 * computes prices
	 * 
	 * @throws SolverException
	 * @throws MaceException
	 * @throws LoggingException
	 * @throws FactoryException
	 */
	public void solve() throws SolverException, MaceException,
			FactoryException {

		computeAllocation();
		if (market.getEnvironment() != null) {
			log.debug("Computing prices");
			Pricing.computePrices(market);
		} else {
			throw new MaceException("Environment was null");
		}
	}

	/**
	 * Add a buyer allocation to this outcome
	 * 
	 * @param bean
	 *            Buyer allocation in form of a BuyerBean
	 */
	public void addAllocationBuyerBean(OutcomeBuyerBean bean)
			throws MaceException {
		allocationBuyerBeanPool.addBean(bean);
	}

	/**
	 * Generates an allocation seller bean. If an exisiting bean is found in the
	 * data pool, it is returned. Otherwise, a new one will be added.
	 * 
	 * @param order
	 *            The order for which an allocation bean should be generated
	 * @return AllocationBean
	 */
	public OutcomeSellerBean createAllocationSellerBean(
			AbstractBundleOrder order) throws MaceException {
		OutcomeSellerBean bean = allocationSellerBeanPool.getBean(order
				.getAbstractOrder().getAgent(), order.getBundle());
		if (bean == null) {
			bean = new OutcomeSellerBean(order);
			allocationSellerBeanPool.addBean(bean);
		}
		return bean;
	}

	/**
	 * Returns a seller bean for a given agent / bundle pair
	 * 
	 * @param agent
	 *            Seller agent
	 * @param bundle
	 *            Bundle
	 * @return Seller bean (<null> in case no bean is found)
	 */
	public OutcomeSellerBean getAllocationSellerBean(Agent agent, Bundle bundle) {
		return allocationSellerBeanPool.getBean(agent, bundle);
	}

	/**
	 * Returns an allocation bean for a given buyer agent
	 * 
	 * @param agent
	 *            Agent
	 * @return Buyer bean (<null> in case no bean is found)
	 */
	public OutcomeBuyerBean getAllocationBuyerBean(Agent agent) {
		return allocationBuyerBeanPool.getBean(agent);
	}

	/**
	 * Tests if a buyer is part of the allocation or not
	 * 
	 * @param agent
	 *            Buyer agent
	 * @return <true> if the buyer is part of the allocation, <false> otherwise
	 */
	public boolean buyerIsInAllocation(Agent agent) {
		if (allocationBuyerBeanPool.getBean(agent) != null) {
			return true;
		}
		return false;
	}

	/**
	 * Tests if a seller is part of the allocation or not
	 * 
	 * @param agent
	 *            Seller agent
	 * @return <true> if the seller is part of the allocation, <false> otherwise
	 */
	public boolean sellerIsInAllocation(Agent agent) {
		// Parse all bundles and check, if there is an allocation buyer for this
		// seller
		for (int a = 0; a < market.getEnvironment().getBundlePool().size(); a++) {
			if (allocationSellerBeanPool.getBean(agent, market.getEnvironment()
					.getBundlePool().getBundle(a)) != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a vector including all unsuccessful buyer orders, i.e. orders
	 * that are not part of the allocation
	 * 
	 * @param orderBuyerPool
	 *            Given buyer order pool
	 * @return Unsuccessful buyer orders
	 */
	public Vector<OrderBuyer> getUnsuccesfullBuyerOrders(
			OrderBuyerPool orderBuyerPool) {
		Vector<OrderBuyer> result = new Vector<OrderBuyer>();
		// Parse each order in the order poool
		Iterator iterator = orderBuyerPool.getValueIterator();
		while (iterator.hasNext()) {
			OrderBuyer orderBuyer = (OrderBuyer) iterator.next();
			if (!buyerIsInAllocation(orderBuyer.getAgent())) {
				result.add(orderBuyer);
			}
		}
		return result;
	}

	/**
	 * Returns a vector including all unsuccessful seller orders, i.e. orders
	 * that are not part of the allocation (percentage allocation=0)
	 * 
	 * @param orderSellerPool
	 *            Given seller order pool
	 * @return Unsuccessful seller orders
	 */
	public Vector<OrderSeller> getUnsuccesfullSellerOrders(
			OrderSellerPool orderSellerPool) {
		Vector<OrderSeller> result = new Vector<OrderSeller>();
		// Parse each order in the order poool
		Iterator iterator = orderSellerPool.getValueIterator();
		while (iterator.hasNext()) {
			OrderSeller orderSeller = (OrderSeller) iterator.next();
			if (!sellerIsInAllocation(orderSeller.getAgent())) {
				result.add(orderSeller);
			}
		}
		return result;
	}

	/**
	 * Returns the highest price (buyer-side) for a bundle. This method ignores
	 * quality characteristics.
	 * 
	 * @param bundle
	 *            Bundle
	 * @return Highest price in the current allocation
	 */
	public AbstractOutcomeBean getHighestSuccessfullBuyerPrice(Bundle bundle) {
		AbstractOutcomeBean highestOutcome = null;
		for (Iterator iterator = allocationBuyerBeanPool.getValueIterator(); iterator
				.hasNext();) {
			AbstractOutcomeBean buyerBean = (AbstractOutcomeBean) iterator
					.next();
			if (buyerBean.getBundle().equals(bundle)) {
				if (highestOutcome == null) {
					highestOutcome = buyerBean;
				} else {
					if (buyerBean.getPrice() > highestOutcome.getPrice()) {
						highestOutcome = buyerBean;
					}
				}
			}
		}
		return highestOutcome;
	}

	/**
	 * Returns all allocated seller beans
	 * 
	 * @return OutcomeSellerBeanPool including seller beans
	 */
	public OutcomeSellerBeanPool getAllocatedSellerBeans() throws MaceException {
		OutcomeSellerBeanPool data = new OutcomeSellerBeanPool();
		for (Iterator iterator = getAllocationSellerBeanPool()
				.getValueIterator(); iterator.hasNext();) {
			OutcomeSellerBean bean = (OutcomeSellerBean) iterator.next();
			if (bean.hasAllocatedSlots() == true) {
				data.addBean(bean);
			}
		}
		return data;
	}

	/**
	 * Computes the number of orders in the allocation (buyers+sellers)
	 * 
	 * @return Number of orders in the allocation
	 */
	public int computeNumberOrdersInAllocation() {
		return getAllocationBuyerBeanPool().size()
				+ getAllocationSellerBeanPool().getNumberAllocations();
	}

	/**
	 * Computes the welfare of all buyers
	 * 
	 * @return Welfare of all buyers
	 */
	public double getWelfareBuyer() {
		double welfareBuyer = 0;
		Iterator iterator = getAllocationBuyerBeanPool().getValueIterator();
		while (iterator.hasNext()) {
			OutcomeBuyerBean bean = (OutcomeBuyerBean) iterator.next();
			welfareBuyer += ((BundleOrderBuyer) bean.getOrder()).getSlots()
					* bean.getOrder().getBid();
		}
		return welfareBuyer;
	}

	/**
	 * Computes the welfare of all sellers
	 * 
	 * @return Welfare of all sellers
	 */
	public double getWelfareSeller() {
		double welfareSeller = 0;
		Iterator iterator = getAllocationSellerBeanPool().getValueIterator();
		while (iterator.hasNext()) {
			OutcomeSellerBean bean = (OutcomeSellerBean) iterator.next();
			welfareSeller += bean.getOverallPercentage()
					* bean.getOrder().getBid();
		}
		return welfareSeller;
	}

	/**
	 * Computes the budget of the current allocation (buyer prices - seller
	 * prices)
	 * 
	 * @return Budget
	 */
	public double computeBudget() {
		return getAllocationBuyerBeanPool().computePrices()
				- getAllocationSellerBeanPool().computePrices();
	}

	/**
	 * @return Returns the welfare.
	 */
	public double getWelfare() {
		return welfare;
	}

	/**
	 * @param welfare
	 *            The welfare to set.
	 */
	public void setWelfare(double welfare) {
		this.welfare = welfare;
	}

	/**
	 * @return Returns the allocationBuyerBeanPool.
	 */
	public OutcomeBuyerBeanPool getAllocationBuyerBeanPool() {
		return allocationBuyerBeanPool;
	}

	/**
	 * @param allocationBuyerBeanPool
	 *            The allocationBuyerBeanPool to set.
	 */
	public void setAllocationBuyerBeanPool(
			OutcomeBuyerBeanPool allocationBuyerBeanPool) {
		this.allocationBuyerBeanPool = allocationBuyerBeanPool;
	}

	/**
	 * @return Returns the allocationSellerBeanPool.
	 */
	public OutcomeSellerBeanPool getAllocationSellerBeanPool() {
		return allocationSellerBeanPool;
	}

	/**
	 * @param allocationSellerBeanPool
	 *            The allocationSellerBeanPool to set.
	 */
	public void setAllocationSellerBeanPool(
			OutcomeSellerBeanPool allocationSellerBeanPool) {
		this.allocationSellerBeanPool = allocationSellerBeanPool;
	}

	
	/**
	 * @return Returns the currentSolver.
	 */
	public AbstractWinnerDetermination getCurrentSolver() {
		return currentSolver;
	}

	/**
	 * @param currentSolver
	 *            The currentSolver to set.
	 */
	public void setCurrentSolver(AbstractWinnerDetermination currentSolver) {
		this.currentSolver = currentSolver;
	}

	/**
	 * Returns the runtime
	 * 
	 * @return runtime
	 */
	public double getRuntime() {
		return runtime;
	}

	/**
	 * Sets the runtime
	 * 
	 * @param runtime
	 *            Runtime to set
	 */
	public void setRuntime(double runtime) {
		this.runtime = runtime;
	}
	
	@Override
	public String toString() {
		return "Outcome"
		 + "{ welfare=" + getWelfare()
		 + ", runtime=" + getRuntime()
		 + ", allocationBuyerBeanPool=" + getAllocationBuyerBeanPool()
		 + ", allocationSellerBeanPool=" + getAllocationSellerBeanPool()
		 + ", currentSolver=" + getCurrentSolver()
		 + " }";
	}

	public static void main(String args[]) {
		Market market = null;
		try {
			market = new Market();
		} catch (MaceException e) {
			// TODO é©ìÆê∂ê¨Ç≥ÇÍÇΩ catch ÉuÉçÉbÉN
			e.printStackTrace();
		}
		Outcome outcome = new Outcome(market);
		System.out.println(outcome);
	}
}