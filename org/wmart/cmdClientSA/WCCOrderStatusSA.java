package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;

/**
 * WCCOrderStatusSAクラスは, スタンドアロン版クライアントにおいて実行される OrderStatusコマンドである.
 * 
 * @author 川部 祐司
 * @author Ikki Fujiwara, NII
 */
public class WCCOrderStatusSA extends WCOrderStatusCore implements IClientCmdSA {

	/** サーバーへの参照 */
	private WMart fWMart;

	/** ユーザーID */
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
