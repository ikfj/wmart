package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCBoardDataSAクラスは, スタンドアロン版クライアントにおいて実行される
 * BoardDataコマンドである.
 * @author 川部 祐司
 */
public class WCCBoardDataSA extends WCBoardDataCore implements IClientCmdSA {

  /** サーバーへの参照 */
  private WMart fWMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for WCCBoardDataSA.
   */
  public WCCBoardDataSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.cmdCore.ICommand#doIt()
   */
  public WCommandStatus doIt() {
    fBoardDataArray.clear();
    fBoardDataInfo.clear();
    fStatus = fWMart.doBoardData(fBoardDataInfo, fBoardDataArray);
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
