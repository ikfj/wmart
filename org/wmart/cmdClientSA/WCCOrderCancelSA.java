package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCOrderCancelSAクラスは，スタンドアロン版クライアントにおいて実行される
 * OrderCancelコマンドである．
 * @author 小野　功
 */
public class WCCOrderCancelSA extends WCOrderCancelCore implements IClientCmdSA {

  /** サーバーへの参照 */
  private WMart fWMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for WCCOrderCancelSA.
   */
  public WCCOrderCancelSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.cmdCore.ICommand#doIt()
   */
  public WCommandStatus doIt() {
    fCancelInfo.clear();
    fStatus = fWMart.doOrderCancel(fCancelInfo, fUserID, fOrderID);
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
