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

package edu.iw.mace.outcome.allocator;

import edu.iw.mace.environment.*;
import edu.iw.mace.order.OrderBook;

/**
 * This class provides a MACE data model. This model is used for solving the
 * winner determination problem. It is basically an encapsulation of an order
 * book.
 * 
 * Created: 20.11.2005
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @author Ikki Fujiwara, NII
 * @version 1.2
 */

public class MaceDataModel {

	private GoodPool goodPool = null;

	private BundlePool bundlePool = null;

	private BuyerAgentPool buyerAgentPool = null;

	private SellerAgentPool sellerAgentPool = null;

	private OrderBook orderBook = null;

	private AttributePool attributePool = null;

	private TimeRange timeRange = null;

	private Market market = null;

	/**
	 * Empty constructor
	 */
	public MaceDataModel() {

	}

	/**
	 * Initialises the Mace Data Model with standard pools
	 * 
	 * @param market
	 *            The target market
	 */
	public MaceDataModel(Market market) {
		this.market = market;
		this.goodPool = market.getEnvironment().getGoodPool();
		this.bundlePool = market.getEnvironment().getBundlePool();
		this.buyerAgentPool = market.getEnvironment().getBuyerPool();
		this.sellerAgentPool = market.getEnvironment().getSellerPool();
		this.attributePool = market.getEnvironment().getAttributePool();
		this.timeRange = market.getEnvironment().getTimeRange();
	}

	/**
	 * @return Returns the bundlePool.
	 */
	public BundlePool getBundlePool() {
		return bundlePool;
	}

	/**
	 * @param bundePool
	 *            The bundlePool to set.
	 */
	public void setBundlePool(BundlePool bundePool) {
		this.bundlePool = bundePool;
	}

	/**
	 * @return Returns the goodPool.
	 */
	public GoodPool getGoodPool() {
		return goodPool;
	}

	/**
	 * @param goodPool
	 *            The goodPool to set.
	 */
	public void setGoodPool(GoodPool goodPool) {
		this.goodPool = goodPool;
	}

	/**
	 * @return Returns the buyerAgentPool.
	 */
	public BuyerAgentPool getBuyerAgentPool() {
		return buyerAgentPool;
	}

	/**
	 * @param buyerAgentPool
	 *            The buyerAgentPool to set.
	 */
	public void setBuyerAgentPool(BuyerAgentPool buyerAgentPool) {
		this.buyerAgentPool = buyerAgentPool;
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
	 * @return Returns the sellerAgentPool.
	 */
	public SellerAgentPool getSellerAgentPool() {
		return sellerAgentPool;
	}

	/**
	 * @param sellerAgentPool
	 *            The sellerAgentPool to set.
	 */
	public void setSellerAgentPool(SellerAgentPool sellerAgentPool) {
		this.sellerAgentPool = sellerAgentPool;
	}

	/**
	 * @return Returns the attributePool.
	 */
	public AttributePool getAttributePool() {
		return attributePool;
	}

	/**
	 * @param attributePool
	 *            The attributePool to set.
	 */
	public void setAttributePool(AttributePool attributePool) {
		this.attributePool = attributePool;
	}

	/**
	 * @return the timeRange
	 */
	public TimeRange getTimeRange() {
		return timeRange;
	}

	/**
	 * @param timeRange the timeRange to set
	 */
	public void setTimeRange(TimeRange timeRange) {
		this.timeRange = timeRange;
	}

	/**
	 * @return Returns the market.
	 */
	public Market getMarket() {
		return market;
	}

	/**
	 * @param market
	 *            The market to set.
	 */
	public void setMarket(Market market) {
		this.market = market;
	}
}
