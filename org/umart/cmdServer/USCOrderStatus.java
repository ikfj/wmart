package org.umart.cmdServer;

import java.util.*;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


/**
 * USCOrderStatusクラスは，サーバー上においてUAgentにより実行される
 * 注文照会コマンドである．
 * @author 川部 祐司
 */
public class USCOrderStatus extends UCOrderStatusCore implements IServerCmd {

  /** このコマンドを実行したエージェント */
  private UAgentForNetworkClient fAgent;

  /** サーバーへの参照 */
  private UMartNetwork fUMart;

  /**
   * Constructor for USCOrderStatus.
   */
  public USCOrderStatus() {
    super();
    fAgent = null;
    fUMart = null;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      int userID = fAgent.getLoginStatus().getUserID();
      fStatusArray.clear();
      fStatus = fUMart.doOrderStatus(fStatusArray, userID);
      if (fStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        Iterator itr = fStatusArray.iterator();
        while (itr.hasNext()) {
          HashMap hm = (HashMap) itr.next();
          fAgent.sendMessage(hm.get(LONG_ORDER_ID).toString());
          fAgent.sendMessage(hm.get(STRING_ORDER_TIME).toString());
          fAgent.sendMessage(hm.get(STRING_BRAND_NAME).toString());
          fAgent.sendMessage(hm.get(INT_NEW_REPAY).toString());
          fAgent.sendMessage(hm.get(INT_SELL_BUY).toString());
          fAgent.sendMessage(hm.get(INT_MARKET_LIMIT).toString());
          fAgent.sendMessage(hm.get(LONG_PRICE).toString());
          fAgent.sendMessage(hm.get(LONG_VOLUME).toString());
          fAgent.sendMessage(hm.get(LONG_CONTRACTED_VOLUME).toString());
          fAgent.sendMessage(hm.get(LONG_CANCEL_VOLUME).toString());
        }
      } else {
        fAgent.sendMessage("+ERROR " + fStatus.getErrorCode());
        fAgent.sendMessage(fStatus.getErrorMessage());
      }
    } catch (Exception e) {
      fAgent.sendMessage("+ERROR " + ICommand.INVALID_ARGUMENTS);
      fAgent.sendMessage("USAGE: ORDERSTATUS");
    }
    fAgent.flushMessage();
    return fStatus;
  }

  /**
   * @see org.umart.cmdServer.IServerCmd#setConnection(UAgentForNetworkClient, UMartNetwork)
   */
  public void setConnection(UAgentForNetworkClient agent, UMartNetwork umart) {
    fAgent = agent;
    fUMart = umart;
  }

}
