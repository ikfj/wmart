package org.wmart.core;

import java.io.*;
import java.util.*;

/**
 * ���i�n��f�[�^�x�[�X
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WPriceInfoDB {

	/** ���ʖ� */
	private String fName = "";
	/** �^�C���X���b�g�� */
	private int fSlots;
	/** �ő�X�e�b�v�� */
	private int fMaxSteps;

	/** UPriceInfoElement�I�u�W�F�N�g���i�[���邽�߂̃x�N�^ */
	private Vector<WPriceInfo> fPriceInfoArray;
	/** ���݂�UPriceInfoElement���w�������C���f�b�N�X�B ���i�����肵�Ă���̂�fPriceInfoArray[fCurrentPtr - 1]�܂ł� ���邱�Ƃɒ��ӁB */
	private int fCurrentPtr = 0;

	/**
	 * �R���X�g���N�^
	 */
	public WPriceInfoDB(int slots, int maxsteps) {
		fSlots = slots;
		fMaxSteps = maxsteps + 1; // �ŏI�X�e�b�v�̎��܂�
		fPriceInfoArray = new Vector<WPriceInfo>();
	}

	/**
	 * ������
	 * 
	 */
	public void initialize(String name) {
		fName = name;
		for (int t = 0; t < fMaxSteps; t++) {
			WPriceInfo pi = new WPriceInfo(fSlots);
			fPriceInfoArray.addElement(pi);
		}
		// if (fMaxSteps > fPriceInfoArray.size()) {
		// System.err.println("maxSteps=" + fMaxSteps);
		// System.err.println("fPriceInfoArray.size()=" + fPriceInfoArray.size());
		// System.err.println("Error: initialize error in WPriceInfoDB");
		// System.exit(-1);
		// }
		fCurrentPtr = 0;
		// fDate = 1;
		// fSession = 1;
	}

	/**
	 * ���݂̃X�e�b�v�̉��i�\���擾
	 * 
	 * @return �ǉ����ꂽ WPriceInfo
	 */
	public WPriceInfo getCurrentPriceInfo() {
		WPriceInfo pi = fPriceInfoArray.elementAt(fCurrentPtr);
		// assert (pi.getDate() == fDate && pi.getSession() == fSession);
		// pi.setDate(fDate);
		// pi.setSession(fSession);
		return pi;
	}

	/**
	 * ���O�̃X�e�b�v�̉��i�\���擾
	 * 
	 * @return �ǉ����ꂽ WPriceInfo
	 */
	public WPriceInfo getLatestPriceInfo() {
		if (fCurrentPtr < 1) {
			return null;
		}
		WPriceInfo pi = fPriceInfoArray.elementAt(fCurrentPtr - 1);
		return pi;
	}

	/**
	 * �ߋ�steps���̉��i�\�n����擾
	 * 
	 * @param good
	 *            ���i��
	 * @param steps
	 *            �K�v�ȃX�e�b�v��
	 * @return ���i�\�n��D�v�f0�����߂̉��i�\�ł���D
	 */
	public String[] getPrices(int steps) {
		if (fCurrentPtr < steps) {
			// System.out.println("getPrices Error!: adjust arraysize " + steps + " -> " +
			// fCurrentPtr);
			steps = fCurrentPtr;
		}
		if (fCurrentPtr < 0) {
			return null;
		}
		String result[] = new String[steps];
		for (int i = 0; i < steps; i++) {
			WPriceInfo pi = fPriceInfoArray.elementAt(fCurrentPtr - i - 1);
			result[i] = pi.encode();
		}
		return result;
	}

	/**
	 * ���ׂẴX�e�b�v�̉��i�\�n����o�̓X�g���[���ɏ����o���D
	 * "date/session goodA:offset,10,11,12,...;goodB:offset,20,21,22,..."
	 * 
	 * @param pw
	 *            �o�̓X�g���[��
	 * @throws IOException
	 */
	public void writeTo(PrintWriter pw) throws IOException {
		Enumeration<WPriceInfo> e = fPriceInfoArray.elements();
		while (e.hasMoreElements()) {
			WPriceInfo pi = e.nextElement();
			pw.printf("%d/%d %s", pi.getDate(), pi.getSession(), pi.encode());
		}
	}

	/**
	 * ���݂܂ł̃X�e�b�v�̉��i�\�n����o�̓X�g���[���֏����o���D
	 * "date/session goodA:offset,10,11,12,...;goodB:offset,20,21,22,..."
	 * 
	 * @param pw
	 *            �o�̓X�g���[��
	 * @throws IOException
	 */
	public void writePriceInfoBeforeCurrentPtr(PrintWriter pw) throws IOException {
		for (int i = 0; i < fCurrentPtr; ++i) {
			WPriceInfo pi = fPriceInfoArray.get(i);
			pw.printf("%d/%d %s", pi.getDate(), pi.getSession(), pi.encode());
		}
	}

	/**
	 * ���O�̃X�e�b�v�̑S���i�̉��i�\��1�s1���i�ŏo�̓X�g���[���֏����o���D
	 * "date/session goodA:offset,10,11,12,...\ndate/session goodB:offset,20,21,22,..."
	 * 
	 * @param pw
	 *            �o�̓X�g���[��
	 * @throws IOException
	 */
	public void writeLatestPriceInfo(PrintWriter pw) throws IOException {
		if (fCurrentPtr < 1) {
			return;
		}
		WPriceInfo pi = fPriceInfoArray.get(fCurrentPtr - 1);
		for (Iterator<String> itr = pi.getGoods().iterator(); itr.hasNext();) {
			String good = itr.next();
			writeLatestPriceInfo(good, pw);
		}
	}

	/**
	 * ���O�̃X�e�b�v�̎w�菤�i�̉��i���X�g���o�̓X�g���[���֏����o���D "date/session,goodA:offset,10,11,12,..."
	 * 
	 * @param good
	 *            ���i��
	 * @param pw
	 *            �o�̓X�g���[��
	 * @throws IOException
	 */
	public void writeLatestPriceInfo(String good, PrintWriter pw) throws IOException {
		if (fCurrentPtr < 1) {
			return;
		}
		WPriceInfo pi = fPriceInfoArray.get(fCurrentPtr - 1);
		pw.printf("%d/%d,%s:%d,%s\n", pi.getDate(), pi.getSession(), good, pi.getOffset(),
			pi.csv(good));
	}

	/**
	 * ��艿�i�\�Ɩ�萔�ʕ\��ǉ�����
	 * 
	 * @param price
	 */
	public void addPriceInfo(int date, int session, int offset, WOutcomeTable totalPriceTable,
		WOutcomeTable totalVolumeTable) {
		WPriceInfo pi = fPriceInfoArray.elementAt(fCurrentPtr);
		pi.setDate(date);
		pi.setSession(session);
		pi.setTotalPriceTable(totalPriceTable, offset);
		pi.setTotalVolumeTable(totalVolumeTable, offset);
	}

	/**
	 * �|�C���^�� 1 �X�e�b�v�i�߂�
	 * 
	 */
	public void incrementPointer() {
		if (fCurrentPtr > fMaxSteps) {
			System.err.println("Error: initializerPtr error in WPriceInfoDB");
			System.exit(-1);
		}
		++fCurrentPtr;
		// ++fSession;
		// if (fSession > fSessionsPerDay) {
		// ++fDate;
		// fSession = 1;
		// }
	}

}
