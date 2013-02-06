package org.umart.cmdCore;

import java.util.*;

/**
 * UCPositionCoreクラスは, ポジション照会のためのコマンドオブジェクトである.
 * @author 川部 祐司
 */
public abstract class UCPositionCore implements ICommand {

  /** 当日の売り建玉数を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_TODAY_SELL = "LONG_TODAY_SELL";

  /** 当日の買い建玉数を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_TODAY_BUY = "LONG_TODAY_BUY";

  /** 前日までの売り建玉数を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_YESTERDAY_SELL = "LONG_YESTERDAY_SELL";

  /** 前日までの買い建玉数を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_YESTERDAY_BUY = "LONG_YESTERDAY_BUY";

  /** コマンド名 */
  public static final String CMD_NAME = "Position";

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** 結果を格納するためのHashMap */
  protected HashMap fPositionInfo;

  /**
   * コンストラクタ
   */
  public UCPositionCore() {
    super();
    fPositionInfo = new HashMap();
    fStatus = new UCommandStatus();
  }

  /**
   * @see org.umart.cmdCore.ICommand#isNameEqualTo(String)
   */
  public boolean isNameEqualTo(String name) {
    if (name.equalsIgnoreCase(CMD_NAME)) {
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
   * @see org.umart.cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = fPositionInfo.get(LONG_TODAY_SELL) + " "
        + fPositionInfo.get(LONG_TODAY_BUY) + " "
        + fPositionInfo.get(LONG_YESTERDAY_SELL) + " "
        + fPositionInfo.get(LONG_YESTERDAY_BUY);
    return result;
  }

  /**
   * 注文依頼情報を返す．
   * @return 注文依頼情報
   */
  public HashMap getResults() {
    return fPositionInfo;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<Position>>");
    System.out.println("TodaySell:" + fPositionInfo.get(LONG_TODAY_SELL) + ","
                       + "TodayBuy:" + fPositionInfo.get(LONG_TODAY_BUY) + ","
                       + "YesterdaySell:" +
                       fPositionInfo.get(LONG_YESTERDAY_SELL) + ","
                       + "YesterdayBuy:" + fPositionInfo.get(LONG_YESTERDAY_BUY));
  }
}
