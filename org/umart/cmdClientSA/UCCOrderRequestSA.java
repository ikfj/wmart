package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCOrderRequestSAクラスは, スタンドアロン版クライアントにおいて実行される
 * OrderRequestコマンドである.
 * @author 川部 祐司
 */
public class UCCOrderRequestSA extends UCOrderRequestCore implements
    IClientCmdSA {

  /** サーバーへの参照 */
  private UMart fUMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for WCCOrderRequestSA.
   */
  public UCCOrderRequestSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fRequestInfo.clear();
    fStatus = fUMart.doOrderRequest(fRequestInfo, fUserID,
                                    fBrandName, fNewRepay,
                                    fSellBuy, fMarketLimit,
                                    fPrice, fVolume);
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
