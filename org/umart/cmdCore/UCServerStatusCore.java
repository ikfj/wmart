package org.umart.cmdCore;

import java.util.*;

public abstract class UCServerStatusCore implements ICommand {

  /** コマンド名 */
  public static final String CMD_NAME = "ServerStatus";

  /** コマンドの実行結果の状態 */
  protected UCommandStatus fCommandStatus;

  /** サーバー状態 */
  protected HashMap fData;

  /** 現在の日付（U-Mart暦)を引くためのキー */
  public static final String INT_DATE = "INT_DATE";

  /** 現在の板寄せ回数を引くためのキー */
  public static final String INT_BOARD_NO = "INT_BOARD_NO";

  /** 時間帯を引くためのキー */
  public static final String INT_STATE = "INT_STATE";

  /**　コンストラクタ */
  public UCServerStatusCore() {
    super();
    fCommandStatus = new UCommandStatus();
    fData = new HashMap();
  }

  public boolean isNameEqualTo(String name) {
    if (name.equalsIgnoreCase(CMD_NAME)) {
      return true;
    } else {
      return false;
    }
  }

  public String getName() {
    return CMD_NAME;
  }

  public boolean setArguments(StringTokenizer st) {
    try {
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public void setArguments() {
  }

  public UCommandStatus getStatus() {
    return fCommandStatus;
  }

  public void printOn() {
    System.out.println(fData.toString());
  }

  public HashMap getData() {
    return fData;
  }

  public String getResultString() {
    String returnStr = "";
    returnStr += fData.get(UCServerStatusCore.INT_DATE).toString() + "\n";
    returnStr += fData.get(UCServerStatusCore.INT_BOARD_NO).toString() + "\n";
    returnStr += fData.get(UCServerStatusCore.INT_STATE).toString() + "\n";
    return returnStr;
  }

}
