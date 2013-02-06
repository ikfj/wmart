package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCScheduleSA�N���X�́C�X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * Schedule�R�}���h�ł���D
 * @author ����@��
 */
public class UCCScheduleSA extends UCScheduleCore implements IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private UMart fUMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for UCCScheduleSA.
   */
  public UCCScheduleSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.serverCore.ICCommand#doIt()
   */
  public UCommandStatus doIt() {
    fScheduleInfo.clear();
    fStatus = fUMart.doSchedule(fScheduleInfo);
    return fStatus;
  }

  /**
   * @see org.umart.cmdClientSA.IClientCmdSA#setConnection(UMartStandAlone, int)
   */
  public void setConnection(UMart umart, int userID) {
    fUMart = umart;
    fUserID = userID;
  }

}
