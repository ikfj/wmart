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

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import edu.harvard.econcs.jopt.solver.IMIPResult;
import edu.harvard.econcs.jopt.solver.MIPException;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.iw.mace.environment.Agent;
import edu.iw.mace.environment.Attribute;
import edu.iw.mace.environment.Bundle;
import edu.iw.mace.environment.BundlePool;
import edu.iw.mace.environment.BuyerAgentPool;
import edu.iw.mace.environment.Good;
import edu.iw.mace.environment.MaceException;
import edu.iw.mace.environment.Market;
import edu.iw.mace.environment.SellerAgentPool;
import edu.iw.mace.order.AbstractBundleOrder;
import edu.iw.mace.order.AbstractOrder;
import edu.iw.mace.order.AbstractOrderPool;
import edu.iw.mace.order.BundleOrderBuyer;
import edu.iw.mace.order.BundleOrderSeller;
import edu.iw.mace.order.OrderBuyerPool;
import edu.iw.mace.order.OrderException;
import edu.iw.mace.order.OrderSellerPool;
import edu.iw.mace.outcome.OutcomeBuyerBean;
import edu.iw.mace.outcome.OutcomeSellerBean;
import edu.iw.utils.MathUtils;
import edu.iw.utils.patterns.Entity;

/**
 * This class implements the winner determination model for the resource market applied in the CATNETS project. This is
 * a simplified version of MACE.
 * 
 * Created: 29.10.2005
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */

public class CatnetsResourceMarket extends AbstractWinnerDetermination {

	private static Logger log = Logger.getLogger(CatnetsResourceMarket.class);

	// The CATNETS model does not support multiple time slots. This is a fixed
	// and static variable used
	private static final int TIME_STEP = 1;

	protected int numBuyers;

	protected int numSellers;

	protected int numGoods;

	protected int numAttributes;

