package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCAllBalancesSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * AllBalances�R�}���h�ł���.
 * @author �암 �S�i
 */
public class UCCAllBalancesSA extends UCAllBalancesCore implements IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private UMart fUMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for UCCAllBalancesSA.
   */
  public UCCAllBalancesSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fAllBalancesArray.clear();
    fStatus = fUMart.doAllBalances(fAllBalancesArray, fUserID);
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
