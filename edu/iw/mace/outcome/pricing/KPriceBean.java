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

package edu.iw.mace.outcome.pricing;

import edu.iw.mace.outcome.OutcomeSellerBean;

/**
 * This class encapsulates a seller outcome bean and its k-price "discount"
 * 
 * Created: 26.04.2005
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */

public class KPriceBean {

	private double discount = 0.0d;

	private OutcomeSellerBean outcomeBean = null;

	/**
	 * Constructor
	 * 
	 * @param outcomeBean
	 *            The outcomeSellerBean to set
	 */
	public KPriceBean(OutcomeSellerBean outcomeBean) {
		this(0, outcomeBean);
	}

	/**
	 * Constructor
	 * 
	 * @param discount
	 *            Initial discount
	 * @param outcomeBean
	 *            The outcomeSellerBean to set
	 */
	public KPriceBean(double discount, OutcomeSellerBean outcomeBean) {
		super();
		this.discount = discount;
		this.outcomeBean = outcomeBean;
	}

	/**
	 * @return Returns the discount.
	 */
	public double getDiscount() {
		return discount;
	}

	/**
	 * @param discount
	 *            The discount to set.
	 */
	public void setDiscount(double discount) {
		this.discount = discount;
	}

	/**
	 * @return Returns the outcomeBean.
	 */
	public OutcomeSellerBean getOutcomeBean() {
		return outcomeBean;
	}

	/**
	 * @param outcomeBean
	 *            The outcomeBean to set.
	 */
	public void setOutcomeBean(OutcomeSellerBean outcomeBean) {
		this.outcomeBean = outcomeBean;
	}

}
