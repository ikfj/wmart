package org.wmart.core;

import java.util.*;

/**
 * �����������N���X
 * 
 * @author Ikki Fujiwara, NII
 */

public class WOrder {

	/** �u����v��\���萔 */
	public static int SELL = 1;
	/** �u�����v��\���萔 */
	public static int BUY = 2;
	/** �u���s�v��\���萔 */
	public static int MARKET = 1;
	/** �u�w�l�v��\���萔 */
	public static int LIMIT = 2;

	/** ���[�U�[ID */
	private int fUserID = 0;
	/** ���O�C���� */
	private String fUserName = "";
	/** ����ID */
	private long fOrderID = 0;
	/** �������� (������) */
	private Date fTime = null;
	/** ������ */
	private int fDate = 0;
	/** ������ */
	private int fSession = 0;
	/** �����敪 */
	private int fSellBuy = 0;
	/** ���s�w�l�敪 */
	private int fMarketLimit = 0;
	/** �s�ꖼ */
	private String fAuctioneerName = "";
	/** �������i */
	private long fOrderPrice = -1;
	/** �������� */
	private TreeMap<String, WOrderSpec> fOrderSpecMap = null;
	/** ���蒍�����i�� */
	private String fOrderSellGood = "";
	/** ���蒍������ */
	private long fOrderSellVolume = 0;
	/** �ő��J�n���� */
	private int fEarliestSlot = -1;
	/** �Œx�I������ */
	private int fLatestSlot = -1;
	/** ����� (��葊�育�Ƃ�1��) */
	private Vector<WContract> fContracts = null;
	/** �R���p���[�^�p�̗��� */
	private int fRandomNumber;

	/**
	 * �R���X�g���N�^
	 */
	public WOrder() {
		fTime = new Date();
		fOrderSpecMap = new TreeMap<String, WOrderSpec>();
		fContracts = new Vector<WContract>();
	}

	/**
	 * ������ 1 �X�e�b�v�i�߂� (�ő������ƍŒx���������݂֋߂Â���)
	 * 
	 * @return �ߋ��ɂȂ����� false
	 */
	public boolean nextStep() {
		for (Iterator<WOrderSpec> itr = fOrderSpecMap.values().iterator(); itr.hasNext();) {
			WOrderSpec spec = itr.next();
			spec.nextStep();
		}
		fEarliestSlot--;
		fLatestSlot--;
		return (fEarliestSlot <= 0 && fLatestSlot <= 0);
	}

	/**
	 * �������L�����Z������
	 */
	public void cancel() {
		fOrderPrice = -1;
	}

	/**
	 * �������ׂ𕶎���Őݒ肷��
	 * 
	 * @param encodedSpecs
	 *            "���i��:��]����:�ő�����:�Œx����:���׎���;" �̌J��Ԃ�
	 * @return ���i��
	 */
	public int setOrderSpec(String encodedSpecs) {
		fOrderSpecMap.clear();
		assert (!encodedSpecs.isEmpty());
		String[] specArray = encodedSpecs.split(";");
		for (int i = 0; i < specArray.length; i++) {
			addOrderSpec(specArray[i]);
		}
		return specArray.length;
	}

	/**
	 * �������ׂ𕶎����1���ǉ�����
	 * 
	 */
	public void addOrderSpec(String encodedSpec) {
		WOrderSpec spec = new WOrderSpec(encodedSpec);
		fOrderSpecMap.put(spec.getName(), spec);
		if (fSellBuy == SELL) {
			assert (fOrderSellGood.isEmpty() && fOrderSellVolume == 0) : "selling order cannot have multiple goods";
			fOrderSellGood = spec.getName();
			fOrderSellVolume = spec.getOrderVolume();
		}
		if (fEarliestSlot == -1 || fEarliestSlot > spec.getArrivalTime()) {
			fEarliestSlot = spec.getArrivalTime();
		}
		if (fLatestSlot == -1 || fLatestSlot < spec.getDeadlineTime()) {
			fLatestSlot = spec.getDeadlineTime();
		}
	}

