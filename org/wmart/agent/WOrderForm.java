package org.wmart.agent;

/**
 * �����[�N���X
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WOrderForm {

	/** �����������Ȃ����Ƃ�\���萔 */
	public static final int NONE = 0;
	/** ����������\���萔 */
	public static final int SELL = 1;
	/** ����������\���萔 */
	public static final int BUY = 2;
	/** ���i������������Ă��Ȃ����Ƃ�\���萔 */
	public static final int INVALID_PRICE = -1;
	/** �������ł��邱�Ƃ�\���萔 */
	public static final long INVALID_ORDER_ID = -1;

	/** ����ID */
	private long fOrderId;
	/** �s�ꖼ */
	private String fAuctioneerName;
	/** �����敪 */
	private int fBuySell;
	/** �������� */
	private String fSpec;
	/** �������i */
	private int fOrderPrice;
	/** ��艿�i */
	private double fContractPrice;

	/**
	 * �R���X�g���N�^
	 * 
	 */
	public WOrderForm() {
		fOrderId = INVALID_ORDER_ID;
		fAuctioneerName = "";
		fBuySell = NONE;
		fSpec = "";
		fOrderPrice = INVALID_PRICE;
		fContractPrice = INVALID_PRICE;
	}

	/**
	 * �R�s�[�R���X�g���N�^
	 * 
	 * @param src
	 *            �R�s�[��
	 */
	public WOrderForm(WOrderForm src) {
		fOrderId = src.fOrderId;
		fAuctioneerName = src.fAuctioneerName;
		fBuySell = src.fBuySell;
		fSpec = src.fSpec;
		fOrderPrice = src.fOrderPrice;
		fContractPrice = src.fContractPrice;
	}

	/**
	 * �R�s�[����D
	 * 
	 * @param src
	 *            �R�s�[��
	 * @return �R�s�[��̎������g
	 */
	public WOrderForm copyFrom(WOrderForm src) {
		fOrderId = src.fOrderId;
		fAuctioneerName = src.fAuctioneerName;
		fBuySell = src.fBuySell;
		fSpec = src.fSpec;
		fOrderPrice = src.fOrderPrice;
		fContractPrice = src.fContractPrice;
		return this;
	}

	/**
	 * �����敪�𕶎���ŕԂ��D
	 * 
	 * @return �����敪��\��������
	 */
	public String getBuySellByString() {
		if (fBuySell == WOrderForm.BUY) {
			return "Buy";
		} else if (fBuySell == WOrderForm.SELL) {
			return "Sell";
		} else {
			return "None";
		}
	}

	/**
	 * �����敪��Ԃ��D
	 * 
	 * @return buySell
	 *         �������Ȃ��ꍇ�F(WOrderForm.NONE=0)�C����̏ꍇ�F(WOrderForm.SELL=1)�C�����̏ꍇ�F(WOrderForm.BUY=2)
	 */
	public int getBuySell() {
		return fBuySell;
	}

	/**
	 * �����敪��ݒ肷��D
	 * 
	 * @param buySell
	 *            �����敪(�����������Ȃ�:WOrderForm.NONE(=0), ������:WOrderForm.SELL(=1), ������:WOrderForm.BUY(=2))
	 */
	public void setBuySell(int buySell) {
		fBuySell = buySell;
	}

	/**
	 * ����ID���擾���܂��B
	 * 
	 * @return ����ID
	 */
	public long getOrderId() {
		return fOrderId;
	}

	/**
	 * ����ID��ݒ肵�܂��B
	 * 
	 * @param fOrderId
	 *            ����ID
	 */
	public void setOrderId(long fOrderId) {
		this.fOrderId = fOrderId;
	}

	/**
	 * ����ID���폜����
	 */
	public void removeOrderId() {
		this.fOrderId = INVALID_ORDER_ID;
	}

	/**
	 * �s�ꖼ���擾����
	 * 
	 * @return the auctioneerName
	 */
	public String getAuctioneerName() {
		return fAuctioneerName;
	}

	/**
	 * �s�ꖼ��ݒ肷��
	 * 
	 * @param auctioneerName
	 *            the auctioneerName to set
	 */
	public void setAuctioneerName(String auctioneerName) {
		fAuctioneerName = auctioneerName;
	}

	/**
	 * �������ׂ��擾����
	 * 
	 * @return spec ("���i��:��]����:�ő�����:�Œx����:���׎���;" �̌J��Ԃ�)
	 */
	public String getSpec() {
		return fSpec;
	}

	/**
	 * �������ׂ�ݒ肷��
	 * 
	 * @param spec
	 *            �������� ("���i��:��]����:�ő�����:�Œx����:���׎���;" �̌J��Ԃ�)
	 */
	public void setSpec(String spec) {
		fSpec = spec;
	}

	/**
	 * �������i��Ԃ��D
	 * 
	 * @return price �������i
	 */
	public int getOrderPrice() {
		return fOrderPrice;
	}

	/**
	 * �������i��ݒ肷��D
	 * 
	 * @param price
	 *            �������i
	 */
	public void setOrderPrice(int price) {
		fOrderPrice = price;
	}

	/**
	 * �������i���폜����
	 */
	public void removeOrderPrice() {
		fOrderPrice = INVALID_PRICE;
	}

	/**
	 * ��艿�i���擾���܂��B
	 * 
	 * @return ��艿�i
	 */
	public double getContractPrice() {
		return fContractPrice;
	}

	/**
	 * ��艿�i��ݒ肵�܂��B
	 * 
	 * @param fTotalContractPrice
	 *            ��艿�i
	 */
	public void setContractPrice(double contractPrice) {
		fContractPrice = contractPrice;
	}

	/**
	 * ��艿�i���폜����
	 */
	public void removeContractPrice() {
		fContractPrice = INVALID_PRICE;
	}

	@Override
	public String toString() {
		return String.format("[#%d %s %s %s $%d $%.2f]", fOrderId, fAuctioneerName,
			getBuySellByString(), fSpec, fOrderPrice, fContractPrice);
	}

}
