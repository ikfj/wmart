package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCFuturePriceSAクラスは, スタンドアロン版クライアントにおいて実行される
 * FuturePriceコマンドである.
 * @author 川部 祐司
 */
public class WCCFuturePriceSA extends WCFuturePriceCore implements IClientCmdSA {

  /** サーバーへの参照 */
  private WMart fWMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for WCCFuturePriceSA.
   */
  public WCCFuturePriceSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.cmdCore.ICommand#doIt()
   */
  public WCommandStatus doIt() {
    fFutureArray.clear();
    fStatus = fWMart.doFuturePrice(fFutureArray, fBrandName, fNoOfSteps);
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
