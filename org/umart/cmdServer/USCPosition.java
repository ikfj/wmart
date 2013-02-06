package org.umart.cmdServer;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


/**
 * USCPositionクラスは，サーバー上においてUAgentにより実行される
 * ポジション照会コマンドである．
 * @author 川部 祐司
 */
public class USCPosition extends UCPositionCore implements IServerCmd {

  /** このコマンドを実行したエージェント */
  private UAgentForNetworkClient fAgent;

  /** サーバーへの参照 */
  private UMartNetwork fUMart;

  /**
   * Constructor for USCPosition.
   */
  public USCPosition() {
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
      fPositionInfo.clear();
      fStatus = fUMart.doPosition(fPositionInfo, userID);
      if (fStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        fAgent.sendMessage(fPositionInfo.get(LONG_TODAY_SELL).toString() + " "
                           + fPositionInfo.get(LONG_TODAY_BUY).toString());
        fAgent.sendMessage(fPositionInfo.get(LONG_YESTERDAY_SELL).toString() +
                           " "
                           + fPositionInfo.get(LONG_YESTERDAY_BUY).toString());
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
