package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;

/**
 * WCCMarketStatusSAクラスは, スタンドアロン版クライアントにおいて実行される MarketStatusコマンドである.
 * 
 * @author 川部 祐司
 */
@SuppressWarnings("unused")
public class WCCMarketStatusSA extends WCMarketStatusCore implements IClientCmdSA {

	/** サーバーへの参照 */
	private WMart fWMart;

	/** ユーザーID */
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
