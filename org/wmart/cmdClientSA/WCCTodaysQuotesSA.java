package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * WCCTodaysQuotesSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * TodaysQuotes�R�}���h�ł���.
 * @author �암 �S�i
 */
public class WCCTodaysQuotesSA extends WCTodaysQuotesCore implements
    IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private WMart fWMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for UCCTodaysQuotesSA.
   */
  public WCCTodaysQuotesSA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.cmdCore.ICommand#doIt()
   */
  public WCommandStatus doIt() {
    fQuotesArray.clear();
    fStatus = fWMart.doTodaysQuotes(fQuotesArray, fBrandName, fNoOfBoards);
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
