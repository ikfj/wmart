package org.wmart.cmdClientSA;

import org.umart.serverSA.*;
import org.wmart.cmdCore.*;
import org.wmart.core.*;

/**
 * WCCServerDateSA�N���X�́C�X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s����� ServerDate�R�}���h�ł���D
 * 
 * @author ����@��
 */
@SuppressWarnings("unused")
public class WCCServerDateSA extends WCServerDateCore implements IClientCmdSA {

	/** �T�[�o�[�ւ̎Q�� */
	private WMart fWMart;

	/** ���[�U�[ID */
	private int fUserID;

	/**
	 * Constructor for WCCServerDateSA.
	 */
	public WCCServerDateSA() {
		super();
		fWMart = null;
		fUserID = -1;
	}

	/**
	 * @see org.wmart.cmdCore.ICommand#doIt()
	 */
	public WCommandStatus doIt() {
		fServerDateInfo.clear();
		fStatus = fWMart.doServerDate(fServerDateInfo);
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
