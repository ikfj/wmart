package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCPositionSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * Position�R�}���h�ł���.
 * @author �암 �S�i
 */
public class UCCPositionSA extends UCPositionCore implements IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private UMart fUMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for UCCPositionSA.
   */
  public UCCPositionSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fPositionInfo.clear();
    fStatus = fUMart.doPosition(fPositionInfo, fUserID);
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
