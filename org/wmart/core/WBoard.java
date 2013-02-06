/*
 * WMart
 * Copyright (C) 2009 Ikki Fujiwara, NII <ikki@nii.ac.jp>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or any later version.
 * See the GNU General Public License for more details.
 *
 * This code comes WITHOUT ANY WARRANTY
 */
package org.wmart.core;

import java.util.*;
import java.util.Map.Entry;

import org.apache.log4j.*;

import edu.iw.mace.environment.*;
import edu.iw.mace.order.*;
import edu.iw.mace.outcome.*;
import edu.iw.mace.outcome.allocator.*;
import edu.iw.utils.patterns.*;

/**
 * �X�̎s���\���N���X�BUMart �� MACE �̋��n��������B<br />
 * 
 * @author Ikki Fujiwara, NII
 * @version 1.1
 */

public class WBoard {

	/** [UMart] �X���b�g�� */
	private int fSlots = 0;
	/** [UMart] �����ꗗ */
	private TreeSet<WOrder> fOrderTreeSet = null;
	/** [UMart] �̍ŏI�X�V���� */
	private Date fLastUpdateTime = null;
	/** [UMart] ���ID�����̂��߂̃J�E���^ */
	private static long fContractID = 0;

	/** [MACE] �{�� */
	private Market fMarket = null;
	/** [MACE] ���K�[ */
	private static Logger log = Logger.getLogger(WBoard.class);

	/* ���߃Z�b�V�����̏�� */
	/** ���i���ƁE�X���b�g���Ƃ̍��v��艿�i�\ */
	private WOutcomeTable fTotalPriceTable = null;
	/** ���i���ƁE�X���b�g���Ƃ̍��v��萔�ʕ\ */
	private WOutcomeTable fTotalVolumeTable = null;
	/** �S���i�E�S�X���b�g�̍��v��艿�i = ������z */
	private double fContractPrice = 0.0;
	/** �S���i�E�S�X���b�g�̍��v��萔�� = �o���� */
	private double fContractVolume = 0.0;

