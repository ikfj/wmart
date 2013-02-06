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

package edu.iw.mace.order.splitting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

import edu.iw.mace.environment.AbstractAgentPool;
import edu.iw.mace.environment.Bundle;
import edu.iw.mace.environment.BundlePool;
import edu.iw.mace.environment.BuyerAgentPool;
import edu.iw.mace.environment.Good;
import edu.iw.mace.environment.GoodPool;
import edu.iw.mace.environment.MaceException;
import edu.iw.mace.environment.Market;
import edu.iw.mace.environment.SellerAgentPool;
import edu.iw.mace.order.AbstractOrder;
import edu.iw.mace.order.AbstractOrderPool;
import edu.iw.mace.order.OrderBook;
import edu.iw.mace.order.OrderBuyerPool;
import edu.iw.mace.order.OrderSellerPool;
import edu.iw.mace.outcome.allocator.MaceDataModel;
import edu.iw.mace.outcome.allocator.PreprocessingUtils;
import edu.iw.utils.patterns.AbstractProduct;

/**
 * Abstract class for a graph seperator. Given a graph and a partition decision,
 * instances of this class seperate the graph and the given orderbooks
 * 
 * Created: 15.11.2005
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public abstract class AbstractGraphSeparator implements AbstractProduct {

	protected Vector<NodeEdgeBean> separatedBeans = null;

	protected OrderBuyerPool removedOrdersBuyerPool = null;

	protected OrderSellerPool removedOrdersSellerPool = null;

	private static Logger log = Logger.getLogger(AbstractGraphSeparator.class);

	protected int numberRejectsBuyer = 0;

	protected int numberRejectsSeller = 0;

	/**
	 * Empty constructur; Inititalizes graph separator
	 */
	public AbstractGraphSeparator() {
		separatedBeans = new Vector<NodeEdgeBean>();
		removedOrdersBuyerPool = new OrderBuyerPool();
		removedOrdersSellerPool = new OrderSellerPool();
	}

	/**
	 * Separate a given graph
	 * 
	 * @param graphBuilder
	 *            The graph structure including partition decision
	 */
	abstract public void separateGraph(AbstractGraphBuilder graphBuilder)
			throws MaceException;

	/**
	 * Prepares a set of MaceDataModels as a preProcessing step. Given an order
	 * set, this method tries to identify disjunctive order sets which are
	 * subsequently split into several "smaller" order books.
	 * 
	 * @param market
	 *            Market instance
	 * @param orderBook
	 *            Orderbook
	 * @return Vector containing disjunctive MaceDataModels
	 */
	protected Vector<MaceDataModel> prepareGraphDataModels(Market market,
			OrderBook orderBook) throws MaceException {

		Vector<MaceDataModel> result = new Vector<MaceDataModel>();

		numberRejectsBuyer = 0;
		numberRejectsSeller = 0;

		// Only consider bundles with bids on
		BundlePool relevantBundles = new BundlePool();
		PreprocessingUtils.findRelevantBundles(relevantBundles, orderBook
				.getBuyerOrderPool());
		PreprocessingUtils.findRelevantBundles(relevantBundles, orderBook
				.getSellerOrderPool());

		Vector<GoodPool> disjunctiveSets = null;

		// Split order book in consideration of disjunctive sets?
		if (market.getSettings().isFindDisjunctiveSets()) {
			disjunctiveSets = PreprocessingUtils.getDisjunctiveSets(orderBook);
		} else {
			// No, so copy the goodpool to disjunctiveSets
			disjunctiveSets = new Vector<GoodPool>();
			disjunctiveSets.add(market.getEnvironment().getGoodPool());
		}

		log.info("Found " + disjunctiveSets.size() + " disjunctive order sets");

		for (int a = 0; a < disjunctiveSets.size(); a++) {
			// Create a new instance of the data model
			MaceDataModel maceDataModel = new MaceDataModel();
			maceDataModel.setAttributePool(market.getEnvironment()
					.getAttributePool());
			result.add(maceDataModel);

			GoodPool goods = disjunctiveSets.get(a);
			maceDataModel.setGoodPool(goods);
			// All goods contained in the goodPool belong to the same
			// orderbook
			BundlePool splittedBundlePool = new BundlePool();
			maceDataModel.setBundlePool(splittedBundlePool);
			for (Iterator goodsIterator = goods.getValueIterator(); goodsIterator
					.hasNext();) {
				splittedBundlePool.addEntityPool(relevantBundles
						.getAllBundles((Good) goodsIterator.next()));
			}
			// Find all buyer orders (and agents) bidding on these bundles
			OrderBook newOrderBook = new OrderBook();
			maceDataModel.setOrderBook(newOrderBook);

			BuyerAgentPool buyerAgentPool = new BuyerAgentPool();
			maceDataModel.setBuyerAgentPool(buyerAgentPool);

			prepareAbstractOrderBook(splittedBundlePool, buyerAgentPool,
					orderBook.getBuyerOrderPool(), newOrderBook
							.getBuyerOrderPool());

			SellerAgentPool sellerAgentPool = new SellerAgentPool();
			maceDataModel.setSellerAgentPool(sellerAgentPool);

			prepareAbstractOrderBook(splittedBundlePool, sellerAgentPool,
					orderBook.getSellerOrderPool(), newOrderBook
							.getSellerOrderPool());
		}

		return result;
	}

	/**
	 * Given a bundlePool, this method finds all orders and agents that bid on
	 * the bundles.
	 * 
	 * @param splittedBundlePool
	 *            The given subset of bundles
	 * @param agentPool
	 *            (Possibly Empty) Agent Pool to store the agents bidding on the
	 *            bundles
	 * @param orderPool
	 *            The original order pool
	 * @param newOrderPool
	 *            (Possibly Empty) Order Pool to store the orders for the given
	 *            bundles
	 */
	private void prepareAbstractOrderBook(BundlePool splittedBundlePool,
			AbstractAgentPool agentPool, AbstractOrderPool orderPool,
			AbstractOrderPool newOrderPool) throws MaceException {

		// Parse all orders
		for (Iterator orderIterator = orderPool.getValueIterator(); orderIterator
				.hasNext();) {
			AbstractOrder order = (AbstractOrder) orderIterator.next();
			boolean addThisBundleOrder = false;
			// Parse the given bundle pool
			for (Iterator splittedBundlePoolIterator = splittedBundlePool
					.getValueIterator(); splittedBundlePoolIterator.hasNext();) {
				Bundle bundle = (Bundle) splittedBundlePoolIterator.next();
				// Does this order contain any bid on this particular bundle?
				ArrayList bundleOrders = order.getBundleOrders(bundle);
				if (bundleOrders.size() > 0) {
					addThisBundleOrder = true;
				}
			}
			// If so, add it to the new order book and the new agent pool
			if (addThisBundleOrder) {
				newOrderPool.addOrder(order);
				if (!agentPool.contains(order.getAgent())) {
					agentPool.addAgent(order.getAgent());
				}
			}
		}
	}

	/**
	 * Separate a given orderbook
	 * 
	 * @param market
	 *            The market
	 * @param orderbook
	 *            The orderbook to separate
	 */
	abstract public void separateOrders(Market market, OrderBook orderbook)
			throws MaceException;

	/**
	 * @return Returns the separatedBeans.
	 */
	public Vector getSeparatedBeans() {
		return separatedBeans;
	}

	/**
	 * @param separatedBeans
	 *            The separatedBeans to set.
	 */
	public void setSeparatedBeans(Vector<NodeEdgeBean> separatedBeans) {
		this.separatedBeans = separatedBeans;
	}

	/**
	 * @return the numberRejectsBuyer
	 */
	public int getNumberRejectsBuyer() {
		return numberRejectsBuyer;
	}

	/**
	 * @param numberRejectsBuyer
	 *            the numberRejectsBuyer to set
	 */
	public void setNumberRejectsBuyer(int numberRejectsBuyer) {
		this.numberRejectsBuyer = numberRejectsBuyer;
	}

	/**
	 * @return the numberRejectsSeller
	 */
	public int getNumberRejectsSeller() {
		return numberRejectsSeller;
	}

	/**
	 * @param numberRejectsSeller
	 *            the numberRejectsSeller to set
	 */
	public void setNumberRejectsSeller(int numberRejectsSeller) {
		this.numberRejectsSeller = numberRejectsSeller;
	}

}
