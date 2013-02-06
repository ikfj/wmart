package edu.iw.mace.outcome.pricing;

import java.util.*;

import org.apache.log4j.*;

import edu.iw.mace.environment.*;
import edu.iw.mace.outcome.*;

/**
 * WMart のプライシング
 * 
 * @author Ikki Fujiwara, NII
 * @version 1.1
 */
public class WMartPricing extends AbstractPricing {

	private static Logger log = Logger.getLogger(WMartPricing.class);

	private static final double KVALUE = 0.5;

	/*
	 *
	 */
	@Override
	public void pricing(Market market) throws MaceException {

		log.debug("Starting WMartPricing.");

		// ステップ1. 買い約定ごとに、取引で生まれる剰余を計算する。
		// すべての買い約定をループ
		for (Iterator outcomeBuyerIterator = market.getOutcome().getAllocationBuyerBeanPool().getValueIterator(); outcomeBuyerIterator
			.hasNext();) {
			OutcomeBuyerBean buyerBean = (OutcomeBuyerBean) outcomeBuyerIterator.next();
			// ステップ1-1. 売り注文価格の合計を計算する。
			double buyerValue = buyerBean.getOrder().getBid();
			double allSellersValue = 0.0;
			// その買い約定に関連するすべての売り約定をループ
			for (Iterator outcomeSellerIterator = market.getOutcome().getAllocationSellerBeanPool().getAllocatedBeans(
				buyerBean.getAgent()).iterator(); outcomeSellerIterator.hasNext();) {
				OutcomeSellerBean sellerBean = (OutcomeSellerBean) outcomeSellerIterator.next();
				// その売り約定で割り当てられたすべてのスロットをループ
				for (Iterator slotsIterator = sellerBean.getSlotsKeySet().iterator(); slotsIterator.hasNext();) {
					int slot = (Integer) slotsIterator.next();
					// そのスロットで割り当てられたすべてのスロットビーンをループ
					for (Iterator slotBeanIterator = sellerBean.getAllocation(slot).iterator(); slotBeanIterator
						.hasNext();) {
						OutcomeSellerSlotBean slotBean = (OutcomeSellerSlotBean) slotBeanIterator.next();
						if (slotBean.getBuyerAgent().equals(buyerBean.getAgent())) {
							// そのスロットの売り注文価格を加算
							allSellersValue += sellerBean.getOrder().getBid() * slotBean.getPercentage();
						}
					}
				}
			}
			double surplus = buyerValue - allSellersValue;
			log.debug("buyerValue=" + buyerValue + ", allSellersValue=" + allSellersValue + ", surplus=" + surplus);

			// ステップ1-2. 剰余の半分を買い手の取り分とする。
			// 残り半分を売り手同士で分配する。このとき、売り手同士で利益率が等しくなるようにする。
			double buyerDiscount = surplus * KVALUE;
			double allSellersDiscount = surplus * (1 - KVALUE);
			// その買い約定に関連するすべての売り約定をループ
			for (Iterator outcomeSellerIterator = market.getOutcome().getAllocationSellerBeanPool().getAllocatedBeans(
				buyerBean.getAgent()).iterator(); outcomeSellerIterator.hasNext();) {
				OutcomeSellerBean sellerBean = (OutcomeSellerBean) outcomeSellerIterator.next();
				// その売り約定で割り当てられたすべてのスロットをループ
				for (Iterator slotsIterator = sellerBean.getSlotsKeySet().iterator(); slotsIterator.hasNext();) {
					int slot = (Integer) slotsIterator.next();
					// そのスロットで割り当てられたすべてのスロットビーンをループ
					for (Iterator slotBeanIterator = sellerBean.getAllocation(slot).iterator(); slotBeanIterator
						.hasNext();) {
						OutcomeSellerSlotBean slotBean = (OutcomeSellerSlotBean) slotBeanIterator.next();
						if (slotBean.getBuyerAgent().equals(buyerBean.getAgent())) {
							// そのスロットの売り注文価格が全体に占める割合に応じて剰余を配分
							double sellerSlotValue = sellerBean.getOrder().getBid() * slotBean.getPercentage();
							double sellerSlotDiscount = allSellersDiscount * sellerSlotValue / allSellersValue;
							log.debug(String.format("sellerPrice=%f", sellerSlotValue + sellerSlotDiscount));
							// その剰余を上乗せした売値を加算
							sellerBean.addPriceAt(slot, sellerSlotValue + sellerSlotDiscount);
							sellerBean.addPercentageAt(slot, slotBean.getPercentage());
						}
					}
				}
			}

			// ステップ1-3. 買値を登録する。
			buyerBean.setBid(buyerValue);
			buyerBean.setPrice(buyerValue - buyerDiscount);
			buyerBean.setValuation(buyerBean.getOrder().getValuation());
			log.debug("buyerPrice=" + buyerBean.getPrice());
		}
		// すべての買い約定に対して以上の計算を行う。

		// ステップ2. 売り約定ごと・スロットごとの約定単価を得る。
		// すべての売り約定をループ
		for (Iterator outcomeSellerIterator = market.getOutcome().getAllocationSellerBeanPool().getValueIterator(); outcomeSellerIterator
			.hasNext();) {
			OutcomeSellerBean sellerBean = (OutcomeSellerBean) outcomeSellerIterator.next();
			double sellerValue = sellerBean.getOrder().getBid();
			// ステップ2-1. 売値を登録する。
			sellerBean.setBid(sellerValue);
			sellerBean.setPrice(sellerBean.getOverallPrice());
			sellerBean.setValuation(sellerBean.getOrder().getValuation() * sellerBean.getOverallPercentage());
			log.debug("sellerPrice=" + sellerBean.getPrice());
		}

		log.debug("Finished WMartPricing.");
	}
}