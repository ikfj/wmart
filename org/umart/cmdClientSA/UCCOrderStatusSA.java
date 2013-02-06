package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCOrderStatusSAクラスは, スタンドアロン版クライアントにおいて実行される
 * OrderStatusコマンドである.
 * @author 川部 祐司
 */
public class UCCOrderStatusSA extends UCOrderStatusCore implements IClientCmdSA {

  /** サーバーへの参照 */
  private UMart fUMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for UCCOrderStatusSA.
   */
  public UCCOrderStatusSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fStatusArray.clear();
    fStatus = fUMart.doOrderStatus(fStatusArray, fUserID);
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
