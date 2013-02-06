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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import edu.iw.mace.environment.Bundle;
import edu.iw.mace.environment.MaceException;
import edu.iw.mace.environment.Market;
import edu.iw.mace.order.splitting.Splitter;
import edu.iw.mace.order.splitting.SplitterFactory;
import edu.iw.mace.order.splitting.SplittingException;
import edu.iw.mace.order.splitting.SplittingStatisticsBean;
import edu.iw.mace.outcome.Outcome;
import edu.iw.mace.outcome.OutcomeBuyerBean;
import edu.iw.mace.outcome.OutcomeSellerBean;
import edu.iw.mace.outcome.allocator.MaceDataModel;
import edu.iw.utils.patterns.FactoryException;

/**
 * Class to manage order books
 * 
 * Created: 04.11.2005
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public class OrderbookManagement {

	private OrderBook orderBook;

	private Vector<MaceDataModel> dataModels = null;

	private SplittingStatisticsBean splittingStatisticsBean = null;

	/**
	 * Constructor
	 */
	public OrderbookManagement() {
		orderBook = new OrderBook();
		splittingStatisticsBean = new SplittingStatisticsBean();
	}

	/**
	 * Splits an orderbook
	 * 
	 * @param market
	 *            Market instance
	 * @throws SplittingException
	 * @throws LoggingException
	 */
	public void split(Market market) throws SplittingException {
		// Reset edges and nodes in the market
		Splitter splitter;
		try {
			splitter = (Splitter) SplitterFactory.instance()
					.createAbstractProduct();
			splitter.init(market, orderBook);
			dataModels = splitter.split();
		} catch (FactoryException e) {
			throw new SplittingException(e);
		}
		splittingStatisticsBean = splitter.generateStatistics();
	}

	/**
	 * Removes all executed orders (on an order level!) from an order book
	 * 
	 * @param outcome
	 *            Outcome
	 */
	public void removeExecuted(Outcome outcome) throws OrderException {
		for (Iterator iterator = outcome.getAllocationBuyerBeanPool()
				.getValueIterator(); iterator.hasNext();) {
			OutcomeBuyerBean buyerBean = (OutcomeBuyerBean) iterator.next();
			if (getOrderBook().getBuyerOrderPool().removeEntity(
					buyerBean.getOrder().getAbstractOrder()) == false) {
				throw new OrderException("Could not remove order from agent "
						+ buyerBean.getAgent().getId());
			}
		}
		for (Iterator iterator = outcome.getAllocationSellerBeanPool()
				.getValueIterator(); iterator.hasNext();) {
			OutcomeSellerBean sellerBean = (OutcomeSellerBean) iterator.next();
			if (getOrderBook().getSellerOrderPool().removeEntity(
					sellerBean.getOrder().getAbstractOrder()) == false) {
				throw new OrderException("Could not remove order from agent "
						+ sellerBean.getAgent().getId());
			}
		}
	}

	/**
	 * Solves the problem when a buyer or seller bids on A XOR A to A XOR A'
	 * 
	 * @param market
	 *            Market instance
	 */
	public void dissolveEqualXORBids(Market market) throws MaceException {
		dissolveEqualXORBids(market, orderBook.getBuyerOrderPool());
		dissolveEqualXORBids(market, orderBook.getSellerOrderPool());
	}

	/**
	 * Solves the problem when a buyer or seller bids on A XOR A to A XOR A' for
	 * a given orderbook
	 * 
	 * @param market
	 *            Market instance
	 * @param orderPool
	 *            Orderpool
	 */
	private void dissolveEqualXORBids(Market market, AbstractOrderPool orderPool)
			throws MaceException {
		Iterator iterator = orderPool.getValueIterator();
		while (iterator.hasNext()) {
			AbstractOrder order = (AbstractOrder) iterator.next();
			Iterator bundleOrderIterator = order.getBundleOrders().values()
					.iterator();
			HashMap<String, Bundle> currentBundles = new HashMap<String, Bundle>();
			while (bundleOrderIterator.hasNext()) {
				AbstractBundleOrder bundleOrder = (AbstractBundleOrder) bundleOrderIterator
						.next();
				Bundle bundle = bundleOrder.getBundle();
				// Xor bid on the same bundle?
				if (currentBundles.containsKey(bundle.getId())) {
					Bundle newBundle = bundle.cloneBundle();
					newBundle.setId("_" + bundle.getId());
					while (market.getEnvironment().getBundlePool().contains(
							newBundle)
							|| currentBundles.containsKey(newBundle.getId())) {
						String id = "_" + newBundle.getId();
						newBundle.setId(id);
					}
					market.getEnvironment().getBundlePool()
							.addBundle(newBundle);
					bundleOrder.setBundle(newBundle);
					newBundle.setName(bundle.getName());
					currentBundles.put(newBundle.getId(), newBundle);
				} else {
					currentBundles.put(bundle.getId(), bundle);
				}
			}
		}
	}

	/**
	 * Resets all order books
	 */
	public void reset() {
		orderBook.reset();
	}

	/**
	 * @return Returns the orderBook.
	 */
	public OrderBook getOrderBook() {
		return orderBook;
	}

	/**
	 * @param orderBook
	 *            The orderBook to set.
	 */
	public void setOrderBook(OrderBook orderBook) {
		this.orderBook = orderBook;
	}

	/**
	 * @return Returns the dataModels.
	 */
	public Vector<MaceDataModel> getDataModels() {
		return dataModels;
	}

	/**
	 * @param dataModels
	 *            The dataModels to set.
	 */
	public void setDataModels(Vector<MaceDataModel> dataModels) {
		this.dataModels = dataModels;
	}

	/**
	 * @return the splittingStatisticsBean
	 */
	public SplittingStatisticsBean getSplittingStatisticsBean() {
		return splittingStatisticsBean;
	}

	/**
	 * @param splittingStatisticsBean
	 *            the splittingStatisticsBean to set
	 */
	public void setSplittingStatisticsBean(
			SplittingStatisticsBean splittingStatisticsBean) {
		this.splittingStatisticsBean = splittingStatisticsBean;
	}

	@Override
	public String toString() {
		String s = "OrderbookManagement"
		 + "{ orderBook=" + getOrderBook()
		 + ", splittingStatisticsBean=" + getSplittingStatisticsBean()
		 + ", dataModels{";
		if (getDataModels() != null) {
			for (Iterator<MaceDataModel> i = getDataModels().iterator(); i
					.hasNext();) {
				MaceDataModel datamodel = (MaceDataModel) i.next();
				s = s + " " + datamodel + ",";
			}
		}
		s = s + " }";
		return s + " }";
	}

	public static void main(String args[]) {
		OrderbookManagement orderbookmanagement = new OrderbookManagement();
		System.out.println(orderbookmanagement);
	}

}
