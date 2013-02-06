package org.umart.cmdServer;

import java.util.*;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


/**
 * USCTodaysQuotesクラスは，サーバー上においてUAgentにより実行される
 * 当日価格照会コマンドである．
 * @author 川部 祐司
 */
public class USCTodaysQuotes extends UCTodaysQuotesCore implements IServerCmd {

  /** このコマンドを実行したエージェント */
  private UAgentForNetworkClient fAgent;

  /** サーバーへの参照 */
  private UMartNetwork fUMart;

  /**
   * Constructor for USCTodaysQuotes.
   */
  public USCTodaysQuotes() {
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
      fQuotesArray.clear();
      fStatus =
          fUMart.doTodaysQuotes(fQuotesArray, fBrandName, fNoOfBoards);
      if (fStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        Iterator itr = fQuotesArray.iterator();
        while (itr.hasNext()) {
          HashMap hm = (HashMap) itr.next();
          fAgent.sendMessage(hm.get(STRING_BRAND_NAME).toString() + " "
                             + hm.get(INT_DATE).toString() + " "
                             + hm.get(INT_BOARD_NO).toString() + " "
                             //+ hm.get(STRING_TIME).toString() + " "
                             + hm.get(LONG_PRICE).toString() + " "
                             + hm.get(LONG_VOLUME).toString() + " "
                             + hm.get(LONG_ASKED_QUOTATION).toString() + " "
                             + hm.get(LONG_BID_QUOTATION).toString());
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
