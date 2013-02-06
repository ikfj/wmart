package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCMarketStatusSAクラスは, スタンドアロン版クライアントにおいて実行される
 * MarketStatusコマンドである.
 * @author 川部 祐司
 */
public class UCCMarketStatusSA extends UCMarketStatusCore implements
    IClientCmdSA {

  /** サーバーへの参照 */
  private UMart fUMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for UCCMarketStatusSA.
   */
  public UCCMarketStatusSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fMarketInfo.clear();
    fStatus = fUMart.doMarketStatus(fMarketInfo);
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
