package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCHistoricalQuotesSAクラスは, スタンドアロン版クライアントにおいて
 * 実行されるHistoricalQuotesコマンドである.
 * @author 川部 祐司
 */
public class WCCHistoricalQuotesSA extends WCHistoricalQuotesCore implements
    IClientCmdSA {

  /** サーバーへの参照 */
  private WMart fWMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for WCCHistoricalQuotesSA.
   */
  public WCCHistoricalQuotesSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.cmdCore.ICommand#doIt()
   */
  public WCommandStatus doIt() {
    fQuotesArray.clear();
    fStatus = fWMart.doHistoricalQuotes(fQuotesArray, fBrandName, fNoOfDays);
    return fStatus;
  }

  /**
   * @see org.wmart.cmdClientSA.IClientCmdSA#setConnection(WMart, int)
   */
  public void setConnection(WMart wmart, int userID) {
    fWMart = wmart;
    fUserID = userID;
  }
}
