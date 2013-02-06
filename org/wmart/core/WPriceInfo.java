package org.wmart.core;

import java.util.*;

/**
 * ���i�n��f�[�^�x�[�X�Ɋ܂܂��1�X�e�b�v�̉��i���
 *
 * @author Ikki Fujiwara, NII
 *
 */
public class WPriceInfo {

	/** �����ȉ��i */
	public static final long INVALID_PRICE = -1;

	/** �� */
	private int fDate = 0;
	/** �� */
	private int fSession = 0;
	/** �n�[�̃Z�b�V�����ԍ� */
	private int fOffset = 0;
	/** �X���b�g�� */
	private int fSlots = 0;
	/** ���i���ƁE�X���b�g���Ƃ̎s�ꉿ�i (���ϖ��P��) �\ */
	private WOutcomeTable fMarketPriceTable = null;
	/** ���i���ƁE�X���b�g���Ƃ̍��v��艿�i�\ */
	private WOutcomeTable fTotalPriceTable = null;
	/** ���i���ƁE�X���b�g���Ƃ̍��v��萔�ʕ\ */
	private WOutcomeTable fTotalVolumeTable = null;

	/**
	 * �R���X�g���N�^
	 */
	public WPriceInfo(int slots) {
		fSlots = slots;
	}

	/**
	 * �S���i�̑S�X���b�g�̎s�ꉿ�i�\�𕶎���Ŏ擾����
	 *
	 * @return ���i�\ "goodA:offset,10,11,12,...;goodB:offset,21,22,23,...;"
	 */
	public String encode() {
		if (fMarketPriceTable == null) {
			computeMarketPriceTable();
		}
		return fMarketPriceTable.encode();
	}

	/**
	 * �w�菤�i�̑S�X���b�g�̎s�ꉿ�i���X�g�� CSV �Ŏ擾����
	 *
	 * @return ���i���X�g "10,11,12..."
	 */
	public String csv(String good) {
		if (fMarketPriceTable == null) {
			computeMarketPriceTable();
		}
		return fMarketPriceTable.csv(good);
	}

	/**
	 * �w�菤�i�̂���X���b�g�̎s�ꉿ�i���擾����
	 *
	 * @param good
	 * @return
	 */
	public double getPrice(String good, int slot) {
		if (fMarketPriceTable == null) {
			computeMarketPriceTable();
		}
		return fMarketPriceTable.get(good, slot);
	}

	/**
	 * �s�ꉿ�i�\���v�Z����
	 *
	 */
	private void computeMarketPriceTable() {
		assert (fTotalPriceTable != null && fTotalVolumeTable != null) : "TotalPriceTable and TotalVolumeTable are not set!";
		fMarketPriceTable = new WOutcomeTable(fSlots, fOffset, WPriceInfo.INVALID_PRICE);
		for (Iterator<String> itr = fTotalPriceTable.goodsIterator(); itr.hasNext();) {
			String good = itr.next();
			for (int slot = fOffset; slot < fOffset + fSlots; slot++) {
				double value = fTotalPriceTable.get(good, slot) / fTotalVolumeTable.get(good, slot);
				fMarketPriceTable.put(good, slot, value);
			}
		}
	}

	/**
	 * ���i�����X�g���擾����
	 *
	 * @return ���i���𗅗񂵂� ArrayList
	 */
	public ArrayList<String> getGoods() {
		ArrayList<String> goods = new ArrayList<String>();
		if (fTotalPriceTable != null) {
			for (Iterator<String> itr = fTotalPriceTable.goodsIterator(); itr.hasNext();) {
				goods.add(itr.next());
			}
		}
		return goods;
	}

	/**
	 * ���i���ƁE�X���b�g���Ƃ̍��v��艿�i�\��ݒ肵�܂��B
	 *
	 * @param fTotalPriceTable
	 *            ���i���ƁE�X���b�g���Ƃ̍��v��艿�i�\
	 */
	public void setTotalPriceTable(WOutcomeTable totalPriceTable, int offset) {
		if (fOffset <= 0) {
			fOffset = offset;
		}
		assert (fOffset == offset) : "offset differs!";
		fTotalPriceTable = totalPriceTable;
	}

	/**
	 * ���i���ƁE�X���b�g���Ƃ̍��v��萔�ʕ\��ݒ肵�܂��B
	 *
	 * @param fTotalVolumeTable
	 *            ���i���ƁE�X���b�g���Ƃ̍��v��萔�ʕ\
	 */
	public void setTotalVolumeTable(WOutcomeTable totalVolumeTable, int offset) {
		if (fOffset <= 0) {
			fOffset = offset;
		}
		assert (fOffset == offset) : "offset differs!";
		fTotalVolumeTable = totalVolumeTable;
	}

	/**
	 * �߂�Ԃ��D
	 *
	 * @return ��
	 */
	public int getSession() {
		return fSession;
	}

	/**
	 * ����Ԃ��D
	 *
	 * @return ��
	 */
	public int getDate() {
		return fDate;
	}

	/**
	 * �߂�ݒ肷��D
	 *
	 * @param session
	 *            ��
	 */
	public void setSession(int session) {
		fSession = session;
	}

	/**
	 * ����ݒ肷��D
	 *
	 * @param date
	 *            ��
	 */
	public void setDate(int date) {
		fDate = date;
	}

	/**
	 * �n�[�̃Z�b�V�����ԍ����擾���܂��B
	 *
	 * @return �n�[�̃Z�b�V�����ԍ�
	 */
	public int getOffset() {
		return fOffset;
	}

}
