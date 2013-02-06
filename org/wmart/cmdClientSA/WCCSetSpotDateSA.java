package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCSetSpotDateSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * SetSpotDate�R�}���h�ł���.
 * @author �암 �S�i
 */
public class WCCSetSpotDateSA extends WCSetSpotDateCore implements IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private WMart fWMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for WCCSetSpotDateSA.
   */
  public WCCSetSpotDateSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.cmdCore.ICommand#doIt()
   */
  public WCommandStatus doIt() {
    fStatus = fWMart.doSetSpotDate(fUserID, fStartPoint);
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
