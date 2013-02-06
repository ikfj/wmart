package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCScheduleSAクラスは，スタンドアロン版クライアントにおいて実行される
 * Scheduleコマンドである．
 * @author 小野　功
 */
public class UCCScheduleSA extends UCScheduleCore implements IClientCmdSA {

  /** サーバーへの参照 */
  private UMart fUMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for UCCScheduleSA.
   */
  public UCCScheduleSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.serverCore.ICCommand#doIt()
   */
  public UCommandStatus doIt() {
    fScheduleInfo.clear();
    fStatus = fUMart.doSchedule(fScheduleInfo);
    return fStatus;
  }

  /**
   * @see org.umart.cmdClientSA.IClientCmdSA#setConnection(UMartStandAlone, int)
   */
  public void setConnection(UMart umart, int userID) {
    fUMart = umart;
    fUserID = userID;
  }

}
