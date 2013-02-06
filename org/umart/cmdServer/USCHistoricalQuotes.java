package org.umart.cmdServer;

import java.util.*;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


/**
 * USCHistoricalQuotesクラスは，サーバー上においてUAgentにより実行される
 * 過去の価格照会コマンドである．
 * @author 川部 祐司
 */
public class USCHistoricalQuotes extends UCHistoricalQuotesCore implements
    IServerCmd {

  /** このコマンドを実行したエージェント */
  private UAgentForNetworkClient fAgent;

  /** サーバーへの参照 */
  private UMartNetwork fUMart;

  /**
   * Constructor for USCHistoricalQuotes.
   */
  public USCHistoricalQuotes() {
    super();
    fAgent = null;
    fUMart = null;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      fQuotesArray.clear();
      fStatus =
          fUMart.doHistoricalQuotes(fQuotesArray, fBrandName, fNoOfDays);
      if (fStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        Iterator itr = fQuotesArray.iterator();
        while (itr.hasNext()) {
          HashMap hm = (HashMap) itr.next();
          fAgent.sendMessage(
              hm.get(STRING_BRAND_NAME).toString()
              + " "
              + hm.get(INT_DATE).toString()
              + " "
              + hm.get(LONG_START_PRICE).toString()
              + " "
              + hm.get(LONG_HIGHEST_PRICE).toString()
              + " "
              + hm.get(LONG_LOWEST_PRICE).toString()
              + " "
              + hm.get(LONG_END_PRICE).toString()
              + " "
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
