package org.wmart.cmdCore;

import java.util.*;

/**
 * �s�ꉿ�i���擾���邽�߂�SVMP�R�}���h�̒��ۃN���X�ł��D
 * 
 * @author �암 �S�i
 * @author Ikki Fujiwara, NII
 */
public abstract class WCMarketPriceCore implements ICommand {

	/** ���������������߂̃L�[(�l��String�I�u�W�F�N�g) */
	public static final String STRING_BRAND_NAME = "STRING_BRAND_NAME";

	/** �s�ꖼ���������߂̃L�[(�l��String�I�u�W�F�N�g) */
	public static final String STRING_AUCTIONEER_NAME = "STRING_AUCTIONEER_NAME";

	/** ���t���������߂̃L�[(�l��Integer�I�u�W�F�N�g) */
	public static final String INT_DAY = "INT_DAY";

	/** �񂹉񐔂��������߂̃L�[(�l��Integer�I�u�W�F�N�g) */
	public static final String INT_BOARD_NO = "INT_BOARD_NO";

	/** �X�e�b�v�����������߂̃L�[(�l��Integer�I�u�W�F�N�g) */
	public static final String INT_STEP = "INT_STEP";

	/** ���i���������߂̃L�[(�l��String�I�u�W�F�N�g) */
	public static final String STRING_MARKET_PRICE = "STRING_MARKET_PRICE";

	/** �R�}���h�� */
	public static final String CMD_NAME = "MarketPrice";

	/** ������ */
	protected String fBrandName;

	/** �s�ꖼ */
	protected String fAuctioneerName;

	/** �K�v�ȏ��̃X�e�b�v�� */
	protected int fNoOfSteps;

	/** �R�}���h�̎��s��� */
	protected WCommandStatus fStatus;

	/** ���ʂ��i�[���邽�߂�ArrayList */
	protected ArrayList fArray;

	/**
	 * �R���X�g���N�^
	 */
	public WCMarketPriceCore() {
		super();
		fBrandName = "";
		fAuctioneerName = "";
		fNoOfSteps = -1;
		fArray = new ArrayList();
		fStatus = new WCommandStatus();
	}

	/**
	 * @see org.umart.cmdCore.ICommand#isNameEqualTo(String)
	 */
	public boolean isNameEqualTo(String name) {
		if (name.equalsIgnoreCase(CMD_NAME)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @see org.umart.cmdCore.ICommand#getName()
	 */
	public String getName() {
		return CMD_NAME;
	}

	/**
	 * @see org.umart.cmdCore.ICommand#setArguments(StringTokenizer)
	 */
	public boolean setArguments(StringTokenizer st) {
		try {
			fBrandName = st.nextToken();
			fAuctioneerName = st.nextToken();
			fNoOfSteps = Integer.parseInt(st.nextToken());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * �R�}���h���s�ɕK�v�Ȉ�����ݒ肷��D
	 * 
	 * @param brandName
	 * @param auctioneerName
	 * @param noOfSteps
	 */
	public void setArguments(String brandName, String auctioneerName, int noOfSteps) {
		fBrandName = brandName;
		fAuctioneerName = auctioneerName;
		fNoOfSteps = noOfSteps;
	}

	/**
	 * @see org.umart.cmdCore.ICommand#getResultString()
	 */
	public String getResultString() {
		String result = "";
		Iterator itr = fArray.iterator();
		while (itr.hasNext()) {
			HashMap os = (HashMap) itr.next();
			result += os.get(STRING_BRAND_NAME) + " " + os.get(STRING_AUCTIONEER_NAME) + " " + os.get(INT_DAY) + " "
				+ os.get(INT_BOARD_NO) + " " + os.get(INT_STEP) + " " + os.get(STRING_MARKET_PRICE) + " ";
		}
		return result;
	}

	/**
	 * �s�ꉿ�i��Ԃ��D
	 * 
	 * @return �s�ꉿ�i
	 */
	public ArrayList getResults() {
		return fArray;
	}

	/**
	 * @see org.umart.cmdCore.ICommand#doIt()
	 */
	public abstract WCommandStatus doIt();

	/**
	 * @see org.umart.cmdCore.ICommand#printOn()
	 */
	public void printOn() {
		System.out.println("<<MarketPrice " + fBrandName + fNoOfSteps + ">>");
		Iterator itr = fArray.iterator();
		while (itr.hasNext()) {
			HashMap os = (HashMap) itr.next();
			System.out.println("BrandName:" + os.get(STRING_BRAND_NAME) + "," + "AuctioneerName:"
				+ os.get(STRING_AUCTIONEER_NAME) + "," + "Day:" + os.get(INT_DAY) + "," + "BoardNo:"
				+ os.get(INT_BOARD_NO) + "," + "Step:" + os.get(INT_STEP) + "," + "MarketPrice:"
				+ os.get(STRING_MARKET_PRICE));
		}
	}
}