	/**
	 * �������ׂ�1���ǉ�����
	 * 
	 */
	public void addOrderSpec(String name, int orderVolume, int arrivalTime, int deadlineTime,
		int totalTime) {
		WOrderSpec spec = new WOrderSpec(name, orderVolume, arrivalTime, deadlineTime, totalTime);
		fOrderSpecMap.put(name, spec);
		if (fSellBuy == SELL) {
			assert (fOrderSellGood.isEmpty() && fOrderSellVolume == 0) : "selling order cannot have multiple goods";
			fOrderSellGood = name;
			fOrderSellVolume = orderVolume;
		}
		if (fEarliestSlot == -1 || fEarliestSlot > arrivalTime) {
			fEarliestSlot = arrivalTime;
		}
		if (fLatestSlot == -1 || fLatestSlot < deadlineTime) {
			fLatestSlot = deadlineTime;
		}
	}

	/**
	 * �������ׂ𕶎���Ŏ擾����
	 * 
	 * @return "���i��:��]����:�ő�����:�Œx����:���׎���;" �̌J��Ԃ�
	 */
	public String getOrderSpec() {
		StringBuilder encodedSpecs = new StringBuilder();
		for (Iterator<WOrderSpec> itr = fOrderSpecMap.values().iterator(); itr.hasNext();) {
			WOrderSpec spec = itr.next();
			encodedSpecs.append(spec.encode()).append(";");
		}
		return encodedSpecs.toString();
	}

	/**
	 * ������ǉ�����.
	 * 
	 * @param contractID
	 *            ���ID
	 * @param time
	 *            ��莞�� (������)
	 * @param date
	 *            ����
	 * @param session
	 *            ����
	 * @param overallPrice
	 *            ��艿�i
	 * @param overallPercentage
	 *            ���䗦 (���������ł� 0 or 1)
	 * @param goods
	 *            ���i�� (���������ł� bundle)
	 * @param soldPrices
	 *            �����艿�i���X�g
	 * @param soldPercentages
	 *            ������䗦���X�g
	 * @param orderVolume
	 *            �������� (�P�����v)
	 */
	public void addContract(long contractID, Date time, int date, int session, double overallPrice,
		double overallPercentage, String goods, HashMap<Integer, Double> soldPrices,
		HashMap<Integer, Double> soldPercentages, double orderVolume) {
		WContract contract = new WContract(contractID, time, date, session, overallPrice,
			overallPercentage, goods, soldPrices, soldPercentages, orderVolume);
		fContracts.addElement(contract);
	}

	/**
	 * ��艿�i���擾����
	 * 
	 * @return ��艿�i
	 */
	public double getContractPrice() {
		double sum = 0.0;
		for (Iterator<WContract> itr = fContracts.iterator(); itr.hasNext();) {
			sum += itr.next().getPrice();
		}
		return sum;
	}

	/**
	 * ���䗦���擾����
	 * 
	 * @return ���䗦
	 */
	public double getContractPercentage() {
		double sum = 0.0;
		for (Iterator<WContract> itr = fContracts.iterator(); itr.hasNext();) {
			sum += itr.next().getPercentage();
		}
		return sum;
	}

	/**
	 * ��萔�ʂ��擾����
	 * 
	 * @return ��萔�� (���������ł͒l�ɈӖ��Ȃ��B0 ���ۂ��Ŕ��肷�邱��)
	 */
	public double getContractVolume() {
		double sum = 0.0;
		for (Iterator<WContract> itr = fContracts.iterator(); itr.hasNext();) {
			sum += itr.next().getVolume();
		}
		return sum;
	}

	/**
	 * �X���b�g���Ƃ̔����薾�ׂ𕶎���Ŏ擾����
	 * 
	 * @return "���i��:��������:�ő�����:�Œx����:��萔��(�ő�),...,��萔��(�Œx),:��艿�i(�ő�),...,��艿�i(�Œx),"
	 */
	public String getSoldSpec() {
		StringBuilder encodedSpecs = new StringBuilder();
		encodedSpecs.append(fOrderSellGood).append(":");
		encodedSpecs.append(fOrderSellVolume).append(":");
		encodedSpecs.append(fEarliestSlot).append(":");
		encodedSpecs.append(fLatestSlot).append(":");
		if (fSellBuy == SELL) {
			for (int slot = fEarliestSlot; slot <= fLatestSlot; slot++) {
				encodedSpecs.append(getSoldVolumeAt(slot)).append(",");
			}
			encodedSpecs.append(":");
			for (int slot = fEarliestSlot; slot <= fLatestSlot; slot++) {
				encodedSpecs.append(getSoldPriceAt(slot)).append(",");
			}
		}
		return encodedSpecs.toString();
	}

