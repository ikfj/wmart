package org.wmart.core;

import java.util.*;

/**
 * WOrder�I�u�W�F�N�g�������������� �\�[�g���邽�߂ɗ��p����Comparator�ł��D
 * 
 * @author �암 �T�i
 * @author �X�� �̕�
 * @author ���� ��
 */
public class WOrderComparator implements Comparator {

	/**
	 * �����`��(���s����C�w�l�C���s�����̏�)�C���i(����)�C ���蔃��(����C�����̏�)�C�񂹊���(���蒍���͏����C���������� �~��), �񂹊��Ԃ����������Ɋւ��Ă̓����_���Ɏ����������Œ����� �\�[�g���邽�߂ɗ��p�����D
	 * 
	 * @param o1
	 *            ����1
	 * @param o2
	 *            ����2
	 * @return ����o1 &lt; o2�Ȃ��-1, o1 &gt; o2�Ȃ��+1
	 */
	public int compare(Object o1, Object o2) {
		WOrder u1 = (WOrder) o1;
		WOrder u2 = (WOrder) o2;
		int u1M = getMarketLimitPriority(u1);
		int u2M = getMarketLimitPriority(u2);
		if (u1M < u2M) {
			return -1;
		} else if (u1M > u2M) {
			return 1;
		}
		if (u1.getOrderPrice() < u2.getOrderPrice()) {
			return -1;
		} else if (u1.getOrderPrice() > u2.getOrderPrice()) {
			return 1;
		}
		if (u1.getSellBuy() < u2.getSellBuy()) {
			return -1;
		} else if (u1.getSellBuy() > u2.getSellBuy()) {
			return 1;
		}
		if (u1.getSellBuy() == WOrder.SELL) {
			if (u1.getSession() < u2.getSession()) {
				return -1;
			} else if (u1.getSession() > u2.getSession()) {
				return 1;
			}
		} else {
			if (u1.getSession() < u2.getSession()) {
				return 1;
			} else if (u1.getSession() > u2.getSession()) {
				return -1;
			}
		}
		if (u1.getRandomNumber() < u2.getRandomNumber()) {
			return -1;
		} else {
			return 1;
		}
	}

	/**
	 * �����`���ɂ��D�揇�ʂ�Ԃ��D
	 * 
	 * @param o
	 *            ����
	 * @return �����u���s����v�Ȃ��0, �u�w�l�v�Ȃ��1, �u���s�����v�Ȃ��2
	 */
	private final int getMarketLimitPriority(WOrder o) {
		if (o.getMarketLimit() == WOrder.MARKET) {
			if (o.getSellBuy() == WOrder.SELL) {
				return 0;
			} else {
				return 2;
			}
		} else {
			return 1;
		}
	}

}
