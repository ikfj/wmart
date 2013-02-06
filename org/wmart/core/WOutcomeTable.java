/**
 *
 */
package org.wmart.core;

import java.util.*;
import java.util.Map.Entry;

/**
 * ��薾�ׂ������N���X�B���i���~�X���b�g�ԍ��Ŏw�肳���\�`���̐��l�f�[�^������
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WOutcomeTable {

	/** �l���Ȃ��Ƃ��̃f�t�H���g�l */
	private double fDefaultValue;
	/** �X���b�g�� */
	private int fSlots;
	/** �n�[�̃X���b�g�ԍ� */
	private int fOffset;
	/** ���i���ƁE�X���b�g���Ƃ̕\ */
	private TreeMap<String, double[]> fTable = null;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param slots
	 *            �X���b�g��
	 * @param offset
	 *            �n�[�̃X���b�g�ԍ�
	 * @param defaultValue
	 *            �f�t�H���g�l
	 */
	public WOutcomeTable(int slots, int offset, double defaultValue) {
		fDefaultValue = defaultValue;
		fSlots = slots;
		fOffset = offset;
		fTable = new TreeMap<String, double[]>();
	}

	/**
	 * �S�f�[�^�𕶎���Ƃ��Ď擾
	 * 
	 * @return ���l�\ "goodA:offset,10,11,12,...;goodB:offset,21,22,23,...;"
	 */
	public String encode() {
		StringBuilder result = new StringBuilder();
		for (Iterator<Entry<String, double[]>> itr = fTable.entrySet().iterator(); itr.hasNext();) {
			Entry<String, double[]> entry = itr.next();
			String key = entry.getKey();
			double[] a = entry.getValue();
			result.append(key).append(":");
			result.append(fOffset);
			for (int i = 0; i < a.length; i++) {
				result.append(",").append(a[i]);
			}
			result.append(";");
		}
		return result.toString();
	}

	/**
	 * �w�菤�i�̑S�X���b�g�̃f�[�^�� CSV �Ŏ擾
	 * 
	 * @param good
	 * @return ���l���X�g "10,11,12,...,"
	 */
	public String csv(String good) {
		StringBuilder result = new StringBuilder();
		double[] a = fTable.get(good);
		for (int i = 0; i < a.length; i++) {
			result.append(a[i]);
			if (i + 1 < a.length) {
				result.append(",");
			}
		}
		return result.toString();
	}

	/**
	 * �w�菤�i�̑S�X���b�g�̍��v�l���擾
	 */
	public double sum(String good) {
		double sum = 0.0;
		double[] a = fTable.get(good);
		for (int i = 0; i < a.length; i++) {
			sum += a[i];
		}
		return sum;
	}

	/**
	 * �S���i�E�S�X���b�g�̍��v�l���擾
	 */
	public double sum() {
		double sum = 0.0;
		for (Iterator<double[]> itr = fTable.values().iterator(); itr.hasNext();) {
			double[] a = itr.next();
			for (int i = 0; i < a.length; i++) {
				sum += a[i];
			}
		}
		return sum;
	}

	/**
	 * �Z���ɒl�����Z
	 * 
	 * @param good
	 * @param slot
	 * @param value
	 */
	public void add(String good, int slot, double value) {
		assert fDefaultValue == 0.0 : "The default value must be 0.0 to add another value.";
		if (!fTable.containsKey(good)) {
			double[] a = new double[fSlots];
			for (int i = 0; i < a.length; i++) {
				a[i] = fDefaultValue;
			}
			fTable.put(good, a);
		}
		fTable.get(good)[slot - fOffset] += value;
	}

	/**
	 * �Z���ɒl��ݒ�
	 * 
	 * @param good
	 * @param slot
	 * @param value
	 */
	public void put(String good, int slot, double value) {
		if (!fTable.containsKey(good)) {
			double[] a = new double[fSlots];
			for (int i = 0; i < a.length; i++) {
				a[i] = fDefaultValue;
			}
			fTable.put(good, a);
		}
		fTable.get(good)[slot - fOffset] = value;
	}

	/**
	 * �Z���̒l���擾
	 * 
	 * @param good
	 * @param slot
	 * @return
	 */
	public double get(String good, int slot) {
		if (!fTable.containsKey(good)) {
			return fDefaultValue;
		}
		return fTable.get(good)[slot - fOffset];
	}

	/**
	 * �Z���̒l���擾
	 * 
	 * @param good
	 * @param slot
	 * @param defaultValue
	 * @return
	 */
	public double get(String good, int slot, double defaultValue) {
		if (!fTable.containsKey(good)) {
			return defaultValue;
		}
		return fTable.get(good)[slot - fOffset];
	}

	/**
	 * ���i���̃C�e���[�^���擾
	 */
	public Iterator<String> goodsIterator() {
		return fTable.keySet().iterator();
	}

}