	/**
	 * �w�莞���̔����艿�i���擾����
	 * 
	 * @param slot
	 *            ����
	 * @return �����艿�i
	 */
	public double getSoldPriceAt(int slot) {
		double sum = 0.0;
		for (Iterator<WContract> itr = fContracts.iterator(); itr.hasNext();) {
			sum += itr.next().getSoldPriceAt(slot);
		}
		return sum;
	}

	/**
	 * �w�莞���̔�����䗦���擾����
	 * 
	 * @param slot
	 *            ����
	 * @return ������䗦
	 */
	public double getSoldPercentageAt(int slot) {
		double sum = 0.0;
		for (Iterator<WContract> itr = fContracts.iterator(); itr.hasNext();) {
			WContract contract = itr.next();
			sum += contract.getSoldPercentageAt(slot);
		}
		return sum;
	}

	/**
	 * �w�莞���̔����萔�ʂ��擾����
	 * 
	 * @param slot
	 *            ����
	 * @return �����萔��
	 */
	public double getSoldVolumeAt(int slot) {
		double sum = 0.0;
		for (Iterator<WContract> itr = fContracts.iterator(); itr.hasNext();) {
			WContract contract = itr.next();
			sum += contract.getSoldVolumeAt(slot);
		}
		return sum;
	}

	/**
	 * ���[�U�[ID���擾���܂��B
	 * 
	 * @return ���[�U�[ID
	 */
	public int getUserID() {
		return fUserID;
	}

	/**
	 * ���[�U�[ID��ݒ肵�܂��B
	 * 
	 * @param fUserID
	 *            ���[�U�[ID
	 */
	public void setUserID(int fUserID) {
		this.fUserID = fUserID;
	}

	/**
	 * ���O�C�������擾���܂��B
	 * 
	 * @return ���O�C����
	 */
	public String getUserName() {
		return fUserName;
	}

	/**
	 * ���O�C������ݒ肵�܂��B
	 * 
	 * @param fUserName
	 *            ���O�C����
	 */
	public void setUserName(String fUserName) {
		this.fUserName = fUserName;
	}

	/**
	 * ���������擾���܂��B
	 * 
	 * @return ������
	 */
	public String getBrandName() {
		return fOrderSellGood;
	}

	/**
	 * ��������ݒ肵�܂��B
	 * 
	 * @param fBrandName
	 *            ������
	 */
	public void setBrandName(String fBrandName) {
		this.fOrderSellGood = fBrandName;
	}

	/**
	 * ����ID���擾���܂��B
	 * 
	 * @return ����ID
	 */
	public long getOrderID() {
		return fOrderID;
	}

	/**
	 * ����ID��ݒ肵�܂��B
	 * 
	 * @param fOrderID
	 *            ����ID
	 */
	public void setOrderID(long fOrderID) {
		this.fOrderID = fOrderID;
	}

	/**
	 * ��������(������)���擾���܂��B
	 * 
	 * @return ��������(������)
	 */
	public Date getTime() {
		return fTime;
	}

	/**
	 * ��������(������)��ݒ肵�܂��B
	 * 
	 * @param fTime
	 *            ��������(������)
	 */
	public void setTime(Date fTime) {
		this.fTime = fTime;
	}

	/**
	 * ���������擾���܂��B
	 * 
	 * @return ������
	 */
	public int getDate() {
		return fDate;
	}

	/**
	 * ��������ݒ肵�܂��B
	 * 
	 * @param fDate
	 *            ������
	 */
	public void setDate(int fDate) {
		this.fDate = fDate;
	}

	/**
	 * �����߂��擾���܂��B
	 * 
	 * @return ������
	 */
	public int getSession() {
		return fSession;
	}

	/**
	 * �����߂�ݒ肵�܂��B
	 * 
	 * @param fSession
	 *            ������
	 */
	public void setSession(int fSession) {
		this.fSession = fSession;
	}

	/**
	 * �����敪���擾���܂��B
	 * 
	 * @return �����敪
	 */
	public int getSellBuy() {
		return fSellBuy;
	}

