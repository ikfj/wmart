package org.umart.cmdCore;

import java.util.*;

/**
 * UCMarketStatusCoreクラスは,
 * マーケット状態問い合わせのためのコマンドオブジェクトである.
 * @author 川部 祐司
 */
public abstract class UCMarketStatusCore implements ICommand {

  /** マーケット状態を引くためのキー(値はIntegerオブジェクト) */
  public static final String INT_MARKET_STATUS = "INT_MARKET_STATUS";

  /** コマンド名 */
  public static final String CMD_NAME = "MarketStatus";

  /** 別名 */
  public static final String CMD_ALIAS = "104";

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** 結果を格納するためのHashMap */
  protected HashMap fMarketInfo;

  /**
   * コンストラクタ
   */
  public UCMarketStatusCore() {
    super();
    fMarketInfo = new HashMap();
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
    return true;
  }

  /**
   * @see org.umart.cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = "" + fMarketInfo.get(INT_MARKET_STATUS);
    return result;
  }

  /**
   * HashMapを返す．
   * @return HashMap
   */
  public HashMap getMarketInfo() {
    return fMarketInfo;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<MarketStatus>>");
    System.out.println("MarketStatus:"
                       + fMarketInfo.get(INT_MARKET_STATUS));
  }
}
