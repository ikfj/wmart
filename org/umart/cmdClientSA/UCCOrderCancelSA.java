package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCOrderCancelSA�N���X�́C�X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * OrderCancel�R�}���h�ł���D
 * @author ����@��
 */
public class UCCOrderCancelSA extends UCOrderCancelCore implements IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private UMart fUMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for UCCOrderCancelSA.
   */
  public UCCOrderCancelSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fCancelInfo.clear();
    fStatus = fUMart.doOrderCancel(fCancelInfo, fUserID, fOrderID);
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
