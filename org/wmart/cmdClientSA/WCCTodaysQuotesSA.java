package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCTodaysQuotesSAクラスは, スタンドアロン版クライアントにおいて実行される
 * TodaysQuotesコマンドである.
 * @author 川部 祐司
 */
public class WCCTodaysQuotesSA extends WCTodaysQuotesCore implements
    IClientCmdSA {

  /** サーバーへの参照 */
  private WMart fWMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for UCCTodaysQuotesSA.
   */
  public WCCTodaysQuotesSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.cmdCore.ICommand#doIt()
   */
  public WCommandStatus doIt() {
    fQuotesArray.clear();
    fStatus = fWMart.doTodaysQuotes(fQuotesArray, fBrandName, fNoOfBoards);
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
