package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


public class WCCAccountHistorySA extends WCAccountHistoryCore implements
    IClientCmdSA {

  /** U-Martオブジェクトへの参照 */
  private WMart fWMart;

  /** ユーザID */
  private int fUserID;

  /** コンストラクタ */
  UCCAccountHistorySA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  public void setConnection(WMart wmart, int userID) {
    fWMart = wmart;
    fUserID = userID;
  }

  public WCommandStatus doIt() {
    fArray.clear();
    fCommandStatus = fWMart.doAccountHistory(fArray, fUserID, fTargetUserId,
                                             fNoOfDays);
    return fCommandStatus;
  }

}
