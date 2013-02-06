package org.wmart.core;

import org.apache.log4j.*;

/**
 * ������̏�Ԃ�\�����ۃN���X
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public abstract class WServerStatus {

	/** ���K�[ */
	private static Logger log = Logger.getLogger(WServerStatus.class);
	/** ���ʖ� */
	protected String fName = "";
	/** ��Ԃ�\��������̔z�� */
	protected String[] fStateStrings = {};
	/** ��� */
	protected int fState = 0;
	/** ���݂̓� */
	protected int fDate = 0;
	/** ���݂̐� */
	protected int fSession = 0;

	/**
	 * �R���X�g���N�^
	 */
	public WServerStatus() {
		this("", 0, 0, 0);
	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param state
	 */
	public WServerStatus(String name) {
		this(name, 0, 0, 0);
	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param state
	 * @param date
	 * @param session
	 */
	public WServerStatus(int state, int date, int session) {
		this("", state, date, session);
	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param name
	 * @param state
	 * @param date
	 * @param session
	 */
	public WServerStatus(String name, int state, int date, int session) {
		fName = name;
		fState = state;
		fDate = date;
		fSession = session;
		fStateStrings = null;
	}

	/**
	 * �R�s�[�R���X�g���N�^
	 */
	public WServerStatus(WServerStatus src) {
		fState = src.fState;
		fName = src.fName;
		fStateStrings = src.fStateStrings;
	}

	/**
	 * ��Ԃ𕶎���ŕԂ�
	 */
	public String getStateString() {
		return fStateStrings[fState];
	}

	/**
	 * ��Ԃ��擾����
	 * 
	 * @return the state
	 */
	public int getState() {
		return fState;
	}

	/**
	 * ��Ԃ�ݒ肷��
	 * 
	 * @param state
	 *            the state to set
	 */
	public void setState(int state) {
		fState = state;
		log.debug(toString());
	}

	/**
	 * ���݂̓��t(U-Mart��)��Ԃ��D
	 * 
	 * @return ���݂̓��t(U-Mart��)
	 */
	public int getDate() {
		return fDate;
	}

	/**
	 * ���݂̓��t(U-Mart��)��ݒ肷��D
	 * 
	 * @param date
	 *            ���݂̓��t(U-Mart��)
	 */
	public void setDate(int date) {
		fDate = date;
	}

	/**
	 * ���t��1���i�߂�D
	 * 
	 * @return 1���i�߂���̓��t
	 */
	public int incrementDate() {
		++fDate;
		return fDate;
	}

	/**
	 * ���݂̐߂�Ԃ��D
	 * 
	 * @return ���݂̐�
	 */
	public int getSession() {
		return fSession;
	}

	/**
	 * ���݂̐߂�ݒ肷��D
	 * 
	 * @param session
	 *            ��
	 */
	public void setSession(int session) {
		fSession = session;
	}

	/**
	 * �߂�1�i�߂�D
	 * 
	 * @return 1�i�߂���̐�
	 */
	public int incrementSession() {
		++fSession;
		return fSession;
	}

	/**
	 * �߂�1�ɖ߂��D
	 * 
	 * @return �񂹉�
	 */
	public int resetSession() {
		fSession = 1;
		return fSession;
	}

	/**
	 * ���O���擾����
	 * 
	 * @return the name
	 */
	public String getName() {
		return fName;
	}

	/**
	 * ���O��ݒ肷��
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		fName = name;
	}

	/**
	 * ��Ԃ�W���o�͂ɕ\������
	 */
	public void printOn() {
		System.out.println("    " + toString());
	}

	/*
	 * (�� Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "date" + fDate + "/session" + fSession + " " + getName() + " status: " + getStateString();
	}

}
