package org.umart.cmdCore;

import java.util.*;

/**
 * UCOrderCancelCoreクラスは, 注文取消のためのコマンドオブジェクトである.
 * @author 小野 功
 */
public abstract class UCOrderCancelCore implements ICommand {

  /** 注文IDを引くためのキー(値はIntegerオブジェクト) */
  public static final String LONG_ORDER_ID = "LONG_ORDER_ID";

  /** 取消受付時刻を引くためのキー(値はStringオブジェクト) */
  public static final String STRING_CANCEL_TIME = "STRING_CANCEL_TIME";

  /** 取消数量を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_CANCEL_VOLUME = "LONG_CANCEL_VOLUME";

  /** 非取消数量を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_UNCANCEL_VOLUME = "LONG_UNCANCEL_VOLUME";

  /** コマンド名 */
  public static final String CMD_NAME = "OrderCancel";

  /** 別名 */
  public static final String CMD_ALIAS = "202";

  /** 注文ID */
  protected long fOrderID;

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** 結果を格納するためのHashMap */
  protected HashMap fCancelInfo;

  /**
   * コンストラクタ
   */
  public UCOrderCancelCore() {
    super();
    fOrderID = -1;
    fCancelInfo = new HashMap();
    fStatus = new UCommandStatus();
  }

  /**
   * @see org.umart.cmdCore.ICommand#isNameEqualTo(String)
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
    try {
      fOrderID = Long.parseLong(st.nextToken());
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * コマンド実行に必要な引数を設定する．
   * @param orderID
   */
  public void setArguments(long orderID) {
    fOrderID = orderID;
  }

  /**
   * @see org.umart.cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = fCancelInfo.get(STRING_CANCEL_TIME) + " "
        + fCancelInfo.get(LONG_CANCEL_VOLUME) + " "
        + fCancelInfo.get(LONG_UNCANCEL_VOLUME);
    return result;
  }

  /**
   * 注文取消情報を返す．
   * @return 注文取消情報
   */
  public HashMap getResults() {
    return fCancelInfo;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<OrderCancel " + fOrderID + ">>");
    System.out.println("OrderID:" + fCancelInfo.get(LONG_ORDER_ID) + ","
                       + "CancelTime:" + fCancelInfo.get(STRING_CANCEL_TIME)
                       + ","
                       + "CancelVolume:" + fCancelInfo.get(LONG_CANCEL_VOLUME)
                       + ","
                       + "UncanceldVolume:"
                       + fCancelInfo.get(LONG_UNCANCEL_VOLUME));
  }
}
