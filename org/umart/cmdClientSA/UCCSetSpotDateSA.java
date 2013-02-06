package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCSetSpotDateSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * SetSpotDate�R�}���h�ł���.
 * @author �암 �S�i
 */
public class UCCSetSpotDateSA extends UCSetSpotDateCore implements IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private UMart fUMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for UCCSetSpotDateSA.
   */
  public UCCSetSpotDateSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fStatus = fUMart.doSetSpotDate(fUserID, fStartPoint);
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
