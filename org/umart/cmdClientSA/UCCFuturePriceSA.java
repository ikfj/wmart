package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCFuturePriceSAクラスは, スタンドアロン版クライアントにおいて実行される
 * FuturePriceコマンドである.
 * @author 川部 祐司
 */
public class UCCFuturePriceSA extends UCFuturePriceCore implements IClientCmdSA {

  /** サーバーへの参照 */
  private UMart fUMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for UCCFuturePriceSA.
   */
  public UCCFuturePriceSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fFutureArray.clear();
    fStatus = fUMart.doFuturePrice(fFutureArray, fBrandName, fNoOfSteps);
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
