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

import edu.iw.mace.environment.Market;
import edu.iw.mace.order.OrderBook;

/**
 * Abstract class for building a graph. A graph consists of a set of nodes and
 * edges each having weights.
 * 
 * Created: 04.11.2005
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public abstract class AbstractGraphBuilder {
	
	protected Market market = null;

	// A constant which is added to the node if an order contains a bundle with
	// that node
	protected static final double NODE_ADDER = 1.0d;

	protected NodeEdgeBean nodeEdgeBean = null;

	/**
	 * Empty constructor. Initializes a nodepool and edgepool.
	 */
	public AbstractGraphBuilder() {
		nodeEdgeBean = new NodeEdgeBean();
	}

	/**
	 * Build the graph
	 * 
	 * @param orderBook
	 *            Orderbook of a market
	 */
	public abstract void build(OrderBook orderBook) throws SplittingException;

	/**
	 * @return Returns the edges.
	 */
	public EdgePool getEdges() {
		return nodeEdgeBean.getEdges();
	}

	/**
	 * @return Returns the nodes.
	 */
	public NodePool getNodes() {
		return nodeEdgeBean.getNodes();
	}

	/**
	 * @return the market
	 */
	public Market getMarket() {
		return market;
	}

	/**
	 * @param market
	 *            the market to set
	 */
	public void setMarket(Market market) {
		this.market = market;
	}

}
