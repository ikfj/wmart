package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


public class UCCAccountHistorySA extends UCAccountHistoryCore implements
    IClientCmdSA {

  /** U-Martオブジェクトへの参照 */
  private UMart fUMart;

  /** ユーザID */
  private int fUserID;

  /** コンストラクタ */
  UCCAccountHistorySA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  public void setConnection(UMart umart, int userID) {
    fUMart = umart;
    fUserID = userID;
  }

  public UCommandStatus doIt() {
    fArray.clear();
    fCommandStatus = fUMart.doAccountHistory(fArray, fUserID, fTargetUserId,
                                             fNoOfDays);
    return fCommandStatus;
  }

}
