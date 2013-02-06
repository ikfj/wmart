package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCFuturePriceSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * FuturePrice�R�}���h�ł���.
 * @author �암 �S�i
 */
public class UCCFuturePriceSA extends UCFuturePriceCore implements IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private UMart fUMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for UCCFuturePriceSA.
   */
  public UCCFuturePriceSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fFutureArray.clear();
    fStatus = fUMart.doFuturePrice(fFutureArray, fBrandName, fNoOfSteps);
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
