package org.wmart.cmdCore;

import java.util.*;

/**
 * ��������[�J�[��Ԗ₢���킹�̂��߂̃R�}���h�N���X�B
 * <ul>
 * <li>UCMarketStatusCore �N���X�Ɏs�ꖼ�t�B�[���h��ǉ������B</li>
 * <ul>
 * <li>String auctioneerName</li>
 * </ul>
 * </ul>
 * 
 * @author Ikki Fujiwara, NII
 */
public abstract class WCMarketStatusCore implements ICommand {

	/** �}�[�P�b�g��Ԃ��������߂̃L�[(�l��Integer�I�u�W�F�N�g) */
	public static final String INT_MARKET_STATUS = "INT_MARKET_STATUS";

	/** �R�}���h�� */
	public static final String CMD_NAME = "MarketStatus";

	/** �ʖ� */
	public static final String CMD_ALIAS = "104";

	/** �s�ꖼ */
	protected String fAuctioneerName;

	/** �R�}���h�̎��s��� */
	protected WCommandStatus fStatus;

	/** ���ʂ��i�[���邽�߂�HashMap */
	protected HashMap fMarketInfo;

	/**
	 * �R���X�g���N�^
	 */
	public WCMarketStatusCore() {
		super();
		fAuctioneerName = "";
		fMarketInfo = new HashMap();
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
		String result = "" + fMarketInfo.get(INT_MARKET_STATUS);
		return result;
	}

	/**
	 * HashMap��Ԃ��D
	 * 
	 * @return HashMap
	 */
	public HashMap getMarketInfo() {
		return fMarketInfo;
	}

	/**
	 * @see org.umart.cmdCore.ICommand#doIt()
	 */
	public abstract WCommandStatus doIt();

	/**
	 * @see org.umart.cmdCore.ICommand#printOn()
	 */
	public void printOn() {
		System.out.println("<<MarketStatus " + fAuctioneerName + ">>");
		System.out.println("MarketStatus:" + fMarketInfo.get(INT_MARKET_STATUS));
	}
}