	/**
	 * �����敪��ݒ肵�܂��B
	 * 
	 * @param sellBuy
	 *            �����敪
	 */
	public void setSellBuy(int sellBuy) {
		assert (sellBuy == SELL || sellBuy == BUY);
		this.fSellBuy = sellBuy;
	}

	/**
	 * ���s�w�l�敪���擾���܂��B
	 * 
	 * @return ���s�w�l�敪
	 */
	public int getMarketLimit() {
		return fMarketLimit;
	}

	/**
	 * ���s�w�l�敪��ݒ肵�܂��B
	 * 
	 * @param marketLimit
	 *            ���s�w�l�敪
	 */
	public void setMarketLimit(int marketLimit) {
		assert (marketLimit == MARKET || marketLimit == LIMIT);
		this.fMarketLimit = marketLimit;
	}

	/**
	 * �s�ꖼ��ݒ肵�܂��B
	 * 
	 * @param fAuctioneerName
	 *            �s�ꖼ
	 */
	public void setAuctioneerName(String fAuctioneerName) {
		this.fAuctioneerName = fAuctioneerName;
	}

	/**
	 * �s�ꖼ���擾���܂��B
	 * 
	 * @return �s�ꖼ
	 */
	public String getAuctioneerName() {
		return fAuctioneerName;
	}

	/**
	 * �������i���擾���܂��B
	 * 
	 * @return �������i
	 */
	public long getOrderPrice() {
		return fOrderPrice;
	}

	/**
	 * �������i��ݒ肵�܂��B
	 * 
	 * @param price
	 *            �������i
	 */
	public void setOrderPrice(long price) {
		fOrderPrice = price;
	}

	/**
	 * �������ׂ��擾���܂��B
	 * 
	 * @return ��������
	 */
	public TreeMap<String, WOrderSpec> getOrderSpecMap() {
		return fOrderSpecMap;
	}

	/**
	 * �ő��J�n�������擾���܂��B
	 * 
	 * @return �ő��J�n����
	 */
	public int getEarliestSlot() {
		return fEarliestSlot;
	}

	/**
	 * �Œx�I���������擾���܂��B
	 * 
	 * @return �Œx�I������
	 */
	public int getLatestSlot() {
		return fLatestSlot;
	}

	/**
	 * �������擾���܂��B
	 * 
	 * @return �����
	 */
	public Vector<WContract> getContracts() {
		return fContracts;
	}

	/**
	 * �R���p���[�^�p�̗������擾���܂��B
	 * 
	 * @return �R���p���[�^�p�̗���
	 */
	public int getRandomNumber() {
		return fRandomNumber;
	}

	/**
	 * �R���p���[�^�p�̗�����ݒ肵�܂��B
	 * 
	 * @param RandomNumber
	 *            �R���p���[�^�p�̗���
	 */
	public void setRandomNumber(int RandomNumber) {
		this.fRandomNumber = RandomNumber;
	}

	/*
	 * (�� Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("\nWOrder");
		str.append(String.format("(%s) [date%s/session%s, %s, %s, $%s, ", fOrderID, fDate,
			fSession, (fSellBuy == SELL) ? "sell" : "buy", (fMarketLimit == MARKET) ? "market"
				: "limit", fOrderPrice));
		for (Iterator<WOrderSpec> itr = fOrderSpecMap.values().iterator(); itr.hasNext();) {
			WOrderSpec spec = itr.next();
			str.append(spec.toString()).append(";");
		}
		str.append(", ");
		str.append("[");
		for (Iterator<WContract> itr = fContracts.iterator(); itr.hasNext();) {
			WContract cont = itr.next();
			str.append(cont.toString());
		}
		str.append("]");
		return str.append("]").toString();
	}

	/*
	 * �e�X�g�p
	 */
	public static void main(String args[]) {
		WOrder order = new WOrder();
		order.addOrderSpec("CPU", 4, 3, 6, 2);
		order.addOrderSpec("Storage", 9, 3, 8, 4);
		order.addOrderSpec("Network", 1, 2, 5, 4);
		order.setOrderSpec("CPU:4:3:6:2;Storage:9:3:8:4;Network:1:2:5:4;");

		TreeMap specMap = order.getOrderSpecMap();
		for (Iterator iterator = specMap.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<String, WOrderSpec> entry = (Map.Entry<String, WOrderSpec>) iterator.next();
			System.out.println(entry.getKey() + " = " + entry.getValue());
		}
	}

}
