package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCFuturePriceSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * FuturePrice�R�}���h�ł���.
 * @author �암 �S�i
 */
public class WCCFuturePriceSA extends WCFuturePriceCore implements IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private WMart fWMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for WCCFuturePriceSA.
   */
  public WCCFuturePriceSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.cmdCore.ICommand#doIt()
   */
  public WCommandStatus doIt() {
    fFutureArray.clear();
    fStatus = fWMart.doFuturePrice(fFutureArray, fBrandName, fNoOfSteps);
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
