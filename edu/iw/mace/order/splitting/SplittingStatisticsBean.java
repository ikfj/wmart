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

/**
 * Encapsulates statistical data concerning the splitting algorithm
 * 
 * Created 03.07.2006
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 */

public class SplittingStatisticsBean {

	private int numberRejectsBuyer = 0;

	private int numberRejectsSeller = 0;

	private int numberBidsLeftBuyer = 0;

	private int numberBidsLeftSeller = 0;

	private int numberBidsRightBuyer = 0;

	private int numberBidsRightSeller = 0;

	/**
	 * @return the numberBidsLeftBuyer
	 */
	public int getNumberBidsLeftBuyer() {
		return numberBidsLeftBuyer;
	}

	/**
	 * @param numberBidsLeftBuyer
	 *            the numberBidsLeftBuyer to set
	 */
	public void setNumberBidsLeftBuyer(int numberBidsLeftBuyer) {
		this.numberBidsLeftBuyer = numberBidsLeftBuyer;
	}

	/**
	 * @return the numberBidsLeftSeller
	 */
	public int getNumberBidsLeftSeller() {
		return numberBidsLeftSeller;
	}

	/**
	 * @param numberBidsLeftSeller
	 *            the numberBidsLeftSeller to set
	 */
	public void setNumberBidsLeftSeller(int numberBidsLeftSeller) {
		this.numberBidsLeftSeller = numberBidsLeftSeller;
	}

	/**
	 * @return the numberBidsRightBuyer
	 */
	public int getNumberBidsRightBuyer() {
		return numberBidsRightBuyer;
	}

	/**
	 * @param numberBidsRightBuyer
	 *            the numberBidsRightBuyer to set
	 */
	public void setNumberBidsRightBuyer(int numberBidsRightBuyer) {
		this.numberBidsRightBuyer = numberBidsRightBuyer;
	}

	/**
	 * @return the numberBidsRightSeller
	 */
	public int getNumberBidsRightSeller() {
		return numberBidsRightSeller;
	}

	/**
	 * @param numberBidsRightSeller
	 *            the numberBidsRightSeller to set
	 */
	public void setNumberBidsRightSeller(int numberBidsRightSeller) {
		this.numberBidsRightSeller = numberBidsRightSeller;
	}

	/**
	 * @return the numberRejectsBuyer
	 */
	public int getNumberRejectsBuyer() {
		return numberRejectsBuyer;
	}

	/**
	 * @param numberRejectsBuyer
	 *            the numberRejectsBuyer to set
	 */
	public void setNumberRejectsBuyer(int numberRejectsBuyer) {
		this.numberRejectsBuyer = numberRejectsBuyer;
	}

	/**
	 * @return the numberRejectsSeller
	 */
	public int getNumberRejectsSeller() {
		return numberRejectsSeller;
	}

	/**
	 * @param numberRejectsSeller
	 *            the numberRejectsSeller to set
	 */
	public void setNumberRejectsSeller(int numberRejectsSeller) {
		this.numberRejectsSeller = numberRejectsSeller;
	}

}
