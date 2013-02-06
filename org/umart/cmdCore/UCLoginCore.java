/*
 * Created on 2003/06/08
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.umart.cmdCore;

import java.util.*;

/**
 * @author isao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
abstract public class UCLoginCore implements ICommand {

  /** コマンド名 */
  public static final String CMD_NAME = "Login";

  /** 別名 */
  public static final String CMD_ALIAS = "101";

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** ユーザー名 */
  protected String fUserName;

  /** パスワード */
  protected String fPasswd;

  /**
   * コンストラクタ
   */
  public UCLoginCore() {
    super();
    fStatus = new UCommandStatus();
    fUserName = null;
    fPasswd = null;
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
    try {
      fUserName = st.nextToken();
      fPasswd = st.nextToken();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean setArguments(String userName, String passwd) {
    fUserName = userName;
    fPasswd = passwd;
    return true;
  }

  /* (non-Javadoc)
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    if (fStatus.getStatus()) {
      System.out.println("Login as " + fUserName + " succeeded.");
    } else {
      System.out.println("Login as " + fUserName + " failed.");
    }
  }

  /* (non-Javadoc)
   * @see org.umart.cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    return null;
  }

}
