package org.umart.cmdServer;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


/**
 * USCScheduleクラスは，サーバー上においてUAgentにより実行される運用予定問合せコマンドである．
 * @author 小野　功
 */
public class USCSchedule extends UCScheduleCore implements IServerCmd {

  /** このコマンドを実行したエージェント */
  private UAgentForNetworkClient fAgent;

  /** サーバーへの参照 */
  private UMartNetwork fUMart;

  /**
   * Constructor for USCSchedule.
   */
  public USCSchedule() {
    super();
    fAgent = null;
    fUMart = null;
  }

  /**
   * @see org.umart.serverCore.ICCommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      fScheduleInfo.clear();
      fStatus = fUMart.doSchedule(fScheduleInfo);
      if (fStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        fAgent.sendMessage(fScheduleInfo.get(INT_MAX_DAY) + " "
                           + fScheduleInfo.get(INT_NO_OF_BOARDS));
      } else {
        fAgent.sendMessage("+ERROR " + fStatus.getErrorCode());
        fAgent.sendMessage(fStatus.getErrorMessage());
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
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
