package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCHistoricalQuotesSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ�����
 * ���s�����HistoricalQuotes�R�}���h�ł���.
 * @author �암 �S�i
 */
public class UCCHistoricalQuotesSA extends UCHistoricalQuotesCore implements
    IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private UMart fUMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for UCCHistoricalQuotesSA.
   */
  public UCCHistoricalQuotesSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fQuotesArray.clear();
    fStatus = fUMart.doHistoricalQuotes(fQuotesArray, fBrandName, fNoOfDays);
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
