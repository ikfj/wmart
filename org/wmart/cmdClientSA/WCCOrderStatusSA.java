package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;

/**
 * WCCOrderStatusSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s����� OrderStatus�R�}���h�ł���.
 * 
 * @author �암 �S�i
 * @author Ikki Fujiwara, NII
 */
public class WCCOrderStatusSA extends WCOrderStatusCore implements IClientCmdSA {

	/** �T�[�o�[�ւ̎Q�� */
	private WMart fWMart;

	/** ���[�U�[ID */
	private int fUserID;

	/**
	 * Constructor for WCCOrderStatusSA.
	 */
	public WCCOrderStatusSA() {
		super();
		fWMart = null;
		fUserID = -1;
	}

	/**
	 * @see org.wmart.cmdCore.ICommand#doIt()
	 */
	public WCommandStatus doIt() {
		fStatusArray.clear();
		fStatus = fWMart.doOrderStatus(fStatusArray, fUserID, fAuctioneerName);
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
