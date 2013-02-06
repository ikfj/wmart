package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCAllBalancesSAクラスは, スタンドアロン版クライアントにおいて実行される
 * AllBalancesコマンドである.
 * @author 川部 祐司
 */
public class WCCAllBalancesSA extends WCAllBalancesCore implements IClientCmdSA {

  /** サーバーへの参照 */
  private WMart fWMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for WCCAllBalancesSA.
   */
  public WCCAllBalancesSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.cmdCore.ICommand#doIt()
   */
  public WCommandStatus doIt() {
    fAllBalancesArray.clear();
    fStatus = fWMart.doAllBalances(fAllBalancesArray, fUserID);
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
