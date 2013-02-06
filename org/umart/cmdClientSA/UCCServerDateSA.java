package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCServerDateSA�N���X�́C�X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * ServerDate�R�}���h�ł���D
 * @author ����@��
 */
public class UCCServerDateSA extends UCServerDateCore implements IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private UMart fUMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for UCCServerDateSA.
   */
  public UCCServerDateSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fServerDateInfo.clear();
    fStatus = fUMart.doServerDate(fServerDateInfo);
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
