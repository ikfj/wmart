package org.wmart.cmdClientSA;

import java.util.*;

import org.wmart.cmdCore.*;
import org.wmart.core.*;

/**
 * 取引所ローカルで動作するクライアント用のプロトコルクラス
 * 
 */
public class WProtocolForLocalClient extends WProtocolCore {

	/**
	 * コンストラクタ
	 * 
	 */
	public WProtocolForLocalClient() {
		super();
		fCommandHash.put(WCBalancesCore.CMD_NAME, new WCCBalancesSA());
		fCommandHash.put(WCExecutionsCore.CMD_NAME, new WCCExecutionsSA());
		fCommandHash.put(WCMarketPriceCore.CMD_NAME, new WCCMarketPriceSA());
		fCommandHash.put(WCMarketStatusCore.CMD_NAME, new WCCMarketStatusSA());
		fCommandHash.put(WCMemberProfileCore.CMD_NAME, new WCCMemberProfileSA());
		fCommandHash.put(WCOrderRequestCore.CMD_NAME, new WCCOrderRequestSA());
		fCommandHash.put(WCOrderStatusCore.CMD_NAME, new WCCOrderStatusSA());
		fCommandHash.put(WCPositionCore.CMD_NAME, new WCCPositionSA());
		fCommandHash.put(WCServerDateCore.CMD_NAME, new WCCServerDateSA());
		fCommandHash.put(WCServerStatusCore.CMD_NAME, new WCCServerStatusSA());
		fCommandHash.put(WCServerTimeCore.CMD_NAME, new WCCServerTimeSA());

		// fCommandHash.put(UCOrderCancelCore.CMD_NAME, new UCCOrderCancelSA());
		// fCommandHash.put(UCOrderCheckCore.CMD_NAME, new UCCOrderCheckSA());
		// fCommandHash.put(UCOrderIDHistoryCore.CMD_NAME, new UCCOrderIDHistorySA());
		// fCommandHash.put(UCTodaysQuotesCore.CMD_NAME, new UCCTodaysQuotesSA());
		// fCommandHash.put(UCBoardInformationCore.CMD_NAME, new UCCBoardInformationSA());
		// fCommandHash.put(UCHistoricalQuotesCore.CMD_NAME, new UCCHistoricalQuotesSA());
		// fCommandHash.put(UCScheduleCore.CMD_NAME, new UCCScheduleSA());
		// fCommandHash.put(UCAllBalancesCore.CMD_NAME, new UCCAllBalancesSA());
		// fCommandHash.put(UCAllPositionsCore.CMD_NAME, new UCCAllPositionsSA());
		// fCommandHash.put(UCBoardDataCore.CMD_NAME, new UCCBoardDataSA());
		// fCommandHash.put(UCSpotIntervalCore.CMD_NAME, new UCCSpotIntervalSA());
		// fCommandHash.put(UCSetSpotDateCore.CMD_NAME, new UCCSetSpotDateSA());
		// fCommandHash.put(UCExchangeProfileCore.CMD_NAME, new UCCExchangeProfileSA());
		// fCommandHash.put(UCAccountHistoryCore.CMD_NAME, new UCCAccountHistorySA());
	}

	/**
	 * 取引所への通信経路を確保する．
	 * 
	 * @param wmart
	 *            取引所
	 * @param userID
	 *            ユーザID
	 */
	public void setConnection(WMart wmart, int userID) {
		Collection c = fCommandHash.values();
		Iterator itr = c.iterator();
		while (itr.hasNext()) {
			IClientCmdSA isc = (IClientCmdSA) itr.next();
			isc.setConnection(wmart, userID);
		}
	}

}
