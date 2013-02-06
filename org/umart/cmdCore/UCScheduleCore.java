package org.umart.cmdCore;

import java.util.*;

/**
 * UCScheduleCoreクラスは，運用予定問合せを行うコマンドオブジェクトである．
 * @author 小野　功
 */
public abstract class UCScheduleCore implements ICommand {

  /** サーバ運用日数を引くためのキー */
  public static final String INT_MAX_DAY = "INT_MAX_DAY";

  /** １日の板寄せ回数を引くためのキー */
  public static final String INT_NO_OF_BOARDS = "INT_NO_OF_BOARDS";

  /** コマンド名 */
  public static final String CMD_NAME = "Schedule";

  /** 別名 */
  public static final String CMD_ALIAS = "";

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** 結果を格納するためのHashMap */
  protected HashMap fScheduleInfo;

  /**
   * Constructor for UCScheduleCore.
   */
  public UCScheduleCore() {
    super();
    fStatus = new UCommandStatus();
    fScheduleInfo = new HashMap();
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
   * @see ICommand#getResultString()
   */
  public String getResultString() {
    String result = fScheduleInfo.get(INT_MAX_DAY) + " "
        + fScheduleInfo.get(INT_NO_OF_BOARDS);
    return result;
  }

  /**
   * 結果を返す．
   * @return 結果
   */
  public HashMap getResults() {
    return fScheduleInfo;
  }

  /**
   * @see ICCommand#setArguments(StringTokenizer)
   */
  public boolean setArguments(StringTokenizer st) {
    return true;
  }

  /**
   * @see ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<" + CMD_NAME + ">>");
    System.out.println(fScheduleInfo.toString());
  }

}
