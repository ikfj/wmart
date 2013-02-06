package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCSpotIntervalSAクラスは, スタンドアロン版クライアントにおいて実行される
 * SpotIntervalコマンドである.
 * @author 川部 祐司
 */
public class UCCSpotIntervalSA extends UCSpotIntervalCore implements
    IClientCmdSA {

  /** サーバーへの参照 */
  private UMart fUMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for UCCSpotIntervalSA.
   */
  public UCCSpotIntervalSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fIntervalInfo.clear();
    fStatus = fUMart.doSpotInterval(fIntervalInfo);
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