	protected int numBundles;

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.mace.outcome.allocator.AbstractWinnerDetermination#solveParticularModel(edu.iw.mace .Market,
	 * java.util.Vector)
	 */
	@Override
	public void solveParticularModel(Market market, Vector<MaceDataModel> dataModels) throws SolverException,
		MaceException {

		double completeRuntime = 0.0d;
		double completeWelfare = 0.0d;

		for (int a = 0; a < dataModels.size(); a++) {

			log.debug("Starting to allocate for dataModel number " + a);

			MIPWrapper mip = MIPWrapper.makeNewMaxMIP();
			mip.setObjectiveMax(true);
			setSolveParam(mip);

			MaceDataModel maceDataModel = (MaceDataModel) dataModels.get(a);

			if (maceDataModel.getBuyerAgentPool().size() > 0 && maceDataModel.getSellerAgentPool().size() > 0
				&& maceDataModel.getBundlePool().size() > 0) {

				numBuyers = maceDataModel.getBuyerAgentPool().size();
				numSellers = maceDataModel.getSellerAgentPool().size();
				numGoods = maceDataModel.getGoodPool().size();
				numAttributes = maceDataModel.getAttributePool().size();
				numBundles = maceDataModel.getBundlePool().size();

				log.info("Building model");
				buildModel(mip, maceDataModel);

				// Solve the MIP
				log.info("Starting solver");
				try {
					result = solverClient.solve(mip);
				} catch (MIPException e) {
					outcome.setWelfare(-1);
					runtime = -2;
					throw new SolverException(e);
				}

				log.info("Result returned after round about " + Math.round(result.getRuntime()) + "s.");
				completeRuntime += result.getRuntime();
				completeWelfare += result.getObjectiveValue();

				parseResult(a, maceDataModel, result);

				mip.clearSolveParams();
				log.debug("Solving done");
			} else {
				log.warn("Found empty maceDataModel");
			}
		}
		if (completeRuntime <= 0) {
			completeRuntime = 0;
		} else {
			runtime = completeRuntime;
		}
		if (completeWelfare < 0 && completeWelfare >= -MathUtils.SMALL_NUMBER)
			completeWelfare = 0;

		outcome.setWelfare(completeWelfare);
		outcome.setRuntime(runtime);
		if (completeWelfare < 0) {
			throw new SolverException("Found negative welfare: " + completeWelfare);
		}
		log.debug("Complete time for solving the problem: " + completeRuntime);
	}

	/**
	 * Parses the result of the optimization problem
	 * 
	 * @param dataModelNumber
	 *            MaceDataModel number (i.e., order book number in case of splitting)
	 * @param maceDataModel
	 *            MaceDataModel
	 * @param result
	 *            The result object to parse
	 * @throws SolverException
	 */
	private void parseResult(int dataModelNumber, MaceDataModel maceDataModel, IMIPResult result)
		throws SolverException {

		try {

			log.debug("Starting to parse the result");
			Map map = result.getValues();

			/**
			 * Get the objective value (should be 22) and the values of the variables.
			 */

			// Output X
			for (int n = 0; n < maceDataModel.getBuyerAgentPool().size(); n++) {
				Agent agent = maceDataModel.getBuyerAgentPool().getAgent(n);
				AbstractOrderPool ordersAgent = maceDataModel.getOrderBook().getBuyerOrderPool().getOrderPool(agent);
				for (int i = 0; i < maceDataModel.getBundlePool().size(); i++) {
					Bundle bundle = maceDataModel.getBundlePool().getBundle(i);
					try {
						BundleOrderBuyer buyerBundleOrder = (BundleOrderBuyer) ordersAgent.getFirstBundleOrder(bundle,
							agent);
						int value = 0;
						if (buyerBundleOrder != null) {
							value = ((Double) map.get("z_b" + n + "_s" + i + "_t" + TIME_STEP)).intValue();
						}
						if (value == 1) {
							if (buyerBundleOrder == null) {
								log.error("BuyerBundle order for " + bundle.toString() + " " + agent.getId()
									+ " was null");
							} else {
								OutcomeBuyerBean bean = new OutcomeBuyerBean(buyerBundleOrder);
								bean.setOrderBookNumber(dataModelNumber);
								outcome.addAllocationBuyerBean(bean);
							}
						}
					} catch (OrderException e) {
						throw new SolverException(e);
					}
				}
			}

			// Output Y
			for (int n = 0; n < maceDataModel.getBuyerAgentPool().size(); n++) {
				for (int m = 0; m < maceDataModel.getSellerAgentPool().size(); m++) {
					Agent sellerAgent = maceDataModel.getSellerAgentPool().getAgent(m);
					AbstractOrderPool ordersSellerAgent = maceDataModel.getOrderBook().getSellerOrderPool()
						.getOrderPool(sellerAgent);
					for (int i = 0; i < maceDataModel.getBundlePool().size(); i++) {
						Bundle bundle = maceDataModel.getBundlePool().getBundle(i);
						try {
							BundleOrderSeller sellerBundleOrder = (BundleOrderSeller) ordersSellerAgent
								.getFirstBundleOrder(bundle, sellerAgent);
							if (sellerBundleOrder != null) {
								if (map.containsKey("y_m" + m + "_n" + n + "_s" + i + "_t" + TIME_STEP)) {
									double value = ((Double) map
										.get("y_m" + m + "_n" + n + "_s" + i + "_t" + TIME_STEP)).doubleValue();
									if (!MathUtils.between(value, 0, 1, MathUtils.SMALL_NUMBER)) {
										log.error("Value not between 0 and 1: " + value);
									}
									if (value > 0) {
										if (value > 1) {
											value = 1;
										}
										if (sellerBundleOrder == null) {
											log.error("Could not find order in sellerorder pool");
										} else {
											OutcomeSellerBean bean = outcome
												.createAllocationSellerBean(sellerBundleOrder);
											bean.setOrderBookNumber(dataModelNumber);
											Agent allocationBuyer = maceDataModel.getBuyerAgentPool().getAgent(n);
											bean.addAllocation(allocationBuyer, value, TIME_STEP);
										}
									}
								}
							}
						} catch (OrderException e) {
							throw new SolverException(e);
						}
					}
				}
			}
		} catch (MaceException e) {
			throw new SolverException(e);
		}

	}

	/**
	 * Builds the optimization model of the winner determination problem
	 * 
	 * @param mip
	 *            MIP Wrapper instance
	 * @param maceDataModel
	 *            MaceDataModel
	 * @throws SolverException
	 */
	private void buildModel(MIPWrapper mip, MaceDataModel maceDataModel) throws SolverException, MaceException {

		try {

			log.info("Starting to build the model for " + maceDataModel.getOrderBook().getBuyerOrderPool().size()
				+ " buyer orders, " + maceDataModel.getOrderBook().getSellerOrderPool().size() + " seller orders, "
				+ numBundles + " bundles and " + numBuyers + " buyers + " + numSellers + " sellers " + numGoods
				+ " goods and " + numAttributes + " attributes");

			// Generate instances of the variable pool
			VariablePool varZ = new VariablePool();
			VariablePool varY = new VariablePool();

			OrderBuyerPool orderBuyerPool = maceDataModel.getOrderBook().getBuyerOrderPool();
			OrderSellerPool orderSellerPool = maceDataModel.getOrderBook().getSellerOrderPool();

			BundlePool bundlePool = maceDataModel.getBundlePool();
			BuyerAgentPool buyerAgentPool = maceDataModel.getBuyerAgentPool();
			SellerAgentPool sellerAgentPool = maceDataModel.getSellerAgentPool();

			// Now define Z of type boolean
			// For each buyer that has an odrer
			for (Iterator<Entity> buyerOrderPoolIterator = orderBuyerPool.getValueIterator(); buyerOrderPoolIterator
				.hasNext();) {
				AbstractOrder buyerOrder = (AbstractOrder) buyerOrderPoolIterator.next();
				Agent buyerAgent = buyerOrder.getAgent();
				int n = buyerAgentPool.getIndex(buyerAgent);
				// Now for each atomic bid
				for (Iterator<AbstractBundleOrder> iterator = buyerOrder.getBundleOrders().values().iterator(); iterator
					.hasNext();) {
					AbstractBundleOrder bundleOrder = iterator.next();
					int i = maceDataModel.getBundlePool().getIndex(bundleOrder.getBundle());
					varZ.add(mip.makeNewBooleanVar("z_b" + n + "_s" + i + "_t" + TIME_STEP), n, TIME_STEP, i);
				}
			}

			// Define Y
			// For each seller that has an order
			for (Iterator<Entity> sellerOrderPoolIterator = orderSellerPool.getValueIterator(); sellerOrderPoolIterator
				.hasNext();) {
				AbstractOrder sellerOrder = (AbstractOrder) sellerOrderPoolIterator.next();
				Agent sellerAgent = sellerOrder.getAgent();
				int m = sellerAgentPool.getIndex(sellerAgent);
				// Iterate over all seller (atomic) orders
				for (Iterator<AbstractBundleOrder> iterator = sellerOrder.getBundleOrders().values().iterator(); iterator
					.hasNext();) {
					AbstractBundleOrder bundleOrderSeller = iterator.next();
					int i = bundlePool.getIndex(bundleOrderSeller.getBundle());
					// ... and over all buyer orders
					for (Iterator<Entity> buyerOrderPoolIterator = orderBuyerPool.getValueIterator(); buyerOrderPoolIterator
						.hasNext();) {
						AbstractOrder buyerOrder = (AbstractOrder) buyerOrderPoolIterator.next();
						boolean sellerContainsRelevantBundles = false;
						// ... over all atomic bids by a buyer
						for (Iterator<AbstractBundleOrder> buyerIterator = buyerOrder.getBundleOrders().values()
							.iterator(); buyerIterator.hasNext();) {
							BundleOrderBuyer bundleOrder = (BundleOrderBuyer) buyerIterator.next();
							Bundle buyerBundle = bundleOrder.getBundle();
							// Does this seller order contain any goods that may
							// be of intereset for
							// a buyer, i.e. SellerBundle UNION BuyerBundle !=
							// EmptySet
							if (buyerBundle.unionNotEmpty(bundleOrderSeller.getBundle())) {
								sellerContainsRelevantBundles = true;
							}
						}
						// The seller has a bundle that may be of interest for
						// buyer "n". So generate connections, i.e. define
						// y(m,n,s,t)
						if (sellerContainsRelevantBundles == true) {
							int n = buyerAgentPool.getIndex(buyerOrder.getAgent());
							// Iterate over a slots which the seller offers
							for (int t = bundleOrderSeller.getEarly(); t <= bundleOrderSeller.getLatest(); t++) {
								// If the current slot is between the buyer
								// order [early, late], generate a variable
								if (buyerOrder.getMinEarly() <= t && buyerOrder.getMaxLate() >= t) {
									varY.add(mip.makeNewDoubleVar("y_m" + m + "_n" + n + "_s" + i + "_t" + t, 0, 1), m,
										n, i, t);
								}
							}
						}
					}
				}
			}

			// -----------------------------------------------------------------------------
			// Build the objective function
			// -----------------------------------------------------------------------------
			log.debug("Building objective function");

			// For each buyer order
			for (Iterator<Entity> buyerOrderPoolIterator = orderBuyerPool.getValueIterator(); buyerOrderPoolIterator
				.hasNext();) {
				AbstractOrder buyerOrder = (AbstractOrder) buyerOrderPoolIterator.next();
				Agent buyerAgent = buyerOrder.getAgent();
				int n = buyerAgentPool.getIndex(buyerAgent);
				// For each atomic bid of that buyer
				for (Iterator<AbstractBundleOrder> buyerIterator = buyerOrder.getBundleOrders().values().iterator(); buyerIterator
					.hasNext();) {
					AbstractBundleOrder bundleOrder = buyerIterator.next();
					int i = maceDataModel.getBundlePool().getIndex(bundleOrder.getBundle());
					double bid = bundleOrder.getBid();
					if (varZ.get(n, TIME_STEP, i) == null) {
						log.error(n + " " + TIME_STEP + " " + i + " is null!");
					}
					if (bid > 0) {
						mip.addObjectiveTerm(varZ.get(n, TIME_STEP, i), (float) bid);
					}
				}
			}

			// For each seller order
			for (Iterator<Entity> sellerOrderPoolIterator = orderSellerPool.getValueIterator(); sellerOrderPoolIterator
				.hasNext();) {
				AbstractOrder sellerOrder = (AbstractOrder) sellerOrderPoolIterator.next();
				Agent sellerAgent = sellerOrder.getAgent();
				int m = sellerAgentPool.getIndex(sellerAgent);
				// For each atomic bid of the seller
				for (Iterator<AbstractBundleOrder> sellerIterator = sellerOrder.getBundleOrders().values().iterator(); sellerIterator
					.hasNext();) {
					AbstractBundleOrder bundleOrderSeller = sellerIterator.next();
					double bid = bundleOrderSeller.getBid();
					int i = bundlePool.getIndex(bundleOrderSeller.getBundle());
					// Now iterate over all buyer bids on check whether or not
					// this seller is interesting for the buyer - i.e. whether
					// the seller provides a bundles with relevant goods
					for (Iterator<Entity> buyerOrderPoolIterator = orderBuyerPool.getValueIterator(); buyerOrderPoolIterator
						.hasNext();) {
						AbstractOrder buyerOrder = (AbstractOrder) buyerOrderPoolIterator.next();
						boolean sellerContainsRelevantBundles = false;
						// For each atomic bid of that buyer
						for (Iterator<AbstractBundleOrder> buyerIterator = buyerOrder.getBundleOrders().values()
							.iterator(); buyerIterator.hasNext();) {
							BundleOrderBuyer bundleOrder = (BundleOrderBuyer) buyerIterator.next();
							Bundle buyerBundle = bundleOrder.getBundle();
							if (buyerBundle.unionNotEmpty(bundleOrderSeller.getBundle())) {
								sellerContainsRelevantBundles = true;
							}
						}
						// ... the seller has something for the buyer
						if (sellerContainsRelevantBundles == true) {
							int n = buyerAgentPool.getIndex(buyerOrder.getAgent());
							// Subtract the bid
							mip.addObjectiveTerm(varY.get(m, n, i, TIME_STEP), (float) -bid);
						}
					}
				}
			}

			// -----------------------------------------------------------------------------
			// Build the max seller constraint. i.e. Sum Y <= 1
			// This constraint is necessary, as the definition of Y is
			// concatenated to each
			// buyer.
			// -----------------------------------------------------------------------------
			log.debug("Building seller max 1 constraint");
			// For each seller order
			for (Iterator<Entity> sellerOrderPoolIterator = orderSellerPool.getValueIterator(); sellerOrderPoolIterator
				.hasNext();) {
				AbstractOrder sellerOrder = (AbstractOrder) sellerOrderPoolIterator.next();
				Agent sellerAgent = sellerOrder.getAgent();
				int m = sellerAgentPool.getIndex(sellerAgent);
				// Iterator over the atomic bids
				for (Iterator<AbstractBundleOrder> sellerIterator = sellerOrder.getBundleOrders().values().iterator(); sellerIterator
					.hasNext();) {
					AbstractBundleOrder bundleOrderSeller = sellerIterator.next();
					int i = bundlePool.getIndex(bundleOrderSeller.getBundle());
					Constraint c = mip.beginNewLEQConstraint(1);
					// Iterate over all buyer orders
					for (Iterator<Entity> buyerOrderPoolIterator = orderBuyerPool.getValueIterator(); buyerOrderPoolIterator
						.hasNext();) {
						AbstractOrder buyerOrder = (AbstractOrder) buyerOrderPoolIterator.next();
						boolean sellerContainsRelevantBundles = false;
						for (Iterator<AbstractBundleOrder> buyerIterator = buyerOrder.getBundleOrders().values()
							.iterator(); buyerIterator.hasNext();) {
							AbstractBundleOrder bundleOrder = buyerIterator.next();
							Bundle buyerBundle = bundleOrder.getBundle();
							// Does the seller has relevant bundles for
							// this agent?
							if (buyerBundle.unionNotEmpty(bundleOrderSeller.getBundle())) {
								sellerContainsRelevantBundles = true;
							}
						}
						// If so, generate the term constraint
						if (sellerContainsRelevantBundles == true) {
							Agent buyerAgent = buyerOrder.getAgent();
							int n = buyerAgentPool.getIndex(buyerAgent);
							c.addTerm(varY.get(m, n, i, TIME_STEP), 1);
						}
					}
					mip.endConstraint(c);
				}

			}

			// -----------------------------------------------------------------------------
			// Build the quality constraint
			// -----------------------------------------------------------------------------
			log.debug("Building quality constraints");
			// For each buyer order
			for (Iterator<Entity> buyerOrderPoolIterator = orderBuyerPool.getValueIterator(); buyerOrderPoolIterator
				.hasNext();) {
				AbstractOrder buyerOrder = (AbstractOrder) buyerOrderPoolIterator.next();
				Agent buyerAgent = buyerOrder.getAgent();
				int n = buyerAgentPool.getIndex(buyerAgent);
				// ... over all atomic bids by a buyer
				for (Iterator<AbstractBundleOrder> buyerIterator = buyerOrder.getBundleOrders().values().iterator(); buyerIterator
					.hasNext();) {
					AbstractBundleOrder bundleOrder = (AbstractBundleOrder) buyerIterator.next();
					// .. for all goods in this bundle
					for (int r = 0; r < bundleOrder.getBundle().size(); r++) {
						Good good = bundleOrder.getBundle().getGood(r);
						// ... and for all attributes
						for (int a = 0; a < numAttributes; a++) {
							Attribute attribute = maceDataModel.getAttributePool().getAttribute(a);
							// Begin a new constraint
							Constraint c = mip.beginNewLEQConstraint(0);
							for (Iterator<Entity> sellerOrderPoolIterator = orderSellerPool.getValueIterator(); sellerOrderPoolIterator
								.hasNext();) {
								AbstractOrder sellerOrder = (AbstractOrder) sellerOrderPoolIterator.next();
								// Iterate over all seller orders
								for (Iterator<AbstractBundleOrder> sellerIterator = sellerOrder.getBundleOrders()
									.values().iterator(); sellerIterator.hasNext();) {
									AbstractBundleOrder bundleOrderSeller = sellerIterator.next();
									// Does the bundle of this seller
									// contain the good we are interest in?
									if (bundleOrderSeller.getBundle().contains(good)) {
										Agent sellerAgent = sellerOrder.getAgent();
										int m = maceDataModel.getSellerAgentPool().getIndex(sellerAgent);
										// Get the offered quality of this
										// seller
										double sellQuality = bundleOrderSeller.getQuality().getQuality(good,
											attribute.getId());
										// Get the index of the seller
										// bundle
										int k = bundlePool.getIndex(bundleOrderSeller.getBundle());
										// Subtract this product
										c.addTerm(varY.get(m, n, k, TIME_STEP), (float) -sellQuality);
									}
								}
							}
							// Finally, add the buyer quality constraint
							double buyQual = bundleOrder.getQuality().getQuality(good, attribute.getId());
							int i = bundlePool.getIndex(bundleOrder.getBundle());
							c.addTerm(varZ.get(n, TIME_STEP, i), (float) buyQual);
							mip.endConstraint(c);
						}

					}
				}
			}
			log.debug("Model built including " + mip.getNumConstraints() + " constraints.");
		} catch (OrderException ex) {
			throw new SolverException(ex);
		} catch (MIPException ex) {
			File file = new File("error_" + System.currentTimeMillis() + ".xml");
			log.error("Dumping orderbook to " + file.getAbsolutePath());
			maceDataModel.getMarket().export(file, maceDataModel.getOrderBook());
			throw new SolverException(ex);
		}
	}

}
