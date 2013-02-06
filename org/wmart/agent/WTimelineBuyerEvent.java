package org.wmart.agent;

import java.util.*;

/**
 * �^�C�����C������1���̔����\���\���N���X
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WTimelineBuyerEvent {

	/** �����[���X�g */
	private ArrayList<WOrderForm> fOrderForms;

	/**
	 * �R���X�g���N�^
	 */
	public WTimelineBuyerEvent() {
		fOrderForms = new ArrayList<WOrderForm>();
	}

	/**
	 * ������ǉ� (1���i���ɕ�����)
	 * 
	 * @param auctioneer
	 *            �s�ꖼ
	 * @param hint
	 *            ���i�ʒP��
	 * @param spec
	 *            ���v����
	 */
	public void addPerGood(String auctioneer, String hint, String spec) {
		String[] sp1 = spec.split(";");
		String[] sp2 = hint.split(";");
		assert (sp1.length == sp2.length) : "Unmatch number of goods in demand file";
		for (int i = 0; i < sp1.length; i++) {
			String sp3[] = sp1[i].split(":");
			String good = sp3[0];
			int volume = Integer.parseInt(sp3[1]);
			int atime = Integer.parseInt(sp3[2]);
			int dtime = Integer.parseInt(sp3[3]);
			int length = Integer.parseInt(sp3[4]);
			double uprice = Double.parseDouble(sp2[i]);
			int price1 = Math.round((float) uprice * volume * length);
			String spec1 = String.format("%s:%d:%d:%d:%d;", good, volume, atime, dtime, length);
			add(auctioneer, price1, spec1);
		}
	}

	/**
	 * ������ǉ�
	 * 
	 * @param auctioneer
	 *            �s�ꖼ
	 * @param price
	 *            ���i
	 * @param spec
	 *            ���v����
	 */
	public void add(String auctioneer, int price, String spec) {
		fOrderForms.add(makeOrderForm(auctioneer, price, spec));
	}

	/**
	 * �������}�[�W
	 * 
	 * @param otherEvent
	 *            �����\��
	 */
	public void addAll(WTimelineBuyerEvent otherEvent) {
		fOrderForms.addAll(otherEvent.getOrderForms());
	}

	/**
	 * �����[�𐶐�
	 */
	private WOrderForm makeOrderForm(String auctioneer, int price, String spec) {
		WOrderForm form = new WOrderForm();
		form.setAuctioneerName(auctioneer);
		form.setBuySell(WOrderForm.BUY);
		form.setOrderPrice(price);
		form.setSpec(spec);
		return form;
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

	/* (�� Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s", fOrderForms);
	}

}
