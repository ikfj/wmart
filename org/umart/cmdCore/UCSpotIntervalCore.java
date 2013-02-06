package org.umart.cmdCore;

import java.util.*;

/**
 * UCSpotIntervalCoreクラスは, 現物価格の更新頻度の問い合わせのための
 * コマンドオブジェクトである.
 * @author 川部 祐司
 */
public abstract class UCSpotIntervalCore implements ICommand {

  /** 現物価格の更新頻度を引くためのキー(値はIntegerオブジェクト) */
  public static final String INT_SPOT_INTERVAL = "INT_SPOT_INTERVAL";

  /** コマンド名 */
  public static final String CMD_NAME = "SpotInterval";

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** 結果を格納するためのHashMap */
  protected HashMap fIntervalInfo;

  /**
   * コンストラクタ
   */
  public UCSpotIntervalCore() {
    super();
    fIntervalInfo = new HashMap();
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
    String result = "";
    return result;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * 現物価格の更新頻度を返す．
   * @return 現物価格の更新頻度
   */
  public HashMap getResults() {
    return fIntervalInfo;
  }

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<SpotInterval>>");
    System.out.println("SpotInterval:"
                       + fIntervalInfo.get(INT_SPOT_INTERVAL));
  }
}
