package org.wmart.agent;

import java.util.*;

import org.apache.log4j.*;
import org.wmart.cmdCore.*;
import org.wmart.core.*;
import org.wmart.logger.*;

/**
 * ������
 * 
 * @author Ikki Fujiwara, NII
 */
public class WSimpleBuyerAgent extends WAgent {

	/** ���i�����X�g */
	public String[] fGoods = { "A", "B", "C", "D", "E" };
	/** �^�X�N�Œ�P�� */
	public int fMinUnitPrice = 2;
	/** �^�X�N�ō��P�� */
	public int fMaxUnitPrice = 4;
	/** �^�X�N�ő吔�� */
	public int fMaxVolume = 20;
	/** ���[�N�t���[�ő啝 (����^�X�N��) */
	public int fMaxWidth = fGoods.length - 2;
	/** 1��̒������̊��Ғl */
	public double fAvgOrders = 1.0;
	/** 1��̒������̍ő�l */
	public int fMaxOrders = 10;
	/** ������񃍃O */
	private WResourceLog fRLog = null;

	private static Logger log = Logger.getLogger(WSimpleBuyerAgent.class);

	/**
	 * �R���X�g���N�^
	 */
	public WSimpleBuyerAgent(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

	@Override
	public void doActions(int day, int session, int serverState, int maxDays, int sessionsPerDay,
		int sessionsForward, int maxLength) {
		switch (serverState) {
		case WServerStatusMaster.SHUTDOWN:
			fRLog = new WResourceLog(day, session, fLoginName);
			updateStatus(fRLog);
			writeResourceLog(fRLog);
			break;
		case WServerStatusMaster.AFTER_HOURS:
			if (session == sessionsPerDay) { // �Ƃ肠�����敨��p�B�ŏI�Z�b�V�����̂ݎd������
				log.info("������������");
				fRLog = new WResourceLog(day, session, fLoginName);
				updateStatus(fRLog);
				ArrayList<WOrderForm> forms = new ArrayList<WOrderForm>();
				// �敨����
				int earliest = (day + 1) * sessionsPerDay + 1; // ����Ώۂ̍ő�����
				int latest = (day + 1) * sessionsPerDay + sessionsForward; // ����Ώۂ̍Œx����
				int numOrders = poissonDistributed(fAvgOrders, fMaxOrders);
				for (int i = 0; i < numOrders; i++) {
					addForwardOrder(forms, earliest, latest, maxLength);
				}
				// �����[�𑗂�
				for (Iterator<WOrderForm> itr = forms.iterator(); itr.hasNext();) {
					WOrderForm form = itr.next();
					orderRequest(form);
					fRLog.addBuying("#0", form.getSpec());
				}
				// ������񃍃O���X�V
				writeResourceLog(fRLog);
				break;
			}
		}
	}

	/**
	 * ���󋵂��擾���A������񃍃O���X�V����
	 */
	private void updateStatus(WResourceLog rlog) {
		ArrayList<HashMap<String, String>> arr = getOrderStatus("forward");
		for (Iterator<HashMap<String, String>> itr = arr.iterator(); itr.hasNext();) {
			HashMap<String, String> os = itr.next();
			int sellBuy = Integer.valueOf(os.get(WCOrderStatusCore.INT_SELL_BUY));
			assert (sellBuy == WOrder.BUY);
			long orderPrice = Integer.valueOf(os.get(WCOrderStatusCore.LONG_ORDER_PRICE));
			double contractPrice = Double.valueOf(os.get(WCOrderStatusCore.DOUBLE_CONTRACT_PRICE));
			if (orderPrice < 0 || contractPrice == 0.0) {
				// �L�����Z���ς� or �����
				String orderSpec = os.get(WCOrderStatusCore.STRING_ORDER_SPEC);
				if (orderPrice < 0) {
					rlog.addCancelBuying("#0", orderSpec);
					// log.info(String.format("canceled orders [%s]", orderSpec));
				} else {
					rlog.addBuying("#0", orderSpec);
					// log.info(String.format("remaining orders [%s]", orderSpec));
				}
			} else {
				// ���ς�
				String orderSpec = os.get(WCOrderStatusCore.STRING_ORDER_SPEC);
				rlog.addBought("#0", orderSpec);
				// log.info(String.format("contracted orders [%s]", orderSpec));
			}
		}
	}

	/**
	 * �����[�𐶐����ă��X�g�ɒǉ�����B �O�^�X�N(1��) �� ���ԃ^�X�N(1�`maxwidth����) �� ��^�X�N(1��) ����Ȃ郏�[�N�t���[�𐶐��B
	 */
	private void addForwardOrder(ArrayList<WOrderForm> forms, int earliest, int latest, int maxlen) {
		assert (fGoods.length >= 2 + fMaxWidth) : "Number of goods must be more than 2 + maxwidth";
		WOrderForm form = new WOrderForm();
		form.setAuctioneerName("forward");
		form.setBuySell(WOrderForm.BUY);

		int x = latest - earliest + 1 - maxlen; // �ő�]�T����
		int n = 2 + decayDistributed(0.5, fMaxWidth); // ���[�N�t���[���\������^�X�N��
		// [0] = �O�^�X�N
		// [1]�`[n-2] = ���ԃ^�X�N
		// [n-1] = ��^�X�N
		String[] goods = choose(n, fGoods); // �e�^�X�N�̏��i��
		int[] volumes = uniform(n, 1, fMaxVolume); // �e�^�X�N�̎�����
		int[] lengths = uniform(n, 1, maxlen / 3); // �e�^�X�N�̒���
		int[] atimes = new int[n]; // �e�^�X�N�̊J�n����
		int[] dtimes = new int[n]; // �e�^�X�N�̏I������
		int margin = exponentialDistributed(lambdaForProbabilityAt(x, 0.01), x);
		atimes[0] = earliest + margin;
		dtimes[0] = atimes[0] + lengths[0] - 1;
		int maxMidLength = max(Arrays.copyOfRange(lengths, 1, n - 1)); // ���ԃ^�X�N�̒��ōŒ��̂���
		for (int i = 1; i <= n - 2; i++) {
			atimes[i] = dtimes[0] + 1;
			dtimes[i] = atimes[i] + maxMidLength - 1;
		}
		atimes[n - 1] = dtimes[0] + maxMidLength + 1;
		dtimes[n - 1] = atimes[n - 1] + lengths[n - 1] - 1;
		double unitprice = uniformDistributed(fMinUnitPrice, fMaxUnitPrice);
		int price = Math.round((float) unitprice * innerProduct(volumes, lengths));

		StringBuilder spec = new StringBuilder();
		for (int i = 0; i < n; i++) {
			spec.append(String.format("%s:%d:%d:%d:%d;", goods[i], volumes[i], atimes[i],
				dtimes[i], lengths[i]));
		}
		form.setSpec(spec.toString());
		form.setOrderPrice(price);
		forms.add(form);

		StringBuilder logstr = new StringBuilder();
		logstr.append(String.format("%s%d*%d[%d-%d], ", goods[0], volumes[0], lengths[0],
			atimes[0], dtimes[0]));
		for (int i = 1; i < n - 1; i++) {
			logstr.append(String.format("%s%d*%d%s", goods[i], volumes[i], lengths[i],
				(i < n - 2) ? "+" : ""));
		}
		logstr.append(String.format("[%d-%d], ", atimes[1], dtimes[1]));
		logstr.append(String.format("%s%d*%d[%d-%d]", goods[n - 1], volumes[n - 1], lengths[n - 1],
			atimes[n - 1], dtimes[n - 1]));
		logstr.append(String.format(" $%d @%.2f", price, unitprice));
		log.info(logstr);
	}

	/**
	 * �����_���Ȑ����̔z��
	 */
	private int[] uniform(int size, int min, int max) {
		Random rand = getRandom();
		int[] u = new int[size];
		for (int i = 0; i < u.length; i++) {
			u[i] = min + rand.nextInt(max - min + 1);
		}
		return u;
	}

	/**
	 * �����_���Ȓl��I��
	 */
	private double uniformDistributed(double min, double max) {
		Random rand = getRandom();
		return min + rand.nextDouble() * (max - min);
	}

	/**
	 * �w�����z�ɏ]���Ēl��I��
	 * 
	 * @param lambda
	 * @param max
	 * @return 0�`max �̐����Bmax �𒴂���l�� max �Ɋۂ߂���B
	 */
	private int exponentialDistributed(double lambda, int max) {
		Random rand = getRandom();
		double v = -Math.log(rand.nextDouble()) / lambda;
		if (v > max) {
			v = max;
		}
		return (int) Math.floor(v);
	}

	/**
	 * �w�����z�ŁA�I�΂��l�� x �ȏ�ɂȂ�m���� p �ƂȂ�悤�ȌW�� �� �����߂�
	 * 
	 * @param x
	 * @param p
	 * @return
	 */
	private double lambdaForProbabilityAt(double x, double p) {
		return -(Math.log(p) / x);
	}

	/**
	 * Decay ���z�ɏ]���Ēl��I��
	 * 
	 * @param alpha
	 *            �W��
	 * @param max
	 *            �ő�l
	 * @return
	 */
	private int decayDistributed(double alpha, int max) {
		Random rand = getRandom();
		for (int k = 1; k < max; k++) {
			if (rand.nextDouble() > alpha) {
				return k;
			}
		}
		return max;
	}

	/**
	 * Poisson ���z�ɏ]���Ēl��I�� http://en.wikipedia.org/wiki/Poisson_distribution
	 * 
	 * @param lambda
	 *            ���Ғl
	 * @param max
	 *            �ő�l (���Ғl��2�{�ȏ���w�肷��ׂ��B�������A���܂�傫���ƃI�[�o�[�t���[����)
	 * @return
	 */
	private int poissonDistributed(double lambda, int max) {
		Random rand = getRandom();
		double r = rand.nextDouble();
		double p = 0.0;
		for (int k = 0; k < max; k++) {
			p += Math.pow(lambda, k) * Math.exp(-lambda) / factorial(k);
			// max ���傫������Ƃ����� p ������\��������B�p�����[�^��ς�����`�F�b�N����ׂ�
			if (p > r) {
				return k;
			}
		}
		return max;
	}

	/**
	 * �K��
	 */
	private long factorial(long n) {
		assert (n >= 0);
		long r = 1;
		while (n > 1) {
			r *= n--;
		}
		return r;
	}

	/**
	 * �z��̗v�f���烉���_���� n ��I��
	 * 
	 * @param n
	 *            �I�Ԍ�
	 * @param given
	 *            �z��
	 * @return
	 */
	private String[] choose(int n, String[] given) {
		assert (0 < n && n <= given.length);
		ArrayList<String> a = new ArrayList<String>(Arrays.asList(given));
		ArrayList<String> b = new ArrayList<String>();
		Random rand = getRandom();
		for (int i = 0; i < n; i++) {
			b.add(a.remove(rand.nextInt(a.size())));
		}
		return b.toArray(new String[n]);
	}

	/**
	 * 2�̔z��̓��� (�Ή�����v�f�̐ς̘a)
	 */
	private int innerProduct(int[] a, int[] b) {
		assert (a.length == b.length);
		int sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum += a[i] * b[i];
		}
		return sum;
	}

