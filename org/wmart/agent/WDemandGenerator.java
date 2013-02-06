package org.wmart.agent;

import java.io.*;
import java.util.*;

/**
 * ���v�t�@�C���𐶐�����
 * 
 * @author ikki
 * 
 */
public class WDemandGenerator {

	/** 1���̃X���b�g�� */
	private int fSessionsPerDay;
	/** �^�p���� */
	private int fMaxDate;
	/** �敨����Ώۓ��� */
	private int fDaysForward;
	/** �敨����ΏۃX���b�g�� */
	private int fSlotsForward;
	/** ���[�N�t���[�ő咷 */
	private int fMaxLength;
	/** ���[�N�t���[�ő啝 (����^�X�N��) */
	private int fMaxWidth;
	/** ���i�����X�g */
	private String[] fGoods;
	/** �^�X�N�Œ�P�� */
	private int fMinUnitPrice;
	/** �^�X�N�ō��P�� */
	private int fMaxUnitPrice;
	/** �^�X�N�ő吔�� */
	private int fMaxVolume;
	/** ���p�p�x (1�������胏�[�N�t���[���̊��Ғl) */
	private int fFreq;
	/** ���� */
	private Random fRandom;
	/** �������ꂽ�����v�� */
	private int fResultVolume;
	/** �������ꂽ�������z */
	private int fResultPrice;
	/** �������ꂽ���[�N�t���[�� */
	private int fResultCount;
	/** �������ꂽ���[�N�t���[�S�̒��̕��z */
	private int[] fResultCountPerLength;
	/** �z�肷�鋟���� (1��������S���i���v) */
	private int fSupplyVolumePerDay;
	/** �������z�O���t��\�� */
	private boolean fShowChart = false;

	/**
	 * ���C��
	 * 
	 */
	public static void main(String[] args) {
		// �^�p����
		int maxDate = 30;
		// �敨����Ώۓ���
		int daysForward = 7;
		// ���p�p�x
		int[] freqs = { 1, 3, 5, 7, 10, 14, 20, 30, 40, 50 };
		// �o���G�[�V������
		int numSeeds = 10;
		// �o�͐�t�H���_��
		String outputDir = "demand";

		String goods = "A;B;C;D;E";
		int minUnitPrice = 2;
		int maxUnitPrice = 10;
		int maxVolume = 100;

		int i = 1;
		int n = freqs.length * numSeeds;
		System.out.println(String.format("Start to generate %d files", n));
		for (int q = 0; q < freqs.length; q++) {
			for (int seed = 0; seed < numSeeds; seed++) {
				int freq = freqs[q];
				String filename = String.format("%s%sx%03df%02dq%03dv%02d.csv", outputDir,
					File.separator, maxDate, daysForward, freq, seed);
				WDemandGenerator dg = new WDemandGenerator(seed, maxDate, daysForward, goods,
					minUnitPrice, maxUnitPrice, maxVolume, freq);
				System.out.print(String.format("(%3d/%3d) ", i, n));
				dg.generateDemandFile(filename);
				i++;
			}
		}
		System.out.println("Finished!");
	}

	/**
	 * �R���X�g���N�^
	 */
	public WDemandGenerator(int seed, int maxDate, int daysForward, String goods, int minUnitPrice,
		int maxUnitPrice, int maxVolume, int freq) {
		fSessionsPerDay = 24;
		fMaxDate = maxDate;
		fDaysForward = daysForward;
		fSlotsForward = fSessionsPerDay * fDaysForward;
		fGoods = goods.split(";");
		fMinUnitPrice = minUnitPrice;
		fMaxUnitPrice = maxUnitPrice;
		fMaxVolume = maxVolume;
		fFreq = freq;
		fRandom = new Random(seed);
		// ���[�N�t���[�ő咷��1���B�������敨����Ώۓ����̔���������Ȃ�
		fMaxLength = fSessionsPerDay;
		if (fMaxLength > fSlotsForward / 2) {
			fMaxLength = fSlotsForward / 2;
		}
		// ���[�N�t���[�ő啝�� ���i�� - 2
		fMaxWidth = fGoods.length - 2;
		// ���v���
		fResultVolume = 0;
		fResultPrice = 0;
		fResultCount = 0;
		fResultCountPerLength = new int[fMaxLength];
		fSupplyVolumePerDay = 100 * fSessionsPerDay * fGoods.length;
	}

