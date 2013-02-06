package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCAllBalancesSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * AllBalances�R�}���h�ł���.
 * @author �암 �S�i
 */
public class WCCAllBalancesSA extends WCAllBalancesCore implements IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private WMart fWMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for WCCAllBalancesSA.
   */
  public WCCAllBalancesSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.cmdCore.ICommand#doIt()
   */
  public WCommandStatus doIt() {
    fAllBalancesArray.clear();
    fStatus = fWMart.doAllBalances(fAllBalancesArray, fUserID);
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
