package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCSpotPriceSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * SpotPrice�R�}���h�ł���.
 * @author �암 �S�i
 */
public class UCCSpotPriceSA extends UCSpotPriceCore implements IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private UMart fUMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for UCCSpotPriceSA.
   */
  public UCCSpotPriceSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fSpotArray.clear();
    fStatus = fUMart.doSpotPrice(fSpotArray, fBrandName, fNoOfSteps);
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
