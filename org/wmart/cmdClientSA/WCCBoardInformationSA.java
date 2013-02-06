package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCBoardInformationSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ�����
 * ���s�����BoardInformation�R�}���h�ł���.
 * @author �암 �S�i
 */
public class WCCBoardInformationSA extends WCBoardInformationCore implements
    IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private WMart fWMart;

  /** ���[�U�[ID */
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
