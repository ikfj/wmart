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
public class WCCOrderCheckSA extends WCOrderCheckCore implements IClientCmdSA {

  /** U-Martオブジェクトへの参照 */
  private WMart fWMart;

  /** ユーザID */
  private int fUserID;

  /**
   * Constructor for WCCCheckOrderSA.
   */
  public WCCOrderCheckSA() {
    super();
  }

  /**
   * @see org.wmart.cmdCore.ICommand#doIt()
   */
  public WCommandStatus doIt() {
    fData.clear();
    fCommandStatus = fWMart.doOrderCheck(fOrderID, fData);
    return fCommandStatus;
  }

  /**
   * @see org.wmart.serverCore.IStandAloneCmd#setConnection(WMart, int)
   */
  public void setConnection(WMart wmart, int userID) {
    fWMart = wmart;
    fUserID = userID;
  }

}
