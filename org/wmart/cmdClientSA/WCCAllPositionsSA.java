package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCAllPositionsSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * AllPositions�R�}���h�ł���.
 * @author �암 �S�i
 */
public class WCCAllPositionsSA extends WCAllPositionsCore implements
    IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private WMart fWMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for WCCAllPositionsSA.
   */
  public WCCAllPositionsSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.cmdCore.ICommand#doIt()
   */
  public WCommandStatus doIt() {
    fAllPositionsArray.clear();
    fStatus = fWMart.doAllPositions(fAllPositionsArray, fUserID);
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
