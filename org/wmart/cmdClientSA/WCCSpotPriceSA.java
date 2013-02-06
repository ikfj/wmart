package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCSpotPriceSAクラスは, スタンドアロン版クライアントにおいて実行される
 * SpotPriceコマンドである.
 * @author 川部 祐司
 */
public class WCCSpotPriceSA extends WCSpotPriceCore implements IClientCmdSA {

  /** サーバーへの参照 */
  private WMart fWMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for WCCSpotPriceSA.
   */
  public WCCSpotPriceSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.cmdCore.ICommand#doIt()
   */
  public WCommandStatus doIt() {
    fSpotArray.clear();
    fStatus = fWMart.doSpotPrice(fSpotArray, fBrandName, fNoOfSteps);
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