	/**
	 * �R���X�g���N�^
	 */
	public WBoard(int slots) {
		fSlots = slots;
		fOrderTreeSet = new TreeSet(new WOrderComparator());
		fLastUpdateTime = new Date();
		try {
			fMarket = new Market();
		} catch (MaceException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		log.debug("Successful init of forward market");
	}

	/**
	 * ������ fOrderTreeSet �ɓo�^����
	 * 
	 * @param order
	 *            ����
	 */
	public final void appendOrder(WOrder order) {
		synchronized (fOrderTreeSet) {
			if (!fOrderTreeSet.add(order)) {
				System.err.println("Error in WBoard.appendOrder.");
				System.exit(1);
			}
			fLastUpdateTime = new Date();
		}
	}

	/**
	 * �w�肳�ꂽ������ fOrderTreeSet ����폜����D
	 * 
	 * @param userID
	 *            ���[�U�[ID
	 * @param orderID
	 *            ����ID
	 * @return true:�����Cfalse:���s
	 */
	public boolean removeOrder(int userID, long orderID) {
		synchronized (fOrderTreeSet) {
			Iterator itr = fOrderTreeSet.iterator();
			while (itr.hasNext()) {
				WOrder o = (WOrder) itr.next();
				if (o.getUserID() == userID && o.getOrderID() == orderID) {
					itr.remove();
					fLastUpdateTime = new Date();
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * �o�^����Ă���S�Ă̒����� fOrderTreeSet ����폜����B
	 */
	public final void clear() {
		fOrderTreeSet.clear();
		fLastUpdateTime = new Date();
	}

	/**
	 * �񂹂��s��
	 * 
	 * @param date
	 *            ��
	 * @param session
	 *            ��
	 * @param offset
	 *            �n�[�̎���
	 * @return true: ��肠��, false: ���Ȃ�
	 */
	public boolean makeContracts(int date, int session, int offset) {
		fTotalPriceTable = new WOutcomeTable(fSlots, offset, 0.0);
		fTotalVolumeTable = new WOutcomeTable(fSlots, offset, 0.0);
		fContractPrice = 0.0;
		fContractVolume = 0.0;
		if (fOrderTreeSet.size() < 2) { // ��������2��菬����
			return false;
		}
		long maxPrice = searchMaxPrice(fOrderTreeSet);
		if (maxPrice == 0) { // �S�Đ��s����
			return false;
		}
		executeContract(maxPrice, date, session);
		if (fContractVolume == 0.0) {
			return false;
		}
		return true;
	}

	/**
	 * MACE �Ŗ�菈�������s����
	 * 
	 * @param maxPrice
	 *            �o�^����Ă��钍���ɂ�����ō��������i (�s�g�p)
	 * @param date
	 *            ��
	 * @param session
	 *            ��
	 */
	private void executeContract(long maxPrice, int date, int session) {
		// MACE �����Z�b�g
		fMarket.resetOrderBook();

		// �������ʂ�
		transformOrders(date, session);
		if (fMarket.getOrderBookManagement().getOrderBook().getBuyerOrderPool().size() < 0
			&& fMarket.getOrderBookManagement().getOrderBook().getSellerOrderPool().size() < 0) {
			return;
		}

		// ��菈�������s
		log.debug("Starting MACE.");
		try {
			WinnerDeterminationFactory.instance().setValue(
				WinnerDeterminationFactory.MODELS[WinnerDeterminationFactory.WMART]);
			fMarket.solve();
		} catch (MaceException e) {
			log.error(e.fillInStackTrace());
		}
		log.debug("Finished MACE. welfare=" + fMarket.getOutcome().getWelfare());

		// ���ʂ�߂�
		transformContracts(date, session);
		try {
			fMarket.removeExecuted(); // �ꕔ�ł���肵���I�[�_�[�͍폜�B�S����肵�ĂȂ��I�[�_�[�͎c��B
		} catch (MaceException e) {
			log.error(e.fillInStackTrace());
		}
	}

	/**
	 * ������ WMart ���� MACE �֎ʂ�
	 */
	private void transformOrders(int date, int session) {
		for (Iterator<WOrder> itr = fOrderTreeSet.iterator(); itr.hasNext();) {
			WOrder order = itr.next();
			if (order.getSellBuy() == WOrder.BUY) {
				OrderBuyer orderBuyer = null;
				try {
					orderBuyer = generateBuyerOrder(order);
					if (fMarket.getOrderBookManagement().getOrderBook().getBuyerOrderPool()
						.getOrder(orderBuyer.getAgent()) == null) {
						fMarket.getOrderBookManagement().getOrderBook().getBuyerOrderPool()
							.addOrder(orderBuyer);
					} else {
						log.error("Tried to overwrite a buyer order from " + order);
					}
				} catch (MaceException e) {
					e.printStackTrace();
				}
				log.debug(" Generated a new buyer order for " + order);
			} else if (order.getSellBuy() == WOrder.SELL) {
				OrderSeller orderSeller = null;
				try {
					orderSeller = generateSellerOrder(order);
					if (fMarket.getOrderBookManagement().getOrderBook().getSellerOrderPool()
						.getOrder(orderSeller.getAgent()) == null) {
						fMarket.getOrderBookManagement().getOrderBook().getSellerOrderPool()
							.addOrder(orderSeller);
					} else {
						log.error("Tried to overwrite a seller order from " + order);
					}
				} catch (MaceException e) {
					e.printStackTrace();
				}
				log.debug(" Generated a new seller order for " + order);
			}
		}
	}

	/**
	 * Generates a new buyer order for a given message
	 * 
	 * @return New order
	 */
	private OrderBuyer generateBuyerOrder(WOrder order) throws MaceException {
		// Get the agent
		String agentId = Long.toString(order.getOrderID());
		// WMart �́u�����v�� MACE �́u�G�[�W�F���g�v�ɂ�����BMACE ���ł� 1 �G�[�W�F���g 1 �I�[�_�[�ɂȂ�B
		Agent agent = fMarket.getEnvironment().getBuyerPool().getAgent(agentId);
		if (agent == null) {
			agent = new Agent(agentId, Agent.AGENT_BUYER);
			fMarket.getEnvironment().getBuyerPool().addAgent(agent);
		}
		// Generate a new instance of an order
		OrderBuyer orderBuyer = new OrderBuyer(agent);
		// Generate a new instance of an bundle order
		BundleOrderBuyer bundleOrderBuyer = new BundleOrderBuyer(orderBuyer);
		orderBuyer.addBundleOrder(bundleOrderBuyer);
		// Get the bundle
		Bundle bundle = getBundle(order.getOrderSpecMap());
		bundleOrderBuyer.setBundle(bundle);
		// Set the quality characteristics
		AttributesBundle attributesBundle = getAttributesBundle(bundle, order.getOrderSpecMap());
		bundleOrderBuyer.setQuality(attributesBundle);
		// Set the bid and the valuation
		bundleOrderBuyer.setBid(order.getOrderPrice());
		bundleOrderBuyer.setValuation(order.getOrderPrice());
		// Set the time range
		fMarket.getEnvironment().getTimeRange()
			.addTimeRange(order.getEarliestSlot(), order.getLatestSlot());
		// Return the order
		return orderBuyer;
	}

	/**
	 * Generates a new seller order for a given message
	 * 
	 * @return New order
	 */
	private OrderSeller generateSellerOrder(WOrder order) throws MaceException {
		// Get the agent
		String agentId = Long.toString(order.getOrderID());
		// WMart �́u�����v�� MACE �́u�G�[�W�F���g�v�ɂ�����BMACE ���ł� 1 �G�[�W�F���g 1 �I�[�_�[�ɂȂ�B
		Agent agent = fMarket.getEnvironment().getSellerPool().getAgent(agentId);
		if (agent == null) {
			agent = new Agent(agentId, Agent.AGENT_SELLER);
			fMarket.getEnvironment().getSellerPool().addAgent(agent);
		}
		// Generate a new instance of an order
		OrderSeller orderSeller = new OrderSeller(agent);
		// Generate a new instance of an bundle order
		BundleOrderSeller bundleOrderSeller = new BundleOrderSeller(orderSeller);
		orderSeller.addBundleOrder(bundleOrderSeller);
		// Get the bundle
		Bundle bundle = getBundle(order.getOrderSpecMap());
		bundleOrderSeller.setBundle(bundle);
		// Set the quality characteristics
		AttributesBundle attributesBundle = getAttributesBundle(bundle, order.getOrderSpecMap());
		bundleOrderSeller.setQuality(attributesBundle);
		// Set the bid and the reservation
		bundleOrderSeller.setBid(order.getOrderPrice());
		bundleOrderSeller.setValuation(order.getOrderPrice());
		// Set the time range
		fMarket.getEnvironment().getTimeRange()
			.addTimeRange(order.getEarliestSlot(), order.getLatestSlot());
		// Return the order
		return orderSeller;
	}

	/**
	 * Returns a bundle for a given specifications
	 * 
	 * @param goodsOrder
	 *            ordered goods and their specifications
	 * @return Bundle
	 */
	private Bundle getBundle(TreeMap<String, WOrderSpec> orderSpecMap) throws MaceException {
		Bundle bundle = new Bundle();
		for (Iterator<String> iGoods = orderSpecMap.keySet().iterator(); iGoods.hasNext();) {
			// Check, if the good already exists in the good pool
			String key = iGoods.next();
			Good good = fMarket.getEnvironment().getGoodPool().getGood(key);
			if (good == null) {
				good = new Good(key);
				for (int i = 0; i < WOrderSpec.ATTRIBUTES.length; i++) {
					String attr = WOrderSpec.ATTRIBUTES[i];
					Attribute attribute = new Attribute(attr);
					if (!fMarket.getEnvironment().getAttributePool().contains(attribute)) {
						fMarket.getEnvironment().getAttributePool().addAttribute(attribute);
					} else {
						attribute = fMarket.getEnvironment().getAttributePool()
							.getAttribute(attribute);
					}
					good.addAttribute(attribute);
				}
				fMarket.getEnvironment().getGoodPool().addGood(good);
			}
			bundle.add(good);
		}
		bundle.setName(bundle.getId());
		// Check, if this bundle already exists ...
		if (fMarket.getEnvironment().getBundlePool().contains(bundle))
			bundle = fMarket.getEnvironment().getBundlePool().getBundle(bundle);
		else
			fMarket.getEnvironment().getBundlePool().addBundle(bundle);
		return bundle;
	}

	/**
	 * Generates an attribute object for a given bundle and a given specifications
	 * 
	 * @param bundle
	 *            bundle
	 * @param goodsOrder
	 *            requirements
	 * @return Bundle
	 */
	private AttributesBundle getAttributesBundle(Bundle bundle,
		TreeMap<String, WOrderSpec> orderSpecMap) throws MaceException {
		AttributesBundle attributesBundle = new AttributesBundle();
		for (int a = 0; a < bundle.size(); a++) {
			Good good = bundle.getGood(a);
			WOrderSpec spec = orderSpecMap.get(good.getId());
			for (int i = 0; i < WOrderSpec.ATTRIBUTES.length; i++) {
				String attr = WOrderSpec.ATTRIBUTES[i];
				attributesBundle.addQuality(good, attr, spec.get(attr));
			}
		}
		return attributesBundle;
	}

	/**
	 * ���ʂ� MACE ���� WMart �֎ʂ�
	 */
	private void transformContracts(int date, int session) {
		Date time = new Date();
		Outcome outcome = fMarket.getOutcome();

		// ��������C�e���[�g
		for (Iterator<Entity> sellerAllocationIterator = outcome.getAllocationSellerBeanPool()
			.getValueIterator(); sellerAllocationIterator.hasNext();) {
			OutcomeSellerBean sellerBean = (OutcomeSellerBean) sellerAllocationIterator.next();
			String sellerAgentId = sellerBean.getAgent().getId();
			double bid = sellerBean.getBid();
			double price = sellerBean.getOverallPrice();
			double percentage = sellerBean.getOverallPercentage();
			assert (sellerBean.getBundle().size() == 1); // ���i�͒P��̂͂�
			String good = sellerBean.getBundle().getId();
			HashMap<Integer, Double> prices = sellerBean.getPrices();
			HashMap<Integer, Double> percentages = sellerBean.getPercentages();
			double volume = sellerBean.getOrder().getQuality()
				.getQuality(sellerBean.getBundle().getGood(0), "Volume");
			// �I�[�_�[�ɖ�������������
			WOrder order = getOrderById(Long.valueOf(sellerAgentId));
			order.addContract(++fContractID, time, date, session, price, percentage, good, prices,
				percentages, volume);
			// ���i�\�ɒǉ�
			for (Iterator<Entry<Integer, Double>> itr = prices.entrySet().iterator(); itr.hasNext();) {
				Entry<Integer, Double> entry = itr.next();
				fTotalPriceTable.add(good, entry.getKey(), entry.getValue());
				fContractPrice += entry.getValue();
			}
			// ���ʕ\�ɒǉ�
			for (Iterator<Entry<Integer, Double>> itr = percentages.entrySet().iterator(); itr
				.hasNext();) {
				Entry<Integer, Double> entry = itr.next();
				fTotalVolumeTable.add(good, entry.getKey(), entry.getValue() * volume);
				fContractVolume += entry.getValue() * volume;
			}
			// ���M���O
			StringBuilder logstr = new StringBuilder();
			logstr.append(String.format("%s sells %s: bid=%.2f/slot, price=%.2f, total=%.0f%%,",
				sellerAgentId, good, bid, price, percentage * 100));
			log.info(logstr);
		}

		// �ڍׂȃ��M���O
		for (Iterator<WOrder> itr1 = getOrderArray(); itr1.hasNext();) {
			WOrder order = itr1.next();
			if (!order.getContracts().isEmpty()) {
				StringBuilder logstr = new StringBuilder();
				logstr.append("\n  t\t");
				for (int t = order.getEarliestSlot(); t <= order.getLatestSlot(); t++) {
					logstr.append(String.format("%d\t", t));
				}
				logstr.append("\n  $\t");
				for (int t = order.getEarliestSlot(); t <= order.getLatestSlot(); t++) {
					double p = order.getSoldPriceAt(t);
					logstr.append((p < 0.0) ? "--\t" : String.format("%.2f\t", p));
				}
				logstr.append("\n  %\t");
				for (int t = order.getEarliestSlot(); t <= order.getLatestSlot(); t++) {
					double p = order.getSoldPercentageAt(t);
					logstr.append((p < 0.0) ? "--\t" : String.format("%.0f\t", p * 100));
				}
				log.info(logstr);
			}
		}

		// ���������C�e���[�g
		for (Iterator<Entity> buyerAllocationIterator = outcome.getAllocationBuyerBeanPool()
			.getValueIterator(); buyerAllocationIterator.hasNext();) {
			OutcomeBuyerBean buyerBean = (OutcomeBuyerBean) buyerAllocationIterator.next();
			String buyerAgentId = buyerBean.getAgent().getId();
			double bid = buyerBean.getBid();
			double price = buyerBean.getPrice();
			String goods = buyerBean.getBundle().getId();
			// �I�[�_�[�ɖ�������������
			WOrder order = getOrderById(Long.valueOf(buyerAgentId));
			order.addContract(++fContractID, time, date, session, price, 1.0, goods, null, null,
				1.0);
			// ���M���O
			StringBuilder logstr = new StringBuilder();
			logstr.append(String.format("%s buys %s: bid=%.2f, price=%.1f, from ", buyerAgentId,
				goods, bid, price));
			// �Ή����锄�����C�e���[�g
			ArrayList sellers = outcome.getAllocationSellerBeanPool().getAllocatedBeans(
				buyerBean.getAgent());
			for (int a = 0; a < sellers.size(); a++) {
				String sellerAgentId = ((OutcomeSellerBean) sellers.get(a)).getAgent().getId();
				logstr.append(sellerAgentId).append(";");
			}
			log.info(logstr);
		}
	}

	/**
	 * �����ŗ^����ꂽ�c���[�Z�b�g�ɓo�^����Ă��钍���̒��ōł������������i��Ԃ��B
	 * 
	 * @param orderArray
	 *            �����̓o�^���ꂽ�c���[�Z�b�g
	 * @return �ō��������i
	 */
	private static long searchMaxPrice(TreeSet orderTreeSet) {
		long maxprice = 0;
		Iterator itr = orderTreeSet.iterator();
		while (itr.hasNext()) {
			WOrder o = (WOrder) itr.next();
			if (o.getOrderPrice() > maxprice) {
				maxprice = o.getOrderPrice();
			}
		}
		return maxprice;
	}

	/**
	 * ������ID�Ō���
	 * 
	 * @return ����
	 */
	private WOrder getOrderById(long orderId) {
		for (Iterator<WOrder> itr = fOrderTreeSet.iterator(); itr.hasNext();) {
			WOrder order = itr.next();
			if (order.getOrderID() == orderId) {
				return order;
			}
		}
		return null;
	}

	/**
	 * ����(WOrder)���i�[���������q��Ԃ��B
	 * 
	 * @return ����(WOrder)���i�[���������q
	 */
	public Iterator<WOrder> getOrderArray() {
		return fOrderTreeSet.iterator();
	}

	/**
	 * ��菈���ɂ����������Ԃ��擾����
	 * 
	 * @return ���v����
	 */
	public double getRuntime() {
		return fMarket.getOutcome().getRuntime();
	}

	/**
	 * �w�肳�ꂽ���i�̋C�z�l (���蒍�����i�̍ň��l) ���擾����
	 * 
	 * @param good
	 *            ���i��
	 * @return ���蒍�����i�̍ň��l�B�������A������肪����� 0�B �S�Ă̔��蒍�������s���Ȃ�� -1�B
	 */
	public long getQuotation(String good) {
		if (fContractVolume > 0) {
			return 0;
		}
		Iterator itr = fOrderTreeSet.iterator();
		while (itr.hasNext()) {
			WOrder o = (WOrder) itr.next();
			assert o.getOrderSpecMap().size() == 1;
			if (o.getSellBuy() == WOrder.SELL && o.getMarketLimit() == WOrder.LIMIT) {
				if (o.getOrderSpecMap().get(0).getName().equalsIgnoreCase(good)) {
					return o.getOrderPrice();
				}
			}
		}
		return -1;
	}

	/**
	 * �̍ŏI�X�V������Ԃ��D
	 * 
	 * @return �̍ŏI�X�V����
	 */
	public Date getLastUpdateTime() {
		return fLastUpdateTime;
	}

	/**
	 * ���i���ƁE�X���b�g���Ƃ̍��v��艿�i�\���擾���܂��B
	 * 
	 * @return ���i���ƁE�X���b�g���Ƃ̍��v��艿�i�\
	 */
	public WOutcomeTable getTotalPriceTable() {
		return fTotalPriceTable;
	}

	/**
	 * ���i���ƁE�X���b�g���Ƃ̍��v��萔�ʕ\���擾���܂��B
	 * 
	 * @return ���i���ƁE�X���b�g���Ƃ̍��v��萔�ʕ\
	 */
	public WOutcomeTable getTotalVolumeTable() {
		return fTotalVolumeTable;
	}

	/**
	 * �S���i�E�S�X���b�g�̍��v��艿�i = ������z���擾���܂��B
	 * 
	 * @return �S���i�E�S�X���b�g�̍��v��艿�i =������z
	 */
	public double getContractPrice() {
		return fContractPrice;
	}

	/**
	 * �S���i�E�S�X���b�g�̍��v��萔�� = �o�������擾���܂��B
	 * 
	 * @return �S���i�E�S�X���b�g�̍��v��萔�� = �o����
	 */
	public double getContractVolume() {
		return fContractVolume;
	}

	/**
	 * �e�X�g�p
	 */
	public static void main(String args[]) {
		WBoard board = new WBoard(4);
		WOrderManager orderManager = new WOrderManager();
		WOrder o = null;

		int situation = 0;
		switch (situation) {
		case 0:
			o = orderManager.createOrder(1, "provider1", "", "forward", WOrder.SELL, WOrder.LIMIT,
				20, "", 1, 1);
			o.addOrderSpec("serviceA", 40, 0, 3, 4);
			board.appendOrder(o);
			o = orderManager.createOrder(2, "provider2", "", "forward", WOrder.SELL, WOrder.LIMIT,
				15, "", 1, 1);
			o.addOrderSpec("serviceB", 30, 0, 3, 4);
			board.appendOrder(o);
			o = orderManager.createOrder(3, "provider3", "", "forward", WOrder.SELL, WOrder.LIMIT,
				9, "", 1, 1);
			o.addOrderSpec("serviceB", 30, 2, 3, 2);
			board.appendOrder(o);
			o = orderManager.createOrder(4, "user1", "", "forward", WOrder.BUY, WOrder.LIMIT, 60,
				"", 1, 1);
			o.addOrderSpec("serviceA", 20, 1, 3, 3);
			o.addOrderSpec("serviceB", 20, 1, 3, 3);
			board.appendOrder(o);
			o = orderManager.createOrder(5, "user2", "", "forward", WOrder.BUY, WOrder.LIMIT, 40,
				"", 1, 1);
			o.addOrderSpec("serviceA", 10, 0, 2, 3);
			o.addOrderSpec("serviceB", 30, 3, 3, 1);
			board.appendOrder(o);
			break;
		case 1:
			o = orderManager.createOrder(1, "provider1", "", "forward", WOrder.SELL, WOrder.LIMIT,
				5, "", 1, 1);
			o.addOrderSpec("Storage", 10, 1, 1, 1);
			board.appendOrder(o);
			o = orderManager.createOrder(2, "user2", "", "forward", WOrder.BUY, WOrder.LIMIT, 6,
				"", 1, 1);
			o.addOrderSpec("Storage", 3, 1, 1, 1);
			board.appendOrder(o);
			break;
		case 2:
			o = orderManager.createOrder(1, "provider1", "", "forward", WOrder.SELL, WOrder.LIMIT,
				8, "", 1, 1);
			o.addOrderSpec("Storage", 4, 1, 1, 1);
			board.appendOrder(o);
			o = orderManager.createOrder(2, "provider2", "", "forward", WOrder.SELL, WOrder.LIMIT,
				10, "", 1, 1);
			o.addOrderSpec("Process", 5, 1, 1, 1);
			board.appendOrder(o);
			o = orderManager.createOrder(3, "user3", "", "forward", WOrder.BUY, WOrder.LIMIT, 20,
				"", 1, 1);
			o.addOrderSpec("Storage", 3, 1, 1, 1);
			o.addOrderSpec("Process", 3, 1, 1, 1);
			board.appendOrder(o);
			break;
		case 3:
			o = orderManager.createOrder(1, "provider1", "", "forward", WOrder.SELL, WOrder.LIMIT,
				5, "", 1, 1);
			o.addOrderSpec("Storage", 10, 1, 1, 1);
			board.appendOrder(o);
			o = orderManager.createOrder(2, "user2", "", "forward", WOrder.BUY, WOrder.LIMIT, 4,
				"", 1, 1);
			o.addOrderSpec("Storage", 6, 1, 1, 1);
			board.appendOrder(o);
			o = orderManager.createOrder(3, "user3", "", "forward", WOrder.BUY, WOrder.LIMIT, 3,
				"", 1, 1);
			o.addOrderSpec("Storage", 4, 1, 1, 1);
			board.appendOrder(o);
			break;
		case 4:
			o = orderManager.createOrder(1, "provider1", "", "forward", WOrder.SELL, WOrder.LIMIT,
				20, "", 1, 1);
			o.addOrderSpec("Storage", 10, 1, 1, 1);
			board.appendOrder(o);
			o = orderManager.createOrder(2, "provider2", "", "forward", WOrder.SELL, WOrder.LIMIT,
				40, "", 1, 1);
			o.addOrderSpec("Process", 20, 1, 1, 1);
			board.appendOrder(o);
			o = orderManager.createOrder(3, "user3", "", "forward", WOrder.BUY, WOrder.LIMIT, 30,
				"", 1, 1);
			o.addOrderSpec("Storage", 10, 1, 1, 1);
			o.addOrderSpec("Process", 10, 1, 1, 1);
			board.appendOrder(o);
			o = orderManager.createOrder(4, "user4", "", "forward", WOrder.BUY, WOrder.LIMIT, 50,
				"", 1, 1);
			o.addOrderSpec("Storage", 10, 1, 1, 1);
			o.addOrderSpec("Process", 10, 1, 1, 1);
			board.appendOrder(o);
			break;
		case 5:
			o = orderManager.createOrder(1, "provider1", "", "forward", WOrder.SELL, WOrder.LIMIT,
				20, "", 1, 1);
			o.addOrderSpec("Storage", 10, 0, 1, 1);
			board.appendOrder(o);
			o = orderManager.createOrder(3, "provider3", "", "forward", WOrder.SELL, WOrder.LIMIT,
				40, "", 1, 1);
			o.addOrderSpec("Process", 20, 0, 1, 1);
			board.appendOrder(o);
			o = orderManager.createOrder(5, "user5", "", "forward", WOrder.BUY, WOrder.LIMIT, 60,
				"", 1, 1);
			o.addOrderSpec("Storage", 10, 0, 1, 1);
			o.addOrderSpec("Process", 10, 0, 1, 1);
			board.appendOrder(o);
			o = orderManager.createOrder(6, "user6", "", "forward", WOrder.BUY, WOrder.LIMIT, 50,
				"", 1, 1);
			o.addOrderSpec("Storage", 10, 1, 1, 1);
			o.addOrderSpec("Process", 10, 1, 1, 1);
			board.appendOrder(o);
			break;
		}
		board.makeContracts(1, 1, 1);
	}
}
