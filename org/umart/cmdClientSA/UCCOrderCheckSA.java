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
public class UCCOrderCheckSA extends UCOrderCheckCore implements IClientCmdSA {

  /** U-Martオブジェクトへの参照 */
  private UMart fUMart;

  /** ユーザID */
  private int fUserID;

  /**
   * Constructor for UCCCheckOrderSA.
   */
  public UCCOrderCheckSA() {
    super();
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fData.clear();
    fCommandStatus = fUMart.doOrderCheck(fOrderID, fData);
    return fCommandStatus;
  }

  /**
   * @see org.umart.serverCore.IStandAloneCmd#setConnection(UMart, int)
   */
  public void setConnection(UMart umart, int userID) {
    fUMart = umart;
    fUserID = userID;
  }

}
