package org.wmart.cmdClientSA;

import org.wmart.cmdCore.WCOrderRequestCore;
import org.wmart.cmdCore.WCommandStatus;
import org.wmart.core.WMart;

/**
 * WCCOrderRequestSAクラスは, スタンドアロン版クライアントにおいて実行される OrderRequestコマンドである.
 * 
 * @author Ikki Fujiwara
 */
public class WCCOrderRequestSA extends WCOrderRequestCore implements IClientCmdSA {

	/** サーバーへの参照 */
	private WMart fWMart;

	/** ユーザーID */
	private int fUserID;

	/**
	 * Constructor for WCCOrderRequestSA.
	 */
	public WCCOrderRequestSA() {
		super();
		fWMart = null;
		fUserID = -1;
	}

	/**
	 * @see org.wmart.cmdCore.ICommand#doIt()
	 */
	public WCommandStatus doIt() {
		fRequestInfo.clear();
		fStatus = fWMart.doOrderRequest(fRequestInfo, fUserID, fBrandName, fAuctioneerName,
			fSellBuy, fMarketLimit, fPrice, fSpec);
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
