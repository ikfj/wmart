package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCSpotPriceSAクラスは, スタンドアロン版クライアントにおいて実行される
 * SpotPriceコマンドである.
 * @author 川部 祐司
 */
public class UCCSpotPriceSA extends UCSpotPriceCore implements IClientCmdSA {

  /** サーバーへの参照 */
  private UMart fUMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for UCCSpotPriceSA.
   */
  public UCCSpotPriceSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fSpotArray.clear();
    fStatus = fUMart.doSpotPrice(fSpotArray, fBrandName, fNoOfSteps);
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
