package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCOrderRequestSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * OrderRequest�R�}���h�ł���.
 * @author �암 �S�i
 */
public class UCCOrderRequestSA extends UCOrderRequestCore implements
    IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private UMart fUMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for WCCOrderRequestSA.
   */
  public UCCOrderRequestSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fRequestInfo.clear();
    fStatus = fUMart.doOrderRequest(fRequestInfo, fUserID,
                                    fBrandName, fNewRepay,
                                    fSellBuy, fMarketLimit,
                                    fPrice, fVolume);
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
