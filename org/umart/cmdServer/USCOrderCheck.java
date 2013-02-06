/*
 * Created on 2003/06/10
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.umart.cmdServer;

import java.util.*;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


/**
 * @author isao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class USCOrderCheck extends UCOrderCheckCore implements IServerCmd {

  /** このコマンドを実行したエージェント */
  private UAgentForNetworkClient fAgent;

  /** サーバーへの参照 */
  private UMartNetwork fUMart;

  /**
   * コンストラクタ
   */
  public USCOrderCheck() {
    super();
    fAgent = null;
    fUMart = null;
  }

  /* (non-Javadoc)
   * @see org.umart.cmdServer.IServerCmd#setConnection(org.umart.serverNet.UAgent, org.umart.serverNet.UMartNetwork)
   */
  public void setConnection(UAgentForNetworkClient agent, UMartNetwork umart) {
    fAgent = agent;
    fUMart = umart;
  }

  /* (non-Javadoc)
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      int userID = fAgent.getLoginStatus().getUserID();
      fData.clear();
      fCommandStatus = fUMart.doOrderCheck(fOrderID, fData);
      if (fCommandStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        fAgent.sendMessage(fData.get(UCOrderCheckCore.INT_USER_ID).toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.LONG_ORDER_ID).toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.STRING_ORDER_TIME).
                           toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.STRING_BRAND_NAME).
                           toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.INT_ORDER_DATE).toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.INT_ORDER_BOARD_NO).
                           toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.INT_NEW_REPAY).toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.INT_SELL_BUY).toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.INT_MARKET_LIMIT).
                           toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.LONG_ORDER_PRICE).
                           toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.LONG_ORDER_VOLUME).
                           toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.
                                     LONG_SUM_OF_CONTRACT_VOLUME).toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.LONG_CANCEL_VOLUME).
                           toString());
        ArrayList contractInfoArray = (ArrayList) fData.get(UCOrderCheckCore.
            ARRAYLIST_CONTRACT_INFORMATION_ARRAY);
        Iterator itr = contractInfoArray.iterator();
        while (itr.hasNext()) {
          HashMap contractInfo = (HashMap) itr.next();
          fAgent.sendMessage(contractInfo.get(UCOrderCheckCore.LONG_CONTRACT_ID).
                             toString());
          fAgent.sendMessage(contractInfo.get(UCOrderCheckCore.
                                              STRING_CONTRACT_TIME).toString());
          fAgent.sendMessage(contractInfo.get(UCOrderCheckCore.
                                              INT_CONTRACT_DATE).toString());
          fAgent.sendMessage(contractInfo.get(UCOrderCheckCore.
                                              INT_CONTRACT_BOARD_NO).toString());
          fAgent.sendMessage(contractInfo.get(UCOrderCheckCore.
                                              LONG_CONTRACT_PRICE).toString());
          fAgent.sendMessage(contractInfo.get(UCOrderCheckCore.
                                              LONG_CONTRACT_VOLUME).toString());
        }
      } else {
        fAgent.sendMessage("+ERROR " + fCommandStatus.getErrorCode());
        fAgent.sendMessage(fCommandStatus.getErrorMessage());
      }
    } catch (Exception e) {
      fAgent.sendMessage("+ERROR " + ICommand.INVALID_ARGUMENTS);
      fAgent.sendMessage("USAGE: ORDERCANCEL <ORDERID>");
    }
    fAgent.flushMessage();
    return fCommandStatus;
  }

}
