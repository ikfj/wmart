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

package edu.iw.mace.environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import edu.iw.mace.environment.BundlePool;
import edu.iw.mace.environment.Environment;
import edu.iw.mace.environment.GoodPool;
import edu.iw.mace.order.OrderBook;
import edu.iw.mace.order.OrderException;
import edu.iw.mace.order.OrderbookManagement;
import edu.iw.mace.order.splitting.SplittingException;
import edu.iw.mace.outcome.Outcome;
import edu.iw.mace.outcome.allocator.SolverException;
import edu.iw.mace.outcome.allocator.WinnerDeterminationFactory;
import edu.iw.mace.outcome.pricing.PricingFactory;
import edu.iw.utils.XMLUtils;
import edu.iw.utils.patterns.FactoryException;
import edu.iw.utils.patterns.ObserverSubject;

/**
 * Mace Market Service main class. Controls the market process.
 * 
 * Created: 11.01.2005
 * 
 * @author Bjˆrn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public class Market extends ObserverSubject {

	private Environment environment;

	private OrderbookManagement orderBookManagement;

	private Outcome outcome;

	private WinnerDeterminationFactory winnerDeterminationFactory = null;

	private PricingFactory pricingFactory = null;

	private Settings settings = null;

	private File currentFile = null;

	private long completeWelfare = 0;

	/**
	 * Constructor of the market class that instantiates everything...
	 * 
	 */
	public Market() throws MaceException {
		this(true);
	}

	/**
	 * Constructor of the market class that instantiates everything...
	 * 
	 * @param instantitiateSettings
	 *            Initialize settings object
	 */
	public Market(boolean instantitiateSettings) throws MaceException {
		super();
		environment = new Environment();
		orderBookManagement = new OrderbookManagement();
		outcome = new Outcome(this);
		try {
			settings = new Settings();
		} catch (FactoryException e) {
			throw new MaceException(e);
		}
		pricingFactory = PricingFactory.instance();
		winnerDeterminationFactory = WinnerDeterminationFactory.instance();
		completeWelfare = 0;
	}

	/**
	 * Solves the problem: It determines an allocation and prices
	 * 
	 * @param updateEnvironment
	 *            Update the environment after the outcome is determined
	 * @throws MaceException
	 */
	public synchronized void solve(boolean updateEnvironment)
			throws MaceException {
		try {
			outcome = new Outcome(this);
			GoodPool clonedGoodPool = null;
			BundlePool clonedBundlePool = null;
			clonedGoodPool = environment.getGoodPool().cloneGoodPool();
			clonedBundlePool = environment.getBundlePool().cloneBundlePool();
			getOrderBookManagement().dissolveEqualXORBids(this);
			getOrderBookManagement().split(this);
			outcome.solve();
			environment.setGoodPool(clonedGoodPool);
			environment.setBundlePool(clonedBundlePool);
			if (updateEnvironment) {
				// updateEnvironment();
			}
			completeWelfare += outcome.getWelfare();
		} catch (SolverException e) {
			throw (new MaceException(e));
		} catch (SplittingException e) {
			throw (new MaceException(e));
		} catch (FactoryException e) {
			throw (new MaceException(e));
		}
	}

	/**
	 * Solves the problem:
	 * 
	 * @throws MaceException
	 */
	public synchronized void solve() throws MaceException {
		solve(true);
	}

	/**
	 * Computes an allocation
	 * ikki: NOT USED
	 * 
	 * @throws MaceException
	 */
	public void computeAllocation() throws MaceException {
		try {
			outcome = new Outcome(this);
			// GoodPool clonedGoodPool = null;
			// BundlePool clonedBundlePool = null;
			// clonedGoodPool = environment.getGoodPool().cloneGoodPool();
			// clonedBundlePool = environment.getBundlePool().cloneBundlePool();
			getOrderBookManagement().dissolveEqualXORBids(this);
			getOrderBookManagement().split(this);
			outcome.computeAllocation();
			// environment.setGoodPool(clonedGoodPool);
			// environment.setBundlePool(clonedBundlePool);
			completeWelfare += outcome.getWelfare();
		} catch (SolverException e) {
			throw (new MaceException(e));
		} catch (SplittingException e) {
			throw (new MaceException(e));
		} catch (FactoryException e) {
			throw (new MaceException(e));
		}
	}

	/**
	 * Resets the order book
	 */
	public void resetOrderBook() {
		orderBookManagement.reset();
	}

	/**
	 * Removes all executed orders
	 */
	public void removeExecuted() throws MaceException {
		try {
			orderBookManagement.removeExecuted(outcome);
		} catch (OrderException e) {
			throw new MaceException(e);
		}
	}

	/**
	 * Exports the current order book into a file
	 * 
	 * @param file
	 *            File to export
	 * @param orderbook
	 *            Order book to export
	 */
	public void export(File file, OrderBook orderbook) throws MaceException {
		try {
			DocumentBuilder builder = XMLUtils.createDocumentBuilder();
			Document document = builder.newDocument();

			// First of all generate an auction element (root element)
			Element root = document.createElement("auction");

			document.appendChild(root);

			// Generate the environment
			Element environmentTag = document.createElement("environment");
			root.appendChild(environmentTag);
			// Export the environment
			getEnvironment().toXML(document, environmentTag);

			// Generate the orders
			Element orderStreams = document.createElement("orderbook");
			orderStreams.setAttribute("type", "file");

			orderStreams.appendChild(orderbook.generateStreamFile(document,
					file.getParent(), "orderbook_"
							+ file.getName().replaceAll(".xml", ""), 0));

			root.appendChild(orderStreams);

			OutputFormat format = new OutputFormat();
			FileWriter writer = new FileWriter(file);
			XMLSerializer serializer = new XMLSerializer(writer, format);
			serializer.asDOMSerializer();
			serializer.serialize(document.getDocumentElement());

		} catch (ParserConfigurationException e) {
			throw new MaceException(e);
		} catch (IOException e) {
			throw new MaceException(e);
		}

	}

	/**
	 * @return Returns the environment.
	 */
	public Environment getEnvironment() {
		return environment;
	}

	/**
	 * @param environment
	 *            The environment to set.
	 */
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	/**
	 * @return Returns the outcome.
	 */
	public Outcome getOutcome() {
		return outcome;
	}

	/**
	 * @param outcome
	 *            The outcome to set.
	 */
	public void setOutcome(Outcome outcome) {
		this.outcome = outcome;
	}

	/**
	 * @return Returns the currentFile.
	 */
	public File getCurrentFile() {
		return currentFile;
	}

	/**
	 * @param currentFile
	 *            The currentFile to set.
	 */
	public void setCurrentFile(File currentFile) {
		this.currentFile = currentFile;
	}

	/**
	 * @return Returns the orderBook.
	 */
	public OrderbookManagement getOrderBookManagement() {
		return orderBookManagement;
	}

	/**
	 * @param orderBookManagement The OrderbookManagement to set.
	 */
	public void setOrderBookManagement(OrderbookManagement orderBookManagement) {
		this.orderBookManagement = orderBookManagement;
	}

	/**
	 * @return Returns the pricingFactory.
	 */
	public PricingFactory getPricingFactory() {
		return pricingFactory;
	}

	/**
	 * @param pricingFactory
	 *            The pricingFactory to set.
	 */
	public void setPricingFactory(PricingFactory pricingFactory) {
		this.pricingFactory = pricingFactory;
	}

	/**
	 * @return Returns the settings.
	 */
	public Settings getSettings() {
		return settings;
	}

	/**
	 * @param settings
	 *            The settings to set.
	 */
	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	/**
	 * @return Returns the solverFactory.
	 */
	public WinnerDeterminationFactory getWinnerDeterminationFactory() {
		return winnerDeterminationFactory;
	}

	/**
	 * @param solverFactory
	 *            The solverFactory to set.
	 */
	public void setWinnerDeterminationFactory(
			WinnerDeterminationFactory solverFactory) {
		this.winnerDeterminationFactory = solverFactory;
	}

	@Override
	public String toString() {
		return "Market"
		 + "{\n  environment=" + getEnvironment()
		 + ",\n  orderBookManagement=" + getOrderBookManagement()
		 + ",\n  outcome=" + getOutcome()
		 + ",\n  winnerDeterminationFactory=" + getWinnerDeterminationFactory()
		 + ",\n  pricingFactory=" + getPricingFactory()
		 + ",\n  settings=" + getSettings()
		 + ",\n  currentFile=" + getCurrentFile()
		 + ",\n  completeWelfare=" + getCurrentFile()
		 + ",\n}";
	}

	public static void main(String args[]) {
		Market market = null;
		try {
			market = new Market();
		} catch (MaceException e) {
			// TODO é©ìÆê∂ê¨Ç≥ÇÍÇΩ catch ÉuÉçÉbÉN
			e.printStackTrace();
		} 
		System.out.println(market);
	}
}