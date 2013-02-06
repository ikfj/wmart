package org.wmart.agent;

import java.util.*;

import org.apache.log4j.*;
import org.wmart.core.*;

/**
 * �����
 * 
 * @author Ikki Fujiwara, NII
 */
public class WSpecialSellerAgentForSaint2010 extends WAgent {

	/** ���i�� */
	public String fGood = "A";
	/** �Œ�P�� */
	public int fMinUnitPrice = 1;
	/** �ō��P�� */
	public int fMaxUnitPrice = 1;
	/** ���� */
	public int fVolume = 100;

	private static Logger log = Logger.getLogger(WSpecialSellerAgentForSaint2010.class);

	/**
	 * �R���X�g���N�^
	 */
	public WSpecialSellerAgentForSaint2010(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

	@Override
	public void doActions(int day, int session, int serverState, int maxDays, int sessionsPerDay, int slotsForward,
		int maxLength) {
		if (serverState != WServerStatusMaster.AFTER_HOURS) {
			return; // �}�V���G�[�W�F���g�� AFTER_HOURS �Ɋ�������
		}
		if (session != sessionsPerDay) {
			return; // �ŏI�Z�b�V�����̂ݎd������
		}
		ArrayList<WOrderForm> forms = new ArrayList<WOrderForm>();

		// �敨�����B�����������ʂ���������Ԃ�1�����Ƃ���
		int t0 = (day + 1) * sessionsPerDay + 1; // ����Ώۂ̍ő�����
		int t2 = (day + 1) * sessionsPerDay + slotsForward; // ����Ώۂ̍Œx����
		log.info("�����蒍����");
		addOrder(forms, "forward", -1, -1, t0, t2, day);

		// �����[�𑗂�
		for (Iterator<WOrderForm> itr = forms.iterator(); itr.hasNext();) {
			WOrderForm form = itr.next();
			orderRequest(form);
		}
	}

	/**
	 * �����[�𐶐����ă��X�g�ɒǉ�����
	 */
	private void addOrder(ArrayList<WOrderForm> forms, String auctioneer, double unitprice0, int volume0, int earliest,
		int latest, int day) {
		WOrderForm form = new WOrderForm();
		form.setAuctioneerName(auctioneer);
		form.setBuySell(WOrderForm.SELL);
		String good = "";
		int price = -1;
		int volume = 0;
		int length = 0;
		int atime = -1;
		int dtime = -1;
		if (fLoginName.equalsIgnoreCase("provider1")) {
			good = "serviceA";
			price = 20;
			volume = 40;
			length = 4;
			atime = 0 + earliest;
			dtime = 3 + earliest;
		}
		if (fLoginName.equalsIgnoreCase("provider2")) {
			good = "serviceB";
			price = 15;
			volume = (day == 1) ? 30 : 20;
			length = 4;
			atime = 0 + earliest;
			dtime = 3 + earliest;
		}
		if (fLoginName.equalsIgnoreCase("provider3")) {
			good = "serviceB";
			price = 9;
			volume = (day == 1) ? 30 : 20;
			length = 2;
			atime = 2 + earliest;
			dtime = 3 + earliest;
		}
		// �����s��
		if (day >= 2) {
			length = 1;
			atime = earliest;
			dtime = earliest;
		}
		double unitprice = price;
		form.setSpec(String.format("%s:%d:%d:%d:%d;", good, volume, atime, dtime, length));
		form.setOrderPrice(price);
		forms.add(form);
		log.info(String.format("\t%s%d*%d[%d-%d] $%d @%.2f", good, volume, length, atime, dtime, price, unitprice));
	}

	/**
	 * �����[�𐶐����ă��X�g�ɒǉ�����
	 */
	private void addOrder1(ArrayList<WOrderForm> forms, String auctioneer, double unitprice, int volume, int earliest,
		int latest) {
		if (unitprice > 0 && volume > 0) {
			int length = latest - earliest + 1;
			int price = Math.round((float) unitprice * volume);
			WOrderForm form = new WOrderForm();
			form.setAuctioneerName(auctioneer);
			form.setBuySell(WOrderForm.SELL);
			form.setOrderPrice(price);
			form.setSpec(String.format("%s:%d:%d:%d:%d;", fGood, volume, earliest, latest, length));
			forms.add(form);
			log.info(String.format("\t%s%d*%d[%d-%d] $%d @%.2f", fGood, volume, length, earliest, latest, price,
				unitprice));
		}
	}

	/**
	 * �����_���Ȓl��I��
	 */
	private double uniformDistributed(double min, double max) {
		Random rand = getRandom();
		return min + rand.nextDouble() * (max - min);
	}

	/**
	 * �G�[�W�F���g�̃V�X�e���p�����[�^��ݒ肷��D
	 * 
	 * @param args
	 *            �V�X�e���p�����[�^
	 */
	@Override
	public void setParameters(String[] args) {
		super.setParameters(args);
		for (int i = 0; i < args.length; ++i) {
			StringTokenizer st = new StringTokenizer(args[i], "= ");
			String key = st.nextToken();
			String value = st.nextToken();
			if (key.equalsIgnoreCase("good")) {
				fGood = value;
				println("Good set to " + fGood);
			} else if (key.equalsIgnoreCase("minprice")) {
				fMinUnitPrice = Integer.valueOf(value);
				println("MinUnitPrice set to " + fMinUnitPrice);
			} else if (key.equalsIgnoreCase("maxprice")) {
				fMaxUnitPrice = Integer.valueOf(value);
				println("MaxUnitPrice set to " + fMaxUnitPrice);
			} else if (key.equalsIgnoreCase("volume")) {
				fVolume = Integer.valueOf(value);
				println("Volume set to " + fVolume);
			}
		}
	}

}
