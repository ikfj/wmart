package org.wmart.cmdClientSA;

import org.umart.serverSA.*;
import org.wmart.cmdCore.*;
import org.wmart.core.*;

/**
 * WCCServerDateSAクラスは，スタンドアロン版クライアントにおいて実行される ServerDateコマンドである．
 * 
 * @author 小野　功
 */
@SuppressWarnings("unused")
public class WCCServerDateSA extends WCServerDateCore implements IClientCmdSA {

	/** サーバーへの参照 */
	private WMart fWMart;

	/** ユーザーID */
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
