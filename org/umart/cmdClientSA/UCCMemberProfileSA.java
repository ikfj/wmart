package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


public class UCCMemberProfileSA extends UCMemberProfileCore implements
    IClientCmdSA {

  /** U-Martオブジェクトへの参照 */
  private UMart fUMart;

  /** ユーザID */
  private int fUserID;

  /** コンストラクタ */
  UCCMemberProfileSA() {
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
    fCommandStatus = fUMart.doMemberProfile(fData, fUserID, fTargetUserId);
    return fCommandStatus;
  }

}
