package org.umart.cmdClientSA;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;


/**
 * @author user
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class UCCOrderIDHistorySA extends UCOrderIDHistoryCore implements
    IClientCmdSA {

  /** U-Mart�I�u�W�F�N�g�ւ̎Q�� */
  private UMart fUMart;

  /** ���[�UID */
  private int fUserID;

  /**
   * Constructor for UCCOrderIDHistorySA.
   */
  public UCCOrderIDHistorySA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fCommandStatus = fUMart.doOrderIDHistory(fUserID, fOrderIDHistory,
                                             fTargetUserID, fNoOfSteps);
    return fCommandStatus;
  }

  /**
   * @see org.umart.cmdClientSA.IClientCmdSA#setConnection(UMart, int)
   */
  public void setConnection(UMart umart, int userID) {
    fUMart = umart;
    fUserID = userID;
  }

}
