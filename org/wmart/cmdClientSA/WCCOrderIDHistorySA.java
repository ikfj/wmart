package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;


/**
 * @author user
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class WCCOrderIDHistorySA extends WCOrderIDHistoryCore implements
    IClientCmdSA {

  /** U-Martオブジェクトへの参照 */
  private WMart fWMart;

  /** ユーザID */
  private int fUserID;

  /**
   * Constructor for WCCOrderIDHistorySA.
   */
  public WCCOrderIDHistorySA() {
    super();
    fWMart = null;
    fUserID = -1;
  }

  /**
   * @see org.wmart.cmdCore.ICommand#doIt()
   */
  public WCommandStatus doIt() {
    fCommandStatus = fWMart.doOrderIDHistory(fUserID, fOrderIDHistory,
                                             fTargetUserID, fNoOfSteps);
    return fCommandStatus;
  }

  /**
   * @see org.wmart.cmdClientSA.IClientCmdSA#setConnection(WMart, int)
   */
  public void setConnection(WMart wmart, int userID) {
    fWMart = wmart;
    fUserID = userID;
  }

}
