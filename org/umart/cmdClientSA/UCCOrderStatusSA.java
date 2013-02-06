package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCOrderStatusSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * OrderStatus�R�}���h�ł���.
 * @author �암 �S�i
 */
public class UCCOrderStatusSA extends UCOrderStatusCore implements IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private UMart fUMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for UCCOrderStatusSA.
   */
  public UCCOrderStatusSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fStatusArray.clear();
    fStatus = fUMart.doOrderStatus(fStatusArray, fUserID);
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
