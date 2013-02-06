package org.wmart.analyzer;

import java.util.*;

/**
 * �����̃^�C�����C����\���N���X (�r�W���A���C�U�p�A�z��x�[�X)
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WTimelineSellerArray {

	/** ���O */
	private String fName;
	/** �l���X�g */
	private ArrayList<Double> fValues;

	/**
	 * �R���X�g���N�^
	 */
	public WTimelineSellerArray(String username) {
		fName = username;
		fValues = new ArrayList<Double>();
	}

	/**
	 * �����񂩂�㏑���ǂݍ���
	 * 
	 * @param encodedTimeline
	 *            "���i��:�n�[����,������(0),...,������(slots-1),"
	 */
	public void overread(String encodedTimeline) {
		String[] sp1 = encodedTimeline.split(",");
		String[] sp2 = sp1[0].split(":");
		fName = sp2[0];
		int offset = Integer.parseInt(sp2[1]);
		for (int i = 0; i + 1 < sp1.length; i++) {
			setValue(offset + i - 1, Double.valueOf(sp1[i + 1]));
		}
	}

	private void setValue(int index, double value) {
		assert index >= 0;
		while (fValues.size() <= index) {
			fValues.add(0.0);
		}
		fValues.set(index, value);
	}

	/**
	 * ���O���擾���܂��B
	 * 
	 * @return ���O
	 */
	public String getName() {
		return fName;
	}

	/**
	 * �l���X�g���擾���܂��B
	 * 
	 * @return �l���X�g
	 */
	public ArrayList<Double> getValues() {
		return fValues;
	}

	/**
	 * �l�̃C�e���[�^���擾���܂��B
	 * 
	 * @return �l�̃C�e���[�^
	 */
	public Iterator<Double> iterator() {
		return fValues.iterator();
	}

	/**
	 * ����e�X�g�p
	 */
	public static void main(String argv[]) {
		WTimelineSellerArray tl = new WTimelineSellerArray("test");
		tl.overread("A:3,1.0,2.0,3.0,");
		System.out.println(tl.getValues().toString());
	}
}
