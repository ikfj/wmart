package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCHistoricalQuotesSAクラスは, スタンドアロン版クライアントにおいて
 * 実行されるHistoricalQuotesコマンドである.
 * @author 川部 祐司
 */
public class UCCHistoricalQuotesSA extends UCHistoricalQuotesCore implements
    IClientCmdSA {

  /** サーバーへの参照 */
  private UMart fUMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for UCCHistoricalQuotesSA.
   */
  public UCCHistoricalQuotesSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fQuotesArray.clear();
    fStatus = fUMart.doHistoricalQuotes(fQuotesArray, fBrandName, fNoOfDays);
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
