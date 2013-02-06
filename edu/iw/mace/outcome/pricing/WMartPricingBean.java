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
 * 売り約定ごと・スロットごとの価格と数量 (パーセンテージ) を表す
 * 
 * @author Bjn Schnizler, University of Karlsruhe (TH)
 * @author Ikki Fujiwara, NII
 * @version 1.2
 */

public class WMartPricingBean {

	private double price = 0.0;
	private double percentage = 0.0;
	private OutcomeSellerBean outcomeBean = null;

	/**
	 * Constructor
	 * 
	 * @param price
	 *            Initial price
	 * @param outcomeBean
	 *            The outcomeSellerBean to set
	 */
	public WMartPricingBean(OutcomeSellerBean outcomeBean) {
		this.outcomeBean = outcomeBean;
	}

	/**
	 * @return Returns the total contract price.
	 */
	public double getContractPrice() {
		if (percentage > 0.0) {
			return price / percentage;
		}
		return 0;
	}

	/**
	 * @return Returns the price.
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            The price to set.
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * @param price
	 *            The price to add.
	 */
	public void addPrice(double price) {
		this.price += price;
	}

	/**
	 * @return Returns the percentage.
	 */
	public double getPercentage() {
		return percentage;
	}

	/**
	 * @param percentage
	 *            The percentage to set.
	 */
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	/**
	 * @param percentage
	 *            The percentage to add.
	 */
	public void addPercentage(double percentage) {
		this.percentage += percentage;
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
