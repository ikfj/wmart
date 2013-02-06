package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCBoardInformationSAクラスは, スタンドアロン版クライアントにおいて
 * 実行されるBoardInformationコマンドである.
 * @author 川部 祐司
 */
public class WCCBoardInformationSA extends WCBoardInformationCore implements
    IClientCmdSA {

  /** サーバーへの参照 */
  private WMart fWMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for WCCBoardInformationSA.
   */
  public WCCBoardInformationSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.cmdCore.ICommand#doIt()
   */
  public WCommandStatus doIt() {
    fBoardInfo.clear();
    fStatus = fWMart.doBoardInformation(fBoardInfo);
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
