package org.umart.serverCore;

/**
 * UPriceInfoDBで用いられる一ステップ分の価格情報を取り扱うクラスです．
 * 日，節，現物価格，先物価格を保持しています．
 * @author 川部 裕司
 * @author 森下 領平
 * @author 小野 功
 */
public class UPriceInfo {

	/** 価格が成立していないときに用いる定数 */
  public static final long INVALID_PRICE = -1;

  /** 日 */
  private int fDate;

  /** 節 */
  private int fSession;

  /** 現物価格 */
  private long fSpotPrice;

  /** 先物価格。ただし, 価格が存在しない場合は, -1が設定される。 */
  private long fFuturePrice;

  /**
   * UPriceInfoオブジェクトの生成と初期化を行う。
   */
  public UPriceInfo() {
    fDate = 0;
    fSession = 0;
    fSpotPrice = UPriceInfo.INVALID_PRICE;
    fFuturePrice = UPriceInfo.INVALID_PRICE;
  }

  /**
   * 現物価格を返す。
   * @return 現物価格
   */
  public long getSpotPrice() {
    return fSpotPrice;
  }

  /**
   * 先物価格を返す。
   * @return 先物価格。価格が存在しない場合は, -1が返る。
   */
  public long getFuturePrice() {
    return fFuturePrice;
  }

  /**
   * 現物価格を設定する。
   * @param spotPrice 現物価格
   */
  public void setSpotPrice(long spotPrice) {
    fSpotPrice = spotPrice;
  }

  /**
   * 先物価格を設定する。
   * @param futurePrice 先物価格。価格が存在しない場合は -1を設定する。
   */
  public void setFuturePrice(long futurePrice) {
    fFuturePrice = futurePrice;
  }

  /**
   * 節を返す．
   * @return 節
   */
  public int getSession() {
    return fSession;
  }

  /**
   * 日を返す．
   * @return 日
   */
  public int getDate() {
    return fDate;
  }

  /**
   * 節を設定する．
   * @param session 節
   */
  public void setSession(int session) {
    fSession = session;
  }

  /**
   * 日を設定する．
   * @param date 日
   */
  public void setDate(int date) {
    fDate = date;
  }

}