	/**
	 * ���v�t�@�C���̒���������
	 * 
	 * @throws FileNotFoundException
	 */
	private void lengthsOfDemands(String filename) {
		HashMap<Integer, Integer> aaa = new HashMap<Integer, Integer>();
		// 1�߂����胏�[�N�t���[���̊��Ғl
		double avgOrders = (double) fFreq / fSessionsPerDay;
		// 1�߂����胏�[�N�t���[���̍ő�l�͊��Ғl��2�{�B������ 3 �������Ȃ�
		int maxOrders = (int) Math.ceil(avgOrders * 2);
		if (maxOrders < 3) {
			maxOrders = 3;
		}
		int sum = 0;
		int count = 0;
		// �ŏ��� daysForward ���͐敨�����]�T�Ƃ��ċ󂯂�
		for (int day = fDaysForward + 1; day <= fMaxDate; day++) {
			for (int session = 1; session <= fSessionsPerDay; session++) {
				int numOrders = poissonDistributed(avgOrders, maxOrders);
				for (int i = 0; i < numOrders; i++) {
					double lambda = lambdaForProbabilityAt(fSessionsPerDay / 2, 0.05); // �S�̂�5%��12����
					int length = 2 + exponentialDistributed(lambda, fMaxLength - 2); // ���[�N�t���[�S�̒�
					System.out.println(length);
					if (!aaa.containsKey(length)) {
						aaa.put(length, 0);
					}
					aaa.put(length, aaa.get(length) + 1);
					count++;
				}
				// �ŏI���� maxLength �����]�T���c���ďI��
				if (day >= fMaxDate && session >= fSessionsPerDay - fMaxLength + 1) {
					break;
				}

			}
		}
		for (Iterator iterator = aaa.keySet().iterator(); iterator.hasNext();) {
			Integer len = (Integer) iterator.next();
			System.out.println(String.format("%d\t%d", len, aaa.get(len)));
		}
		System.out.println(String.format("count = %d", count));
	}

	/**
	 * ���v�t�@�C���𐶐�����
	 * 
	 * @throws FileNotFoundException
	 */
	private void generateDemandFile(String filename) {
		System.out.print(String.format("Generating %s ...", filename));
		// 1�߂����胏�[�N�t���[���̊��Ғl
		double avgOrders = (double) fFreq / fSessionsPerDay;
		// 1�߂����胏�[�N�t���[���̍ő�l�͊��Ғl��2�{�B������ 3 �������Ȃ�
		int maxOrders = (int) Math.ceil(avgOrders * 2);
		if (maxOrders < 3) {
			maxOrders = 3;
		}
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(filename, false)); // �㏑��
			// �ŏ��� daysForward ���͐敨�����]�T�Ƃ��ċ󂯂�
			for (int day = fDaysForward + 1; day <= fMaxDate; day++) {
				for (int session = 1; session <= fSessionsPerDay; session++) {
					int numOrders = poissonDistributed(avgOrders, maxOrders);
					for (int i = 0; i < numOrders; i++) {
						pw.println(generateDemandLine(day, session));
					}
					// �ŏI���� maxLength �����]�T���c���ďI��
					if (day >= fMaxDate && session >= fSessionsPerDay - fMaxLength + 1) {
						break;
					}
				}
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// ���ʕ\��
		int validDays = fMaxDate - fDaysForward - 1; // ���葊������
		float ratio = (float) fResultVolume / (fSupplyVolumePerDay * validDays);
		System.out.print(String.format(" Count=%4d, Ratio=%3.2f", fResultCount, ratio));
		if (fShowChart) {
			System.out.println();
			for (int i = 0; i < fResultCountPerLength.length; i++) {
				StringBuilder bar = new StringBuilder();
				for (int j = 0; j < fResultCountPerLength[i]; j++) {
					bar.append("*");
				}
				System.out.println(String.format("%4d|%s %d", i + 1, bar.toString(),
					fResultCountPerLength[i]));
			}
		}
		System.out.println(" [  OK  ]");
	}

