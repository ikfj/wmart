package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCBalancesSAクラスは, スタンドアロン版クライアントにおいて実行される
 * Balancesコマンドである.
 * @author 川部 祐司
 */
public class WCCBalancesSA extends WCBalancesCore implements IClientCmdSA {

  /** サーバーへの参照 */
  private WMart fWMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for WCCBalancesSA.
   */
  public WCCBalancesSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.cmdCore.ICommand#doIt()
   */
  public WCommandStatus doIt() {
    fTodayInfo.clear();
    fYesterdayInfo.clear();
    fStatus = fWMart.doBalances(fTodayInfo, fYesterdayInfo, fUserID);
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
