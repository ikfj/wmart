package org.wmart.cmdCore;

import java.util.*;

/**
 * �����˗��̂��߂̃R�}���h�N���X�B
 * <ul>
 * <li>UCOrderRequestCore�N���X�Ɏs�ꖼ�t�B�[���h�ƒ������׃t�B�[���h��ǉ������B</li>
 * <ul>
 * <li>int newRepay �� String auctioneerName</li>
 * <li>int volume �� String spec</li>
 * </ul>
 * <li>String brandName �͔p�~���Ă��Ȃ� (�󗓂Ŏg�p)�B</li> </ul>
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public abstract class WCOrderRequestCore implements ICommand {

	/** ����ID���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
	public static final String LONG_ORDER_ID = "LONG_ORDER_ID";
	/** ��������(������)���������߂̃L�[(�l��String�I�u�W�F�N�g) */
	public static final String STRING_ORDER_TIME = "STRING_ORDER_TIME";
	/** �R�}���h�� */
	public static final String CMD_NAME = "OrderRequest";
	/** �ʖ� */
	public static final String CMD_ALIAS = "201";
	/** ������ */
	protected String fBrandName;
	/** �s�ꖼ */
	protected String fAuctioneerName;
	/** �����敪 */
	protected int fSellBuy;
	/** ���s�w�l�敪 */
	protected int fMarketLimit;
	/** ��]������i */
	protected long fPrice;
	/** �������� */
	protected String fSpec;
	/** �R�}���h�̎��s��� */
	protected WCommandStatus fStatus;
	/** ���ʂ��i�[���邽�߂�HashMap */
	protected HashMap fRequestInfo;

	/**
	 * �R���X�g���N�^
	 */
	public WCOrderRequestCore() {
		super();
		fAuctioneerName = "";
		fBrandName = "";
		fSellBuy = -1;
		fMarketLimit = -1;
		fPrice = -1;
		fSpec = "";
		fRequestInfo = new HashMap();
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
			fBrandName = st.nextToken();
			fAuctioneerName = st.nextToken();
			fSellBuy = Integer.parseInt(st.nextToken());
			fMarketLimit = Integer.parseInt(st.nextToken());
			fPrice = Long.parseLong(st.nextToken());
			fSpec = st.nextToken();
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
	 * @param sellBuy
	 * @param marketLimit
	 * @param price
	 * @param spec
	 */
	public void setArguments(String brandName, String auctioneerName, int sellBuy, int marketLimit, long price,
		String spec) {
		fBrandName = brandName;
		fAuctioneerName = auctioneerName;
		fSellBuy = sellBuy;
		fMarketLimit = marketLimit;
		fPrice = price;
		fSpec = spec;
	}

	/**
	 * @see org.umart.cmdCore.ICommand#getResultString()
	 */
	public String getResultString() {
		String result = fRequestInfo.get(LONG_ORDER_ID) + " " + fRequestInfo.get(STRING_ORDER_TIME);
		return result;
	}

	/**
	 * �����˗�����Ԃ��D
	 * 
	 * @return �����˗����
	 */
	public HashMap getResults() {
		return fRequestInfo;
	}

	/**
	 * @see org.umart.cmdCore.ICommand#doIt()
	 */
	public abstract WCommandStatus doIt();

	/**
	 * @see org.umart.cmdCore.ICommand#printOn()
	 */
	public void printOn() {
		System.out.println("<<OrderRequest " + fBrandName + fAuctioneerName + fSellBuy + fMarketLimit + fPrice + fSpec
			+ ">>");
		System.out.println("OrderID:" + fRequestInfo.get(LONG_ORDER_ID) + "," + "OrderTime:"
			+ fRequestInfo.get(STRING_ORDER_TIME));
	}
}