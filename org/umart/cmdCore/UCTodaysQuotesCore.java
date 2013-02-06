package org.umart.cmdCore;

import java.util.*;

/**
 * UCTodaysQuotesCoreクラスは, 当日価格照会のためのコマンドオブジェクトである．
 * @author 川部 祐司
 */
public abstract class UCTodaysQuotesCore implements ICommand {

  /** 銘柄名を引くためのキー(値はStringオブジェクト) */
  public static final String STRING_BRAND_NAME = "STRING_BRAND_NAME";

  /** 日付を引くためのキー(値はIntegerオブジェクト) */
  public static final String INT_DATE = "INT_DATE";

  /** 現在の板寄せ回数を引くためのキー(値はIntegerオブジェクト) */
  public static final String INT_BOARD_NO = "INT_BOARD_NO";

  /** 時刻を引くためのキー(値はStringオブジェクト) */
  public static final String STRING_TIME = "STRING_TIME";

  /** 約定価格を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_PRICE = "LONG_PRICE";

  /** 約定数量を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_VOLUME = "INT_VOLUME";

  /** 売り気配を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_ASKED_QUOTATION = "LONG_ASKED_QUOTATION";

  /** 買い気配を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_BID_QUOTATION = "LONG_BID_QUOTATION";

  /** コマンド名 */
  public static final String CMD_NAME = "TodaysQuotes";

  /** 別名 */
  public static final String CMD_ALIAS = "301";

  /** 銘柄名 */
  protected String fBrandName;

  /** 必要な情報の板寄せ数 */
  protected int fNoOfBoards;

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** 結果を格納するためのArrayList */
  protected ArrayList fQuotesArray;

  /**
   * コンストラクタ
   */
  public UCTodaysQuotesCore() {
    super();
    fBrandName = "";
    fNoOfBoards = -1;
    fQuotesArray = new ArrayList();
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
      fBrandName = st.nextToken();
      fNoOfBoards = Integer.parseInt(st.nextToken());
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * コマンド実行に必要な引数を設定する．
   * @param brandName
   * @param noOfBoards
   */
  public void setArguments(String brandName, int noOfBoards) {
    fBrandName = brandName;
    fNoOfBoards = noOfBoards;
  }

  /**
   * @see org.umart.cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = "";
    Iterator itr = fQuotesArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      result += os.get(STRING_BRAND_NAME)
          + " "
          + os.get(INT_DATE)
          + " "
          + os.get(INT_BOARD_NO)
          + " "
          + os.get(STRING_TIME)
          + " "
          + os.get(LONG_PRICE)
          + " "
          + os.get(LONG_VOLUME)
          + " "
          + os.get(LONG_ASKED_QUOTATION)
          + " "
          + os.get(LONG_BID_QUOTATION)
          + " ";
    }
    return result;
  }

  /**
   * 当日価格照会を返す．
   * @return 当日価格照会
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
    System.out.println("<<TodaysQuotes " + fBrandName + " " + fNoOfBoards +
                       ">>");
    Iterator itr = fQuotesArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      System.out.println(os.toString());
    }
  }
}
