package org.wmart.core;

import java.util.*;
import java.util.Map.Entry;

/**
 * ������\���N���X
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WContract {

	/** ���ID */
	private long fContractID = 0;
	/** ��莞�� (������) */
	private Date fTime = null;
	/** ���� */
	private int fDate = 0;
	/** ���� */
	private int fSession = 0;
	/** ��艿�i */
	private double fPrice = 0.0;
	/** ���䗦 (���������ł� 0 or 1) */
	private double fPercentage = 0.0;
	/** ���i�� (���������ł� bundle) */
	private String fGoods = "";
	/** �����艿�i���X�g */
	private HashMap<Integer, Double> fSoldPrices = null;
	/** ������䗦���X�g */
	private HashMap<Integer, Double> fSoldPercentages = null;
	/** �������� */
	private double fOrderVolume = 0;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param contractID
	 *            ���ID
	 * @param time
	 *            ��莞�� (������)
	 * @param date
	 *            ����
	 * @param session
	 *            ����
	 * @param price
	 *            ��艿�i
	 * @param percentage
	 *            ���䗦 (���蒍���ł̓X���b�g���Ƃ̔䗦�̒P�����v (max. �X���b�g��)�B���������ł� 0 or 1)
	 * @param goods
	 *            ���i�� (���蒍���ł͒P��B���������ł� bundle)
	 * @param prices
	 *            �����艿�i���X�g
	 * @param percentages
	 *            ������䗦���X�g
	 * @param orderVolume
	 *            �������� (�X���b�g���Ƃ̐��ʂ̒P�����v (max. �X���b�g��))
	 */
	public WContract(long contractID, Date time, int date, int session, double price,
		double percentage, String goods, HashMap<Integer, Double> soldPrices,
		HashMap<Integer, Double> soldPercentages, double orderVolume) {
		fContractID = contractID;
		fTime = time;
		fDate = date;
		fSession = session;
		fPrice = price;
		fPercentage = percentage;
		fGoods = goods;
		fSoldPrices = soldPrices;
		fSoldPercentages = soldPercentages;
		fOrderVolume = orderVolume;
	}

	/**
	 * �X���b�g���Ƃ̔����艿�i�n�b�V���𕶎���Ŏ擾����
	 * 
	 * @return �����艿�i���X�g "����:���i;..."
	 */
	public String encodeSoldPrices() {
		StringBuilder s = new StringBuilder();
		for (Iterator<Entry<Integer, Double>> itr = fSoldPrices.entrySet().iterator(); itr
			.hasNext();) {
			Entry<Integer, Double> entry = itr.next();
			s.append(entry.getKey()).append(":").append(entry.getValue()).append(";");
		}
		return s.toString();
	}

	/**
	 * �X���b�g���Ƃ̔����萔�ʃn�b�V���𕶎���Ŏ擾����
	 * 
	 * @return �����萔�ʃ��X�g "����:���i;..."
	 */
	public String encodeSoldVolumes() {
		StringBuilder s = new StringBuilder();
		for (Iterator<Entry<Integer, Double>> itr = fSoldPercentages.entrySet().iterator(); itr
			.hasNext();) {
			Entry<Integer, Double> entry = itr.next();
			s.append(entry.getKey()).append(":").append(entry.getValue() * fOrderVolume)
				.append(";");
		}
		return s.toString();
	}

	/**
	 * �w��X���b�g�̔����艿�i���擾����
	 * 
	 * @param slot
	 * @return �����艿�i
	 */
	public double getSoldPriceAt(int slot) {
		if (fSoldPrices != null && !fSoldPrices.isEmpty() && fSoldPrices.containsKey(slot)) {
			return fSoldPrices.get(slot);
		}
		return 0.0;
	}

	/**
	 * �w��X���b�g�̔�����䗦���擾����
	 * 
	 * @param slot
	 * @return ������䗦
	 */
	public double getSoldPercentageAt(int slot) {
		if (fSoldPercentages != null && !fSoldPercentages.isEmpty()
			&& fSoldPercentages.containsKey(slot)) {
			return fSoldPercentages.get(slot);
		}
		return 0.0;
	}

	/**
	 * �w��X���b�g�̔����萔�ʂ��擾����
	 * 
	 * @param slot
	 * @return
	 */
	public double getSoldVolumeAt(int slot) {
		return getSoldPercentageAt(slot) * fOrderVolume;
	}

	/**
	 * ��萔�ʂ��擾����
	 * 
	 * @return
	 */
	public double getVolume() {
		return fPercentage * fOrderVolume;
	}

	/**
	 * ���ID���擾���܂��B
	 * 
	 * @return ���ID
	 */
	public long getContractID() {
		return fContractID;
	}

	/**
	 * ���ID��ݒ肵�܂��B
	 * 
	 * @param fContractID
	 *            ���ID
	 */
	public void setContractID(long fContractID) {
		this.fContractID = fContractID;
	}

	/**
	 * ��莞�� (������)���擾���܂��B
	 * 
	 * @return ��莞�� (������)
	 */
	public Date getTime() {
		return fTime;
	}

	/**
	 * ��莞�� (������)��ݒ肵�܂��B
	 * 
	 * @param fTime
	 *            ��莞�� (������)
	 */
	public void setTime(Date fTime) {
		this.fTime = fTime;
	}

	/**
	 * �������擾���܂��B
	 * 
	 * @return ����
	 */
	public int getDate() {
		return fDate;
	}

	/**
	 * ������ݒ肵�܂��B
	 * 
	 * @param fDate
	 *            ����
	 */
	public void setDate(int fDate) {
		this.fDate = fDate;
	}

	/**
	 * ���߂��擾���܂��B
	 * 
	 * @return ����
	 */
	public int getSession() {
		return fSession;
	}

	/**
	 * ���߂�ݒ肵�܂��B
	 * 
	 * @param fSession
	 *            ����
	 */
	public void setSession(int fSession) {
		this.fSession = fSession;
	}

	/**
	 * ��艿�i���擾���܂��B
	 * 
	 * @return ��艿�i
	 */
	public double getPrice() {
		return fPrice;
	}

	/**
	 * ��艿�i��ݒ肵�܂��B
	 * 
	 * @param fPrice
	 *            ��艿�i
	 */
	public void setPrice(double fPrice) {
		this.fPrice = fPrice;
	}

	/**
	 * ���䗦 (���������ł� 0 or 1)���擾���܂��B
	 * 
	 * @return ���䗦 (���������ł� 0 or 1)
	 */
	public double getPercentage() {
		return fPercentage;
	}

	/**
	 * ���䗦 (���������ł� 0 or 1)��ݒ肵�܂��B
	 * 
	 * @param fPercentage
	 *            ���䗦 (���������ł� 0 or 1)
	 */
	public void setPercentage(double fPercentage) {
		this.fPercentage = fPercentage;
	}

	/**
	 * ���i�� (���������ł� bundle)���擾���܂��B
	 * 
	 * @return ���i�� (���������ł� bundle)
	 */
	public String getGoods() {
		return fGoods;
	}

	/**
	 * ���i�� (���������ł� bundle)��ݒ肵�܂��B
	 * 
	 * @param fGoods
	 *            ���i�� (���������ł� bundle)
	 */
	public void setGoods(String fGoods) {
		this.fGoods = fGoods;
	}

	/**
	 * �����艿�i���X�g���擾���܂��B
	 * 
	 * @return �����艿�i���X�g
	 */
	public HashMap<Integer, Double> getSoldPrices() {
		return fSoldPrices;
	}

	/**
	 * �����艿�i���X�g��ݒ肵�܂��B
	 * 
	 * @param fSoldPrices
	 *            �����艿�i���X�g
	 */
	public void setSoldPrices(HashMap<Integer, Double> fSoldPrices) {
		this.fSoldPrices = fSoldPrices;
	}

	/**
	 * ������䗦���X�g���擾���܂��B
	 * 
	 * @return ������䗦���X�g
	 */
	public HashMap<Integer, Double> getSoldPercentages() {
		return fSoldPercentages;
	}

	/**
	 * ������䗦���X�g��ݒ肵�܂��B
	 * 
	 * @param fSoldPercentages
	 *            ������䗦���X�g
	 */
	public void setSoldPercentages(HashMap<Integer, Double> fSoldPercentages) {
		this.fSoldPercentages = fSoldPercentages;
	}

	/**
	 * �������ʂ��擾���܂��B
	 * 
	 * @return ��������
	 */
	public double getOrderVolume() {
		return fOrderVolume;
	}

	/**
	 * �������ʂ�ݒ肵�܂��B
	 * 
	 * @param fTotalOrderVolume
	 *            ��������
	 */
	public void setOrderVolume(double fOrderVolume) {
		this.fOrderVolume = fOrderVolume;
	}

	/*
	 * (�� Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("WContract(%s) [date%s/session%s, $%s, %s%%, %s]", fContractID, fDate,
			fSession, fPrice, fPercentage * 100, fGoods);
	}

}