package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCAllBalancesSAクラスは, スタンドアロン版クライアントにおいて実行される
 * AllBalancesコマンドである.
 * @author 川部 祐司
 */
public class UCCAllBalancesSA extends UCAllBalancesCore implements IClientCmdSA {

  /** サーバーへの参照 */
  private UMart fUMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for UCCAllBalancesSA.
   */
  public UCCAllBalancesSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fAllBalancesArray.clear();
    fStatus = fUMart.doAllBalances(fAllBalancesArray, fUserID);
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
