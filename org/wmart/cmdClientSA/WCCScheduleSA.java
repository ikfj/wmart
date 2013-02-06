package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCScheduleSAクラスは，スタンドアロン版クライアントにおいて実行される
 * Scheduleコマンドである．
 * @author 小野　功
 */
public class WCCScheduleSA extends WCScheduleCore implements IClientCmdSA {

  /** サーバーへの参照 */
  private WMart fWMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for WCCScheduleSA.
   */
  public WCCScheduleSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.serverCore.ICCommand#doIt()
   */
  public WCommandStatus doIt() {
    fScheduleInfo.clear();
    fStatus = fWMart.doSchedule(fScheduleInfo);
    return fStatus;
  }

  /**
   * @see org.wmart.cmdClientSA.IClientCmdSA#setConnection(UMartStandAlone, int)
   */
  public void setConnection(WMart wmart, int userID) {
    fWMart = wmart;
    fUserID = userID;
  }

}
