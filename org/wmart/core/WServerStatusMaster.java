package org.wmart.core;

/**
 * ������}�X�^�[��ԃN���X
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WServerStatusMaster extends WServerStatus {

	/** �ŏ��̃Z�b�V�����̊J�n�O */
	public static final int BEFORE_TRADING = 0;
	/** ������t�� (���ƕ��s) */
	public static final int BUSINESS_HOURS = 1;
	/** ���҂� */
	public static final int CONCLUDING_AUCTIONS = 2;
	/** ������t�� (����) */
	public static final int AFTER_HOURS = 3;
	/** ��菀���� */
	public static final int PREPARING_AUCTIONS = 4;
	/** �Ō�̃Z�b�V�����̖��҂� */
	public static final int AFTER_TRADING = 7;
	/** �S�Z�b�V�����I���� */
	public static final int SHUTDOWN = 8;
	/** �X�[�p�[���[�U�[���O�C���҂� */
	public static final int SU_LOGIN = 9;
	/** ��Ԃ�\��������̔z�� */
	public static final String[] STATES = { "BEFORE_TRADING", "BUSINESS_HOURS",
		"CONCLUDING_AUCTIONS", "AFTER_HOURS", "PREPARING_AUCTIONS", "RESERVED", "RESERVED",
		"AFTER_TRADING", "SHUTDOWN", "SU_LOGIN", };

	/**
	 * �R���X�g���N�^
	 * 
	 */
	public WServerStatusMaster() {
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
	public WServerStatusMaster(int state, int date, int session) {
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
	public WServerStatusMaster(String name, int state, int date, int session) {
		super(name, state, date, session);
		fStateStrings = STATES;
	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param name
	 */
	public WServerStatusMaster(String name) {
		super(name);
		fStateStrings = STATES;
	}

	/**
	 * �R�s�[�R���X�g���N�^
	 * 
	 * @param src
	 */
	public WServerStatusMaster(WServerStatus src) {
		super(src);
	}

	/**
	 * �����𐶐�
	 * 
	 * @return ����
	 */
	public Object clone() {
		return new WServerStatusMaster(this);
	}

}
