package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCScheduleSA�N���X�́C�X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * Schedule�R�}���h�ł���D
 * @author ����@��
 */
public class WCCScheduleSA extends WCScheduleCore implements IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private WMart fWMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for WCCScheduleSA.
   */
  public WCCScheduleSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.serverCore.ICCommand#doIt()
   */
  public WCommandStatus doIt() {
    fScheduleInfo.clear();
    fStatus = fWMart.doSchedule(fScheduleInfo);
    return fStatus;
  }

  /**
   * @see org.wmart.cmdClientSA.IClientCmdSA#setConnection(UMartStandAlone, int)
   */
  public void setConnection(WMart wmart, int userID) {
    fWMart = wmart;
    fUserID = userID;
  }

}
