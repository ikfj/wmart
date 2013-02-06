package org.wmart.core;

import java.util.*;

/**
 * ���[�U�[�ЂƂ蕪�̒����W�����Ǘ�����N���X�B�ꕔ�܂��͑S�������ς݂ł��钍���ƁA�S���������ł��钍���ɕ����ĊǗ�����B
 * 
 * @author �암 �T�i
 * @author �X�� �̕�
 * @author ���� ��
 * @author Ikki Fujiwara, NII
 */
public class WOrderArray {

	/** ���[�U�[ID */
	private int fUserID;
	/** �ꕔ�^�S�����ςݒ������X�g */
	private Vector fContractedOrders;
	/** �S������蒍�����X�g */
	private Vector fUncontractedOrders;
	/** �������� */
	private ArrayList fHistory;
	/** ���ςݒ������X�g�̒��Œ��߃Z�b�V�����Ő������������n�܂�C���f�b�N�X */
	private int fIndexOfLatestContractedOrder;

	/**
	 * userID�������[�U�[��WOrderArray���쐬����D
	 * 
	 * @param userID
	 *            ���[�U�[ID
	 */
	public WOrderArray(int userID) {
		fUserID = userID;
		fContractedOrders = new Vector();
		fUncontractedOrders = new Vector();
		fHistory = new ArrayList();
	}

	/**
	 * ������ǉ�����B�ꕔ�܂��͑S�������ς݂Ȃ���ς݃��X�g�ɁA�S���������Ȃ疢��胊�X�g�ɒǉ�����B
	 * 
	 * @param o
	 *            ����
	 */
	public void addOrder(WOrder o) {
		if (o.getContractVolume() > 0) {
			fContractedOrders.addElement(o);
		} else if (o.getContractVolume() == 0) {
			fUncontractedOrders.addElement(o);
		} else {
			System.out.println("Contract volume is under 0");
			System.exit(1);
		}
	}

	/**
	 * ��蕪�̒�����S�Ď�菜���D
	 */
	public void removeAllContractedOrders() {
		fContractedOrders.removeAllElements();
	}

	/**
	 * ����蕪�̒�����S�Ď�菜���D
	 */
	public void removeAllUncontractedOrders() {
		fUncontractedOrders.removeAllElements();
	}

	/**
	 * ���ςݒ������X�g�̒��Œ��߃Z�b�V�����Ő������������n�܂�C���f�b�N�X���擾���܂��B
	 * 
	 * @return ���ςݒ������X�g�̒��Œ��߃Z�b�V�����Ő������������n�܂�C���f�b�N�X
	 */
	public int getIndexOfLatestContractedOrder() {
		return fIndexOfLatestContractedOrder;
	}

	/**
	 * ���ςݒ������X�g�̒��Ŏ��̃Z�b�V�����Ő������镪���n�܂�C���f�b�N�X���L�����܂��B
	 */
	public void resetIndexOfLatestContractedOrder() {
		this.fIndexOfLatestContractedOrder = fContractedOrders.size();
	}

	/**
	 * ��蕪�̒�������Ԃ��D
	 * 
	 * @return ��蕪�̒�����
	 */
	public int getNoOfContractedOrders() {
		return fContractedOrders.size();
	}

	/**
	 * ����蕪�̒�������Ԃ��D
	 * 
	 * @return ����蕪�̒�����
	 */
	public int getNoOfUncontractedOrders() {
		return fUncontractedOrders.size();
	}

	/**
	 * index�Ŏw�肳����蕪�̒�����Ԃ��D
	 * 
	 * @param index
	 *            �Y����
	 * @return ����
	 */
	public WOrder getContractedOrderAt(int index) {
		return (WOrder) fContractedOrders.elementAt(index);
	}

	/**
	 * index�Ŏw�肳��関��蕪�̒�����Ԃ��D
	 * 
	 * @param index
	 *            �Y����
	 * @return ����
	 */
	public WOrder getUncontractedOrderAt(int index) {
		return (WOrder) fUncontractedOrders.elementAt(index);
	}

	/**
	 * ����������Ԃ��D
	 * 
	 * @return ���������̃��X�g
	 */
	public ArrayList getHistory() {
		return fHistory;
	}

	/**
	 * userID��Ԃ�
	 * 
	 * @return ���[�U�[ID
	 */
	public int getUserID() {
		return fUserID;
	}

	/**
	 * ��蕪�̒����W����Ԃ��D
	 * 
	 * @return ��蕪�̒����W��
	 */
	public Vector getContractedOrders() {
		return fContractedOrders;
	}

	/**
	 * ����蕪�̒����W����Ԃ��D
	 * 
	 * @return ����蕪�̒����W��
	 */
	public Vector getUncontractedOrders() {
		return fUncontractedOrders;
	}

	/*
	 * (�� Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("\nWOrderArray(user%s) [\nUncontracted=%s, \nContracted=%s\n]", fUserID,
			fUncontractedOrders, fContractedOrders);
	}

}
