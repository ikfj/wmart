package org.umart.serverCore;

/**
 * 日，節，サーバー状態を管理するクラスです．
 * サーバー状態には，取引き開始前時間帯，注文受付時間帯，
 * 板寄せ時間帯，値洗い時間帯，取引後時間帯，決算時間帯，
 * 決算後時間帯があります．
 * @author 小野　功
 */
public class UServerStatus {

  /** 取引き開始前時間帯を表す定数 */
  public static final int BEFORE_TRADING = 0;

  /** 注文受付時間帯を表す定数 */
  public static final int ACCEPT_ORDERS = 1;

  /** 板寄せ時間帯を表す定数 */
  public static final int ITAYOSE = 3;

  /** 値洗い時間帯を表す定数 */
  public static final int MARKING_TO_MARKET = 4;

  /** 取引後時間帯を表す定数 */
  public static final int AFTER_MARKING_TO_MARKET = 5;

  /** 決算時間帯を表す定数 */
  public static final int SETTLEMENT = 6;

  /** 決算後時間帯を表す定数 */
  public static final int AFTER_SETTLEMENT = 7;

  /** スーパーユーザーログイン待ち時間帯 */
  public static final int SU_LOGIN = 9;

  /** サーバー状態(時間帯)を表す文字列の配列 */
  private static final String[] SERVER_STATES = {"BEFORE_TRADING",
      "ACCEPT_ORDERS",
      "RESERVED",
      "ITAYOSE",
      "MARKING_TO_MARKET",
      "AFTER_MARKING_TO_MARKET",
      "SETTLEMENT",
      "AFTER_SETTLEMENT",
      "RESERVERD",
      "SU_LOGIN",
  };

  /** 現在の日付 (U-Mart暦) */
  private int fDate;

  /** 現在の節 */
  private int fSession;

  /** サーバー状態 (時間帯) */
  private int fState;

  /**
   * 	コンストラクタ
   */
  public UServerStatus() {
    fState = UServerStatus.SU_LOGIN;
    fDate = 1;
    fSession = 1;
  }

  /**
   * 	コピーコンストラクタ
   */
  public UServerStatus(UServerStatus src) {
    fState = src.fState;
    fDate = src.fDate;
    fSession = src.fSession;
  }

  /**
   * 複製を生成
   * @return 複製
   */
  public Object clone() {
    return new UServerStatus(this);
  }

  /**
   * 内部状態を出力する．
   */
  public void printOn() {
    System.out.println("Date:" + fDate
                       + ", Session:" + fSession
                       + ", Status:" + getStateString());
  }

  /**
   * コピー
   * @param src コピー元
   */
  public void copyFrom(UServerStatus src) {
    fState = src.fState;
    fDate = src.fDate;
    fSession = src.fSession;
  }

  /**
     取引所の状態を設定する。
     @param state 取引所の状態
   */
  public void setState(int state) {
    fState = state;
  }

  /**
     取引所の状態を返す。
     @return 取引所の状態
   */
  public int getState() {
    return fState;
  }

  /**
     取引所の状態を表す文字列を返す。
     @return 取引所の状態を表す文字列
   */
  public String getStateString() {
    return SERVER_STATES[fState];
  }

  /**
   * 現在の日付(U-Mart暦)を返す．
   * @return 現在の日付(U-Mart暦)
   */
  public int getDate() {
    return fDate;
  }

  /**
   * 現在の日付(U-Mart暦)を設定する．
   * @param date 現在の日付(U-Mart暦)
   */
  public void setDate(int date) {
    fDate = date;
  }

  /**
   * 日付を1日進める．
   * @return 1日進めた後の日付
   */
  public int incrementDate() {
    ++fDate;
    return fDate;
  }

  /**
   * 現在の節を返す．
   * @return 現在の節
   */
  public int getSession() {
    return fSession;
  }

  /**
   * 現在の節を設定する．
   * @param session 節
   */
  public void setSession(int session) {
    fSession = session;
  }

  /**
   * 節を1進める．
   * @return 1進めた後の節
   */
  public int incrementSession() {
    ++fSession;
    return fSession;
  }

  /**
   * 節を1に戻す．
   * @return 板寄せ回数
   */
  public int resetSession() {
    fSession = 1;
    return fSession;
  }

}
