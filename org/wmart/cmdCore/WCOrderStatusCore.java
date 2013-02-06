package org.wmart.cmdCore;

import java.util.*;

/**
 * UCOrderStatusCore�N���X��, �����Ɖ�̂��߂̃R�}���h�I�u�W�F�N�g�ł���.
 * 
 * @author �암 �S�i
 * @author Ikki Fujiwara, NII
 */
public abstract class WCOrderStatusCore implements ICommand {

	/** ����ID���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
	public static final String LONG_ORDER_ID = "LONG_ORDER_ID";
	/** ��������(������)���������߂̃L�[(�l��String�I�u�W�F�N�g) */
	public static final String STRING_ORDER_TIME = "STRING_ORDER_TIME";
	/** ���������������߂̃L�[(�l��String�I�u�W�F�N�g) */
	public static final String STRING_BRAND_NAME = "STRING_BRAND_NAME";
	/** �s�ꖼ���������߂̃L�[(�l��String�I�u�W�F�N�g) */
	public static final String STRING_AUCTIONEER_NAME = "STRING_AUCTIONEER_NAME";
	/** �����敪���������߂̃L�[(�l��Integer�I�u�W�F�N�g) */
	public static final String INT_SELL_BUY = "INT_SELL_BUY";
	/** ���s�w�l�敪���������߂̃L�[(�l��Integer�I�u�W�F�N�g) */
	public static final String INT_MARKET_LIMIT = "INT_MARKET_LIMIT";
	/** �������i���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
	public static final String LONG_ORDER_PRICE = "LONG_ORDER_PRICE";
	/** �������ׂ��������߂̃L�[(�l��String�I�u�W�F�N�g) */
	public static final String STRING_ORDER_SPEC = "STRING_ORDER_SPEC";
	/** ��艿�i���������߂̃L�[(�l��Double�I�u�W�F�N�g) */
	public static final String DOUBLE_CONTRACT_PRICE = "DOUBLE_CONTRACT_PRICE";
	/** ��薾�ׂ��������߂̃L�[(�l��String�I�u�W�F�N�g) */
	public static final String STRING_CONTRACT_SPEC = "STRING_CONTRACT_SPEC";
	/** �R�}���h�� */
	public static final String CMD_NAME = "OrderStatus";
	/** �ʖ� */
	public static final String CMD_ALIAS = "203";
	/** �s�ꖼ */
	protected String fAuctioneerName;
	/** �R�}���h�̎��s��� */
	protected WCommandStatus fStatus;
	/** ���ʂ��i�[���邽�߂�ArrayList */
	protected ArrayList fStatusArray;

	/**
	 * �R���X�g���N�^
	 */
	public WCOrderStatusCore() {
		super();
		fStatusArray = new ArrayList();
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
		Iterator itr = fStatusArray.iterator();
		while (itr.hasNext()) {
			HashMap os = (HashMap) itr.next();
			result = os.get(LONG_ORDER_ID) + " " + os.get(STRING_ORDER_TIME) + " " + os.get(STRING_BRAND_NAME) + " "
				+ os.get(STRING_AUCTIONEER_NAME) + " " + os.get(INT_SELL_BUY) + " " + os.get(INT_MARKET_LIMIT) + " "
				+ os.get(LONG_ORDER_PRICE) + " " + os.get(STRING_ORDER_SPEC) + " " + os.get(DOUBLE_CONTRACT_PRICE)
				+ " " + os.get(STRING_CONTRACT_SPEC) + " ";
		}
		return result;
	}

	/**
	 * �����Ɖ����Ԃ��D
	 * 
	 * @return �����Ɖ���
	 */
	public ArrayList getResults() {
		return fStatusArray;
	}

	/**
	 * @see org.umart.cmdCore.ICommand#doIt()
	 */
	public abstract WCommandStatus doIt();

	/**
	 * @see org.umart.cmdCore.ICommand#printOn()
	 */
	public void printOn() {
		System.out.println("<<OrderStatus>>");
		Iterator itr = fStatusArray.iterator();
		while (itr.hasNext()) {
			HashMap os = (HashMap) itr.next();
			System.out.println("OrderID:" + os.get(LONG_ORDER_ID) + "," + "OrderTime:" + os.get(STRING_ORDER_TIME)
				+ "," + "BrandName:" + os.get(STRING_BRAND_NAME) + "," + "AuctioneerName:"
				+ os.get(STRING_AUCTIONEER_NAME) + "," + "SellBuy:" + os.get(INT_SELL_BUY) + "," + "MarketLimit:"
				+ os.get(INT_MARKET_LIMIT) + "," + "OrderPrice:" + os.get(LONG_ORDER_PRICE) + "," + "Spec:"
				+ os.get(STRING_ORDER_SPEC) + "," + "ContractPrice:" + os.get(DOUBLE_CONTRACT_PRICE) + ","
				+ "SoldVolumes:" + os.get(STRING_CONTRACT_SPEC));
		}
	}
}
