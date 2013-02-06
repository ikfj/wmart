package org.umart.cmdServer;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


/**
 * USCBalancesクラスは，サーバー上においてUAgentにより実行される
 * 現金残高照会コマンドである．
 * @author 川部 祐司
 */
public class USCBalances extends UCBalancesCore implements IServerCmd {

  /** このコマンドを実行したエージェント */
  private UAgentForNetworkClient fAgent;

  /** サーバーへの参照 */
  private UMartNetwork fUMart;

  /**
   * Constructor for USCBalances.
   */
  public USCBalances() {
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
      fTodayInfo.clear();
      fYesterdayInfo.clear();
      fStatus = fUMart.doBalances(fTodayInfo, fYesterdayInfo, userID);
      if (fStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        fAgent.sendMessage(fTodayInfo.get(LONG_CASH).toString());
        fAgent.sendMessage(fTodayInfo.get(LONG_MARGIN).toString());
        fAgent.sendMessage(fTodayInfo.get(LONG_UNREALIZED_PROFIT).toString());
        fAgent.sendMessage(fTodayInfo.get(LONG_SETTLED_PROFIT).toString());
        fAgent.sendMessage(fTodayInfo.get(LONG_FEE).toString());
        fAgent.sendMessage(fTodayInfo.get(LONG_INTEREST).toString());
        fAgent.sendMessage(fTodayInfo.get(LONG_LOAN).toString());
        fAgent.sendMessage(fTodayInfo.get(LONG_SURPLUS).toString());
        fAgent.sendMessage(fYesterdayInfo.get(LONG_CASH).toString());
        fAgent.sendMessage(fYesterdayInfo.get(LONG_MARGIN).toString());
        fAgent.sendMessage(fYesterdayInfo.get(LONG_UNREALIZED_PROFIT).toString());
        fAgent.sendMessage(fYesterdayInfo.get(LONG_SETTLED_PROFIT).toString());
        fAgent.sendMessage(fYesterdayInfo.get(LONG_FEE).toString());
        fAgent.sendMessage(fYesterdayInfo.get(LONG_INTEREST).toString());
        fAgent.sendMessage(fYesterdayInfo.get(LONG_LOAN).toString());
        fAgent.sendMessage(fYesterdayInfo.get(LONG_SURPLUS).toString());
      } else {
        fAgent.sendMessage("+ERROR " + fStatus.getErrorCode());
        fAgent.sendMessage(fStatus.getErrorMessage());
      }
    } catch (Exception e) {
      fAgent.sendMessage("+ERROR " + ICommand.INVALID_ARGUMENTS);
      fAgent.sendMessage("USAGE: BALANCES");
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
