package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;

/**
 * WCCExecutionsSAクラスは, スタンドアロン版クライアントにおいて実行される Executionsコマンドである.
 * 
 * @author 川部 祐司
 * @author Ikki Fujiwara, NII
 */
public class WCCExecutionsSA extends WCExecutionsCore implements IClientCmdSA {

	/** サーバーへの参照 */
	private WMart fWMart;

	/** ユーザーID */
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
