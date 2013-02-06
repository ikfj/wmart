package org.umart.cmdServer;

import java.util.*;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


/**
 * サーバー上においてUAgentにより実行される板情報を取得するためのSVMPコマンドクラスです．
 * @author 川部 祐司
 */
public class USCBoardInformation extends UCBoardInformationCore implements IServerCmd {

  /** このコマンドを実行したエージェント */
  private UAgentForNetworkClient fAgent;

  /** サーバーへの参照 */
  private UMartNetwork fUMart;

  /**
   * コンストラクタ
   */
  public USCBoardInformation() {
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
      fBoardInfo.clear();
      fBoardInfo.clear();
      fStatus = fUMart.doBoardInformation(fBoardInfo);
      if (fStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        fAgent.sendMessage(fBoardInfo.get(STRING_LAST_UPDATE_TIME).toString());
        ArrayList infoArray = (ArrayList) fBoardInfo.get(UCBoardInformationCore.
            ARRAYLIST_BOARD);
        // 以下，成行注文情報
        {
          Iterator itr = infoArray.iterator();
          while (itr.hasNext()) {
            HashMap hm = (HashMap) itr.next();
            if ( ( (Long) hm.get(LONG_PRICE)).longValue() < 0) {
              fAgent.sendMessage(hm.get(LONG_SELL_VOLUME).toString() + " "
                                 + hm.get(LONG_BUY_VOLUME).toString());
              break;
            }
          }
        }
        // 以下，成行注文情報以外
        {
          Iterator itr = infoArray.iterator();
          while (itr.hasNext()) {
            HashMap hm = (HashMap) itr.next();
            if ( ( (Long) hm.get(LONG_PRICE)).longValue() >= 0) {
              fAgent.sendMessage(hm.get(LONG_PRICE).toString() + " "
                                 + hm.get(LONG_SELL_VOLUME).toString() + " "
                                 + hm.get(LONG_BUY_VOLUME).toString());
            }
          }
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
