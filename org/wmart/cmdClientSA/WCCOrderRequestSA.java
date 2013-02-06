package org.wmart.cmdClientSA;

import org.wmart.cmdCore.WCOrderRequestCore;
import org.wmart.cmdCore.WCommandStatus;
import org.wmart.core.WMart;

/**
 * WCCOrderRequestSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s����� OrderRequest�R�}���h�ł���.
 * 
 * @author Ikki Fujiwara
 */
public class WCCOrderRequestSA extends WCOrderRequestCore implements IClientCmdSA {

	/** �T�[�o�[�ւ̎Q�� */
	private WMart fWMart;

	/** ���[�U�[ID */
	private int fUserID;

	/**
	 * Constructor for WCCOrderRequestSA.
	 */
	public WCCOrderRequestSA() {
		super();
		fWMart = null;
		fUserID = -1;
	}

	/**
	 * @see org.wmart.cmdCore.ICommand#doIt()
	 */
	public WCommandStatus doIt() {
		fRequestInfo.clear();
		fStatus = fWMart.doOrderRequest(fRequestInfo, fUserID, fBrandName, fAuctioneerName,
			fSellBuy, fMarketLimit, fPrice, fSpec);
		return fStatus;
	}

	/**
	 * @see org.wmart.cmdClientSA.IClientCmdSA#setConnection(WMart, int)
	 */
	public void setConnection(WMart wmart, int userID) {
		fWMart = wmart;
		fUserID = userID;
	}
}
