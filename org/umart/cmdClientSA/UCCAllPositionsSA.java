package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCAllPositionsSAクラスは, スタンドアロン版クライアントにおいて実行される
 * AllPositionsコマンドである.
 * @author 川部 祐司
 */
public class UCCAllPositionsSA extends UCAllPositionsCore implements
    IClientCmdSA {

  /** サーバーへの参照 */
  private UMart fUMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for UCCAllPositionsSA.
   */
  public UCCAllPositionsSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fAllPositionsArray.clear();
    fStatus = fUMart.doAllPositions(fAllPositionsArray, fUserID);
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
