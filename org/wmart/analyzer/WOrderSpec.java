/*
 * WMart
 * Copyright (C) 2009 Ikki Fujiwara, NII <ikki@nii.ac.jp>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or any later version.
 * See the GNU General Public License for more details.
 *
 * This code comes WITHOUT ANY WARRANTY
 */
package org.wmart.analyzer;

/**
 * �����Ɋ܂܂��X�̏��i�������N���X
 * 
 * @author Ikki Fujiwara @ NII
 * 
 */
public class WOrderSpec {

	/** ���i�� */
	private String fName = "";
	/** �������� */
	private int fOrderVolume = 0;
	/** �ő��J�n���� */
	private int fArrivalTime = 0;
	/** �Œx�I������ */
	private int fDeadlineTime = 0;
	/** ���v���� */
	private int fTotalTime = 0;

	// ���l�^�̗v�f�̖��O���X�g
	public static String[] ATTRIBUTES = { "Volume", "ArrivalTime", "DeadlineTime", "TotalTime" };

	/**
	 * �R���X�g���N�^
	 */
	public WOrderSpec() {
		this("", 0, 0, 0, 0);
	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param name
	 * @param orderVolume
	 * @param arrivalTime
	 * @param deadlineTime
	 * @param totalTime
	 */
	public WOrderSpec(String name, int orderVolume, int arrivalTime, int deadlineTime, int totalTime) {
		fName = name;
		fOrderVolume = orderVolume;
		fArrivalTime = arrivalTime;
		fDeadlineTime = deadlineTime;
		fTotalTime = totalTime;
	}

	/**
	 * �R���X�g���N�^ (���ׂ𕶎���Őݒ肷��)
	 * 
	 * @param encodedSpec
	 *            "���i��:��]����:�ő�����:�Œx����:���׎���"
	 */
	public WOrderSpec(String encodedSpec) {
		decode(encodedSpec);
	}

	/**
	 * ���ׂ𕶎���Őݒ肷��
	 * 
	 * @param encodedSpec
	 */
	public void decode(String encodedSpec) {
		String[] specElements = encodedSpec.split(":");
		fName = specElements[0];
		fOrderVolume = Integer.parseInt(specElements[1]);
		fArrivalTime = Integer.parseInt(specElements[2]);
		fDeadlineTime = Integer.parseInt(specElements[3]);
		fTotalTime = Integer.parseInt(specElements[4]);
	}

	/**
	 * ���ׂ𕶎���Ŏ擾����
	 * 
	 * @return "���i��:��]����:�ő�����:�Œx����:���׎���"
	 */
	public String encode() {
		return String.format("%s:%s:%s:%s:%s", fName, fOrderVolume, fArrivalTime, fDeadlineTime,
			fTotalTime);
	}

	/**
	 * �������𕶎���Ŏw�肵�đ����l���擾����
	 * 
	 * @param key
	 *            ������
	 * @return �����l
	 */
	public int get(String key) {
		if (key.equalsIgnoreCase("Volume")) {
			return fOrderVolume;
		} else if (key.equalsIgnoreCase("ArrivalTime")) {
			return fArrivalTime;
		} else if (key.equalsIgnoreCase("DeadlineTime")) {
			return fDeadlineTime;
		} else if (key.equalsIgnoreCase("TotalTime")) {
			return fTotalTime;
		} else {
			throw new IllegalArgumentException(key);
		}
	}

	/**
	 * ������ 1 �X�e�b�v�i�߂� (�ő������ƍŒx���������݂֋߂Â���)
	 * 
	 * @return �ߋ��ɂȂ����� false
	 */
	public boolean nextStep() {
		fArrivalTime--;
		fDeadlineTime--;
		return (fArrivalTime >= 0 && fDeadlineTime >= 0);
	}

	/**
	 * ���i�����擾���܂��B
	 * 
	 * @return ���i��
	 */
	public String getName() {
		return fName;
	}

	/**
	 * �������ʂ��擾���܂��B
	 * 
	 * @return ��������
	 */
	public int getOrderVolume() {
		return fOrderVolume;
	}

	/**
	 * �ő��J�n�������擾���܂��B
	 * 
	 * @return �ő��J�n����
	 */
	public int getArrivalTime() {
		return fArrivalTime;
	}

	/**
	 * �Œx�I���������擾���܂��B
	 * 
	 * @return �Œx�I������
	 */
	public int getDeadlineTime() {
		return fDeadlineTime;
	}

	/**
	 * ���v���Ԃ��擾���܂��B
	 * 
	 * @return ���v����
	 */
	public int getTotalTime() {
		return fTotalTime;
	}

	/**
	 * ���e��������?
	 */
	public boolean equals(WOrderSpec another) {
		return (fName.equals(another.getName()) && fOrderVolume == another.getOrderVolume()
			&& fArrivalTime == another.getArrivalTime()
			&& fDeadlineTime == another.getDeadlineTime() && fTotalTime == another.getTotalTime());
	}

	/*
	 * (�� Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return encode();
	}

	/**
	 * �f�o�b�O�p
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		WOrderSpec g = new WOrderSpec("CPU", 4, 2, 4, 3);
		System.out.println(g.toString());
	}

}
