package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCSpotIntervalSAクラスは, スタンドアロン版クライアントにおいて実行される
 * SpotIntervalコマンドである.
 * @author 川部 祐司
 */
public class WCCSpotIntervalSA extends WCSpotIntervalCore implements
    IClientCmdSA {

  /** サーバーへの参照 */
  private WMart fWMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for WCCSpotIntervalSA.
   */
  public WCCSpotIntervalSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.cmdCore.ICommand#doIt()
   */
  public WCommandStatus doIt() {
    fIntervalInfo.clear();
    fStatus = fWMart.doSpotInterval(fIntervalInfo);
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
