package org.umart.cmdCore;

import java.util.*;

/**
 * UCOrderStatusCoreクラスは, 注文照会のためのコマンドオブジェクトである.
 * @author 川部 祐司
 */
public abstract class UCOrderStatusCore implements ICommand {

  /** 注文IDを引くためのキー(値はLongオブジェクト) */
  public static final String LONG_ORDER_ID = "LONG_ORDER_ID";

  /** 注文時刻(実時間)を引くためのキー(値はStringオブジェクト) */
  public static final String STRING_ORDER_TIME = "STRING_ORDER_TIME";

  /** 銘柄名を引くためのキー(値はStringオブジェクト) */
  public static final String STRING_BRAND_NAME = "STRING_BRAND_NAME";

  /** 新規返済区分を引くためのキー(値はIntegerオブジェクト) */
  public static final String INT_NEW_REPAY = "INT_NEW_REPAY";

  /** 売買区分を引くためのキー(値はIntegerオブジェクト) */
  public static final String INT_SELL_BUY = "INT_SELL_BUY";

  /** 成行指値区分を引くためのキー(値はIntegerオブジェクト) */
  public static final String INT_MARKET_LIMIT = "INT_MARKET_LIMIT";

  /** 希望取引価格を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_PRICE = "LONG_PRICE";

  /** 希望取引数量を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_VOLUME = "LONG_VOLUME";

  /** 約定数量を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_CONTRACTED_VOLUME = "LONG_CONTRACTED_VOLUME";

  /** 取消数量を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_CANCEL_VOLUME = "LONG_CANCEL_VOLUME";

  /** コマンド名 */
  public static final String CMD_NAME = "OrderStatus";

  /** 別名 */
  public static final String CMD_ALIAS = "203";

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** 結果を格納するためのArrayList */
  protected ArrayList fStatusArray;

  /**
   * コンストラクタ
   */
  public UCOrderStatusCore() {
    super();
    fStatusArray = new ArrayList();
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
    String result = "";
    Iterator itr = fStatusArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      result = os.get(LONG_ORDER_ID) + " "
          + os.get(STRING_ORDER_TIME) + " "
          + os.get(STRING_BRAND_NAME) + " "
          + os.get(INT_NEW_REPAY) + " "
          + os.get(INT_SELL_BUY) + " "
          + os.get(INT_MARKET_LIMIT) + " "
          + os.get(LONG_PRICE) + " "
          + os.get(LONG_VOLUME) + " "
          + os.get(LONG_CONTRACTED_VOLUME) + " "
          + os.get(LONG_CANCEL_VOLUME) + " ";
    }
    return result;
  }

  /**
   * 注文照会情報を返す．
   * @return 注文照会情報
   */
  public ArrayList getResults() {
    return fStatusArray;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<OrderStatus>>");
    Iterator itr = fStatusArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      System.out.println("OrderID:" + os.get(LONG_ORDER_ID) + ","
                         + "OrderTime:" + os.get(STRING_ORDER_TIME) + ","
                         + "BrandName:" + os.get(STRING_BRAND_NAME) + ","
                         + "NewRepay:" + os.get(INT_NEW_REPAY) + ","
                         + "SellBuy:" + os.get(INT_SELL_BUY) + ","
                         + "MarketLimit:" + os.get(INT_MARKET_LIMIT) + ","
                         + "Price:" + os.get(LONG_PRICE) + ","
                         + "Volume:" + os.get(LONG_VOLUME) + ","
                         + "ContractVolume:" + os.get(LONG_CONTRACTED_VOLUME)
                         + ","
                         + "CancelVolume:" + os.get(LONG_CANCEL_VOLUME));
    }
  }
}
