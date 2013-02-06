package org.umart.cmdServer;

import java.util.*;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


/**
 * USCAllBalancesクラスは，サーバー上においてUAgentにより実行される
 * 全会員の現金残高照会コマンドである．
 * @author 川部 祐司
 */
public class USCAllBalances extends UCAllBalancesCore implements IServerCmd {

  /** このコマンドを実行したエージェント */
  private UAgentForNetworkClient fAgent;

  /** サーバーへの参照 */
  private UMartNetwork fUMart;

  /**
   * Constructor for USCAllBalances.
   */
  public USCAllBalances() {
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
      fAllBalancesArray.clear();
      fStatus = fUMart.doAllBalances(fAllBalancesArray, userID);
      if (fStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        Iterator itr = fAllBalancesArray.iterator();
        while (itr.hasNext()) {
          HashMap hm = (HashMap) itr.next();
          fAgent.sendMessage(hm.get(STRING_NAME).toString());
          fAgent.sendMessage(hm.get(LONG_CASH).toString());
          fAgent.sendMessage(hm.get(LONG_MARGIN).toString());
          fAgent.sendMessage(hm.get(LONG_UNREALIZED_PROFIT).toString());
          fAgent.sendMessage(hm.get(LONG_SETTLED_PROFIT).toString());
          fAgent.sendMessage(hm.get(LONG_FEE).toString());
          fAgent.sendMessage(hm.get(LONG_INTEREST).toString());
          fAgent.sendMessage(hm.get(LONG_LOAN).toString());
          fAgent.sendMessage(hm.get(LONG_SURPLUS).toString());
          fAgent.sendMessage(hm.get(LONG_SELL_POSITION).toString());
          fAgent.sendMessage(hm.get(LONG_BUY_POSITION).toString());
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
