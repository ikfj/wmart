package org.wmart.agent;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * �^�C�����C����\���N���X (����G�[�W�F���g�p)
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WTimelineSeller {

	/** ���i�� */
	private String fGood;
	/** �ێ��X���b�g�� */
	private int fSlots;
	/** �n�[���� */
	private int fOffset;
	/** ������ */
	private int fVolume;
	/** ���P�� */
	private int fUnitPrice;
	/** �敨����ΏۃX���b�g�� */
	private int fSlotsForward;
	/** �ŏI�X���b�g�ԍ� */
	private int fMaxSlot;
	/** �݌Ƀ��X�g */
	private LinkedList<Double> fStockList;
	/** ���ナ�X�g */
	private LinkedList<Double> fSalesList;
	/** �������̐��ʃ��X�g (1�X�e�b�v���ƂɃ��Z�b�g�����) */
	private ArrayList<Long> fAskingList;
	/** �X���b�g => �󒍋L�^ */
	private LinkedHashMap<Integer, WTimelineSellerRecord> fRecords;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param good
	 *            ���i��
	 * @param slots
	 *            �ێ��X���b�g��
	 * @param offset
	 *            �n�[�̎���
	 * @param volume
	 *            ������
	 * @param unitPrice
	 *            ���P��
	 * @param slotsForward
	 *            �敨����ΏۃX���b�g��
	 * @param maxSlot
	 *            �ŏI�X���b�g�ԍ�
	 */
	public WTimelineSeller(String good, int slots, int offset, int volume, int unitPrice,
		int slotsForward, int maxSlot) {
		fGood = good;
		fSlots = slots;
		fOffset = offset;
		fVolume = volume;
		fUnitPrice = unitPrice;
		fSlotsForward = slotsForward;
		fMaxSlot = maxSlot;
		fStockList = new LinkedList<Double>();
		fSalesList = new LinkedList<Double>();
		fAskingList = new ArrayList<Long>();
		fRecords = new LinkedHashMap<Integer, WTimelineSellerRecord>();
		for (long slot = 1; slot <= slots; slot++) {
			// �ŏ��� daysForward ���͐敨�����]�T�Ƃ��ċ󂯂�
			fStockList.add((slot <= fSlotsForward) ? 0.0 : (double) fVolume);
			fSalesList.add(0.0);
			fAskingList.add((long) 0);
		}
	}

	/**
	 * �|�C���^��i�߂�
	 * 
	 * @param step
	 *            ���X���b�g��܂Ői�߂邩�Brecord() �̃X�e�b�v���ƍ��킹��
	 */
	public void succeed(int step) {
		for (int i = 0; i < step; i++) {
			int slot = fOffset + fSlots;
			fOffset++;
			// maxDays ���߂����狟���I��
			fStockList.add((fMaxSlot < slot) ? 0.0 : (double) fVolume);
			fStockList.remove(0);
			fSalesList.add(0.0);
			fSalesList.remove(0);
		}
		for (int i = 0; i < fAskingList.size(); i++) {
			fAskingList.set(i, (long) 0);
		}
	}

	/**
	 * ���݂̏󋵂��L�^����
	 * 
	 * @param step
	 *            ���X���b�g��܂ŋL�^���邩�Bsucceed() �̃X�e�b�v���ƍ��킹��
	 */
	public void record(int step) {
		for (int i = 0; i < step; i++) {
			int slot = fOffset + i;
			WTimelineSellerRecord rec;
			if (!fRecords.containsKey(slot)) {
				rec = new WTimelineSellerRecord();
			} else {
				rec = fRecords.get(slot);
			}
			// daysForward + 1 ���ڂ��� maxDays ���ڂ܂ŋ�������
			int volume = (slot <= fSlotsForward || fMaxSlot < slot) ? 0 : fVolume;
			double contractVolume = volume - fStockList.get(i);
			double contractPrice = fSalesList.get(i);
			rec.setTotalOrderVolume(volume);
			rec.setTotalOrderPrice(volume * fUnitPrice);
			rec.setTotalContractVolume(contractVolume);
			rec.setTotalContractPrice(contractPrice);
			rec.setTotalContractProfit(contractPrice - contractVolume * fUnitPrice);
			fRecords.put(slot, rec);
		}
	}

	/**
	 * �L�^�t�@�C���ɏ����o��
	 * 
	 * @param traceFilename
	 *            �t�@�C����
	 * @throws Exception
	 */
	public void writeTo(String traceFilename) throws Exception {
		PrintWriter pw = new PrintWriter(new FileOutputStream(path(traceFilename), false)); // �㏑��
		pw.println("Timeslot,OrderVolume,ContractVolume,Utilization,OrderPrice,ContractPrice,Profit,Welfare");
		long sumOrderVolume = 0;
		double sumContractVolume = 0.0;
		long sumOrderPrice = 0;
		double sumContractPrice = 0.0;
		double sumProfit = 0.0;
		double sumWelfare = 0.0;
		for (Iterator<Entry<Integer, WTimelineSellerRecord>> itr = fRecords.entrySet().iterator(); itr
			.hasNext();) {
			Entry<Integer, WTimelineSellerRecord> entry = itr.next();
			long slot = entry.getKey();
			WTimelineSellerRecord rec = entry.getValue();
			long orderVolume = rec.getTotalOrderVolume();
			double contractVolume = rec.getTotalContractVolume();
			double utilization = contractVolume / orderVolume;
			long orderPrice = rec.getTotalOrderPrice();
			double contractPrice = rec.getTotalContractPrice();
			double profit = rec.getTotalContractProfit(); // ���ꂽ���̗]��
			double welfare = contractPrice - orderPrice; // �S�̗̂]��
			pw.println(String.format("%d,%d,%.2f,%.4f,%d,%.2f,%.2f,%.2f", slot, orderVolume,
				contractVolume, utilization, orderPrice, contractPrice, profit, welfare));
			sumOrderVolume += orderVolume;
			sumContractVolume += contractVolume;
			sumOrderPrice += orderPrice;
			sumContractPrice += contractPrice;
			sumProfit += profit;
			sumWelfare += welfare;
		}
		double avgUtilization = sumContractVolume / sumOrderVolume;
		pw.println(String.format("Total,%d,%.2f,%.4f,%d,%.2f,%.2f,%.2f", sumOrderVolume,
			sumContractVolume, avgUtilization, sumOrderPrice, sumContractPrice, sumProfit,
			sumWelfare));
		pw.close();
	}

	/**
	 * ����ςƂ���
	 */
	public void contractAt(int slot, double volume, double price) {
		int i = slot - fOffset;
		assert (i >= 0) : "Timeline.contractAt(): specified slot " + slot + " is in the past!";
		if (volume == 0) {
			return;
		}
		fStockList.set(i, fStockList.get(i) - volume);
		fSalesList.set(i, fSalesList.get(i) + price);
	}

	/**
	 * �������Ƃ���
	 */
	public void orderAt(int slot, long volume) {
		int i = slot - fOffset;
		assert (i >= 0) : "Timeline.orderAt(): specified slot " + slot + " is in the past!";
		if (volume == 0) {
			return;
		}
		fAskingList.set(i, fAskingList.get(i) + volume);
	}

	/**
	 * �������L�����Z������
	 */
	public void cancelAt(int slot, long volume) {
		orderAt(slot, -volume);
	}

	/**
	 * ����ςł��������ł��Ȃ��݌ɂ��擾
	 */
	public double getFreeStockAt(int slot) {
		int i = slot - fOffset;
		assert (i >= 0) : "Timeline.getFreeStockAt(): specified slot " + slot + " is in the past!";
		return fStockList.get(i) - fAskingList.get(i);
	}

	/**
	 * �݌ɂ𕶎���Ƃ��Ď擾
	 * 
	 * @return "���i��:�n�[����,�݌�(0),...,�݌�(slots-1),"
	 */
	public String encodeStock() {
		StringBuilder result = new StringBuilder();
		result.append(String.format("%s:%d,", fGood, fOffset));
		for (int i = 0; i < fStockList.size(); i++) {
			result.append(String.format("%.2f,", fStockList.get(i)));
		}
		return result.toString();
	}

	/**
	 * �n�[�������擾
	 * 
	 * @return �n�[����
	 */
	public int getOffset() {
		return fOffset;
	}

	/**
	 * �������擾
	 * 
	 * @return ����
	 */
	public int getUnitPrice() {
		return fUnitPrice;
	}

	/**
	 * �p�X��؂蕶�������ɍ��킹��
	 */
	private String path(String slashSeparatedPath) {
		return slashSeparatedPath.replace('/', File.separatorChar);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(String.format("\tTimeslot\tSales\tStock\tAsking"));
		for (int i = 0; i < fStockList.size(); i++) {
			result.append(String.format("\n\t%5d:\t%8.2f\t%8.2f\t%8d", i + fOffset,
				fSalesList.get(i), fStockList.get(i), fAskingList.get(i)));
		}
		return result.toString();
	}

	/*
	 * public static void main(String[] args) { WTimelineSeller timeline = new
	 * WTimelineSeller("serviceA", 7, 0, 100.0, 10000.0); HashMap<Integer, Double> volumes;
	 * HashMap<Integer, Double> prices;
	 *
	 * volumes = new HashMap(); volumes.put(2, 10.0); volumes.put(3, 20.0); volumes.put(4, 30.0);
	 * System.out.println(volumes); timeline.reserveResource(volumes); prices = new HashMap();
	 * prices.put(2, -100.0); prices.put(3, -200.0); prices.put(4, -300.0);
	 * System.out.println(prices); timeline.reserveBudget(prices);
	 * System.out.println(timeline.toString()); timeline.succeed(1);
	 *
	 * volumes = new HashMap(); volumes.put(3, 1.0); volumes.put(4, 2.0); volumes.put(5, 3.0);
	 * System.out.println(volumes); timeline.reserveResource(volumes); prices = new HashMap();
	 * prices.put(3, -10.0); prices.put(4, -20.0); prices.put(5, -30.0); System.out.println(prices);
	 * timeline.reserveBudget(prices); System.out.println(timeline.toString()); timeline.succeed(1);
	 *
	 * volumes = new HashMap(); volumes.put(4, 0.1); volumes.put(5, 0.2); volumes.put(6, 0.3);
	 * System.out.println(volumes); timeline.reserveResource(volumes); prices = new HashMap();
	 * prices.put(4, -1.0); prices.put(5, -2.0); prices.put(6, -3.0); System.out.println(prices);
	 * timeline.reserveBudget(prices); System.out.println(timeline.toString()); timeline.succeed(1);
	 * }
	 */
}
