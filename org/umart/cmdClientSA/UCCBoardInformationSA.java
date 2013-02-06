package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCBoardInformationSAクラスは, スタンドアロン版クライアントにおいて
 * 実行されるBoardInformationコマンドである.
 * @author 川部 祐司
 */
public class UCCBoardInformationSA extends UCBoardInformationCore implements
    IClientCmdSA {

  /** サーバーへの参照 */
  private UMart fUMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * Constructor for UCCBoardInformationSA.
   */
  public UCCBoardInformationSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fBoardInfo.clear();
    fStatus = fUMart.doBoardInformation(fBoardInfo);
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
