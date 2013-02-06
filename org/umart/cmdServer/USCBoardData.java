package org.umart.cmdServer;

import java.util.*;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


/**
 * USCBoardDataクラスは，サーバー上においてUAgentにより実行される
 * 板情報を取得するためのコマンドである．
 * @author 川部 祐司
 */
public class USCBoardData extends UCBoardDataCore implements IServerCmd {

  /** このコマンドを実行したエージェント */
  private UAgentForNetworkClient fAgent;

  /** サーバーへの参照 */
  private UMartNetwork fUMart;

  /**
   * Constructor for USCBoardData.
   */
  public USCBoardData() {
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
      fBoardDataArray.clear();
      fBoardDataInfo.clear();
      fStatus = fUMart.doBoardData(fBoardDataInfo, fBoardDataArray);
      if (fStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        fAgent.sendMessage(fBoardDataInfo.get(LONG_TOTAL_BUY_VOLUME).toString() + " "
                           + fBoardDataInfo.get(LONG_MIN_PRICE).toString() + " "
                           + fBoardDataInfo.get(LONG_MAX_PRICE).toString() + " "
                           + fBoardDataInfo.get(LONG_CONTRACT_PRICE).toString() + " "
                           + fBoardDataInfo.get(LONG_CONTRACT_VOLUME).toString());
        Iterator itr = fBoardDataArray.iterator();
        while (itr.hasNext()) {
          HashMap hm = (HashMap) itr.next();
          fAgent.sendMessage(hm.get(LONG_PRICE).toString() + " "
                             + hm.get(STRING_SELL_BUY).toString() + " "
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
