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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import edu.iw.mace.environment.MaceException;
import edu.iw.mace.environment.Market;
import edu.iw.mace.order.BundleOrderBuyer;
import edu.iw.mace.outcome.OutcomeBuyerBean;
import edu.iw.mace.outcome.OutcomeSellerBean;
import edu.iw.mace.outcome.OutcomeSellerSlotBean;

/**
 * An implementation of the K-Pricing schema
 * 
 * Created: 25.04.2005
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public class KPrice extends AbstractPricing {

	private static Logger log = Logger.getLogger(KPrice.class);

	private static final double KVALUE = 0.5;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.iw.auctionsimulator.outcome.pricing.AbstractPricing#pricing(edu.iw.auctionsimulator.Market
	 * )
	 */
	@Override
	public void pricing(Market market) throws MaceException {

		log.debug("K-Pricing started");

		HashMap<String, KPriceBean> sellerBeans = new HashMap<String, KPriceBean>();

		for (Iterator outcomeBuyerIterator = market.getOutcome().getAllocationBuyerBeanPool()
			.getValueIterator(); outcomeBuyerIterator.hasNext();) {
			OutcomeBuyerBean buyerBean = (OutcomeBuyerBean) outcomeBuyerIterator.next();
			// Get all seller beans that allocate to this particular buyer
			ArrayList result = market.getOutcome().getAllocationSellerBeanPool().getAllocatedBeans(
				buyerBean.getAgent());
			double allSellerReservation = 0.0d;
			if (result.size() == 0) {
				if (buyerBean.getBid() > 0) {
					throw new MaceException("K-Pricing: No seller found for buyer  "
						+ buyerBean.getAgent().getId());
				}
			} else {
				// Iterate over all seller beans that allocate to this buyer
				for (int b = 0; b < result.size(); b++) {
					OutcomeSellerBean sellerBean = (OutcomeSellerBean) result.get(b);
					// Does this seller already has a K-Price Bean? If not,
					// generate a new one
					if (sellerBeans.containsKey(sellerBean.getId()) == false) {
						sellerBeans.put(sellerBean.getId(), new KPriceBean(sellerBean));
					}
					// Now iterate over all time slots of this seller
					for (Iterator timeSlots = sellerBean.getSlotsKeySet().iterator(); timeSlots
						.hasNext();) {
						int time = ((Integer) timeSlots.next()).intValue();
						// Get the slot bean allocated on this time slot
						for (int c = 0; c < sellerBean.getAllocation(time).size(); c++) {
							OutcomeSellerSlotBean slotBean = (OutcomeSellerSlotBean) sellerBean
								.getAllocation(time).toArray()[c];
							// Is the "bean" relevant to the buyer?
							if (slotBean.getBuyerAgent().equals(buyerBean.getAgent())) {
								// Add the value of this allocation to the
								// seller's reservation prices
								allSellerReservation += slotBean.getPercentage()
									* sellerBean.getOrder().getBid();
							}
						}
					}
				}
			}
			// The surplus of this transaction is defined as "buyer bid -
			// sellers bids" (beta-value in the MACE paper)
			log.debug("buyerValue=" + buyerBean.getOrder().getBid()
				* ((BundleOrderBuyer) buyerBean.getOrder()).getSlots() + ", sellerValue="
				+ allSellerReservation);

			double surplus = buyerBean.getOrder().getBid()
				* ((BundleOrderBuyer) buyerBean.getOrder()).getSlots() - allSellerReservation;
			// Negative spread?
			if (surplus < 0) {
				File file = new File("error_" + System.currentTimeMillis() + ".xml");
				log.error("Dumping orderbook to " + file.getAbsolutePath());
				market.export(file, market.getOrderBookManagement().getOrderBook());
				throw new MaceException("K-Price: Found negative spread! " + surplus + " bid: "
					+ buyerBean.getOrder().getBid() + " slots: "
					+ ((BundleOrderBuyer) buyerBean.getOrder()).getSlots() + " discount "
					+ allSellerReservation);

			}

			// Now iterate again all seller orders and partition the spread
			for (int b = 0; b < result.size(); b++) {
				OutcomeSellerBean sellerBean = (OutcomeSellerBean) result.get(b);
				// Now iterate over all time slots of this seller
				for (Iterator timeSlots = sellerBean.getSlotsKeySet().iterator(); timeSlots
					.hasNext();) {
					int time = ((Integer) timeSlots.next()).intValue();
					// Get the slot bean allocated on this time slot
					for (int c = 0; c < sellerBean.getAllocation(time).size(); c++) {
						OutcomeSellerSlotBean slotBean = (OutcomeSellerSlotBean) sellerBean
							.getAllocation(time).toArray()[c];
						// Is the "bean" relevant to the buyer?
						if (slotBean.getBuyerAgent().equals(buyerBean.getAgent())) {
							// Get the price bean of this seller
							KPriceBean priceBean = sellerBeans.get(sellerBean.getId());
							// Get the value of this slot
							double slotPrice = slotBean.getPercentage()
								* sellerBean.getOrder().getBid();
							// Compute the proportion of a seller's allocation
							double spreadSlot = slotPrice / allSellerReservation;
							// And give the seller (1-k)*surplus*spreadSlot
							double sellerDiscount = spreadSlot * surplus * (1 - KVALUE);
							// Add this value to the seller's price bean
							double temp = priceBean.getDiscount();
							priceBean.setDiscount(temp + sellerDiscount);
						}
					}
				}
			}

			// Now compute the price of a buyer
			buyerBean.setBid(buyerBean.getOrder().getBid()
				* ((BundleOrderBuyer) buyerBean.getOrder()).getSlots());
			// Subtract the discount
			buyerBean.setPrice(buyerBean.getBid() - KVALUE * surplus);
			buyerBean.setValuation(buyerBean.getOrder().getValuation()
				* ((BundleOrderBuyer) buyerBean.getOrder()).getSlots());

			if (buyerBean.getPrice() < 0) {
				throw new MaceException("K-Price: Buyer prices is less than 0: "
					+ buyerBean.getPrice());
			}
			if (buyerBean.getBid() < buyerBean.getPrice()) {
				throw new MaceException("K-Price: Buyer Bid < Price " + buyerBean.getBid() + " "
					+ buyerBean.getPrice());
			}

		}
		log.debug("EndTest");
		// Iterate over all seller Beans and compute the price of each seller
		for (Iterator<KPriceBean> priceSellers = sellerBeans.values().iterator(); priceSellers
			.hasNext();) {
			KPriceBean bean = priceSellers.next();
			double askedSellerPriceForOrder = bean.getOutcomeBean().computeAllocationReservation(
				bean.getOutcomeBean().getOrder().getBid());
			double askedSellerPriceForOrderValuation = bean.getOutcomeBean()
				.computeAllocationReservation(bean.getOutcomeBean().getOrder().getValuation());
			if (askedSellerPriceForOrder < 0) {
				throw new MaceException("K-Price: Found negative seller price: "
					+ askedSellerPriceForOrder);
			}
			if (bean.getDiscount() < 0) {
				throw new MaceException("K-Price: Found negative seller discount: "
					+ bean.getDiscount());
			}
			bean.getOutcomeBean().setBid(askedSellerPriceForOrder);
			bean.getOutcomeBean().setPrice(askedSellerPriceForOrder + bean.getDiscount());
			bean.getOutcomeBean().setValuation(askedSellerPriceForOrderValuation);
		}

		log.debug("K-Pricing done");
	}
}