package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCSetSpotDateSAクラスは, スタンドアロン版クライアントにおいて実行される
 * SetSpotDateコマンドである.
 * @author 川部 祐司
 */
public class UCCSetSpotDateSA extends UCSetSpotDateCore implements IClientCmdSA {

  /** サーバーへの参照 */
  private UMart fUMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for UCCSetSpotDateSA.
   */
  public UCCSetSpotDateSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fStatus = fUMart.doSetSpotDate(fUserID, fStartPoint);
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
