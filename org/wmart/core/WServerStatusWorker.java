package org.wmart.core;

/**
 * ��������[�J�[��ԃN���X
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WServerStatusWorker extends WServerStatus {

	/** ������� */
	public static final int INITIALIZED = 0;
	/** ������t�� (���O) */
	public static final int BEFORE_CONCLUSION = 1;
	/** ���҂� */
	public static final int CONCLUDING = 2;
	/** ������t�� (����) */
	public static final int AFTER_CONCLUSION = 3;
	/** ��菀���� */
	public static final int PREPARING = 4;
	/** ��Ԃ�\��������̔z�� */
	public static final String[] STATES = { "INITIALIZED", "BEFORE_CONCLUSION", "CONCLUDING", "AFTER_CONCLUSION",
		"PREPARING", };

	/**
	 * �R���X�g���N�^
	 * 
	 */
	public WServerStatusWorker() {
		super();
		fStateStrings = STATES;
	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param state
	 * @param date
	 * @param session
	 */
	public WServerStatusWorker(int state, int date, int session) {
		super(state, date, session);
		fStateStrings = STATES;
	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param name
	 * @param state
	 * @param date
	 * @param session
	 */
	public WServerStatusWorker(String name, int state, int date, int session) {
		super(name, state, date, session);
		fStateStrings = STATES;
	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param name
	 */
	public WServerStatusWorker(String name) {
		super(name);
		fStateStrings = STATES;
	}

	/**
	 * �R�s�[�R���X�g���N�^
	 * 
	 * @param src
	 */
	public WServerStatusWorker(WServerStatus src) {
		super(src);
	}

	/**
	 * �����𐶐�
	 * 
	 * @return ����
	 */
	public Object clone() {
		return new WServerStatusWorker(this);
	}
}
