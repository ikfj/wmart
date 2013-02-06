package edu.iw.mace.outcome.pricing;

import java.util.*;

import org.apache.log4j.*;

import edu.iw.mace.environment.*;
import edu.iw.mace.outcome.*;

/**
 * WMart �̃v���C�V���O
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

		// �X�e�b�v1. ������育�ƂɁA����Ő��܂���]���v�Z����B
		// ���ׂĂ̔����������[�v
		for (Iterator outcomeBuyerIterator = market.getOutcome().getAllocationBuyerBeanPool().getValueIterator(); outcomeBuyerIterator
			.hasNext();) {
			OutcomeBuyerBean buyerBean = (OutcomeBuyerBean) outcomeBuyerIterator.next();
			// �X�e�b�v1-1. ���蒍�����i�̍��v���v�Z����B
			double buyerValue = buyerBean.getOrder().getBid();
			double allSellersValue = 0.0;
			// ���̔������Ɋ֘A���邷�ׂĂ̔���������[�v
			for (Iterator outcomeSellerIterator = market.getOutcome().getAllocationSellerBeanPool().getAllocatedBeans(
				buyerBean.getAgent()).iterator(); outcomeSellerIterator.hasNext();) {
				OutcomeSellerBean sellerBean = (OutcomeSellerBean) outcomeSellerIterator.next();
				// ���̔�����Ŋ��蓖�Ă�ꂽ���ׂẴX���b�g�����[�v
				for (Iterator slotsIterator = sellerBean.getSlotsKeySet().iterator(); slotsIterator.hasNext();) {
					int slot = (Integer) slotsIterator.next();
					// ���̃X���b�g�Ŋ��蓖�Ă�ꂽ���ׂẴX���b�g�r�[�������[�v
					for (Iterator slotBeanIterator = sellerBean.getAllocation(slot).iterator(); slotBeanIterator
						.hasNext();) {
						OutcomeSellerSlotBean slotBean = (OutcomeSellerSlotBean) slotBeanIterator.next();
						if (slotBean.getBuyerAgent().equals(buyerBean.getAgent())) {
							// ���̃X���b�g�̔��蒍�����i�����Z
							allSellersValue += sellerBean.getOrder().getBid() * slotBean.getPercentage();
						}
					}
				}
			}
			double surplus = buyerValue - allSellersValue;
			log.debug("buyerValue=" + buyerValue + ", allSellersValue=" + allSellersValue + ", surplus=" + surplus);

			// �X�e�b�v1-2. ��]�̔����𔃂���̎�蕪�Ƃ���B
			// �c�蔼���𔄂�蓯�m�ŕ��z����B���̂Ƃ��A����蓯�m�ŗ��v�����������Ȃ�悤�ɂ���B
			double buyerDiscount = surplus * KVALUE;
			double allSellersDiscount = surplus * (1 - KVALUE);
			// ���̔������Ɋ֘A���邷�ׂĂ̔���������[�v
			for (Iterator outcomeSellerIterator = market.getOutcome().getAllocationSellerBeanPool().getAllocatedBeans(
				buyerBean.getAgent()).iterator(); outcomeSellerIterator.hasNext();) {
				OutcomeSellerBean sellerBean = (OutcomeSellerBean) outcomeSellerIterator.next();
				// ���̔�����Ŋ��蓖�Ă�ꂽ���ׂẴX���b�g�����[�v
				for (Iterator slotsIterator = sellerBean.getSlotsKeySet().iterator(); slotsIterator.hasNext();) {
					int slot = (Integer) slotsIterator.next();
					// ���̃X���b�g�Ŋ��蓖�Ă�ꂽ���ׂẴX���b�g�r�[�������[�v
					for (Iterator slotBeanIterator = sellerBean.getAllocation(slot).iterator(); slotBeanIterator
						.hasNext();) {
						OutcomeSellerSlotBean slotBean = (OutcomeSellerSlotBean) slotBeanIterator.next();
						if (slotBean.getBuyerAgent().equals(buyerBean.getAgent())) {
							// ���̃X���b�g�̔��蒍�����i���S�̂ɐ�߂銄���ɉ����ď�]��z��
							double sellerSlotValue = sellerBean.getOrder().getBid() * slotBean.getPercentage();
							double sellerSlotDiscount = allSellersDiscount * sellerSlotValue / allSellersValue;
							log.debug(String.format("sellerPrice=%f", sellerSlotValue + sellerSlotDiscount));
							// ���̏�]����悹�������l�����Z
							sellerBean.addPriceAt(slot, sellerSlotValue + sellerSlotDiscount);
							sellerBean.addPercentageAt(slot, slotBean.getPercentage());
						}
					}
				}
			}

			// �X�e�b�v1-3. ���l��o�^����B
			buyerBean.setBid(buyerValue);
			buyerBean.setPrice(buyerValue - buyerDiscount);
			buyerBean.setValuation(buyerBean.getOrder().getValuation());
			log.debug("buyerPrice=" + buyerBean.getPrice());
		}
		// ���ׂĂ̔������ɑ΂��Ĉȏ�̌v�Z���s���B

		// �X�e�b�v2. �����育�ƁE�X���b�g���Ƃ̖��P���𓾂�B
		// ���ׂĂ̔���������[�v
		for (Iterator outcomeSellerIterator = market.getOutcome().getAllocationSellerBeanPool().getValueIterator(); outcomeSellerIterator
			.hasNext();) {
			OutcomeSellerBean sellerBean = (OutcomeSellerBean) outcomeSellerIterator.next();
			double sellerValue = sellerBean.getOrder().getBid();
			// �X�e�b�v2-1. ���l��o�^����B
			sellerBean.setBid(sellerValue);
			sellerBean.setPrice(sellerBean.getOverallPrice());
			sellerBean.setValuation(sellerBean.getOrder().getValuation() * sellerBean.getOverallPercentage());
			log.debug("sellerPrice=" + sellerBean.getPrice());
		}

		log.debug("Finished WMartPricing.");
	}
}