	/**
	 * 1���̎��v�𐶐�����
	 * 
	 * <pre>
	 * (BEGIN) ���� task[0]   ��
	 *         ����  ...      ��
	 *         ���� task[n-2] ���� task[n-1] ���� (END)
	 * </pre>
	 * 
	 * <p>
	 * n�̃^�X�N����Ȃ郏�[�N�t���[�B<br />
	 * �S�̂̒����� 2 �` maxLength �͈̔͂Ŏw�����z�B����������_���Ȓ�����2�������A�O���� task[0]�`task[n-2] �ɁA�㔼�� task[n-1] �Ɋ��蓖�Ă�B<br />
	 * ���i�̓^�X�N���ƂɈقȂ�n��ނ������_���ɑI���B<br />
	 * �P���͑S�^�X�N������ minUnitPrice �` maxUnitPrice �͈̔͂Ń����_���B<br />
	 * �ʂ͊e�^�X�N���Ƃ� 1 �` maxVolume �͈̔͂Ń����_���B
	 * </p>
	 * 
	 * @param day
	 *            ���p�J�n��
	 * @param session
	 *            ���p�J�n��
	 * @return "�敨������/��,���p�J�n��/��,���D���i,��������"
	 */
	private String generateDemandLine(int day, int session) {
		int slot = (day - 1) * fSessionsPerDay + session; // ���p�J�n�X���b�g�ԍ�
		int margin = uniformDistributed(2, fDaysForward); // �敨�����]�T
		int fwdDay = day - margin; // �敨������
		int fwdSession = fSessionsPerDay; // �敨������

		int n = uniformDistributed(2, 5); // �^�X�N��
		String[] goods = choose(n, fGoods); // �e�^�X�N�̏��i��
		int[] volumes = uniform(n, 1, fMaxVolume); // �e�^�X�N�̎�����
		int[] lengths = new int[n]; // �e�^�X�N�̒���
		int[] atimes = new int[n]; // �e�^�X�N�̊J�n����
		int[] dtimes = new int[n]; // �e�^�X�N�̏I������
		double[] uprices = new double[n]; // �e�^�X�N�̒P��

		// �e�^�X�N�̒��������߂�
		double lambda = lambdaForProbabilityAt(fSessionsPerDay / 2, 0.05); // �S�̂�5%��12����
		int length = 2 + exponentialDistributed(lambda, fMaxLength - 2); // ���[�N�t���[�S�̒�
		int[] len2 = uniformDivide(2, length); // �S�̒���2����
		for (int i = 0; i <= n - 2; i++) {
			lengths[i] = len2[0];
			atimes[i] = slot;
			dtimes[i] = atimes[0] + lengths[0] - 1;
		}
		lengths[n - 1] = len2[1];
		atimes[n - 1] = dtimes[n - 2] + 1;
		dtimes[n - 1] = atimes[n - 1] + lengths[n - 1] - 1;

		// �P���͑S�^�X�N���ʂƂ���
		double uprice = uniformDistributed((double) fMinUnitPrice, (double) fMaxUnitPrice);
		double sum = 0.0;
		for (int i = 0; i < n; i++) {
			uprices[i] = uprice;
			sum += uprices[i] * volumes[i] * lengths[i];
		}
		int price = Math.round((float) sum);

		// ���v�����L�^
		for (int i = 0; i < n; i++) {
			fResultVolume += volumes[i] * lengths[i];
		}
		fResultPrice += price;
		fResultCount++;
		fResultCountPerLength[length - 1]++;

		// �����񉻂��ĕԂ�
		StringBuilder line = new StringBuilder();
		line.append(String.format("%d/%d,%d/%d,%d,", fwdDay, fwdSession, day, session, price));
		for (int i = 0; i < n; i++) {
			line.append(String.format("%s:%d:%d:%d:%d;", goods[i], volumes[i], atimes[i],
				dtimes[i], lengths[i]));
		}
		line.append(",");
		for (int i = 0; i < n; i++) {
			line.append(String.format("%.6f;", uprices[i]));
		}
		return line.toString();
	}

