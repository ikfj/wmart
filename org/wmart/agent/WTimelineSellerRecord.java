package org.wmart.agent;

/**
 * �^�C�����C������1���_�ɂ����鋟���󋵂�\���N���X
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WTimelineSellerRecord {

	/** ���v�������� */
	long fTotalOrderVolume;
	/** ���v�������i */
	long fTotalOrderPrice;
	/** ���v��萔�� */
	double fTotalContractVolume;
	/** ���v��艿�i */
	double fTotalContractPrice;
	/** ���v��藘�v (����蕪�͖���) */
	double fTotalContractProfit;

	/**
	 * �R���X�g���N�^
	 */
	public WTimelineSellerRecord() {
		fTotalOrderVolume = 0;
		fTotalOrderPrice = 0;
		fTotalContractVolume = 0.0;
		fTotalContractPrice = 0.0;
		fTotalContractProfit = 0.0;
	}

	/**
	 * ���v�������ʂ��擾
	 * 
	 * @return ���v��������
	 */
	public long getTotalOrderVolume() {
		return fTotalOrderVolume;
	}

	/**
	 * ���v�������ʂ�ݒ�
	 * 
	 * @param fTotalOrderVolume
	 *            ���v��������
	 */
	public void setTotalOrderVolume(long fTotalOrderVolume) {
		this.fTotalOrderVolume = fTotalOrderVolume;
	}

	/**
	 * ���v�������i���擾
	 * 
	 * @return ���v�������i
	 */
	public long getTotalOrderPrice() {
		return fTotalOrderPrice;
	}

	/**
	 * ���v�������i��ݒ�
	 * 
	 * @param fTotalOrderPrice
	 *            ���v�������i
	 */
	public void setTotalOrderPrice(long fTotalOrderPrice) {
		this.fTotalOrderPrice = fTotalOrderPrice;
	}

	/**
	 * ���v��萔�ʂ��擾
	 * 
	 * @return ���v��萔��
	 */
	public double getTotalContractVolume() {
		return fTotalContractVolume;
	}

	/**
	 * ���v��萔�ʂ�ݒ�
	 * 
	 * @param fTotalContractVolume
	 *            ���v��萔��
	 */
	public void setTotalContractVolume(double fTotalContractVolume) {
		this.fTotalContractVolume = fTotalContractVolume;
	}

	/**
	 * ���v��艿�i���擾
	 * 
	 * @return ���v��艿�i
	 */
	public double getTotalContractPrice() {
		return fTotalContractPrice;
	}

	/**
	 * ���v��艿�i��ݒ�
	 * 
	 * @param fTotalContractPrice
	 *            ���v��艿�i
	 */
	public void setTotalContractPrice(double fTotalContractPrice) {
		this.fTotalContractPrice = fTotalContractPrice;
	}

	/**
	 * ���v��藘�v (����蕪�͖���)���擾
	 * 
	 * @return ���v��藘�v (����蕪�͖���)
	 */
	public double getTotalContractProfit() {
		return fTotalContractProfit;
	}

	/**
	 * ���v��藘�v (����蕪�͖���)��ݒ�
	 * 
	 * @param fTotalContractProfit
	 *            ���v��藘�v (����蕪�͖���)
	 */
	public void setTotalContractProfit(double fTotalContractProfit) {
		this.fTotalContractProfit = fTotalContractProfit;
	}
}
