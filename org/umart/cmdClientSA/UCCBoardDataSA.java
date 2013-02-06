package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCBoardDataSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * BoardData�R�}���h�ł���.
 * @author �암 �S�i
 */
public class UCCBoardDataSA extends UCBoardDataCore implements IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private UMart fUMart;

  /** ���[�U�[ID */
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
