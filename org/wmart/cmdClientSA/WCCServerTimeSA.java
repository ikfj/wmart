package org.wmart.cmdClientSA;

import org.umart.serverSA.*;
import org.wmart.cmdCore.*;
import org.wmart.core.*;

/**
 * WCCServerTimeSA�N���X�́C�X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s����� ServerTime�R�}���h�ł���D
 * 
 * @author ����@��
 */
@SuppressWarnings("unused")
public class WCCServerTimeSA extends WCServerTimeCore implements IClientCmdSA {

	/** �T�[�o�[�ւ̎Q�� */
	private WMart fWMart;

	/** ���[�U�[ID */
	private int fUserID;

	/**
	 * Constructor for WCCServerTimeSA.
	 */
	public WCCServerTimeSA() {
		super();
		fWMart = null;
		fUserID = -1;
	}

	/**
	 * @see org.wmart.cmdCore.ICommand#doIt()
	 */
	public WCommandStatus doIt() {
		fServerTimeInfo.clear();
		fStatus = fWMart.doServerTime(fServerTimeInfo);
		return fStatus;
	}

	/**
	 * @see org.wmart.cmdClientSA.IClientCmdSA#setConnection(UMartStandAlone, int)
	 */
	public void setConnection(WMart wmart, int userID) {
		fWMart = wmart;
		fUserID = userID;
	}

}
