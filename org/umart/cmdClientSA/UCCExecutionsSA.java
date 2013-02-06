package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCExecutionsSAクラスは, スタンドアロン版クライアントにおいて実行される
 * Executionsコマンドである.
 * @author 川部 祐司
 */
public class UCCExecutionsSA extends UCExecutionsCore implements IClientCmdSA {

  /** サーバーへの参照 */
  private UMart fUMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for UCCExecutionsSA.
   */
  public UCCExecutionsSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fExecutionsArray.clear();
    fStatus = fUMart.doExecutions(fExecutionsArray, fUserID);
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
