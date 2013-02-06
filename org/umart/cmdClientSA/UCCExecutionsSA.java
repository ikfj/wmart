package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCExecutionsSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * Executions�R�}���h�ł���.
 * @author �암 �S�i
 */
public class UCCExecutionsSA extends UCExecutionsCore implements IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private UMart fUMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for UCCExecutionsSA.
   */
  public UCCExecutionsSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fExecutionsArray.clear();
    fStatus = fUMart.doExecutions(fExecutionsArray, fUserID);
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
