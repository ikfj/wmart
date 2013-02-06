package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCBoardDataSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * BoardData�R�}���h�ł���.
 * @author �암 �S�i
 */
public class WCCBoardDataSA extends WCBoardDataCore implements IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private WMart fWMart;

  /** ���[�U�[ID */
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
