package org.umart.cmdCore;

import java.util.*;

/**
 * すべての参加者の売買ポジションを照会するためのSVMPコマンドの抽象クラスです．
 * @author 川部 祐司
 */
public abstract class UCAllPositionsCore implements ICommand {

  /** エージェント名を引くためのキー(値はStringオブジェクト) */
  public static final String STRING_NAME = "STRING_NAME";

  /** 昨日までの売り建玉数を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_SELL_POSITION = "LONG_SELL_POSITION";

  /** 昨日までの買い建玉数を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_BUY_POSITION = "LONG_BUY_POSITION";

  /** コマンド名 */
  public static final String CMD_NAME = "AllPositions";

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** 結果を格納するためのArrayList */
  protected ArrayList fAllPositionsArray;

  /**
   * コンストラクタ
   */
  public UCAllPositionsCore() {
    super();
    fAllPositionsArray = new ArrayList();
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
    Iterator itr = fAllPositionsArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      result += os.get(STRING_NAME) + " "
          + os.get(LONG_SELL_POSITION) + " "
          + os.get(LONG_BUY_POSITION) + " ";
    }
    return result;
  }

  /**
   * すべての参加者の売買ポジションを返す．
   * @return すべての参加者の売買ポジション
   */
  public ArrayList getResults() {
    return fAllPositionsArray;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<AllPositions>>");
    Iterator itr = fAllPositionsArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      System.out.println("Name:" + os.get(STRING_NAME) + ","
                         + "SellPosition:" + os.get(LONG_SELL_POSITION) + ","
                         + "BuyPosition:" + os.get(LONG_BUY_POSITION));
    }
  }
}
