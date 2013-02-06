package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCTodaysQuotesSAクラスは, スタンドアロン版クライアントにおいて実行される
 * TodaysQuotesコマンドである.
 * @author 川部 祐司
 */
public class UCCTodaysQuotesSA extends UCTodaysQuotesCore implements
    IClientCmdSA {

  /** サーバーへの参照 */
  private UMart fUMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for UCCTodaysQuotesSA.
   */
  public UCCTodaysQuotesSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fQuotesArray.clear();
    fStatus = fUMart.doTodaysQuotes(fQuotesArray, fBrandName, fNoOfBoards);
    return fStatus;
  }

  /**
   * @see org.umart.cmdClientSA.IClientCmdSA#setConnection(UMart, int)
   */
  public void setConnection(UMart umart, int userID) {
    fUMart = umart;
    fUserID = userID;
  }
}
