package org.wmart.agent;

import java.util.*;

/**
 * �e�X�g�p�G�[�W�F���g�N���X
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WRandomAgent extends WAgent {

	/** �������i�̕��̃f�t�H���g�l */
	public static final int DEFAULT_WIDTH_OF_PRICE = 20;

	/** �������ʂ̍ő�l�̃f�t�H���g�l */
	public static final int DEFAULT_MAX_QUANT = 50;

	/** �������ʂ̍ŏ��l�̃f�t�H���g�l */
	public static final int DEFAULT_MIN_QUANT = 10;

	/** ��/���|�W�V�����̍ő�l�̃f�t�H���g�l */
	public static final int DEFAULT_MAX_POSITION = 300;

	/** ���߂̉��i�������Ȃ��Ƃ��ɗ��p���鉿�i�̃f�t�H���g�l */
	public static final int DEFAULT_NOMINAL_PRICE = 3000;

	/** �������i�̕� */
	private int fWidthOfPrice = DEFAULT_WIDTH_OF_PRICE;

	/** �������ʂ̍ő�l */
	private int fMaxQuant = DEFAULT_MAX_QUANT;

	/** �������ʂ̍ŏ��l */
	private int fMinQuant = DEFAULT_MIN_QUANT;

	/** ��/���|�W�V�����̍ő�l */
	private int fMaxPosition = DEFAULT_MAX_POSITION;

	/** ���߂̉��i�������Ȃ��Ƃ��ɗ��p���鉿�i */
	private int fNominalPrice = DEFAULT_NOMINAL_PRICE;

	/** �������i�̃v���p�e�B��(WidthOfPrice) */
	public static final String WIDTH_OF_PRICE_KEY = "WidthOfPrice";

	/** �������ʂ̍ő�l�̃v���p�e�B��(MaxQuant) */
	public static final String MAX_QUANT_KEY = "MaxQuant";

	/** �������ʂ̍ŏ��l�̃v���p�e�B��(MinQuant) */
	public static final String MIN_QUANT_KEY = "MinQuant";

	/** ��/���|�W�V�����̍ő�l�̃v���p�e�B��(MaxPosition) */
	public static final String MAX_POSITION_KEY = "MaxPosition";

	/** ���߂̉��i�������Ȃ��Ƃ��ɗ��p���鉿�i�̃v���p�e�B��(NominalPrice) */
	public static final String NOMINAL_PRICE_KEY = "NominalPrice";

	/**
	 * �R���X�g���N�^
	 * 
	 * @param loginName
	 *            ���O�C����
	 * @param passwd
	 *            �p�X���[�h
	 * @param realName
	 *            ����
	 * @param seed
	 *            �����̎�
	 */
	public WRandomAgent(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

	/**
	 * ���i����Ԃ��D
	 * 
	 * @return ���i��
	 */
	public int getWidthOfPrice() {
		return fWidthOfPrice;
	}

	/**
	 * �ŏ��������ʂ�Ԃ��D
	 * 
	 * @return �ŏ���������
	 */
	public int getMinQuant() {
		return fMinQuant;
	}

	/**
	 * �ő咍�����ʂ�Ԃ��D
	 * 
	 * @return �ő咍������
	 */
	public int getMaxQuant() {
		return fMaxQuant;
	}

	/**
	 * �ő�|�W�V������Ԃ��D
	 * 
	 * @return �ő�|�W�V����
	 */
	public int getMaxPosition() {
		return fMaxPosition;
	}

	/**
	 * �s�ꉿ�i������̂Ƃ��̒������i��Ԃ��D
	 * 
	 * @return �s�ꉿ�i������̂Ƃ��̒������i
	 */
	public int getNominalPrice() {
		return fNominalPrice;
	}

	/**
	 * �����[���쐬����D
	 * 
	 * @param day
	 *            ��
	 * @param session
	 *            ��
	 * @param �������
	 * @param noOfSessionsPerDay
	 *            1���̐ߐ�
	 * @param spotPrices
	 *            �������i�\�n��DspotPrices[0]����spotPrices[719]�܂ł�720�ߕ��̃f�[�^���i�[����Ă���DspotPrices[719]�����߂̉��i�ł���D
	 *            �������C���i���������Ă��Ȃ��ꍇ�C-1�������Ă���̂Œ��ӂ��邱�ƁD
	 * @param forwardPrices
	 *            �敨���i�\�n��DfuturePrices[0]����futurePrices[29]�܂ł�30�����̃f�[�^���i�[����Ă���DfuturePrices[29]�����߂̉��i�ł��� �D
	 *            �������C���i���������Ă��Ȃ��ꍇ�C-1�������Ă���̂Œ��ӂ��邱�ƁD
	 * @param position
	 *            �|�W�V�����D���Ȃ�Δ����z��(�����O�E�|�W�V����)�C���Ȃ�Δ���z���i�V���[�g�E�|�W�V�����j��\���D
	 * @param money
	 *            �����c���D�^��long�ł��邱�Ƃɒ��ӁD
	 * @return UOrderForm[] �����[�̔z��
	 */
	public WOrderForm[] makeOrderForms(int day, int session, int maxDays, int noOfSessionsPerDay, String[] spotPrices,
		String[] forwardPrices, int position, long money) {
		Random rand = getRandom();
		WOrderForm[] forms = new WOrderForm[1];
		forms[0] = new WOrderForm();
		forms[0].setBuySell(rand.nextInt(2) + 1);

		// �� (09/9/7)
		int price = 100 + rand.nextInt(200);
		if (price <= 0) {
			price = 1;
		}
		int volume = rand.nextInt(50);
		int earliest = rand.nextInt(3);
		int latest = earliest + rand.nextInt(3);
		int total = latest - earliest + 1;
		String specstr = "serviceA:" + volume + ":" + earliest + ":" + latest + ":" + total + ";";
		// "���i��:��]����:�ő�����:�Œx����:���׎���;" �̌J��Ԃ�
		String auctioneer = "forward";

		forms[0].setAuctioneerName(auctioneer);
		forms[0].setOrderPrice(price);
		forms[0].setSpec(specstr);
		println("" + day + "/" + session + ", " + forms[0].getBuySellByString() + ", $" + forms[0].getOrderPrice() + ", ["
			+ forms[0].getSpec());
		return forms;
	}

	/**
	 * �G�[�W�F���g�̃V�X�e���p�����[�^��ݒ肷��D
	 * 
	 * @param args
	 *            �V�X�e���p�����[�^
	 */
	public void setParameters(String[] args) {
		super.setParameters(args);
		for (int i = 0; i < args.length; ++i) {
			StringTokenizer st = new StringTokenizer(args[i], "= ");
			String key = st.nextToken();
			String value = st.nextToken();
			if (key.equals(WRandomAgent.WIDTH_OF_PRICE_KEY)) {
				fWidthOfPrice = Integer.parseInt(value);
				println("WidthOfPrice has been changed to " + fWidthOfPrice);
			} else if (key.equals(WRandomAgent.MIN_QUANT_KEY)) {
				fMinQuant = Integer.parseInt(value);
				println("MinQuant has been changed to " + fMinQuant);
			} else if (key.equals(WRandomAgent.MAX_QUANT_KEY)) {
				fMaxQuant = Integer.parseInt(value);
				println("MaxQuant has been changed to " + fMaxQuant);
			} else if (key.equals(WRandomAgent.MAX_POSITION_KEY)) {
				fMaxPosition = Integer.parseInt(value);
				println("MaxPosition has been changed to " + fMaxPosition);
			} else if (key.equals(WRandomAgent.NOMINAL_PRICE_KEY)) {
				fNominalPrice = Integer.parseInt(value);
				println("NominalPrice has been changed to " + fNominalPrice);
			} else {
				println("Unknown parameter:" + key + " in RandomStrategy.setParameters");
			}
		}
	}

}
