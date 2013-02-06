package org.umart.cmdCore;

import java.util.*;

/**
 * UCHistoricalQuotesCoreクラスは,
 * 過去の日足での価格推移を取得するためのコマンドオブジェクトである．
 * @author 川部 祐司
 */
public abstract class UCHistoricalQuotesCore implements ICommand {

  /** 銘柄名を引くためのキー(値はStringオブジェクト) */
  public static final String STRING_BRAND_NAME = "STRING_BRAND_NAME";

  /** 日付を引くためのキー(値はIntegerオブジェクト) */
  public static final String INT_DATE = "INT_DATE";

  /** 始値を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_START_PRICE = "LONG_START_PRICE";

  /** 高値を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_HIGHEST_PRICE = "LONG_HIGHEST_PRICE";

  /** 安値を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_LOWEST_PRICE = "LONG_LOWEST_PRICE";

  /** 終値を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_END_PRICE = "LONG_END_PRICE";

  /** 出来高を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_VOLUME = "LONG_VOLUME";

  /** コマンド名 */
  public static final String CMD_NAME = "HistoricalQuotes";

  /** 銘柄名 */
  protected String fBrandName;

  /** 必要な情報の日数 */
  protected int fNoOfDays;

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** 結果を格納するためのArrayList */
  protected ArrayList fQuotesArray;

  /**
   * コンストラクタ
   */
  public UCHistoricalQuotesCore() {
    super();
    fBrandName = "";
    fNoOfDays = -1;
    fQuotesArray = new ArrayList();
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
    try {
      fBrandName = st.nextToken();
      fNoOfDays = Integer.parseInt(st.nextToken());
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * コマンド実行に必要な引数を設定する．
   * @param brandName
   */
  public void setArguments(String brandName, int noOfDays) {
    fBrandName = brandName;
    fNoOfDays = noOfDays;
  }

  /**
   * @see org.umart.cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = "";
    Iterator itr = fQuotesArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      result += os.get(STRING_BRAND_NAME) + " "
          + os.get(INT_DATE) + " "
          + os.get(LONG_START_PRICE) + " "
          + os.get(LONG_HIGHEST_PRICE) + " "
          + os.get(LONG_LOWEST_PRICE) + " "
          + os.get(LONG_END_PRICE) + " "
          + os.get(LONG_VOLUME) + " ";
    }
    return result;
  }

  /**
   * 過去の価格推移を返す．
   * @return 過去の価格推移
   */
  public ArrayList getResults() {
    return fQuotesArray;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<HistoricalQuotes " + fBrandName + " " + fNoOfDays
                       + ">>");
    Iterator itr = fQuotesArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      System.out.println("BrandName:" + os.get(STRING_BRAND_NAME) + ","
                         + "Date:" + os.get(INT_DATE) + ","
                         + "StartPrice:" + os.get(LONG_START_PRICE) + ","
                         + "HighestPrice:" + os.get(LONG_HIGHEST_PRICE) + ","
                         + "LowestPrice:" + os.get(LONG_LOWEST_PRICE) + ","
                         + "EndPrice:" + os.get(LONG_END_PRICE) + ","
                         + "Volume:" + os.get(LONG_VOLUME));
    }
  }
}
