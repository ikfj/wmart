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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import edu.iw.mace.environment.Agent;
import edu.iw.mace.environment.MaceException;
import edu.iw.mace.order.AbstractBundleOrder;

/**
 * OutcomeSellerBean
 * 
 * @author Bjn Schnizler, University of Karlsruhe (TH)
 * @author Ikki Fujiwara, NII
 * @version 1.2
 */
public class OutcomeSellerBean extends AbstractOutcomeBean {

	private HashMap<Integer, Double> prices = null;
	private HashMap<Integer, Double> percentages = null;
	private HashMap<Integer, ArrayList<OutcomeSellerSlotBean>> slots = null;

	/**
	 * Constructor
	 * 
	 * @param order
	 *            The seller order
	 */
	public OutcomeSellerBean(AbstractBundleOrder order) throws MaceException {
		super(order);
		prices = new HashMap<Integer, Double>();
		percentages = new HashMap<Integer, Double>();
		slots = new HashMap<Integer, ArrayList<OutcomeSellerSlotBean>>();
	}

	/**
	 * Adds a new allocation to this outcome bean
	 * 
	 * @param buyerAgent
	 *            The buyer agent that gets allocated
	 * @param percentage
	 *            The percentage allocation to the buyer
	 * @param timeSlot
	 *            The time slot of this allocation
	 */
	public void addAllocation(Agent buyerAgent, double percentage, int timeSlot) {
		ArrayList<OutcomeSellerSlotBean> allocationInSlot = getAllocation(timeSlot);
		if (allocationInSlot == null) {
			allocationInSlot = new ArrayList<OutcomeSellerSlotBean>();
			slots.put(new Integer(timeSlot), allocationInSlot);
		}
		allocationInSlot.add(new OutcomeSellerSlotBean(buyerAgent, percentage));
	}

	/**
	 * Returns the keys of the allocated slot beans
	 * 
	 * @return Keyset of the allocated slot beans
	 */
	public Set<Integer> getSlotsKeySet() {
		return slots.keySet();
	}

	/**
	 * Returns the allocated slots in one time step "slot"
	 * 
	 * @param slot
	 *            The time slot
	 * @return ArrayList containing all slots
	 */
	public ArrayList<OutcomeSellerSlotBean> getAllocation(int slot) {
		return slots.get(new Integer(slot));
	}

	/**
	 * Checks, if a seller has allocated slots or not
	 * 
	 * @return <true> if the seller has allocated slots, <false> otherwise
	 */
	public boolean hasAllocatedSlots() {
		if (slots.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if a buyer gets some resources from this particular seller
	 * 
	 * @param buyerAgent
	 *            The buyer to look for
	 * @param t
	 *            Time slot to search for
	 * @return <true> if the buyer has allocated slots, <false> otherwise
	 */
	public boolean hasAllocatedSlots(Agent buyerAgent, int t) {
		ArrayList<OutcomeSellerSlotBean> allocationInSlot = getAllocation(t);
		if (allocationInSlot != null) {
			for (Iterator<OutcomeSellerSlotBean> allocationInIterator = allocationInSlot.iterator(); allocationInIterator
				.hasNext();) {
				OutcomeSellerSlotBean sellerSlotBean = allocationInIterator.next();
				if (sellerSlotBean.getBuyerAgent().equals(buyerAgent)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Computes the reservation price for the allocation, i.e. downscales the original reservation price acoording to
	 * the (partial) allocation. Example: Original Bid=10, percentage=0.5 --> Result: New Bid=5
	 * 
	 * @param orderPrice
	 *            The original order price per slot
	 * @return New reservation price for the whole time range
	 */
	public double computeAllocationReservation(double orderPrice) {
		return getOverallPercentage() * orderPrice;
	}

	/**
	 * Returns the complete percentage allocation
	 * 
	 * @return Percentage allocation of this agent (スロットごとのパーセンテージの単純合計。最大値==スロット数)
	 */
	public double getOverallPercentage() {
		double result = 0.0d;
		for (Iterator<Integer> iterator = slots.keySet().iterator(); iterator.hasNext();) {
			int t = iterator.next().intValue();
			ArrayList<OutcomeSellerSlotBean> allocationInSlot = getAllocation(t);
			if (allocationInSlot != null) {
				for (int x = 0; x < allocationInSlot.size(); x++) {
					OutcomeSellerSlotBean sellerSlotBean = allocationInSlot.get(x);
					if (sellerSlotBean != null) {
						result += sellerSlotBean.getPercentage();
					}
				}
			}
		}
		return result;
	}

	/**
	 * 合計約定価格を取得する
	 * 
	 * @return
	 */
	public double getOverallPrice() {
		double result = 0.0;
		for (Iterator<Double> itr = prices.values().iterator(); itr.hasNext();) {
			Double price = itr.next();
			result += price;
		}
		return result;
	}

	/**
	 * 売り約定価格リストを取得する
	 * 
	 * @return prices
	 */
	public HashMap<Integer, Double> getPrices() {
		return prices;
	}

	/**
	 * 売り約定比率リストを取得する
	 * 
	 * @return percentages
	 */
	public HashMap<Integer, Double> getPercentages() {
		return percentages;
	}

	/**
	 * 指定したタイムスロットに売り約定価格を加算する
	 * 
	 * @param slot
	 *            タイムスロット
	 * @param value
	 *            価格
	 */
	public void addPriceAt(int slot, double value) {
		if (!prices.containsKey(slot)) {
			prices.put(slot, 0.0);
		}
		prices.put(slot, prices.get(slot) + value);
	}

	/**
	 * 指定したタイムスロットに売り約定比率を加算する
	 * 
	 * @param slot
	 *            タイムスロット
	 * @param value
	 *            比率
	 */
	public void addPercentageAt(int slot, double value) {
		if (!percentages.containsKey(slot)) {
			percentages.put(slot, 0.0);
		}
		percentages.put(slot, percentages.get(slot) + value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.mace.outcome.AbstractOutcomeBean#computeUtility()
	 */
	@Override
	public double computeUtility() {
		return (getPrice() - getValuation());
	}

}