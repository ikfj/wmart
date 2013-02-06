package org.umart.cmdServer;

import java.util.Collection;
import java.util.Iterator;

import org.umart.cmdCore.UCAccountHistoryCore;
import org.umart.cmdCore.UCAllBalancesCore;
import org.umart.cmdCore.UCAllPositionsCore;
import org.umart.cmdCore.UCBalancesCore;
import org.umart.cmdCore.UCBoardDataCore;
import org.umart.cmdCore.UCBoardInformationCore;
import org.umart.cmdCore.UCExchangeProfileCore;
import org.umart.cmdCore.UCExecutionsCore;
import org.umart.cmdCore.UCFuturePriceCore;
import org.umart.cmdCore.UCHistoricalQuotesCore;
import org.umart.cmdCore.UCLogoutCore;
import org.umart.cmdCore.UCMarketStatusCore;
import org.umart.cmdCore.UCMemberProfileCore;
import org.umart.cmdCore.UCOrderCancelCore;
import org.umart.cmdCore.UCOrderCheckCore;
import org.umart.cmdCore.UCOrderIDHistoryCore;
import org.umart.cmdCore.UCOrderRequestCore;
import org.umart.cmdCore.UCOrderStatusCore;
import org.umart.cmdCore.UCPositionCore;
import org.umart.cmdCore.UCScheduleCore;
import org.umart.cmdCore.UCServerDateCore;
import org.umart.cmdCore.UCServerStatusCore;
import org.umart.cmdCore.UCServerTimeCore;
import org.umart.cmdCore.UCSetSpotDateCore;
import org.umart.cmdCore.UCSpotIntervalCore;
import org.umart.cmdCore.UCSpotPriceCore;
import org.umart.cmdCore.UCTodaysQuotesCore;
import org.umart.cmdCore.UProtocolCore;
import org.umart.serverNet.UAgentForNetworkClient;
import org.umart.serverNet.UMartNetwork;


/**
 * UProtocolForNetClientクラスと対応してサーバ上で動作するSVMPコマンドオブジェクトからなるプロトコルクラスです．
 * @author isao
 *
 */
public class UProtocolForServer extends UProtocolCore {
	
	/**
	 * コンストラクタ
	 *
	 */
	public UProtocolForServer() {
		super();
    fCommandHash.put(UCLogoutCore.CMD_NAME, new USCLogout());
    fCommandHash.put(UCOrderRequestCore.CMD_NAME, new USCOrderRequest());
    fCommandHash.put(UCSpotPriceCore.CMD_NAME, new USCSpotPrice());
    fCommandHash.put(UCFuturePriceCore.CMD_NAME, new USCFuturePrice());
    fCommandHash.put(UCScheduleCore.CMD_NAME, new USCSchedule());
    fCommandHash.put(UCServerDateCore.CMD_NAME, new USCServerDate());
    fCommandHash.put(UCPositionCore.CMD_NAME, new USCPosition());
    fCommandHash.put(UCBalancesCore.CMD_NAME, new USCBalances());
    fCommandHash.put(UCOrderStatusCore.CMD_NAME, new USCOrderStatus());
    fCommandHash.put(UCHistoricalQuotesCore.CMD_NAME, new USCHistoricalQuotes());
    fCommandHash.put(UCTodaysQuotesCore.CMD_NAME, new USCTodaysQuotes());
    fCommandHash.put(UCOrderCancelCore.CMD_NAME, new USCOrderCancel());
    fCommandHash.put(UCExecutionsCore.CMD_NAME, new USCExecutions());
    fCommandHash.put(UCServerTimeCore.CMD_NAME, new USCServerTime());
    fCommandHash.put(UCBoardInformationCore.CMD_NAME, new USCBoardInformation());
    fCommandHash.put(UCMarketStatusCore.CMD_NAME, new USCMarketStatus());
    fCommandHash.put(UCSpotIntervalCore.CMD_NAME, new USCSpotInterval());
    fCommandHash.put(UCBoardDataCore.CMD_NAME, new USCBoardData());
    fCommandHash.put(UCAllBalancesCore.CMD_NAME, new USCAllBalances());
    fCommandHash.put(UCAllPositionsCore.CMD_NAME, new USCAllPositions());
    fCommandHash.put(UCSetSpotDateCore.CMD_NAME, new USCSetSpotDate());
    fCommandHash.put(UCOrderCheckCore.CMD_NAME, new USCOrderCheck());
    fCommandHash.put(UCOrderIDHistoryCore.CMD_NAME, new USCOrderIDHistory());
    fCommandHash.put(UCServerStatusCore.CMD_NAME, new USCServerStatus());
    fCommandHash.put(UCMemberProfileCore.CMD_NAME, new USCMemberProfile());
    fCommandHash.put(UCExchangeProfileCore.CMD_NAME, new USCExchangeProfile());
    fCommandHash.put(UCAccountHistoryCore.CMD_NAME, new USCAccountHistory());
	}
	
  /**
   * 取引所への通信経路を確保する．
   * @param agent エージェント
   * @param umart 取引所
   */
  public void setConnection(UAgentForNetworkClient agent, UMartNetwork umart) {
    Collection c = fCommandHash.values();
    Iterator itr = c.iterator();
    while (itr.hasNext()) {
      IServerCmd isc = (IServerCmd)itr.next();
      isc.setConnection(agent, umart);
    }
  }
	
}
