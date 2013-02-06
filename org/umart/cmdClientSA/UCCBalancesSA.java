package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCBalancesSAクラスは, スタンドアロン版クライアントにおいて実行される
 * Balancesコマンドである.
 * @author 川部 祐司
 */
public class UCCBalancesSA extends UCBalancesCore implements IClientCmdSA {

  /** サーバーへの参照 */
  private UMart fUMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for UCCBalancesSA.
   */
  public UCCBalancesSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fTodayInfo.clear();
    fYesterdayInfo.clear();
    fStatus = fUMart.doBalances(fTodayInfo, fYesterdayInfo, fUserID);
    return fStatus;
  }

  /**
   * @see org.umart.cmdClientSA.IClientCmdSA#setConnection(UMart, int)
   */
  public void setConnection(UMart umart, int userID) {
    fUMart = umart;
    fUserID = userID;
  }
}
