package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;

/**
 * WCCMarketPriceSAクラスは, スタンドアロン版クライアントにおいて実行される MarketPriceコマンドである.
 * 
 * @author Ikki Fujiwara
 */
@SuppressWarnings("unused")
public class WCCMarketPriceSA extends WCMarketPriceCore implements IClientCmdSA {

	/** サーバーへの参照 */
	private WMart fWMart;

	/** ユーザーID */
	private int fUserID;

	/**
	 * Constructor for WCCFuturePriceSA.
	 */
	public WCCMarketPriceSA() {
		super();
		fWMart = null;
		fUserID = -1;
	}

	/**
	 * @see org.wmart.cmdCore.ICommand#doIt()
	 */
	public WCommandStatus doIt() {
		fArray.clear();
		fStatus = fWMart.doMarketPrice(fArray, fBrandName, fAuctioneerName, fNoOfSteps);
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
