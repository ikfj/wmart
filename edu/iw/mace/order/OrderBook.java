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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import edu.iw.utils.XMLUtils;

/**
 * Represents an order book
 * 
 * Created: 15.01.2005
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public class OrderBook {

	private OrderBuyerPool buyerOrderPool = null;

	private OrderSellerPool sellerOrderPool = null;

	/**
	 * Constructor
	 */
	public OrderBook() {
		reset();
	}

	/**
	 * Resets the order book, i.e. generates new instances of the buyer order
	 * pool and the seller order pool
	 */
	public void reset() {
		buyerOrderPool = new OrderBuyerPool();
		sellerOrderPool = new OrderSellerPool();
	}

	/**
	 * Computes the number of bids contained in this order book (seller and
	 * buyer bids)
	 * 
	 * @return Number of bids
	 */
	public int computeNumberBids() {
		return getBuyerOrderPool().computeNumberBids()
				+ getSellerOrderPool().computeNumberBids();
	}

	/**
	 * Computes the number of buyer order bids contained in this pool
	 * 
	 * @return Number of buyer order bids
	 */
	public int computeNumberBuyerOrderBids() {
		return buyerOrderPool.size();
	}

	/**
	 * Computes the number of seller order bids contained in this pool
	 * 
	 * @return Number of seller order bids
	 */
	public int computeNumberSellerOrderBids() {
		return sellerOrderPool.size();
	}

	/**
	 * Computes the number of bids in this orderbook (seller bids + buyer bids)
	 * 
	 * @return Number of bids
	 */
	public int computeNumberOrderBids() {
		return computeNumberBuyerOrderBids() + computeNumberSellerOrderBids();
	}
	
	/**
	 * Generates an exportable stream file for this orderbook
	 * 
	 * @param document
	 *            Document root
	 * @param path
	 *            Path for the stream file
	 * @param preFile
	 *            Prefix of the file
	 * @param tick
	 *            Current round
	 * @return An XML element containing all orderbook information
	 */
	public Element generateStreamFile(Document document, String path,
			String preFile, int tick) {
		try {
			DocumentBuilder builder = XMLUtils.createDocumentBuilder();
			Document streamDocument = builder.newDocument();

			Element root = streamDocument.createElement("orderbook");
			streamDocument.appendChild(root);
			root.setAttribute("time", String.valueOf(tick));
			root.appendChild(getBuyerOrderPool().toXML(streamDocument, tick));
			root.appendChild(getSellerOrderPool().toXML(streamDocument, tick));

			Element mainElement = document.createElement("stream");
			mainElement.setAttribute("time", String.valueOf(tick));
			mainElement.setAttribute("file", preFile + "_"
					+ String.valueOf(tick) + ".xml");

			if (path == null || path.equals("")) {
				path = ".";
			}

			OutputFormat format = new OutputFormat();
			FileWriter writer = new FileWriter(new File(path + "/" + preFile
					+ "_" + String.valueOf(tick) + ".xml"));
			XMLSerializer serializer = new XMLSerializer(writer, format);
			serializer.asDOMSerializer();
			serializer.serialize(streamDocument.getDocumentElement());

			return mainElement;

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * @return Returns the buyerOrderPool.
	 */
	public OrderBuyerPool getBuyerOrderPool() {
		return buyerOrderPool;
	}

	/**
	 * @param buyerOrderPool
	 *            The buyerOrderPool to set.
	 */
	public void setBuyerOrderPool(OrderBuyerPool buyerOrderPool) {
		this.buyerOrderPool = buyerOrderPool;
	}

	/**
	 * @return Returns the sellerOrderPool.
	 */
	public OrderSellerPool getSellerOrderPool() {
		return sellerOrderPool;
	}

	/**
	 * @param sellerOrderPool
	 *            The sellerOrderPool to set.
	 */
	public void setSellerOrderPool(OrderSellerPool sellerOrderPool) {
		this.sellerOrderPool = sellerOrderPool;
	}

}