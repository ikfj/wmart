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
package edu.iw.mace.outcome;

import edu.iw.mace.environment.MaceException;
import edu.iw.mace.order.AbstractBundleOrder;

/**
 * An outcome bean for a buyer
 * 
 * Created: 21.08.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.0
 */
public class OutcomeBuyerBean extends AbstractOutcomeBean {

	/**
	 * Constructor
	 * @param order The buyer order
	 */
	public OutcomeBuyerBean(AbstractBundleOrder order) throws MaceException {
		super(order);
	}


	/* (non-Javadoc)
	 * @see edu.iw.mace.outcome.AbstractOutcomeBean#computeUtility()
	 */
	@Override
	public double computeUtility() {
		return (getValuation() - getPrice());
	}

}
