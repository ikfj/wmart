/*
 * Created on 2003/06/07
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.umart.cmdCore;

import java.util.*;

/**
 * UCLogoutCore�N���X��,
 * ���O�A�E�g�̂��߂̃R�}���h�I�u�W�F�N�g�ł���.
 * @author ����@��
 */
abstract public class UCLogoutCore implements ICommand {

  /** �R�}���h�� */
  public static final String CMD_NAME = "Logout";

  /** �ʖ� */
  public static final String CMD_ALIAS = "102";

  /** �R�}���h�̎��s��� */
  protected UCommandStatus fStatus;

  public UCLogoutCore() {
    fStatus = new UCommandStatus();
  }

  /* (non-Javadoc)
   * @see org.umart.cmdCore.ICommand#getName()
   */
  public String getName() {
    return CMD_NAME;
  }

  /* (non-Javadoc)
   * @see org.umart.cmdCore.ICommand#isNameEqualTo(java.lang.String)
   */
  public boolean isNameEqualTo(String name) {
    if (name.equalsIgnoreCase(CMD_NAME) || name.equals(CMD_ALIAS)) {
      return true;
    } else {
      return false;
    }
  }

  /* (non-Javadoc)
   * @see org.umart.cmdCore.ICommand#setArguments(java.util.StringTokenizer)
   */
  public boolean setArguments(StringTokenizer st) {
    return true;
  }

  /* (non-Javadoc)
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    if (fStatus.getStatus()) {
      System.out.println("Logout succeeded.");
    } else {
      System.out.println("Logout failed.");
    }

  }

  /* (non-Javadoc)
   * @see org.umart.cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    return CMD_NAME;
  }

}
