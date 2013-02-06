package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCHistoricalQuotesSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ�����
 * ���s�����HistoricalQuotes�R�}���h�ł���.
 * @author �암 �S�i
 */
public class WCCHistoricalQuotesSA extends WCHistoricalQuotesCore implements
    IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private WMart fWMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for WCCHistoricalQuotesSA.
   */
  public WCCHistoricalQuotesSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.cmdCore.ICommand#doIt()
   */
  public WCommandStatus doIt() {
    fQuotesArray.clear();
    fStatus = fWMart.doHistoricalQuotes(fQuotesArray, fBrandName, fNoOfDays);
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
