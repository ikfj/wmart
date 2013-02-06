package org.umart.cmdCore;

import java.util.*;

/**
 * 先物価格を取得するためのSVMPコマンドの抽象クラスです．
 * @author 川部 祐司
 */
public abstract class UCFuturePriceCore implements ICommand {

  /** 銘柄名を引くためのキー(値はStringオブジェクト) */
  public static final String STRING_BRAND_NAME = "STRING_BRAND_NAME";

  /** 日付を引くためのキー(値はIntegerオブジェクト) */
  public static final String INT_DAY = "INT_DAY";

  /** 板寄せ回数を引くためのキー(値はIntegerオブジェクト) */
  public static final String INT_BOARD_NO = "INT_BOARD_NO";

  /** ステップ数を引くためのキー(値はIntegerオブジェクト) */
  public static final String INT_STEP = "INT_STEP";

  /** 先物価格を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_PRICE = "LONG_PRICE";

  /** コマンド名 */
  public static final String CMD_NAME = "FuturePrice";

  /** 銘柄名 */
  protected String fBrandName;

  /** 必要な情報のステップ数 */
  protected int fNoOfSteps;

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** 結果を格納するためのArrayList */
  protected ArrayList fFutureArray;

  /**
   * コンストラクタ
   */
  public UCFuturePriceCore() {
    super();
    fBrandName = "";
    fNoOfSteps = -1;
    fFutureArray = new ArrayList();
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
      fNoOfSteps = Integer.parseInt(st.nextToken());
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * コマンド実行に必要な引数を設定する．
   * @param brandName
   * @param noOfSteps
   */
  public void setArguments(String brandName, int noOfSteps) {
    fBrandName = brandName;
    fNoOfSteps = noOfSteps;
  }

  /**
   * @see org.umart.cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = "";
    Iterator itr = fFutureArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      result += os.get(STRING_BRAND_NAME) + " "
                 + os.get(INT_DAY) + " "
          + os.get(INT_BOARD_NO) + " "
          + os.get(INT_STEP) + " "
          + os.get(LONG_PRICE) + " ";
    }
    return result;
  }

  /**
   * 先物価格を返す．
   * @return 先物価格
   */
  public ArrayList getResults() {
    return fFutureArray;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<FuturePrice " + fBrandName + fNoOfSteps + ">>");
    Iterator itr = fFutureArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      System.out.println(
          "BrandName:"
          + os.get(STRING_BRAND_NAME)
          + ","
          + "Day:"
          + os.get(INT_DAY)
          + ","
          + "BoardNo:"
          + os.get(INT_BOARD_NO)
          + ","
          + "Step:"
          + os.get(INT_STEP)
          + ","
          + "Price:"
          + os.get(LONG_PRICE));
    }
  }
}
