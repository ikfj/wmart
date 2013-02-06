package org.umart.serverCore;

import java.io.*;

/**
 * 取引所(su)の口座情報を扱うクラスです．
 * 口座情報としては，現金残高，売りポジション，買いポジションを
 * 扱っています．
 * @author 川部 裕司
 * @author 森下 領平
 * @author 小野 功
 */
public class UExchangeAccount {

  /** ユーザーID */
  private final int fUserID = 0;

  /** ユーザー名 */
  private final String fUserName = "su";

  /** パスワード */
  private final String fPasswd = "supasswd";

  /** 現金残高 */
  private long fCash;

  /** 売りポジション (破産者の売りポジションを引き継ぐ) */
  private long fSellPosition;

  /** 買いポジション (破産者の買いポジションを引き継ぐ) */
  private long fBuyPosition;

  /**
   * 初期化された取引所の口座を生成する．
   */
  public UExchangeAccount() {
    fCash = 0;
    fSellPosition = fBuyPosition = 0;
  }

  /**
   * UExchangeAccountの複製を生成する．
   * @return UExchangeAccountオブジェクトの複製
   */
  public Object clone() {
    UExchangeAccount result = new UExchangeAccount();
    result.fCash = fCash;
    result.fSellPosition = fSellPosition;
    result.fBuyPosition = fBuyPosition;
    return result;
  }

  /**
   * 内部情報を出力する．
   * @param pw 出力先
   */
  public void printOn(PrintWriter pw) {
    try {
      pw.println("fUserID = " + fUserID);
      pw.println("fUserName = " + fUserName);
      pw.println("fPasswd = " + fPasswd);
      pw.println("fCash = " + fCash);
      pw.println("fSellPosition = " + fSellPosition);
      pw.println("fBuyPosition = " + fBuyPosition);
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
  }

  /**
   * ユーザーIDを返す。
   * @return ユーザーID
   */
  public int getUserID() {
    return fUserID;
  }

  /**
   * ユーザー名を返す．
   * @return ユーザー名
   */
  public String getUserName() {
    return fUserName;
  }

  /**
   * パスワードを返す．
   * @return パスワード
   */
  public String getPasswd() {
    return fPasswd;
  }

  /**
   * 買いポジションを返す．
   * @return 買いポジション
   */
  public long getBuyPosition() {
    return fBuyPosition;
  }

  /**
   * 売りポジションを返す．
   * @return 売りポジション
   */
  public long getSellPosition() {
    return fSellPosition;
  }

  /**
   * 買いポジションを設定する．
   * @param buyPosition 買いポジション
   */
  public void setBuyPosition(long buyPosition) {
    fBuyPosition = buyPosition;
  }

  /**
   * 売りポジションを設定する．
   * @param sellPosition 売りポジション
   */
  public void setSellPosition(long sellPosition) {
    fSellPosition = sellPosition;
  }

  /**
   * 現金残高を返す．
   * @return 現金残高
   */
  public synchronized long getCash() {
    // System.err.println(Thread.currentThread().getName());
    return fCash;
  }

  /**
   * 現金残高を設定する．
   * @param cash 現金残高
   */
  public synchronized void setCash(long cash) {
    // System.err.println(Thread.currentThread().getName());
    fCash = cash;
  }

  /**
   * cashを現在の現金残高に加える．
   * @param cash 現金残高の増分
   */
  public synchronized void addCash(long cash) {
    // System.err.println(Thread.currentThread().getName());
    fCash += cash;
    //System.out.println(cash + "," + fCash);
  }

  /**
   * sellPositionを現在の売りポジションに加える．
   * @param sellPosition 売りポジションの増分
   */
  public void addSellPosition(long sellPosition) {
    fSellPosition += sellPosition;
  }

  /**
   * buyPositionを現在の買いポジションに加える．
   * @param buyPosition 買いポジションの増分
   */
  public void addBuyPosition(long buyPosition) {
    fBuyPosition += buyPosition;
  }

  /**
   * 売り/買いポジションを0に初期化する．
   */
  public void clearPosition() {
    fSellPosition = 0;
    fBuyPosition = 0;
  }

}
