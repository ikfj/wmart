package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCSetSpotDateSAクラスは, スタンドアロン版クライアントにおいて実行される
 * SetSpotDateコマンドである.
 * @author 川部 祐司
 */
public class WCCSetSpotDateSA extends WCSetSpotDateCore implements IClientCmdSA {

  /** サーバーへの参照 */
  private WMart fWMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for WCCSetSpotDateSA.
   */
  public WCCSetSpotDateSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.cmdCore.ICommand#doIt()
   */
  public WCommandStatus doIt() {
    fStatus = fWMart.doSetSpotDate(fUserID, fStartPoint);
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
