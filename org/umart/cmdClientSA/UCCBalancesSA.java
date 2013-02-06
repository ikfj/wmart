package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCBalancesSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * Balances�R�}���h�ł���.
 * @author �암 �S�i
 */
public class UCCBalancesSA extends UCBalancesCore implements IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private UMart fUMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for UCCBalancesSA.
   */
  public UCCBalancesSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fTodayInfo.clear();
    fYesterdayInfo.clear();
    fStatus = fUMart.doBalances(fTodayInfo, fYesterdayInfo, fUserID);
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
