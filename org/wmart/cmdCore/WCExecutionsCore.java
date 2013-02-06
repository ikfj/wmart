package org.wmart.cmdCore;

import java.util.*;

/**
 * ���Ɖ�̂��߂�SVMP�R�}���h�̒��ۃN���X�ł��D
 * 
 * @author �암 �S�i
 * @author Ikki Fujiwara, NII
 */
public abstract class WCExecutionsCore implements ICommand {

	/** ���ID���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
	public static final String LONG_CONTRACT_ID = "LONG_CONTRACT_ID";
	/** ��莞��(������)���������߂̃L�[(�l��String�I�u�W�F�N�g) */
	public static final String STRING_CONTRACT_TIME = "STRING_CONTRACT_TIME";
	/** ����ID���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
	public static final String LONG_ORDER_ID = "LONG_ORDER_ID";
	/** ����ID���������߂̃L�[(�l��String�I�u�W�F�N�g) */
	public static final String STRING_BRAND_NAME = "STRING_BRAND_NAME";
	/** �s�ꖼ���������߂̃L�[(�l��String�I�u�W�F�N�g) */
	public static final String STRING_AUCTIONEER_NAME = "STRING_AUCTIONEER_NAME";
	/** �����敪���������߂̃L�[(�l��Integer�I�u�W�F�N�g) */
	public static final String INT_SELL_BUY = "INT_SELL_BUY";
	/** �������i���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
	public static final String LONG_ORDER_PRICE = "LONG_ORDER_PRICE";
	/** �������ׂ��������߂̃L�[(�l��String�I�u�W�F�N�g) */
	public static final String STRING_ORDER_SPEC = "STRING_ORDER_SPEC";
	/** ��艿�i���������߂̃L�[(�l��Double�I�u�W�F�N�g) */
	public static final String DOUBLE_CONTRACT_PRICE = "DOUBLE_CONTRACT_PRICE";
	/** ��萔�ʂ��������߂̃L�[(�l��Double�I�u�W�F�N�g) */
	public static final String DOUBLE_CONTRACT_VOLUME = "DOUBLE_CONTRACT_VOLUME";
	/** �����艿�i���X�g���������߂̃L�[(�l��String�I�u�W�F�N�g) */
	public static final String STRING_SOLD_PRICES = "STRING_SOLD_PRICES";
	/** �����萔�ʃ��X�g���������߂̃L�[(�l��String�I�u�W�F�N�g) */
	public static final String STRING_SOLD_VOLUMES = "STRING_CONTRACT_SPEC";
	/** �R�}���h�� */
	public static final String CMD_NAME = "Executions";
	/** �ʖ� */
	public static final String CMD_ALIAS = "204";
	/** �s�ꖼ */
	protected String fAuctioneerName;
	/** �R�}���h�̎��s��� */
	protected WCommandStatus fStatus;
	/** ���ʂ��i�[���邽�߂�ArrayList */
	protected ArrayList fExecutionsArray;

	/**
	 * �R���X�g���N�^
	 */
	public WCExecutionsCore() {
		super();
		fExecutionsArray = new ArrayList();
		fStatus = new WCommandStatus();
	}

	/**
	 * @see org.umart.cmdCore.ICommand#isNameEqualTo(String)
	 */
	public boolean isNameEqualTo(String name) {
		if (name.equalsIgnoreCase(CMD_NAME) || name.equals(CMD_ALIAS)) {
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
			fAuctioneerName = st.nextToken();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * �R�}���h���s�ɕK�v�Ȉ�����ݒ肷��D
	 * 
	 * @param auctioneerName
	 */
	public void setArguments(String auctioneerName) {
		fAuctioneerName = auctioneerName;
	}

	/**
	 * @see org.umart.cmdCore.ICommand#getResultString()
	 */
	public String getResultString() {
		String result = "";
		Iterator itr = fExecutionsArray.iterator();
		while (itr.hasNext()) {
			HashMap os = (HashMap) itr.next();
			result += os.get(LONG_CONTRACT_ID) + " " + os.get(STRING_CONTRACT_TIME) + " " + os.get(LONG_ORDER_ID) + " "
				+ os.get(STRING_BRAND_NAME) + " " + os.get(STRING_AUCTIONEER_NAME) + " " + os.get(INT_SELL_BUY) + " "
				+ os.get(DOUBLE_CONTRACT_PRICE) + " " + os.get(DOUBLE_CONTRACT_VOLUME) + " "
				+ os.get(STRING_SOLD_PRICES) + " " + os.get(STRING_SOLD_VOLUMES);
		}
		return result;
	}

	/**
	 * ������Ԃ��D
	 * 
	 * @return �����
	 */
	public ArrayList getResults() {
		return fExecutionsArray;
	}

	/**
	 * @see org.umart.cmdCore.ICommand#doIt()
	 */
	public abstract WCommandStatus doIt();

	/**
	 * @see org.umart.cmdCore.ICommand#printOn()
	 */
	public void printOn() {
		System.out.println("<<Executions>>");
		Iterator itr = fExecutionsArray.iterator();
		while (itr.hasNext()) {
			HashMap os = (HashMap) itr.next();
			System.out.println("ContractID:" + os.get(LONG_CONTRACT_ID) + "," + "ContractTime:"
				+ os.get(STRING_CONTRACT_TIME) + "," + "OrderID:" + os.get(LONG_ORDER_ID) + "," + "BrandName:"
				+ os.get(STRING_BRAND_NAME) + "," + "AuctioneerName:" + os.get(STRING_AUCTIONEER_NAME) + ","
				+ "SellBuy:" + os.get(INT_SELL_BUY) + "," + "ContractPrice:" + os.get(DOUBLE_CONTRACT_PRICE) + ","
				+ "ContractSpec:" + os.get(DOUBLE_CONTRACT_VOLUME) + "," + "SoldPrices:" + os.get(STRING_SOLD_PRICES)
				+ "," + "SoldVolumes:" + os.get(STRING_SOLD_VOLUMES));
		}
	}
}
