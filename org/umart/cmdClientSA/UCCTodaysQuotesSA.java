package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * UCCTodaysQuotesSA�N���X��, �X�^���h�A�����ŃN���C�A���g�ɂ����Ď��s�����
 * TodaysQuotes�R�}���h�ł���.
 * @author �암 �S�i
 */
public class UCCTodaysQuotesSA extends UCTodaysQuotesCore implements
    IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private UMart fUMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * Constructor for UCCTodaysQuotesSA.
   */
  public UCCTodaysQuotesSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fQuotesArray.clear();
    fStatus = fUMart.doTodaysQuotes(fQuotesArray, fBrandName, fNoOfBoards);
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
