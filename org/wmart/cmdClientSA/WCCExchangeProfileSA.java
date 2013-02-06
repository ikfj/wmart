package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


public class WCCExchangeProfileSA extends WCExchangeProfileCore implements
    IClientCmdSA {

  /** U-Martオブジェクトへの参照 */
  private WMart fWMart;

  /** ユーザID */
  private int fUserID;

  /** コンストラクタ */
  UCCExchangeProfileSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  public void setConnection(WMart wmart, int userID) {
    fWMart = wmart;
    fUserID = userID;
  }

  public WCommandStatus doIt() {
    fData.clear();
    fCommandStatus = fWMart.doExchangeProfile(fData, fUserID);
    return fCommandStatus;
  }

}
