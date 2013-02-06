package org.umart.serverCore;

/**
 * 日ごとの始値, 高値, 安値, 終値, 出来高を保持するためのクラスです．
 * @author 川部 裕司
 * @author 森下 領平
 * @author 小野 功
 */
public class UHistoricalQuote {

  /** 銘柄名 */
  private String fBrandName;

  /** 日付 */
  private int fDate;

  /** 始値 */
  private long fStartPrice;

  /** 高値 */
  private long fHighestPrice;

  /** 安値 */
  private long fLowestPrice;

  /** 終値 */
  private long fEndPrice;

  /** 出来高 */
  private long fVolume;

  /**
   * UHistoricalQuoteオブジェクトの生成, 初期化を行う。
   */
  public UHistoricalQuote() {
    fBrandName = null;
    fDate = -1;
    fStartPrice = -1;
    fHighestPrice = -1;
    fLowestPrice = -1;
    fEndPrice = -1;
    fVolume = -1;
  }

  /**
   * UHistoricalQuoteオブジェクトの生成, 初期化を行う。
   * @param brandName 銘柄
   * @param date 日付
   * @param startPrice 始値
   * @param highestPrice 高値
   * @param lowestPrice 安値
   * @param endPrice 終値
   * @param 出来高
   */
  public UHistoricalQuote(String brandName, int date, 
  		                     long startPrice, long highestPrice, 
  		                     long lowestPrice, long endPrice,
                           long volume) {
    fBrandName = brandName;
    fDate = date;
    fStartPrice = startPrice;
    fHighestPrice = highestPrice;
    fLowestPrice = lowestPrice;
    fEndPrice = endPrice;
    fVolume = volume;
  }

  /**
   * UHistoricalQuoteオブジェクトの複製を返す。
   * @return UHistoricalQuoteオブジェクトの複製
   */
  public Object clone() {
    return new UHistoricalQuote(fBrandName, fDate, fStartPrice, fHighestPrice,
                                  fLowestPrice, fEndPrice, fVolume);
  }

  /**
   * 内部情報を出力する。
   */
  public void printOn() {
    System.out.print("BrandName:" + fBrandName);
    System.out.print(", Date:" + fDate);
    System.out.print(", StartPrice:" + fStartPrice);
    System.out.print(", HighestPrice:" + fHighestPrice);
    System.out.print(", LowestPrice:" + fLowestPrice);
    System.out.print(", EndPrice:" + fEndPrice);
    System.out.println(", Volume:" + fVolume);
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
   * 始値を返す。
   * @return 始値
   */
  public long getStartPrice() {
    return fStartPrice;
  }

  /**
   * 高値を返す。
   * @return 高値
   */
  public long getHighestPrice() {
    return fHighestPrice;
  }

  /**
   * 安値を返す。
   * @return 安値
   */
  public long getLowestPrice() {
    return fLowestPrice;
  }

  /**
   * 終値を返す。
   * @return 終値
   */
  public long getEndPrice() {
    return fEndPrice;
  }

  /**
   * 出来高を返す。
   * @return 出来高
   */
  public long getVolume() {
    return fVolume;
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
   * 始値を設定する。
   * @param startPrice 始値
   */
  public void setStartPrice(long startPrice) {
    fStartPrice = startPrice;
  }

  /**
   * 高値を設定する。
   * @param highestPrice 高値
   */
  public void setHighestPirce(long highestPrice) {
    fHighestPrice = highestPrice;
  }

  /**
   * 安値を設定する。
   * @param lowestPrice 安値
   */
  public void setLowestPrice(long lowestPrice) {
    fLowestPrice = lowestPrice;
  }

  /**
   * 終値を設定する。
   * @param endPrice 終値
   */
  public void setEndPrice(long endPrice) {
    fEndPrice = endPrice;
  }

  /**
   * 出来高を設定する。
   * @param volume 出来高
   */
  public void setVolume(long volume) {
    fVolume = volume;
  }

}
