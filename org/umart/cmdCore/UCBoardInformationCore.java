package org.umart.cmdCore;

import java.util.*;

/**
 * 板情報を取得するためのSVMPコマンドの抽象クラスです．
 * @author 川部 祐司
 */
public abstract class UCBoardInformationCore implements ICommand {

  /** 板寄せが行われた時刻(実時間)を引くためのキー(値はStringオブジェクト) */
  public static final String STRING_LAST_UPDATE_TIME =
      "STRING_LAST_UPDATE_TIME";

  /** 板（（価格，買注文数，売注文数）の配列）を引くためのキー（値はArrayListオブジェクト） */
  public static final String ARRAYLIST_BOARD = "ARRAYLIST_BOARD";

  /** 価格を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_PRICE = "LONG_PRICE";

  /** 買い注文数量を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_BUY_VOLUME = "LONG_BUY_VOLUME";

  /** 売り注文数量を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_SELL_VOLUME = "LONG_SELL_VOLUME";

  /** コマンド名 */
  public static final String CMD_NAME = "BoardInformation";

  /** 別名 */
  public static final String CMD_ALIAS = "302";

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** 結果を格納するためのHashMap */
  protected HashMap fBoardInfo;

  /**
   * コンストラクタ
   */
  public UCBoardInformationCore() {
    fBoardInfo = new HashMap();
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
    result += fBoardInfo.get(STRING_LAST_UPDATE_TIME);
    ArrayList board = (ArrayList) fBoardInfo.get(ARRAYLIST_BOARD);
    Iterator itr = board.iterator();
    while (itr.hasNext()) {
      HashMap elem = (HashMap) itr.next();
      result += " " + elem.get(LONG_PRICE)
          + " " + elem.get(LONG_BUY_VOLUME)
          + " " + elem.get(LONG_SELL_VOLUME);
    }
    return result;
  }

  /**
   * 板情報(HashMap)を返す．
   * @return 板情報
   */
  public HashMap getResults() {
    return fBoardInfo;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<BoardInformation>>");
    System.out.println("LastUpdateTime:" +
                       fBoardInfo.get(STRING_LAST_UPDATE_TIME));
    Iterator itr = ( (ArrayList) fBoardInfo.get(ARRAYLIST_BOARD)).iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      System.out.println("Price:" + os.get(LONG_PRICE) + ","
                         + "BuyVolume:" + os.get(LONG_BUY_VOLUME) + ","
                         + "SellVolume:" + os.get(LONG_SELL_VOLUME));
    }
  }
}
