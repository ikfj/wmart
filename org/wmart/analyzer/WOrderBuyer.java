/**
 *
 */
package org.wmart.analyzer;

import java.util.*;

/**
 * ����������\���N���X (�r�W���A���C�U�p)
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WOrderBuyer {

	/** �������� */
	private TreeMap<String, WOrderSpec> fSpecMap = new TreeMap<String, WOrderSpec>();
	/** �ő����� */
	private int fEarliest = Integer.MAX_VALUE;
	/** �Œx���� */
	private int fLatest = -Integer.MAX_VALUE;
	/** ���ς݃t���O */
	private boolean fContracted = false;

	/** �R���X�g���N�^ */
	public WOrderBuyer() {
	}

	/** �R���X�g���N�^ */
	public WOrderBuyer(boolean contracted) {
		fContracted = contracted;
	}

	/** �R���X�g���N�^ */
	public WOrderBuyer(String encodedSpecs, boolean contracted) {
		setSpec(encodedSpecs);
		fContracted = contracted;
	}

	/**
	 * �������ׂ𕶎���Ŏ擾����
	 * 
	 * @return "���i��:��]����:�ő�����:�Œx����:���׎���;" �̌J��Ԃ� (�\�[�g����Ă���)
	 */
	public String getSpec() {
		StringBuilder encodedSpecs = new StringBuilder();
		for (Iterator<WOrderSpec> itr = fSpecMap.values().iterator(); itr.hasNext();) {
			WOrderSpec spec = itr.next();
			encodedSpecs.append(spec.encode()).append(";");
		}
		return encodedSpecs.toString();
	}

	/**
	 * �������ׂ𕶎���Őݒ肷��
	 * 
	 * @param encodedSpecs
	 */
	public void setSpec(String encodedSpecs) {
		fSpecMap.clear();
		String[] specArray = encodedSpecs.split(";");
		for (int i = 0; i < specArray.length; i++) {
			add(specArray[i]);
		}
	}

	/**
	 * �������ׂ𕶎����1���ǉ�����
	 * 
	 * @param encodedSpec
	 */
	public void add(String encodedSpec) {
		WOrderSpec spec = new WOrderSpec(encodedSpec);
		add(spec);
	}

	/**
	 * �������ׂ�1���ǉ�����
	 * 
	 * @param spec
	 */
	public void add(WOrderSpec spec) {
		fSpecMap.put(spec.encode(), spec);
		if (fEarliest > spec.getArrivalTime()) {
			fEarliest = spec.getArrivalTime();
		}
		if (fLatest < spec.getDeadlineTime()) {
			fLatest = spec.getDeadlineTime();
		}
	}

	/**
	 * �������ׂ̃C�e���[�^���擾����
	 * 
	 * @return
	 */
	public Iterator<WOrderSpec> iterator() {
		return fSpecMap.values().iterator();
	}

	/**
	 * �������ׂ��擾���܂��B
	 * 
	 * @return ��������
	 */
	public TreeMap<String, WOrderSpec> getSpecList() {
		return fSpecMap;
	}

	/**
	 * �ő��������擾���܂��B
	 * 
	 * @return �ő�����
	 */
	public int getEarliest() {
		return fEarliest;
	}

	/**
	 * �ő�������ݒ肵�܂��B
	 * 
	 * @param fEarliest
	 *            �ő�����
	 */
	public void setEarliest(int fEarliest) {
		this.fEarliest = fEarliest;
	}

	/**
	 * �Œx�������擾���܂��B
	 * 
	 * @return �Œx����
	 */
	public int getLatest() {
		return fLatest;
	}

	/**
	 * �Œx������ݒ肵�܂��B
	 * 
	 * @param fLatest
	 *            �Œx����
	 */
	public void setLatest(int fLatest) {
		this.fLatest = fLatest;
	}

	/**
	 * ���ς݃t���O���擾���܂��B
	 * 
	 * @return ���ς݃t���O
	 */
	public boolean isContracted() {
		return fContracted;
	}

	/**
	 * ���ς݃t���O��ݒ肵�܂��B
	 * 
	 * @param fContracted
	 *            ���ς݃t���O
	 */
	public void setContracted(boolean fContracted) {
		this.fContracted = fContracted;
	}

	/**
	 * �������ׂ���v���邩?
	 */
	public boolean equals(WOrderBuyer another) {
		return getSpec().equals(another.getSpec());
	}

	@Override
	public String toString() {
		return String.format("%s%s", getSpec(), fContracted ? "bought" : "buying");
	}

	/**
	 * �f�o�b�O�p
	 */
	public static void main(String argv[]) {
		WOrderBuyer o1 = new WOrderBuyer("D:8:214:221:8;B:8:204:211:8;A:18:212:213:2;", false);
		WOrderBuyer o2 = new WOrderBuyer("A:18:212:213:2;B:8:204:211:8;D:8:214:221:8;", false);
		System.out.println(o1);
		System.out.println(o2);
		System.out.println(o1.equals(o2));
	}
}
