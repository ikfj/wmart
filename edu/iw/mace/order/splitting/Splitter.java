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

import edu.iw.mace.environment.Market;
import edu.iw.mace.order.OrderBook;
import edu.iw.mace.outcome.allocator.MaceDataModel;
import edu.iw.utils.patterns.AbstractProduct;

/**
 * An interface for every splitting algorithm
 * 
 * Created: 26.11.2005
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 */

public interface Splitter extends AbstractProduct {

	/**
	 * Initialises the splitting algorithm
	 * 
	 * @param market
	 *            Market instance
	 * @param orderBook
	 *            Order book instance
	 */
	public void init(Market market, OrderBook orderBook);

	/**
	 * Performs the splitting process
	 * 
	 * @return A vector of {@link MaceDataModel} containing the splitted models
	 * @throws SplittingException
	 */
	public Vector<MaceDataModel> split() throws SplittingException;

	/**
	 * Generates splitting statistics such as number of rejected bids
	 * 
	 * @return {@link SplittingStatisticsBean} containing relevant information
	 */
	public SplittingStatisticsBean generateStatistics();
}
