package org.umart.serverCore;

/**
 * 板寄せごとの約定価格と約定数量を保持するためのクラスです．
 * また, 約定なしの節については売り気配と買い気配を
 * 保持しています．
 * @author 川部 裕司
 * @author 森下 領平
 * @author 小野 功
 */

public class UTodaysQuote {

  /** 銘柄名 */
  private String fBrandName;

  /** 日 */
  private int fDate;

  /** 節 */
  private int fSession;

  /** 約定価格 */
  private long fPrice;

  /** 約定数量 */
  private long fVolume;

  /** 売り気配 */
  private long fAskedQuotation;

  /** 買い気配 */
  private long fBidQuotation;

  /**
   * UTodaysQuoteオブジェクトの生成, 初期化を行う。<br>
   */
  public UTodaysQuote() {
    fBrandName = null;
    fDate = -1;
    fSession = -1;
    fPrice = -1;
    fVolume = -1;
    fAskedQuotation = -1;
    fBidQuotation = -1;
  }

  /**
   * UTodaysQuoteオブジェクトの生成, 初期化を行う。<br>
   * @param brandName 銘柄名
   * @param date 日付
   * @param session 節
   * @param price 約定価格
   * @param volume 約定数量
   * @param askedQuotation 売り気配
   * @param bidQuotation 買い気配
   */
  public UTodaysQuote(String brandName, int date, int session, long price,
                       long volume, long askedQuotation, long bidQuotation) {
    fBrandName = brandName;
    fDate = date;
    fSession = session;
    fPrice = price;
    fVolume = volume;
    fAskedQuotation = askedQuotation;
    fBidQuotation = bidQuotation;
  }

  /**
   * UTodaysQuoteオブジェクトの複製を返す。
   * @return UTodaysQuoteオブジェクトの複製
   */
  public Object clone() {
    return new UTodaysQuote(fBrandName, fDate, fSession, fPrice,
                              fVolume, fAskedQuotation, fBidQuotation);
  }

  /**
   * 内部情報を出力する。
   */
  public void printOn() {
    System.out.print("BrandName:" + fBrandName);
    System.out.print(", Date:" + fDate);
    System.out.print(", BoardNo:" + fSession);
    System.out.print(", Price:" + fPrice);
    System.out.print(", Volume:" + fVolume);
    System.out.print(", AskedQuotation:" + fAskedQuotation);
    System.out.println(", BidQuotation:" + fBidQuotation);
  }

  /**
   * 銘柄名を返す。
   * @return 銘柄名
   */
  public String getBrandName() {
    return fBrandName;
  }

  /**
   * 日付を返す。
   * @return 日付
   */
  public int getDate() {
    return fDate;
  }

  /**
   * 節を返す。
   * @return 節
   */
  public int getSession() {
    return fSession;
  }

  /**
   * 約定価格を返す。
   * @return 約定価格
   */
  public long getPrice() {
    return fPrice;
  }

  /**
   * 約定数量を返す。
   * @return 約定数量
   */
  public long getVolume() {
    return fVolume;
  }

  /**
   * 売り気配を返す。
   * @return 売り気配
   */
  public long getAskedQuotation() {
    return fAskedQuotation;
  }

  /**
   * 買い気配を返す。
   * @return 買い気配
   */
  public long getBidQuotation() {
    return fBidQuotation;
  }

  /**
   * 銘柄名を設定する。
   * @param brandName 銘柄名
   */
  public void setBrandName(String brandName) {
    fBrandName = brandName;
  }

  /**
   * 日付を設定する。
   * @param date 日付
   */
  public void setDate(int date) {
    fDate = date;
  }

  /**
   * 節を設定する。
   * @param session 節
   */
  public void setSession(int session) {
    fSession = session;
  }

  /**
   * 約定価格を設定する。
   * @param price 約定価格
   */
  public void setPrice(long price) {
    fPrice = price;
  }

  /**
   * 約定数量を設定する。
   * @param volume 約定数量
   */
  public void setVolume(long volume) {
    fVolume = volume;
  }

  /**
   * 売り気配を設定する。
   * @param askedQuotation 売り気配
   */
  public void setAskedQuotation(long askedQuotation) {
    fAskedQuotation = askedQuotation;
  }

  /**
   * 買い気配を設定する。
   * @param bidQuotation 買い気配
   */
  public void setBidQuotation(long bidQuotation) {
    fBidQuotation = bidQuotation;
  }

}
