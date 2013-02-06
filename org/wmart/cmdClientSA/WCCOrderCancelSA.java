package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCOrderCancelSA�N���X�́C�X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * OrderCancel�R�}���h�ł���D
 * @author ����@��
 */
public class WCCOrderCancelSA extends WCOrderCancelCore implements IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private WMart fWMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for WCCOrderCancelSA.
   */
  public WCCOrderCancelSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.cmdCore.ICommand#doIt()
   */
  public WCommandStatus doIt() {
    fCancelInfo.clear();
    fStatus = fWMart.doOrderCancel(fCancelInfo, fUserID, fOrderID);
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
