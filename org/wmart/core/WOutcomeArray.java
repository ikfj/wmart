/**
 *
 */
package org.wmart.core;

import java.util.*;

/**
 * ���蒍���̖�薾�ׂ������N���X�B�X���b�g�ԍ��Ŏw�肳��郊�X�g�`���̐��l�f�[�^������
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WOutcomeArray {

	/** �l���Ȃ��Ƃ��̃f�t�H���g�l */
	private double fDefaultValue;
	/** �X���b�g�� */
	private int fSlots;
	/** �X���b�g���Ƃ̃��X�g */
	private ArrayList<Double> fArray = null;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param slots
	 *            �X���b�g��
	 * @param defaultValue
	 *            �f�t�H���g�l
	 */
	public WOutcomeArray(int slots, double defaultValue) {
		fDefaultValue = defaultValue;
		fSlots = slots;
		fArray = new ArrayList<Double>();
		for (int i = 0; i < slots; i++) {
			fArray.add(fDefaultValue);
		}
	}

	/**
	 * �S�X���b�g�̒l�𕶎���Ƃ��Ď擾
	 * 
	 * @return ���l�\ "10:11:12..."
	 */
	public String encode() {
		StringBuilder result = new StringBuilder();
		for (Iterator<Double> itr = fArray.iterator(); itr.hasNext();) {
			result.append(itr.next());
			if (itr.hasNext()) {
				result.append(":");
			}
		}
		return result.toString();
	}

	/**
	 * �S�X���b�g�̒l�� CSV �Ŏ擾
	 * 
	 * @param good
	 * @return
	 */
	public String csv() {
		StringBuilder result = new StringBuilder();
		for (Iterator<Double> itr = fArray.iterator(); itr.hasNext();) {
			result.append(itr.next()).append(",");
		}
		return result.toString();
	}

	/**
	 * �S�X���b�g�̍��v�l���擾
	 */
	public double sum() {
		double sum = 0.0;
		for (Iterator<Double> itr = fArray.iterator(); itr.hasNext();) {
			sum += itr.next();
		}
		return sum;
	}

	/**
	 * �v�f�ɒl�����Z
	 * 
	 * @param slot
	 * @param value
	 */
	public void add(int slot, double value) {
		assert fDefaultValue == 0.0 : "The default value must be 0.0 to add another value.";
		fArray.set(slot, fArray.get(slot) + value);
	}

	/**
	 * �v�f�ɒl��ݒ�
	 * 
	 * @param slot
	 * @param value
	 */
	public void put(int slot, double value) {
		assert (slot > 0 && slot < fArray.size());
		fArray.set(slot, value);
	}

	/**
	 * �v�f�̒l���擾
	 * 
	 * @param slot
	 * @return
	 */
	public double get(int slot) {
		assert (slot > 0 && slot < fArray.size());
		return fArray.get(slot);
	}

	/**
	 * �C�e���[�^���擾
	 */
	public Iterator<Double> iterator() {
		return fArray.iterator();
	}

}
