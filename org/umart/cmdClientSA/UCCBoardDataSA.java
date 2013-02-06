package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCBoardDataSAクラスは, スタンドアロン版クライアントにおいて実行される
 * BoardDataコマンドである.
 * @author 川部 祐司
 */
public class UCCBoardDataSA extends UCBoardDataCore implements IClientCmdSA {

  /** サーバーへの参照 */
  private UMart fUMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for UCCBoardDataSA.
   */
  public UCCBoardDataSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fBoardDataArray.clear();
    fBoardDataInfo.clear();
    fStatus = fUMart.doBoardData(fBoardDataInfo, fBoardDataArray);
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
