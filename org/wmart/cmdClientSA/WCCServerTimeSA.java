package org.wmart.cmdClientSA;

import org.umart.serverSA.*;
import org.wmart.cmdCore.*;
import org.wmart.core.*;

/**
 * WCCServerTimeSAクラスは，スタンドアロン版クライアントにおいて実行される ServerTimeコマンドである．
 * 
 * @author 小野　功
 */
@SuppressWarnings("unused")
public class WCCServerTimeSA extends WCServerTimeCore implements IClientCmdSA {

	/** サーバーへの参照 */
	private WMart fWMart;

	/** ユーザーID */
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
