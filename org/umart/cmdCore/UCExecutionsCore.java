package org.umart.cmdCore;

import java.util.*;

/**
 * 約定照会のためのSVMPコマンドの抽象クラスです．
 * @author 川部 祐司
 */
public abstract class UCExecutionsCore implements ICommand {

  /** 約定IDを引くためのキー(値はLongオブジェクト) */
  public static final String LONG_CONTRACT_ID = "LONG_CONTRACT_ID";

  /** 約定時刻(実時間)を引くためのキー(値はStringオブジェクト) */
  public static final String STRING_CONTRACT_TIME = "STRING_CONTRACT_TIME";

  /** 注文IDを引くためのキー(値はLongオブジェクト) */
  public static final String LONG_ORDER_ID = "LONG_ORDER_ID";

  /** 銘柄IDを引くためのキー(値はStringオブジェクト) */
  public static final String STRING_BRAND_NAME = "STRING_BRAND_NAME";

  /** 新規返済区分を引くためのキー(値はIntegerオブジェクト) */
  public static final String INT_NEW_REPAY = "INT_NEW_REPAY";

  /** 売買区分を引くためのキー(値はIntegerオブジェクト) */
  public static final String INT_SELL_BUY = "INT_SELL_BUY";

  /** 価格を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_PRICE = "LONG_PRICE";

  /** 約定数量を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_VOLUME = "LONG_VOLUME";

  /** コマンド名 */
  public static final String CMD_NAME = "Executions";

  /** 別名 */
  public static final String CMD_ALIAS = "204";

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** 結果を格納するためのArrayList */
  protected ArrayList fExecutionsArray;

  /**
   * コンストラクタ
   */
  public UCExecutionsCore() {
    super();
    fExecutionsArray = new ArrayList();
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
    Iterator itr = fExecutionsArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      result += os.get(LONG_CONTRACT_ID) + " "
                 + os.get(STRING_CONTRACT_TIME) + " "
                 + os.get(LONG_ORDER_ID) + " "
                 + os.get(STRING_BRAND_NAME) + " "
                 + os.get(INT_NEW_REPAY) + " "
                 + os.get(INT_SELL_BUY) + " "
                 + os.get(LONG_PRICE) + " "
                 + os.get(LONG_VOLUME);
    }
    return result;
  }

  /**
   * 約定情報を返す．
   * @return 約定情報
   */
  public ArrayList getResults() {
    return fExecutionsArray;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<Executions>>");
    Iterator itr = fExecutionsArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      System.out.println("ContractID:" + os.get(LONG_CONTRACT_ID) + ","
                          + "ContractTime:" + os.get(STRING_CONTRACT_TIME) + ","
                          + "OrderID:" + os.get(LONG_ORDER_ID) + ","
                          + "BrandName:" + os.get(STRING_BRAND_NAME) + ","
                          + "NewRepay:" + os.get(INT_NEW_REPAY) + ","
                          + "SellBuy:" + os.get(INT_SELL_BUY) + ","
                          + "Price:" + os.get(LONG_PRICE) + ","
                          + "Volume:" + os.get(LONG_VOLUME));
    }
  }
}
