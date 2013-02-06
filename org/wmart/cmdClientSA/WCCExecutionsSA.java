package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;

/**
 * WCCExecutionsSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s����� Executions�R�}���h�ł���.
 * 
 * @author �암 �S�i
 * @author Ikki Fujiwara, NII
 */
public class WCCExecutionsSA extends WCExecutionsCore implements IClientCmdSA {

	/** �T�[�o�[�ւ̎Q�� */
	private WMart fWMart;

	/** ���[�U�[ID */
	private int fUserID;

	/**
	 * Constructor for WCCExecutionsSA.
	 */
	public WCCExecutionsSA() {
		super();
		fWMart = null;
		fUserID = -1;
	}

	/**
	 * @see org.wmart.cmdCore.ICommand#doIt()
	 */
	public WCommandStatus doIt() {
		fExecutionsArray.clear();
		fStatus = fWMart.doExecutions(fExecutionsArray, fUserID, fAuctioneerName);
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
