package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCPositionSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * Position�R�}���h�ł���.
 * @author �암 �S�i
 */
public class WCCPositionSA extends WCPositionCore implements IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private WMart fWMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for WCCPositionSA.
   */
  public WCCPositionSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.cmdCore.ICommand#doIt()
   */
  public WCommandStatus doIt() {
    fPositionInfo.clear();
    fStatus = fWMart.doPosition(fPositionInfo, fUserID);
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
