package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCMarketStatusSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * MarketStatus�R�}���h�ł���.
 * @author �암 �S�i
 */
public class UCCMarketStatusSA extends UCMarketStatusCore implements
    IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private UMart fUMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for UCCMarketStatusSA.
   */
  public UCCMarketStatusSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fMarketInfo.clear();
    fStatus = fUMart.doMarketStatus(fMarketInfo);
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
