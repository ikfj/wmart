package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCAllPositionsSAクラスは, スタンドアロン版クライアントにおいて実行される
 * AllPositionsコマンドである.
 * @author 川部 祐司
 */
public class WCCAllPositionsSA extends WCAllPositionsCore implements
    IClientCmdSA {

  /** サーバーへの参照 */
  private WMart fWMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for WCCAllPositionsSA.
   */
  public WCCAllPositionsSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.cmdCore.ICommand#doIt()
   */
  public WCommandStatus doIt() {
    fAllPositionsArray.clear();
    fStatus = fWMart.doAllPositions(fAllPositionsArray, fUserID);
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
