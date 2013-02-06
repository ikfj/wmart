package org.wmart.cmdCore;

import java.util.*;

/**
 * �����c���Ɖ�̂��߂�SVMP�R�}���h�̒��ۃN���X�ł��D
 * 
 * @author �암 �S�i
 */
public abstract class WCBalancesCore implements ICommand {

	/** �����c�����������߂̃L�[(�l��Long�I�u�W�F�N�g) */
	public static final String LONG_CASH = "LONG_CASH";

	/** �؋����z���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
	public static final String LONG_MARGIN = "LONG_MARGIN";

	/** ���������v���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
	public static final String LONG_UNREALIZED_PROFIT = "LONG_UNREALIZED_PROFIT";

	/** �������v���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
	public static final String LONG_SETTLED_PROFIT = "LONG_SETTLED_PROFIT";

	/** �����������߂̃L�[(�l��Long�I�u�W�F�N�g) */
	public static final String LONG_FEE = "LONG_FEE";

	/** �Z���������������߂̃L�[(�l��Long�I�u�W�F�N�g) */
	public static final String LONG_INTEREST = "LONG_INTEREST";

	/** ������Z���z���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
	public static final String LONG_LOAN = "LONG_LOAN";

	/** �]�T���z���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
	public static final String LONG_SURPLUS = "LONG_SURPLUS";

	/** ����܂ł̔��茚�ʐ����������߂̃L�[(�l��Long�I�u�W�F�N�g) */
	public static final String LONG_SELL_POSITION = "LONG_SELL_POSITION";

	/** ����܂ł̔������ʐ����������߂̃L�[(�l��Long�I�u�W�F�N�g) */
	public static final String LONG_BUY_POSITION = "LONG_BUY_POSITION";

	/** �R�}���h�� */
	public static final String CMD_NAME = "Balances";

	/** �ʖ� */
	public static final String CMD_ALIAS = "205";

	/** �R�}���h�̎��s��� */
	protected WCommandStatus fStatus;

	/** ����(�����̎��Y���)���i�[���邽�߂�HashMap */
	protected HashMap fTodayInfo;

	/** ����(�O���̎��Y���)���i�[���邽�߂�HashMap */
	protected HashMap fYesterdayInfo;

	/**
	 * �R���X�g���N�^
	 */
	public WCBalancesCore() {
		super();
		fTodayInfo = new HashMap();
		fYesterdayInfo = new HashMap();
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
	 * @see org.umart.cmdCore.ICommand#getResultString()
	 */
	public String getResultString() {
		String result = fTodayInfo.get(LONG_CASH) + " " + fTodayInfo.get(LONG_MARGIN) + " "
			+ fTodayInfo.get(LONG_UNREALIZED_PROFIT) + " " + fTodayInfo.get(LONG_SETTLED_PROFIT) + " "
			+ fTodayInfo.get(LONG_FEE) + " " + fTodayInfo.get(LONG_INTEREST) + " " + fTodayInfo.get(LONG_LOAN) + " "
			+ fTodayInfo.get(LONG_SURPLUS) + " " + fTodayInfo.get(LONG_SELL_POSITION) + " "
			+ fTodayInfo.get(LONG_BUY_POSITION) + " " + fYesterdayInfo.get(LONG_CASH) + " "
			+ fYesterdayInfo.get(LONG_MARGIN) + " " + fYesterdayInfo.get(LONG_UNREALIZED_PROFIT) + " "
			+ fYesterdayInfo.get(LONG_SETTLED_PROFIT) + " " + fYesterdayInfo.get(LONG_FEE) + " "
			+ fYesterdayInfo.get(LONG_INTEREST) + " " + fYesterdayInfo.get(LONG_LOAN) + " "
			+ fYesterdayInfo.get(LONG_SURPLUS) + " " + fYesterdayInfo.get(LONG_SELL_POSITION) + " "
			+ fYesterdayInfo.get(LONG_BUY_POSITION);
		return result;
	}

	/**
	 * �����̎��Y����Ԃ��D
	 * 
	 * @return �����̎��Y���
	 */
	public HashMap getTodayResults() {
		return fTodayInfo;
	}

	/**
	 * �O���̎��Y����Ԃ��D
	 * 
	 * @return �O���̎��Y���
	 */
	public HashMap getYesterdayResults() {
		return fYesterdayInfo;
	}

	/**
	 * @see org.umart.cmdCore.ICommand#doIt()
	 */
	public abstract WCommandStatus doIt();

	/**
	 * @see org.umart.cmdCore.ICommand#printOn()
	 */
	public void printOn() {
		System.out.println("<<Balances>>");
		System.out.println("<Today>");
		System.out.println(fTodayInfo.toString());
		System.out.println("<Yesterday>");
		System.out.println(fYesterdayInfo.toString());
	}
}
