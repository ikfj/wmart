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

import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;

import edu.harvard.econcs.jopt.solver.IMIPResult;
import edu.harvard.econcs.jopt.solver.SolveParam;
import edu.harvard.econcs.jopt.solver.client.SolverClient;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.iw.mace.environment.Bundle;
import edu.iw.mace.environment.MaceException;
import edu.iw.mace.environment.Market;
import edu.iw.mace.order.AbstractBundleOrder;
import edu.iw.mace.order.AbstractOrder;
import edu.iw.mace.outcome.Outcome;
import edu.iw.utils.patterns.AbstractProduct;

/**
 * This class represents is the basic class for every winner determination model
 * 
 * Created: 15.09.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */

public abstract class AbstractWinnerDetermination implements AbstractProduct {

	protected Outcome outcome = null;

	protected IMIPResult result;

	protected double runtime = -1;

	private static Logger log = Logger.getLogger(AbstractWinnerDetermination.class);

	protected SolverClient solverClient = null;

	/**
	 * Solves the winner determination problem
	 * 
	 * @param market
	 *            Market instance
	 * @param dataModels
	 *            Mace data models
	 * @throws SolverException
	 * @throws MaceException
	 */
	public void solve(Market market, Vector<MaceDataModel> dataModels) throws SolverException,
		MaceException {

		if (SolverSettingsSingleton.instance().getSolver() == SolverSettingsSingleton.LOCAL_DEFAULT) {
			solverClient = new SolverClient();
			log.debug("Using default solver");
		} else if (SolverSettingsSingleton.instance().getSolver() == SolverSettingsSingleton.LOCAL_LPSOLVE51) {
			solverClient = new SolverClient(SolverClient.SOLVER_LPSOLVE51);
			log.debug("Using LPSolve 5.1");
		} else if (SolverSettingsSingleton.instance().getSolver() == SolverSettingsSingleton.LOCAL_LPSOLVE55) {
			solverClient = new SolverClient(SolverClient.SOLVER_LPSOLVE55);
			log.debug("Using LPSolve 5.5");
		} else if (SolverSettingsSingleton.instance().getSolver() == SolverSettingsSingleton.LOCAL_CPLEX) {
			solverClient = new SolverClient(SolverClient.SOLVER_CPLEX);
			log.debug("Using CPLEX");
		} else if (SolverSettingsSingleton.instance().getSolver() == SolverSettingsSingleton.REMOTE) {
			solverClient = new SolverClient(SolverSettingsSingleton.instance().getHost(),
				SolverSettingsSingleton.instance().getPort());
			log.debug("Using remote solver " + solverClient.getName());
		} else {
			throw new SolverException("Could not initialize the solver.");
		}
		log.debug("Found " + dataModels.size() + " data models.");

		solveParticularModel(market, dataModels);
	}

	/**
	 * Solves a particular winner determination model
	 * 
	 * @param market
	 *            Market instance
	 * @param dataModels
	 *            Mace data models
	 * @throws SolverException
	 */
	protected abstract void solveParticularModel(Market market, Vector<MaceDataModel> dataModels)
		throws SolverException, MaceException;

	/**
	 * Returns the runtime required for solving the problem. This is only the measured value from
	 * the solver. Converting times and side-effects like this are neglected.
	 * 
	 * @return Runtime
	 */
	public double getRuntime() {
		return runtime;
	}

	/**
	 * @return Outcome object
	 */
	public Outcome getOutcome() {
		return outcome;
	}

	/**
	 * Sets the outcome object
	 * 
	 * @param outcome
	 *            Outcome object
	 */
	public void setOutcome(Outcome outcome) {
		this.outcome = outcome;
	}

	/**
	 * Returns a bundle order from an agent order set which contains a given bundle
	 * 
	 * @param ordersFromAgent
	 *            Set containing the orders of an agent
	 * @param bundle
	 *            Bundle to search for
	 * @return Order
	 */
	@Deprecated
	protected AbstractBundleOrder getBundleOrder(
		HashMap<String, AbstractBundleOrder> ordersFromAgent, Bundle bundle) {
		int size = ordersFromAgent.size();
		Object[] objects = ordersFromAgent.values().toArray();
		for (int a = 0; a < size; a++) {
			AbstractOrder order = (AbstractOrder) objects[a];
			HashMap bundleOrders = order.getBundleOrders();
			Object[] objectsBundleOrders = bundleOrders.values().toArray();
			for (int b = 0; b < size; b++) {
				if (((AbstractBundleOrder) objectsBundleOrders[b]).getBundle().equals(bundle)) {
					return (AbstractBundleOrder) objectsBundleOrders[b];
				}
			}
		}
		return null;
	}

	/**
	 * Sets the solver parameters
	 * 
	 * @param mip
	 *            MIPWrapper
	 */
	protected void setSolveParam(MIPWrapper mip) {
		// TODO: ƒpƒ‰ƒ[ƒ^‚ÌˆÓ–¡‚Í?
		// mip.setSolveParam(SolveParam.INFEASIBLE_INFO_CALC, (int)0);
		// mip.setSolveParam(SolveParam.TRE_LIM, (double) 800);
		// mip.setSolveParam(SolveParam.WORK_MEM, (double) 1200);
		// mip.setSolveParam(SolveParam.PRECOMPRESS, (int) 0);
		// mip.setSolveParam(SolveParam.NODEFILE, 3);
		// mip.setSolveParam(SolveParam.VAR_SEL, 3);
		// mip.setSolveParam(SolveParam.START_ALGORITHM, (int) 4);
		mip.setSolveParam(SolveParam.ABSOLUTE_INT_GAP, (double) 0);
		mip.setSolveParam(SolveParam.RELATIVE_OBJ_GAP, (double) 0);
		mip.setSolveParam(SolveParam.TIME_LIMIT, (double) SolverSettingsSingleton.instance()
			.getTimeLimit());
		// mip.setSolveParam(SolveParam.PRE_PASS,(int)0);
		// mip.setSolveParam(SolveParam.PRE_IND, false);
		// mip.setSolveParam(SolveParam.NODELIM,700);
		// mip.setSolveParam(SolveParam.ABSOLUTE_VAR_BOUND_GAP, (double) 0);
		mip.setSolveParam(SolveParam.MIP_EMPHASIS, 1);
	}

}