	/**
	 * 1���̎��v�𐶐�����
	 * <p>
	 * �O�^�X�N(1��) �� ���ԃ^�X�N(1�`maxwidth����) �� ��^�X�N(1��) ����Ȃ郏�[�N�t���[�B<br />
	 * ���ԃ^�X�N���m�͎��ԗ]�T����B�S�^�X�N�̉��׎��Ԃ� maxlength �ȓ��ƂȂ�B
	 * </p>
	 * 
	 * @param day
	 *            ���
	 * @param session
	 *            ���
	 * @return "�敨������/��,����������/��,���D���i,��������"
	 */
	private String generateDemandLine1(int day, int session) {
		int fwdDay = day; // �敨�Ƃ��Ă̔�����
		int fwdSession = fSessionsPerDay; // �敨�Ƃ��Ă̔�����
		int sptDay = day + fDaysForward; // �����Ƃ��Ă̔�����
		int sptSession = session; // �����Ƃ��Ă̔�����

		int n = 2 + decayDistributed(0.5, fMaxWidth); // ���[�N�t���[���\������^�X�N��
		// [0] = �O�^�X�N
		// [1]�`[n-2] = ���ԃ^�X�N
		// [n-1] = ��^�X�N
		String[] goods = choose(n, fGoods); // �e�^�X�N�̏��i��
		int[] volumes = uniform(n, 1, fMaxVolume); // �e�^�X�N�̎�����
		int[] lengths = uniformDivide(n, fMaxLength); // �e�^�X�N�̒���
		int[] atimes = new int[n]; // �e�^�X�N�̊J�n����
		int[] dtimes = new int[n]; // �e�^�X�N�̏I������
		double[] uprices = new double[n]; // �e�^�X�N�̒P��
		// int margin = uniformDistributed(x - fSessionsPerDay + 1, x);
		atimes[0] = (sptDay - 1) * fSessionsPerDay + sptSession;// �\��͎�t�J�n�シ����
		dtimes[0] = atimes[0] + lengths[0] - 1;
		int maxMidLength = max(Arrays.copyOfRange(lengths, 1, n - 1)); // ���ԃ^�X�N�̒��ōŒ��̂���
		for (int i = 1; i <= n - 2; i++) {
			atimes[i] = dtimes[0] + 1;
			dtimes[i] = atimes[i] + maxMidLength - 1;
		}
		atimes[n - 1] = dtimes[0] + maxMidLength + 1;
		dtimes[n - 1] = atimes[n - 1] + lengths[n - 1] - 1;
		// �P���͑S�^�X�N���ʂƂ���
		double uprice = uniformDistributed((double) fMinUnitPrice, (double) fMaxUnitPrice);
		double sum = 0.0;
		for (int i = 0; i < n; i++) {
			uprices[i] = uprice;
			sum += uprices[i] * volumes[i] * lengths[i];
		}
		int price = Math.round((float) sum);
		// ������
		StringBuilder line = new StringBuilder();
		line.append(String.format("%d/%d,%d/%d,%d,", fwdDay, fwdSession, sptDay, sptSession, price));
		for (int i = 0; i < n; i++) {
			line.append(String.format("%s:%d:%d:%d:%d;", goods[i], volumes[i], atimes[i],
				dtimes[i], lengths[i]));
		}
		line.append(",");
		for (int i = 0; i < n; i++) {
			line.append(String.format("%.6f;", uprices[i]));
		}
		return line.toString();
	}

	/**
	 * �^����ꂽ�����𕪊�����
	 * 
	 * @param size
	 *            ������
	 * @param length
	 *            ����
	 * @return �������ꂽ�e�v�f�̒������܂ޔz��
	 */
	public int[] uniformDivide(int size, int length) {
		int[] u = new int[size];
		int[] p = new int[size + 1];
		int min = 0;
		int max = length - size;
		p[0] = min;
		for (int j = 1; j < size; j++) {
			p[j] = min + fRandom.nextInt(max - min + 1);
		}
		p[size] = max;
		Arrays.sort(p);
		for (int i = 0; i < size; i++) {
			u[i] = p[i + 1] - p[i] + 1;
		}
		return u;
	}

	/**
	 * �����_���Ȑ����̔z��
	 */
	private int[] uniform(int size, int min, int max) {
		int[] u = new int[size];
		for (int i = 0; i < u.length; i++) {
			u[i] = min + fRandom.nextInt(max - min + 1);
		}
		return u;
	}

	/**
	 * �����_���Ȓl��I��
	 */
	private int uniformDistributed(int min, int max) {
		return min + fRandom.nextInt(max - min + 1);
	}

	/**
	 * �����_���Ȓl��I��
	 */
	private double uniformDistributed(double min, double max) {
		return min + fRandom.nextDouble() * (max - min);
	}

	/**
	 * �w�����z�ɏ]���Ēl��I��
	 * 
	 * @param lambda
	 * @param max
	 * @return 0�`max �̐����Bmax �𒴂���l�� max �Ɋۂ߂���B
	 */
	private int exponentialDistributed(double lambda, int max) {
		double v = -Math.log(fRandom.nextDouble()) / lambda;
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
		for (int k = 1; k < max; k++) {
			if (fRandom.nextDouble() > alpha) {
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
		double r = fRandom.nextDouble();
		double p = 0.0;
		double prevp = 0.0;
		for (int k = 0; k < max; k++) {
			p += Math.pow(lambda, k) * Math.exp(-lambda) / factorial(k);
			// max ���傫������Ƃ����� p ������\��������B�p�����[�^��ς�����`�F�b�N����ׂ�
			if (p < prevp) {
				throw new Error(String.format("poissonDistributed(%f, %d): max is too large",
					lambda, max));
			}
			if (p > r) {
				return k;
			}
			prevp = p;
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
		for (int i = 0; i < n; i++) {
			b.add(a.remove(fRandom.nextInt(a.size())));
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

}
