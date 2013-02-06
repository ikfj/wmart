package org.umart.cmdClientSA;

import java.util.*;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * ��������[�J���œ��삷��N���C�A���g�p�̃v���g�R���N���X�ł��D
 * @author ����@��
 *
 */
public class UProtocolForLocalClient extends UProtocolCore {

	/**
	 * �R���X�g���N�^
	 *
	 */
  public UProtocolForLocalClient() {
  	super();
  	fCommandHash.put(UCMarketStatusCore.CMD_NAME, new UCCMarketStatusSA());
    fCommandHash.put(UCServerTimeCore.CMD_NAME, new UCCServerTimeSA());
    fCommandHash.put(UCOrderRequestCore.CMD_NAME, new UCCOrderRequestSA());
    fCommandHash.put(UCOrderCancelCore.CMD_NAME, new UCCOrderCancelSA());
    fCommandHash.put(UCOrderCheckCore.CMD_NAME, new UCCOrderCheckSA());
    fCommandHash.put(UCOrderIDHistoryCore.CMD_NAME, new UCCOrderIDHistorySA());
    fCommandHash.put(UCOrderStatusCore.CMD_NAME, new UCCOrderStatusSA());
    fCommandHash.put(UCExecutionsCore.CMD_NAME, new UCCExecutionsSA());
    fCommandHash.put(UCBalancesCore.CMD_NAME, new UCCBalancesSA());
    fCommandHash.put(UCTodaysQuotesCore.CMD_NAME, new UCCTodaysQuotesSA());
    fCommandHash.put(UCBoardInformationCore.CMD_NAME, new UCCBoardInformationSA());
    fCommandHash.put(UCSpotPriceCore.CMD_NAME, new UCCSpotPriceSA());
    fCommandHash.put(UCFuturePriceCore.CMD_NAME, new UCCFuturePriceSA());
    fCommandHash.put(UCHistoricalQuotesCore.CMD_NAME, new UCCHistoricalQuotesSA());
    fCommandHash.put(UCServerDateCore.CMD_NAME, new UCCServerDateSA());
    fCommandHash.put(UCScheduleCore.CMD_NAME, new UCCScheduleSA());
    fCommandHash.put(UCPositionCore.CMD_NAME, new UCCPositionSA());
    fCommandHash.put(UCAllBalancesCore.CMD_NAME, new UCCAllBalancesSA());
    fCommandHash.put(UCAllPositionsCore.CMD_NAME, new UCCAllPositionsSA());
    fCommandHash.put(UCBoardDataCore.CMD_NAME, new UCCBoardDataSA());
    fCommandHash.put(UCSpotIntervalCore.CMD_NAME, new UCCSpotIntervalSA());
    fCommandHash.put(UCSetSpotDateCore.CMD_NAME, new UCCSetSpotDateSA());
    fCommandHash.put(UCServerStatusCore.CMD_NAME, new UCCServerStatusSA());
    fCommandHash.put(UCMemberProfileCore.CMD_NAME, new UCCMemberProfileSA());
    fCommandHash.put(UCExchangeProfileCore.CMD_NAME, new UCCExchangeProfileSA());
    fCommandHash.put(UCAccountHistoryCore.CMD_NAME, new UCCAccountHistorySA());
  }

  /**
   * ������ւ̒ʐM�o�H���m�ۂ���D
   * @param umart �����
   * @param userID ���[�UID
   */
  public void setConnection(UMart umart, int userID) {
    Collection c = fCommandHash.values();
    Iterator itr = c.iterator();
    while (itr.hasNext()) {
      IClientCmdSA isc = (IClientCmdSA) itr.next();
      isc.setConnection(umart, userID);
    }
  }

}
