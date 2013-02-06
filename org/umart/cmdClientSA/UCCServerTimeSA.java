package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCServerTimeSAクラスは，スタンドアロン版クライアントにおいて実行される
 * ServerTimeコマンドである．
 * @author 小野　功
 */
public class UCCServerTimeSA extends UCServerTimeCore implements IClientCmdSA {

  /** サーバーへの参照 */
  private UMart fUMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for UCCServerTimeSA.
   */
  public UCCServerTimeSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fServerTimeInfo.clear();
    fStatus = fUMart.doServerTime(fServerTimeInfo);
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
