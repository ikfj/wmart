package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCSpotPriceSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * SpotPrice�R�}���h�ł���.
 * @author �암 �S�i
 */
public class WCCSpotPriceSA extends WCSpotPriceCore implements IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private WMart fWMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for WCCSpotPriceSA.
   */
  public WCCSpotPriceSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.cmdCore.ICommand#doIt()
   */
  public WCommandStatus doIt() {
    fSpotArray.clear();
    fStatus = fWMart.doSpotPrice(fSpotArray, fBrandName, fNoOfSteps);
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
