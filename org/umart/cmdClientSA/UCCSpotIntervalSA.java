package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCSpotIntervalSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * SpotInterval�R�}���h�ł���.
 * @author �암 �S�i
 */
public class UCCSpotIntervalSA extends UCSpotIntervalCore implements
    IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private UMart fUMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for UCCSpotIntervalSA.
   */
  public UCCSpotIntervalSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fIntervalInfo.clear();
    fStatus = fUMart.doSpotInterval(fIntervalInfo);
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
