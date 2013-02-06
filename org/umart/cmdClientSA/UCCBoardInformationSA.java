package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCBoardInformationSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ�����
 * ���s�����BoardInformation�R�}���h�ł���.
 * @author �암 �S�i
 */
public class UCCBoardInformationSA extends UCBoardInformationCore implements
    IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private UMart fUMart;

  /** ���[�U�[ID */
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
