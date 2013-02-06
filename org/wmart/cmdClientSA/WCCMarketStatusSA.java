package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;

/**
 * WCCMarketStatusSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s����� MarketStatus�R�}���h�ł���.
 * 
 * @author �암 �S�i
 */
@SuppressWarnings("unused")
public class WCCMarketStatusSA extends WCMarketStatusCore implements IClientCmdSA {

	/** �T�[�o�[�ւ̎Q�� */
	private WMart fWMart;

	/** ���[�U�[ID */
	private int fUserID;

	/**
	 * Constructor for WCCMarketStatusSA.
	 */
	public WCCMarketStatusSA() {
		super();
		fWMart = null;
		fUserID = -1;
	}

	/**
	 * @see org.wmart.cmdCore.ICommand#doIt()
	 */
	public WCommandStatus doIt() {
		fMarketInfo.clear();
		fStatus = fWMart.doMarketStatus(fMarketInfo, fAuctioneerName);
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
