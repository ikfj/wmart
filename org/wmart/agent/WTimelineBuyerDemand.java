package org.wmart.agent;

import java.util.*;

/**
 * �^�C�����C������1���̎��v��\���N���X
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WTimelineBuyerDemand {

	/** �����[���X�g */
	private ArrayList<WOrderForm> fOrderForms;
	/** ���v�������i */
	private int fTotalOrderPrice;

	/**
	 * �R���X�g���N�^
	 */
	public WTimelineBuyerDemand() {
		fOrderForms = new ArrayList<WOrderForm>();
		fTotalOrderPrice = 0;
	}

	/**
	 * ������ǉ�
	 * 
	 * @param form
	 *            �����[
	 */
	public void add(WOrderForm form) {
		fOrderForms.add(form);
	}

	/**
	 * �������}�[�W
	 * 
	 * @param forms
	 *            �����[�̃��X�g
	 */
	public void addAll(ArrayList<WOrderForm> forms) {
		fOrderForms.addAll(forms);
	}

	/**
	 * ���v���������ꂽ��?
	 * 
	 * @return true:���ׂĂ̒��������� false:�ЂƂȏ�̒����������
	 */
	public boolean isFulfilled() {
		for (Iterator<WOrderForm> itr = fOrderForms.iterator(); itr.hasNext();) {
			WOrderForm form = itr.next();
			if (form.getContractPrice() <= 0.0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * ���v�������ʂ��擾
	 * 
	 * @return �������ʂ̍��v�B���i�̎�ނɊ֌W�Ȃ��P�����v����
	 */
	public int getTotalOrderVolume() {
		int sum = 0;
		for (Iterator<WOrderForm> itr = fOrderForms.iterator(); itr.hasNext();) {
			WOrderForm form = itr.next();
			String[] sp1 = form.getSpec().split(";");
			for (int i = 0; i < sp1.length; i++) {
				String[] sp2 = sp1[i].split(":");
				int volume = Integer.valueOf(sp2[1]);
				int length = Integer.valueOf(sp2[4]);
				sum += volume * length;
			}
		}
		return sum;
	}

	/**
	 * ���v��萔�ʂ��擾
	 * 
	 * @return ��萔�ʂ̍��v�B���i�̎�ނɊ֌W�Ȃ��P�����v����
	 */
	public int getTotalContractVolume() {
		int sum = 0;
		for (Iterator<WOrderForm> itr = fOrderForms.iterator(); itr.hasNext();) {
			WOrderForm form = itr.next();
			if (form.getContractPrice() > 0.0) {
				String[] sp1 = form.getSpec().split(";");
				for (int i = 0; i < sp1.length; i++) {
					String[] sp2 = sp1[i].split(":");
					int volume = Integer.valueOf(sp2[1]);
					int length = Integer.valueOf(sp2[4]);
					sum += volume * length;
				}
			}
		}
		return sum;

	}

	/**
	 * ���v��艿�i���擾
	 * 
	 * @return ��艿�i�̍��v�B�����̒����͖�������
	 */
	public double getTotalContractPrice() {
		double sum = 0.0;
		for (Iterator<WOrderForm> itr = fOrderForms.iterator(); itr.hasNext();) {
			WOrderForm form = itr.next();
			if (form.getContractPrice() > 0.0) {
				sum += form.getContractPrice();
			}
		}
		return sum;
	}

	/**
	 * ���v��藘�����擾
	 * 
	 * @return ��藘���̍��v�B�����̒����͖�������
	 */
	public double getTotalContractProfit() {
		double sum = 0.0;
		for (Iterator<WOrderForm> itr = fOrderForms.iterator(); itr.hasNext();) {
			WOrderForm form = itr.next();
			if (form.getContractPrice() > 0.0) {
				sum += (double) form.getOrderPrice() - form.getContractPrice();
			}
		}
		return sum;
	}

	/**
	 * �����[�̃C�e���[�^���擾
	 * 
	 * @return �����[�̃C�e���[�^
	 */
	public Iterator<WOrderForm> iterator() {
		return fOrderForms.iterator();
	}

	/**
	 * �����[���X�g���擾���܂��B
	 * 
	 * @return �����[���X�g
	 */
	public ArrayList<WOrderForm> getOrderForms() {
		return fOrderForms;
	}

	/**
	 * ���v�������i���擾���܂��B
	 * 
	 * @return ���v�������i
	 */
	public int getTotalOrderPrice() {
		return fTotalOrderPrice;
	}

	/**
	 * ���v�������i��ݒ肵�܂��B
	 * 
	 * @param totalOrderPrice
	 *            ���v�������i
	 */
	public void setTotalOrderPrice(int totalOrderPrice) {
		this.fTotalOrderPrice = totalOrderPrice;
	}

}
