package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCAllPositionsSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * AllPositions�R�}���h�ł���.
 * @author �암 �S�i
 */
public class UCCAllPositionsSA extends UCAllPositionsCore implements
    IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private UMart fUMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for UCCAllPositionsSA.
   */
  public UCCAllPositionsSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fAllPositionsArray.clear();
    fStatus = fUMart.doAllPositions(fAllPositionsArray, fUserID);
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
