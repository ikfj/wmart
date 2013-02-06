package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


public class UCCServerStatusSA extends UCServerStatusCore implements
    IClientCmdSA {

  /** U-Martオブジェクトへの参照 */
  private UMart fUMart;

  /** ユーザID */
  private int fUserID;

  /** コンストラクタ */
  UCCServerStatusSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  public void setConnection(UMart umart, int userID) {
    fUMart = umart;
    fUserID = userID;
  }

  public UCommandStatus doIt() {
    fData.clear();
    fCommandStatus = fUMart.doServerStatus(fData, fUserID);
    return fCommandStatus;
  }

}
