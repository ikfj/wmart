package org.umart.cmdCore;

import java.util.*;

/**
 * UCServerDateCoreクラスは，サーバ経過問合せのためのコマンドオブジェクトである．
 * @author 小野　功
 */
public abstract class UCServerDateCore implements ICommand {

  /** 経過日数を引くためのキー */
  public static final String INT_DAY = "INT_DAY";

  /** 現在の板寄せ回数を引くためのキー */
  public static final String INT_BOARD_NO = "INT_BOARD_NO";

  /** コマンド名 */
  public static final String CMD_NAME = "ServerDate";

  /** 別名 */
  public static final String CMD_ALIAS = "";

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** 結果を格納するためのHashMap */
  protected HashMap fServerDateInfo;

  /**
   * Constructor for UCServerDateCore.
   */
  public UCServerDateCore() {
    super();
    fStatus = new UCommandStatus();
    fServerDateInfo = new HashMap();
  }

  /**
   * 	@see org.umart.cmdCore.ICommand#isNameEqualTo(String)
   */
  public boolean isNameEqualTo(String name) {
    if (name.equalsIgnoreCase(CMD_NAME) || name.equals(CMD_ALIAS)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * @see org.umart.cmdCore.ICommand#getName()
   */
  public String getName() {
    return CMD_NAME;
  }

  /**
   * @see org.umart.cmdCore.ICommand#setArguments(StringTokenizer)
   */
  public boolean setArguments(StringTokenizer st) {
    return true;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<" + CMD_NAME + ">>");
    System.out.println(fServerDateInfo.toString());
  }

  /**
   * @see org.umart.cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = fServerDateInfo.get(INT_DAY) + " "
        + fServerDateInfo.get(INT_BOARD_NO);
    return result;
  }

  /**
   * 結果を返す．
   * @return 結果
   */
  public HashMap getResults() {
    return fServerDateInfo;
  }

}
