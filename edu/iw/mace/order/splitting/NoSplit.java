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

import java.util.Vector;

import edu.iw.mace.environment.BundlePool;
import edu.iw.mace.environment.MaceException;
import edu.iw.mace.environment.Market;
import edu.iw.mace.order.OrderBook;
import edu.iw.mace.outcome.allocator.MaceDataModel;
import edu.iw.mace.outcome.allocator.PreprocessingUtils;
import edu.iw.utils.patterns.AbstractProduct;

/**
 * Implements the {@link Splitter} interface with a "No-Split" Algorithm. This
 * class simply copies the order book into a {@link MaceDataModel} and does some
 * preprocessing.
 * 
 * Created 03.12.2005
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 */

public class NoSplit implements Splitter, AbstractProduct {

	private Market market = null;

	private OrderBook orderBook = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.mace.order.splitting.Splitter#init(edu.iw.mace.Market,
	 *      edu.iw.mace.order.OrderBook)
	 */
	public void init(Market market, OrderBook orderBook) {
		this.market = market;
		this.orderBook = orderBook;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.mace.order.splitting.Splitter#split()
	 */
	public Vector<MaceDataModel> split() throws SplittingException {
		Vector<MaceDataModel> result = new Vector<MaceDataModel>();
		MaceDataModel model = new MaceDataModel(market);

		model.setOrderBook(orderBook);

		BundlePool relevantBundles = new BundlePool();
		try {
			PreprocessingUtils.findRelevantBundles(relevantBundles, model
					.getOrderBook().getBuyerOrderPool());
			PreprocessingUtils.findRelevantBundles(relevantBundles, model
					.getOrderBook().getSellerOrderPool());
		} catch (MaceException e) {
			throw new SplittingException(e);
		}
		model.setBundlePool(relevantBundles);

		result.add(model);

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.mace.order.splitting.Splitter#generateStatistics()
	 */
	public SplittingStatisticsBean generateStatistics() {
		SplittingStatisticsBean statisticsBean = new SplittingStatisticsBean();
		statisticsBean.setNumberBidsLeftBuyer(orderBook.getBuyerOrderPool().computeNumberBids());
		statisticsBean.setNumberBidsRightBuyer(0);
		statisticsBean.setNumberBidsLeftSeller(orderBook.getSellerOrderPool().computeNumberBids());
		statisticsBean.setNumberBidsRightSeller(0);
		statisticsBean.setNumberRejectsBuyer(0);
		statisticsBean.setNumberRejectsSeller(0);
		return statisticsBean;
	}
}
