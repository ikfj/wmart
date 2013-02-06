package org.umart.cmdServer;

import java.util.*;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


/**
 * USCExecutionsクラスは，サーバー上においてUAgentにより実行される
 * 約定照会コマンドである．
 * @author 川部 祐司
 */
public class USCExecutions extends UCExecutionsCore implements IServerCmd {

  /** このコマンドを実行したエージェント */
  private UAgentForNetworkClient fAgent;

  /** サーバーへの参照 */
  private UMartNetwork fUMart;

  /**
   * Constructor for USCExecutions.
   */
  public USCExecutions() {
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
      fExecutionsArray.clear();
      fStatus = fUMart.doExecutions(fExecutionsArray, userID);
      if (fStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        Iterator itr = fExecutionsArray.iterator();
        while (itr.hasNext()) {
          HashMap hm = (HashMap) itr.next();
          fAgent.sendMessage(hm.get(LONG_CONTRACT_ID).toString() + " "
                             + hm.get(STRING_CONTRACT_TIME).toString() + " "
                             + hm.get(LONG_ORDER_ID).toString() + " "
                             + hm.get(STRING_BRAND_NAME).toString() + " "
                             + hm.get(INT_NEW_REPAY).toString() + " "
                             + hm.get(INT_SELL_BUY).toString() + " "
                             + hm.get(LONG_PRICE).toString() + " "
                             + hm.get(LONG_VOLUME).toString());
        }
      } else {
        fAgent.sendMessage("+ERROR " + fStatus.getErrorCode());
        fAgent.sendMessage(fStatus.getErrorMessage());
      }
    } catch (Exception e) {
      fAgent.sendMessage("+ERROR " + ICommand.INVALID_ARGUMENTS);
      fAgent.sendMessage("USAGE: ORDERCANCEL <ORDERID>");
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