	/**
	 * �z��v�f�̍ő�l
	 */
	private int max(int[] a) {
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < a.length; i++) {
			if (a[i] > max) {
				max = a[i];
			}
		}
		return max;
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
			if (key.equalsIgnoreCase("goods")) {
				fGoods = value.split(";");
				println("Goods set to " + fGoods);
			} else if (key.equalsIgnoreCase("minprice")) {
				fMinUnitPrice = Integer.valueOf(value);
				println("MinUnitPrice set to " + fMinUnitPrice);
			} else if (key.equalsIgnoreCase("maxprice")) {
				fMaxUnitPrice = Integer.valueOf(value);
				println("MaxUnitPrice set to " + fMaxUnitPrice);
			} else if (key.equalsIgnoreCase("maxvolume")) {
				fMaxVolume = Integer.valueOf(value);
				println("MaxVolume set to " + fMaxVolume);
				// } else if (key.equalsIgnoreCase("maxlength")) {
				// fMaxLength = Integer.valueOf(value);
				// println("MaxLength set to " + fMaxLength);
			} else if (key.equalsIgnoreCase("maxwidth")) {
				fMaxWidth = Integer.valueOf(value);
				println("MaxWidth set to " + fMaxWidth);
			} else if (key.equalsIgnoreCase("maxorders")) {
				fMaxOrders = Integer.valueOf(value);
				println("MaxOrders set to " + fMaxOrders);
			} else if (key.equalsIgnoreCase("avgorders")) {
				fAvgOrders = Integer.valueOf(value);
				println("AvgOrders set to " + fAvgOrders);
			}
		}
	}

}
