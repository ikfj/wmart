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

import java.util.Vector;

import edu.iw.mace.environment.MaceException;
import edu.iw.mace.environment.Market;
import edu.iw.mace.outcome.Outcome;
import edu.iw.utils.patterns.FactoryException;

/**
 * Created: 15.09.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */

public class Allocator {

	/**
	 * Given a market instance and a MaceDataModel this method computes an
	 * allocation (i.e., it solves the winner determination problem)
	 * 
	 * @param market
	 *            Market instance
	 * @param dataModel
	 *            A vector of MaceDataModels
	 * @throws SolverException
	 * @throws FactoryException
	 */
	public static void computeAllocation(Market market,
			Vector<MaceDataModel> dataModel) throws SolverException,
			FactoryException, MaceException {
		// Get the winner determination model
		AbstractWinnerDetermination solver = (AbstractWinnerDetermination) market
				.getWinnerDeterminationFactory().createAbstractProduct();
		// Generate a new outcome object
		Outcome outcome = market.getOutcome();
		outcome.setCurrentSolver(solver);
		solver.setOutcome(outcome);
		// Solve
		solver.solve(market, dataModel);
	}

	public static void main(String args[]) {
		System.out.println("started Allocator");
		
		Market market = null;
		try {
			market = new Market();
		} catch (MaceException e1) {
			e1.printStackTrace();
		}
		try {
			Allocator.computeAllocation(market, market.getOrderBookManagement()
					.getDataModels());
		} catch (SolverException e) {
			e.printStackTrace();
		} catch (FactoryException e) {
			e.printStackTrace();
		} catch (MaceException e) {
			e.printStackTrace();
		}

		System.out.println("finished Allocator");
	}

}