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

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import edu.harvard.econcs.jopt.solver.*;
import edu.harvard.econcs.jopt.solver.mip.*;
import edu.iw.mace.environment.*;
import edu.iw.mace.order.*;
import edu.iw.mace.outcome.*;
import edu.iw.utils.*;
import edu.iw.utils.patterns.*;

/**
 * This class implements the winner determination model for the resource market applied in the
 * CATNETS project. This is a simplified version of MACE.
 * 
 * Created: 29.10.2005
 * 
 * @author Bjn Schnizler, University of Karlsruhe (TH)
 * @author Ikki Fujiwara, NII
 * @version 1.2
 */

public class WMartWinnerDetermination extends AbstractWinnerDetermination {

	private static Logger log = Logger.getLogger(WMartWinnerDetermination.class);
	protected int numBuyers;
	protected int numSellers;
	protected int numGoods;
	protected int numAttributes;
	protected int numBundles;
	protected int numTimeSlots;

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.mace.outcome.allocator.AbstractWinnerDetermination#
	 * solveParticularModel(edu.iw.mace.Market, java.util.Vector)
	 */
	@Override
	public void solveParticularModel(Market market, Vector<MaceDataModel> dataModels)
		throws SolverException, MaceException {

		Long roundRuntimeMS;
		double completeRuntime = 0.0d;
		double completeWelfare = 0.0d;

		for (int a = 0; a < dataModels.size(); a++) {

			log.debug("Starting to allocate for dataModel number " + a);

			MIPWrapper mip = MIPWrapper.makeNewMaxMIP();
			mip.setObjectiveMax(true);
			setSolveParam(mip);

			MaceDataModel maceDataModel = dataModels.get(a);

			if (maceDataModel.getBuyerAgentPool().size() > 0
				&& maceDataModel.getSellerAgentPool().size() > 0
				&& maceDataModel.getBundlePool().size() > 0) {

				numBuyers = maceDataModel.getBuyerAgentPool().size();
				numSellers = maceDataModel.getSellerAgentPool().size();
				numGoods = maceDataModel.getGoodPool().size();
				numAttributes = maceDataModel.getAttributePool().size();
				numBundles = maceDataModel.getBundlePool().size();

				log.debug("Building model");
				buildModel(mip, maceDataModel);

				log.debug("Starting solver");
				roundRuntimeMS = System.currentTimeMillis();
				try {
					result = solverClient.solve(mip);
				} catch (MIPException e) {
					outcome.setWelfare(-1);
					runtime = -2;
					throw new SolverException(e);
				}
				roundRuntimeMS = System.currentTimeMillis() - roundRuntimeMS;

				log.debug("Result returned after round " + roundRuntimeMS + " ms");
				completeRuntime += roundRuntimeMS.doubleValue() / 1000;
				completeWelfare += result.getObjectiveValue();

				parseResult(a, maceDataModel, result);

				mip.clearSolveParams();
				log.debug("Solving done");
			} else {
				log.warn("Found empty maceDataModel");
			}
		}
		runtime = completeRuntime;

		if (completeWelfare < 0 && completeWelfare >= -MathUtils.SMALL_NUMBER) {
			completeWelfare = 0;
		}

		outcome.setWelfare(completeWelfare);
		outcome.setRuntime(completeRuntime);
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
		throws SolverException, MaceException {

		log.debug("Starting to parse the result");
		Map map = result.getValues();

		BuyerAgentPool buyerAgentPool = maceDataModel.getBuyerAgentPool();
		SellerAgentPool sellerAgentPool = maceDataModel.getSellerAgentPool();
		OrderBuyerPool orderBuyerPool = maceDataModel.getOrderBook().getBuyerOrderPool();
		OrderSellerPool orderSellerPool = maceDataModel.getOrderBook().getSellerOrderPool();
		GoodPool goodPool = maceDataModel.getGoodPool();

		// 買い注文は約定したか?
		// すべての買い注文をイテレート
		for (Iterator<Entity> buyerOrderPoolIterator = orderBuyerPool.getValueIterator(); buyerOrderPoolIterator
			.hasNext();) {
			AbstractOrder buyerOrder = (AbstractOrder) buyerOrderPoolIterator.next();
			int n = buyerAgentPool.getIndex(buyerOrder.getAgent());
			// U の出力値を取得 (表示のみ)
			if (map.containsKey("u_n" + n)) {
				int u = ((Double) map.get("u_n" + n)).intValue();
				log.debug("u_n" + n + " = " + u);
			}
			// バンドルを取得
			if (buyerOrder.getBundleOrders().size() != 1) {
				throw new MaceException("buyerOrder n=" + n + " has multiple bundles");
			}
			AbstractBundleOrder bundleOrderBuyer = buyerOrder.getBundleOrders().values().iterator()
				.next();
			Bundle buyerBundle = bundleOrderBuyer.getBundle();
			// バンドルが持つ商品をイテレート
			int allocationGoodsCount = 0;
			for (Iterator<Good> goodIterator = buyerBundle.getGoodIterator(); goodIterator
				.hasNext();) {
				Good good = goodIterator.next();
				int i = goodPool.getIndex(good);
				// X の出力値を取得 (表示のみ)
				if (map.containsKey("x_n" + n + "s" + i)) {
					int x = ((Double) map.get("x_n" + n + "s" + i)).intValue();
					log.debug("x_n" + n + "s" + i + " = " + x);
				}
				int buyTotal = ((Double) bundleOrderBuyer.getQuality()
					.getQuality(good, "TotalTime")).intValue();
				int buyArrival = ((Double) bundleOrderBuyer.getQuality().getQuality(good,
					"ArrivalTime")).intValue();
				int buyDeadline = ((Double) bundleOrderBuyer.getQuality().getQuality(good,
					"DeadlineTime")).intValue();
				// 商品の買い注文スロットをイテレート
				int allocationTotalTime = 0;
				for (int t = buyArrival; t <= buyDeadline; t++) {
					// Z の出力値を取得
					if (map.containsKey("z_n" + n + "s" + i + "t" + t)) {
						int z = ((Double) map.get("z_n" + n + "s" + i + "t" + t)).intValue();
						log.debug("z_n" + n + "s" + i + "t" + t + " = " + z);
						allocationTotalTime += z;
					}
				}
				if (allocationTotalTime > 0) {
					// 買い注文スロット数と割り当てスロット数が一致したらその商品は割り当てられた
					if (allocationTotalTime == buyTotal) {
						allocationGoodsCount++;
					} else {
						log.error("unmatch totalTime for buyerOrder " + bundleOrderBuyer
							+ " by buyer " + buyerOrder.getAgent().getId());
					}
				}
			}
			if (allocationGoodsCount > 0) {
				// すべての商品が割り当てられていたら buyerBean を生成
				if (allocationGoodsCount == buyerBundle.size()) {
					OutcomeBuyerBean buyerBean = new OutcomeBuyerBean(bundleOrderBuyer);
					buyerBean.setOrderBookNumber(dataModelNumber);
					outcome.addAllocationBuyerBean(buyerBean);
				} else {
					log.error("lack of goods for buyerOrder " + bundleOrderBuyer + " by buyer "
						+ buyerOrder.getAgent().getId());
				}
			}
		}

		// 売り注文は約定したか?
		// すべての売り注文をイテレート
		for (Iterator<Entity> sellerOrderPoolIterator = orderSellerPool.getValueIterator(); sellerOrderPoolIterator
			.hasNext();) {
			AbstractOrder sellerOrder = (AbstractOrder) sellerOrderPoolIterator.next();
			int m = sellerAgentPool.getIndex(sellerOrder.getAgent());
			// バンドルを取得
			if (sellerOrder.getBundleOrders().size() != 1) {
				throw new MaceException("sellerOrder m=" + m + " has multiple bundles");
			}
			AbstractBundleOrder bundleOrderSeller = sellerOrder.getBundleOrders().values()
				.iterator().next();
			// 商品を取得
			if (bundleOrderSeller.getBundle().size() != 1) {
				throw new MaceException("sellerOrder m=" + m + " has multiple goods");
			}
			Good good = bundleOrderSeller.getBundle().getGood(0);
			int i = goodPool.getIndex(good);
			int sellArrival = ((Double) bundleOrderSeller.getQuality().getQuality(good,
				"ArrivalTime")).intValue();
			int sellDeadline = ((Double) bundleOrderSeller.getQuality().getQuality(good,
				"DeadlineTime")).intValue();
			// 商品の売り注文スロットをイテレート
			for (int t = sellArrival; t <= sellDeadline; t++) {
				// すべての買い注文をイテレート
				for (Iterator<Entity> buyerOrderPoolIterator = orderBuyerPool.getValueIterator(); buyerOrderPoolIterator
					.hasNext();) {
					AbstractOrder buyerOrder = (AbstractOrder) buyerOrderPoolIterator.next();
					int n = buyerAgentPool.getIndex(buyerOrder.getAgent());
					// Y の出力値を取得
					if (map.containsKey("y_m" + m + "n" + n + "s" + i + "t" + t)) {
						double y = ((Double) map.get("y_m" + m + "n" + n + "s" + i + "t" + t))
							.doubleValue();
						log.debug("y_m" + m + "n" + n + "s" + i + "t" + t + " = " + y);
						// 商品が割り当てられていたら sellerBean を生成
						if (y > 0.0) {
							if (y > 1.0) {
								y = 1.0;
							}
							OutcomeSellerBean sellerBean = outcome
								.createAllocationSellerBean(bundleOrderSeller);
							sellerBean.setOrderBookNumber(dataModelNumber);
							sellerBean.addAllocation(buyerAgentPool.getAgent(n), y, t);
						}
					}
				}
			}
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
	private void buildModel(MIPWrapper mip, MaceDataModel maceDataModel) throws SolverException,
		MaceException {

		try {
			log.debug("Starting to build the model for "
				+ maceDataModel.getOrderBook().getBuyerOrderPool().size() + " buyer orders, "
				+ maceDataModel.getOrderBook().getSellerOrderPool().size() + " seller orders, "
				+ numBundles + " bundles and " + numBuyers + " buyers + " + numSellers
				+ " sellers " + numGoods + " goods and " + numAttributes + " attributes");

			// Generate instances of the variable pool
			VariablePool varZ = new VariablePool();
			VariablePool varY = new VariablePool();
			VariablePool varX = new VariablePool();
			VariablePool varU = new VariablePool();

			OrderBuyerPool orderBuyerPool = maceDataModel.getOrderBook().getBuyerOrderPool();
			OrderSellerPool orderSellerPool = maceDataModel.getOrderBook().getSellerOrderPool();

			BuyerAgentPool buyerAgentPool = maceDataModel.getBuyerAgentPool();
			SellerAgentPool sellerAgentPool = maceDataModel.getSellerAgentPool();
			GoodPool goodPool = maceDataModel.getGoodPool();
			// TimeRange timeRange = maceDataModel.getTimeRange();

			// Define Z, X and U
			// For each buyer that has an order
			for (Iterator<Entity> buyerOrderPoolIterator = orderBuyerPool.getValueIterator(); buyerOrderPoolIterator
				.hasNext();) {
				AbstractOrder buyerOrder = (AbstractOrder) buyerOrderPoolIterator.next();
				int n = buyerAgentPool.getIndex(buyerOrder.getAgent());
				if (buyerOrder.getBundleOrders().size() != 1) {
					throw new MaceException("buyerOrder n=" + n + " has multiple bundles");
				}
				// System.out.println("--- Defining Z,X,U for buyer " + n + " ---");
				// for (Iterator itr = buyerOrder.getBundleOrders().values().iterator();
				// itr.hasNext();) {
				// AbstractBundleOrder hoge = (AbstractBundleOrder) itr.next();
				// Good good = hoge.getBundle().getGood(0);
				// System.out.println("▲▲" + buyerOrder.getId() + ", deadline="
				// + hoge.getQuality().getQuality(good, "DeadlineTime"));
				// }
				AbstractBundleOrder bundleOrderBuyer = buyerOrder.getBundleOrders().values()
					.iterator().next();
				// For each good in this bundle
				for (int r = 0; r < bundleOrderBuyer.getBundle().size(); r++) {
					Good good = bundleOrderBuyer.getBundle().getGood(r);
					int i = goodPool.getIndex(good);
					int buyArrival = ((Double) bundleOrderBuyer.getQuality().getQuality(good,
						"ArrivalTime")).intValue();
					int buyDeadline = ((Double) bundleOrderBuyer.getQuality().getQuality(good,
						"DeadlineTime")).intValue();
					// AttributesBundle buyQuality = bundleOrderBuyer.getQuality();
					// System.out.println("good=" + good.getId() + ", " + good + ", buyArrival="
					// + buyArrival + ", buyDeadline=" + buyDeadline + ", quality=" + buyQuality);
					// For each time slot of this good
					for (int t = buyArrival; t <= buyDeadline; t++) {
						varZ.add(mip.makeNewBooleanVar("z_n" + n + "s" + i + "t" + t), n, t, i);
					}
					varX.add(mip.makeNewBooleanVar("x_n" + n + "s" + i), n, i);
				}
				varU.add(mip.makeNewBooleanVar("u_n" + n), n);
			}

			// Define Y
			// For each seller order
			for (Iterator<Entity> sellerOrderPoolIterator = orderSellerPool.getValueIterator(); sellerOrderPoolIterator
				.hasNext();) {
				AbstractOrder sellerOrder = (AbstractOrder) sellerOrderPoolIterator.next();
				int m = sellerAgentPool.getIndex(sellerOrder.getAgent());
				if (sellerOrder.getBundleOrders().size() != 1) {
					throw new MaceException("sellerOrder m=" + m + " has multiple bundles");
				}
				AbstractBundleOrder bundleOrderSeller = sellerOrder.getBundleOrders().values()
					.iterator().next();
				// For each buyer order
				for (Iterator<Entity> buyerOrderPoolIterator = orderBuyerPool.getValueIterator(); buyerOrderPoolIterator
					.hasNext();) {
					AbstractOrder buyerOrder = (AbstractOrder) buyerOrderPoolIterator.next();
					int n = buyerAgentPool.getIndex(buyerOrder.getAgent());
					if (buyerOrder.getBundleOrders().size() != 1) {
						throw new MaceException("buyerOrder n=" + n + " has multiple bundles");
					}
					// System.out.println("--- Defining Y between seller " + m + " and buyer " + n
					// + " ---");
					AbstractBundleOrder bundleOrderBuyer = buyerOrder.getBundleOrders().values()
						.iterator().next();
					// For each good in both bundle
					if (bundleOrderSeller.getBundle().size() != 1) {
						throw new MaceException("sellerOrder m=" + m + " has multiple goods");
					}
					Good good = bundleOrderSeller.getBundle().getGood(0);
					int i = goodPool.getIndex(good);
					if (bundleOrderBuyer.getBundle().contains(good)) {
						int sellArrival = ((Double) bundleOrderSeller.getQuality().getQuality(good,
							"ArrivalTime")).intValue();
						int sellDeadline = ((Double) bundleOrderSeller.getQuality().getQuality(
							good, "DeadlineTime")).intValue();
						int buyArrival = ((Double) bundleOrderBuyer.getQuality().getQuality(good,
							"ArrivalTime")).intValue();
						int buyDeadline = ((Double) bundleOrderBuyer.getQuality().getQuality(good,
							"DeadlineTime")).intValue();
						// Good buyerGood = bundleOrderBuyer.getBundle().getGood(good.getId());
						// AttributesBundle buyQuality = bundleOrderBuyer.getQuality();
						// System.out.println("sellerGood=" + good.getId() + " " + good + ", arrv="
						// + sellArrival + ", dead=" + sellDeadline);
						// System.out.println("buyerGood=" + buyerGood.getId() + " " + buyerGood
						// + ", arrv=" + buyArrival + ", dead=" + buyDeadline);
						// For each time slot
						for (int t = buyArrival; t <= buyDeadline; t++) {
							if (t >= sellArrival && t <= sellDeadline) {
								// MIPWrapper は上下限を設定できなくなったみたいなので素の MIP を使う
								Variable v = new Variable("y_m" + m + "n" + n + "s" + i + "t" + t,
									VarType.DOUBLE, 0, 1);
								mip.add(v);
								varY.add(v, m, n, i, t);
							}
						}
					}
				}
			}

			// ------------------------------------------------------------------
			// Build the objective function
			// ------------------------------------------------------------------
			log.debug("Building objective function");
			// For each buyer order
			for (Iterator<Entity> buyerOrderPoolIterator = orderBuyerPool.getValueIterator(); buyerOrderPoolIterator
				.hasNext();) {
				AbstractOrder buyerOrder = (AbstractOrder) buyerOrderPoolIterator.next();
				Agent buyerAgent = buyerOrder.getAgent();
				int n = buyerAgentPool.getIndex(buyerAgent);
				AbstractBundleOrder bundleOrderBuyer = buyerOrder.getBundleOrders().values()
					.iterator().next();
				double buyerBid = bundleOrderBuyer.getBid();
				mip.addObjectiveTerm(varU.get(n), (float) buyerBid);
			}
			// For each seller order
			for (Iterator<Entity> sellerOrderPoolIterator = orderSellerPool.getValueIterator(); sellerOrderPoolIterator
				.hasNext();) {
				AbstractOrder sellerOrder = (AbstractOrder) sellerOrderPoolIterator.next();
				int m = sellerAgentPool.getIndex(sellerOrder.getAgent());
				AbstractBundleOrder bundleOrderSeller = sellerOrder.getBundleOrders().values()
					.iterator().next();
				double sellerBid = bundleOrderSeller.getBid();
				// For each buyer order
				for (Iterator<Entity> buyerOrderPoolIterator = orderBuyerPool.getValueIterator(); buyerOrderPoolIterator
					.hasNext();) {
					AbstractOrder buyerOrder = (AbstractOrder) buyerOrderPoolIterator.next();
					int n = buyerAgentPool.getIndex(buyerOrder.getAgent());
					AbstractBundleOrder bundleOrderBuyer = buyerOrder.getBundleOrders().values()
						.iterator().next();
					// For each good in both bundle
					Good good = bundleOrderSeller.getBundle().getGood(0);
					int i = goodPool.getIndex(good);
					if (bundleOrderBuyer.getBundle().contains(good)) {
						int buyArrival = ((Double) bundleOrderBuyer.getQuality().getQuality(good,
							"ArrivalTime")).intValue();
						int buyDeadline = ((Double) bundleOrderBuyer.getQuality().getQuality(good,
							"DeadlineTime")).intValue();
						// For each time slot
						for (int t = buyArrival; t <= buyDeadline; t++) {
							if (varY.get(m, n, i, t) != null) {
								mip.addObjectiveTerm(varY.get(m, n, i, t), (float) -sellerBid);
							}
						}
					}
				}
			}

			// ------------------------------------------------------------------
			// Build the price constraint. i.e. Welfare >= 0 for each buyer
			// ------------------------------------------------------------------
			log.debug("Building price constraints");
			// For each buyer order
			for (Iterator<Entity> buyerOrderPoolIterator = orderBuyerPool.getValueIterator(); buyerOrderPoolIterator
				.hasNext();) {
				AbstractOrder buyerOrder = (AbstractOrder) buyerOrderPoolIterator.next();
				Agent buyerAgent = buyerOrder.getAgent();
				int n = buyerAgentPool.getIndex(buyerAgent);
				AbstractBundleOrder bundleOrderBuyer = buyerOrder.getBundleOrders().values()
					.iterator().next();
				double buyerBid = bundleOrderBuyer.getBid();
				// Begin a new constraint
				Constraint c = mip.beginNewGEQConstraint(0);
				// For each seller order
				for (Iterator<Entity> sellerOrderPoolIterator = orderSellerPool.getValueIterator(); sellerOrderPoolIterator
					.hasNext();) {
					AbstractOrder sellerOrder = (AbstractOrder) sellerOrderPoolIterator.next();
					int m = sellerAgentPool.getIndex(sellerOrder.getAgent());
					AbstractBundleOrder bundleOrderSeller = sellerOrder.getBundleOrders().values()
						.iterator().next();
					double sellerBid = bundleOrderSeller.getBid();
					// For each good in both bundle
					Good good = bundleOrderSeller.getBundle().getGood(0);
					int i = goodPool.getIndex(good);
					if (bundleOrderBuyer.getBundle().contains(good)) {
						int buyArrival = ((Double) bundleOrderBuyer.getQuality().getQuality(good,
							"ArrivalTime")).intValue();
						int buyDeadline = ((Double) bundleOrderBuyer.getQuality().getQuality(good,
							"DeadlineTime")).intValue();
						// For each time slot
						for (int t = buyArrival; t <= buyDeadline; t++) {
							if (varY.get(m, n, i, t) != null) {
								c.addTerm(varY.get(m, n, i, t), (float) -sellerBid);
							}
						}
					}
					c.addTerm(varU.get(n), (float) buyerBid);
					mip.endConstraint(c);
				}
			}

			// ------------------------------------------------------------------
			// Build the seller possession constraint. i.e. Sum Y <= 1
			// ------------------------------------------------------------------
			log.debug("Building seller possession constraint");
			// For each seller order
			for (Iterator<Entity> sellerOrderPoolIterator = orderSellerPool.getValueIterator(); sellerOrderPoolIterator
				.hasNext();) {
				AbstractOrder sellerOrder = (AbstractOrder) sellerOrderPoolIterator.next();
				int m = sellerAgentPool.getIndex(sellerOrder.getAgent());
				AbstractBundleOrder bundleOrderSeller = sellerOrder.getBundleOrders().values()
					.iterator().next();
				Good good = bundleOrderSeller.getBundle().getGood(0);
				int i = goodPool.getIndex(good);
				int sellArrival = ((Double) bundleOrderSeller.getQuality().getQuality(good,
					"ArrivalTime")).intValue();
				int sellDeadline = ((Double) bundleOrderSeller.getQuality().getQuality(good,
					"DeadlineTime")).intValue();
				// For each time slot
				for (int t = sellArrival; t <= sellDeadline; t++) {
					// Begin a new constraint
					Constraint c = mip.beginNewLEQConstraint(1);
					// For each buyer order
					for (Iterator<Entity> buyerOrderPoolIterator = orderBuyerPool
						.getValueIterator(); buyerOrderPoolIterator.hasNext();) {
						AbstractOrder buyerOrder = (AbstractOrder) buyerOrderPoolIterator.next();
						int n = buyerAgentPool.getIndex(buyerOrder.getAgent());
						if (varY.get(m, n, i, t) != null) {
							c.addTerm(varY.get(m, n, i, t), 1);
						}
					}
					mip.endConstraint(c);
				}
				// }
			}

			// ------------------------------------------------------------------
			// Build the volume constraint
			// ------------------------------------------------------------------
			log.debug("Building volume constraints");
			// For each buyer order
			for (Iterator<Entity> buyerOrderPoolIterator = orderBuyerPool.getValueIterator(); buyerOrderPoolIterator
				.hasNext();) {
				AbstractOrder buyerOrder = (AbstractOrder) buyerOrderPoolIterator.next();
				AbstractBundleOrder bundleOrderBuyer = buyerOrder.getBundleOrders().values()
					.iterator().next();
				int n = buyerAgentPool.getIndex(buyerOrder.getAgent());
				// For each good in this bundle
				for (int r = 0; r < bundleOrderBuyer.getBundle().size(); r++) {
					Good good = bundleOrderBuyer.getBundle().getGood(r);
					int i = goodPool.getIndex(good);
					int buyArrival = ((Double) bundleOrderBuyer.getQuality().getQuality(good,
						"ArrivalTime")).intValue();
					int buyDeadline = ((Double) bundleOrderBuyer.getQuality().getQuality(good,
						"DeadlineTime")).intValue();
					// For each time slots
					for (int t = buyArrival; t <= buyDeadline; t++) {
						// Begin a new constraint
						Constraint c = mip.beginNewLEQConstraint(0);
						// For each seller order
						for (Iterator<Entity> sellerOrderPoolIterator = orderSellerPool
							.getValueIterator(); sellerOrderPoolIterator.hasNext();) {
							AbstractOrder sellerOrder = (AbstractOrder) sellerOrderPoolIterator
								.next();
							AbstractBundleOrder bundleOrderSeller = sellerOrder.getBundleOrders()
								.values().iterator().next();
							int m = maceDataModel.getSellerAgentPool().getIndex(
								sellerOrder.getAgent());
							double sellVolume = bundleOrderSeller.getQuality().getQuality(good,
								"Volume");
							if (varY.get(m, n, i, t) != null) {
								c.addTerm(varY.get(m, n, i, t), (float) -sellVolume);
							}
						}
						double buyVolume = bundleOrderBuyer.getQuality().getQuality(good, "Volume");
						if (varZ.get(n, t, i) != null) {
							c.addTerm(varZ.get(n, t, i), (float) buyVolume);
						}
						mip.endConstraint(c);
					}
				}
			}

			// ------------------------------------------------------------------
			// Build the all-or-nothing constraint
			// ------------------------------------------------------------------
			log.debug("Building the all-or-nothing constraints");
			// For each buyer order
			for (Iterator<Entity> buyerOrderPoolIterator = orderBuyerPool.getValueIterator(); buyerOrderPoolIterator
				.hasNext();) {
				AbstractOrder buyerOrder = (AbstractOrder) buyerOrderPoolIterator.next();
				Agent buyerAgent = buyerOrder.getAgent();
				int n = buyerAgentPool.getIndex(buyerAgent);
				AbstractBundleOrder bundleOrderBuyer = buyerOrder.getBundleOrders().values()
					.iterator().next();
				Bundle buyerBundle = bundleOrderBuyer.getBundle();
				// Begin a new constraint
				Constraint c = mip.beginNewEQConstraint(0);
				c.addTerm(varU.get(n), -buyerBundle.size());
				// For each good in this bundle
				for (int r = 0; r < buyerBundle.size(); r++) {
					Good good = buyerBundle.getGood(r);
					int i = goodPool.getIndex(good);
					if (varX.get(n, i) != null) {
						c.addTerm(varX.get(n, i), 1);
					}
				}
				mip.endConstraint(c);
			}

			// ------------------------------------------------------------------
			// Build the time constraint
			// ------------------------------------------------------------------
			log.debug("Building the time constraints");
			// For each buyer order
			for (Iterator<Entity> buyerOrderPoolIterator = orderBuyerPool.getValueIterator(); buyerOrderPoolIterator
				.hasNext();) {
				AbstractOrder buyerOrder = (AbstractOrder) buyerOrderPoolIterator.next();
				Agent buyerAgent = buyerOrder.getAgent();
				int n = buyerAgentPool.getIndex(buyerAgent);
				AbstractBundleOrder bundleOrderBuyer = buyerOrder.getBundleOrders().values()
					.iterator().next();
				// For each good in this bundle
				for (int r = 0; r < bundleOrderBuyer.getBundle().size(); r++) {
					Good good = bundleOrderBuyer.getBundle().getGood(r);
					int i = goodPool.getIndex(good);
					int buyTotal = ((Double) bundleOrderBuyer.getQuality().getQuality(good,
						"TotalTime")).intValue();
					int buyArrival = ((Double) bundleOrderBuyer.getQuality().getQuality(good,
						"ArrivalTime")).intValue();
					int buyDeadline = ((Double) bundleOrderBuyer.getQuality().getQuality(good,
						"DeadlineTime")).intValue();
					if (varX.get(n, i) != null) {
						// Begin a new constraint
						Constraint c = mip.beginNewEQConstraint(0);
						c.addTerm(varX.get(n, i), -buyTotal);
						for (int t = buyArrival; t <= buyDeadline; t++) {
							if (varZ.get(n, t, i) != null) {
								c.addTerm(varZ.get(n, t, i), 1);
								// Begin a new constraint
								// この制約は要らないだろう。注文時間内しか変数を作ってないのだから。
								Constraint c1 = mip.beginNewLEQConstraint(0);
								Constraint c2 = mip.beginNewLEQConstraint(0);
								c1.addTerm(varZ.get(n, t, i), buyArrival - t);
								c2.addTerm(varZ.get(n, t, i), t - buyDeadline);
								mip.endConstraint(c1);
								mip.endConstraint(c2);
							}
						}
						mip.endConstraint(c);
					}
				}
			}
			// For each seller order
			for (Iterator<Entity> sellerOrderPoolIterator = orderSellerPool.getValueIterator(); sellerOrderPoolIterator
				.hasNext();) {
				AbstractOrder sellerOrder = (AbstractOrder) sellerOrderPoolIterator.next();
				Agent sellerAgent = sellerOrder.getAgent();
				AbstractBundleOrder bundleOrderSeller = sellerOrder.getBundleOrders().values()
					.iterator().next();
				int m = sellerAgentPool.getIndex(sellerAgent);
				// For each good in this bundle
				Good good = bundleOrderSeller.getBundle().getGood(0);
				int i = goodPool.getIndex(good);
				int sellArrival = ((Double) bundleOrderSeller.getQuality().getQuality(good,
					"ArrivalTime")).intValue();
				int sellDeadline = ((Double) bundleOrderSeller.getQuality().getQuality(good,
					"DeadlineTime")).intValue();
				for (int t = sellArrival; t <= sellDeadline; t++) {
					// Begin a new constraint
					Constraint c1 = mip.beginNewLEQConstraint(0);
					Constraint c2 = mip.beginNewLEQConstraint(0);
					// For each buyer order
					for (Iterator<Entity> buyerOrderPoolIterator = orderBuyerPool
						.getValueIterator(); buyerOrderPoolIterator.hasNext();) {
						AbstractOrder buyerOrder = (AbstractOrder) buyerOrderPoolIterator.next();
						Agent buyerAgent = buyerOrder.getAgent();
						int n = buyerAgentPool.getIndex(buyerAgent);
						if (varY.get(m, n, i, t) != null) {
							c1.addTerm(varY.get(m, n, i, t), sellArrival - t);
							c2.addTerm(varY.get(m, n, i, t), t - sellDeadline);
						}
					}
					mip.endConstraint(c1);
					mip.endConstraint(c2);
				}
			}

			log.debug("Model built including " + mip.getNumConstraints() + " constraints.");
		} catch (MIPException ex) {
			File file = new File("error_" + System.currentTimeMillis() + ".xml");
			log.error("Dumping orderbook to " + file.getAbsolutePath());
			maceDataModel.getMarket().export(file, maceDataModel.getOrderBook());
			throw new SolverException(ex);
		}
	}
}
