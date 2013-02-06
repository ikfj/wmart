package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCOrderCancelSAクラスは，スタンドアロン版クライアントにおいて実行される
 * OrderCancelコマンドである．
 * @author 小野　功
 */
public class UCCOrderCancelSA extends UCOrderCancelCore implements IClientCmdSA {

  /** サーバーへの参照 */
  private UMart fUMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for UCCOrderCancelSA.
   */
  public UCCOrderCancelSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fCancelInfo.clear();
    fStatus = fUMart.doOrderCancel(fCancelInfo, fUserID, fOrderID);
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
