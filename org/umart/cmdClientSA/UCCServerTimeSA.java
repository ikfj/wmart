package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCServerTimeSA�N���X�́C�X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * ServerTime�R�}���h�ł���D
 * @author ����@��
 */
public class UCCServerTimeSA extends UCServerTimeCore implements IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private UMart fUMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for UCCServerTimeSA.
   */
  public UCCServerTimeSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fServerTimeInfo.clear();
    fStatus = fUMart.doServerTime(fServerTimeInfo);
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
