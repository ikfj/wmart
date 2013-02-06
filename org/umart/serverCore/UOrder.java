package org.umart.serverCore;

import java.io.*;
import java.util.*;

/**
 * 注文情報を扱うクラスです．注文情報としては，
 * ユーザーID，銘柄名，注文ID，注文時刻(実時間)，注文日，
 * 注文節，売り/買い，成行/指値，新規/返済，注文価格，
 * 注文数量，約定数量，取消数量，約定情報を扱っています．
 * @author 川部 裕司
 * @author 森下 領平
 * @author 小野 功
 */
public class UOrder {

  /** 「売り」を表す定数 */
  public static int SELL = 1;

  /** 「買い」を表す定数 */
  public static int BUY = 2;

  /** 「成行」を表す定数 */
  public static int MARKET = 1;

  /** 「指値」を表す定数 */
  public static int LIMIT = 2;

  /** 「新規」を表す定数 */
  public static int NEW = 1;

  /** 「返済」を表す定数 */
  public static int REPAY = 2;

  /** ユーザーID */
  private int fUserID;

  /** ログイン名 */
  private String fUserName;

  /** 銘柄名 */
  private String fBrandName;

  /** 注文ID */
  private long fOrderID;

  /** 注文時刻(実時間) */
  private Date fTime;

  /** 注文日 */
  private int fDate;

  /** 注文節 */
  private int fSession;

  /** 売買区分 */
  private int fSellBuy;

  /** 成行指値区分 */
  private int fMarketLimit;

  /** 新規返済区分 */
  private int fNewRepay;

  /** 注文価格 */
  private long fPrice;

  /** 注文数量 */
  private long fVolume;

  /** 約定情報 */
  private Vector fContractInformationArray;

  /** 約定数量 */
  private long fContractVolume;

  /** 取消数量 */
  private long fCancelVolume;

  /** 同一板寄せ期間内の注文をソートするために利用される乱数 */
  private int fRandomNumber;

  /**
   * コンストラクタ
   */
  public UOrder() {
    fUserID = 0;
    fBrandName = new String();
    fOrderID = 0;
    fUserName = "";
    fTime = new Date();
    fDate = 0;
    fSession = 0;
    fSellBuy = SELL;
    fMarketLimit = MARKET;
    fNewRepay = NEW;
    fPrice = 0;
    fVolume = 0;
    fContractInformationArray = new Vector();
    fContractVolume = 0;
    fCancelVolume = 0;
    fRandomNumber = 0;
  }

  /**
   * ユーザーIDを設定する．
   * @param userID ユーザーID
   */
  public void setUserID(int userID) {
    fUserID = userID;
  }

  /**
   * ログイン名を設定する．
   * @param userName ログイン名
   */
  public void setUserName(String userName) {
    fUserName = userName;
  }

  /**
   * ログイン名を返す．
   * @return ログイン名
   */
  public String getUserName() {
    return fUserName;
  }

  /**
   * 銘柄名を設定する．
   * @param name 銘柄名
   */
  public void setBrandName(String name) {
    fBrandName = name;
  }

  /**
   * 注文IDを設定する．
   * @param orderID 注文ID
   */
  public void setOrderID(long orderID) {
    fOrderID = orderID;
  }

  /**
   * 注文時刻(実時間)を設定する．
   * @param time 注文時刻
   */
  public void setTime(Date time) {
    fTime.setTime(time.getTime());
  }

  /**
   * 注文日を設定する．
   * @param date 注文日
   */
  public void setDate(int date) {
    fDate = date;
  }

  /**
   * 注文節を設定する．
   * @param session 注文節
   */
  public void setSession(int session) {
    fSession = session;
  }

  /**
   * 売買区分を設定する．
   * @param sellBuy 売: UOrder.SELL, 買: UOrder.BUY
   */
  public void setSellBuy(int sellBuy) {
    if (sellBuy == SELL || sellBuy == BUY) {
      fSellBuy = sellBuy;
    } else {
      String str = "UOrder.setSellBuy: sellBuy = " + sellBuy;
      throw new IllegalArgumentException(str);
    }
  }

  /**
   * 成行指値区分を設定する．
   * @param marketLimit 成行: UOrder.MARKET, 指値: UOrder.LIMIT
   */
  public void setMarketLimit(int marketLimit) {
    if (marketLimit == MARKET || marketLimit == LIMIT) {
      fMarketLimit = marketLimit;
    } else {
      String str = "UOrder.setMarketLimit: marketLimit = " + marketLimit;
      throw new IllegalArgumentException(str);
    }
  }

  /**
   * 新規返済区分を設定する．
   * @param newRepay 新規: UOrder.NEW, 返済: UOrder.REPAY (現状のUMartでは, 新規のみ)
   */
  public void setNewRepay(int newRepay) {
    if (newRepay == NEW || newRepay == REPAY) {
      fNewRepay = newRepay;
    } else {
      String str = "UOrder.setNewRepay: newRepay = " + newRepay;
      throw new IllegalArgumentException(str);
    }
  }

  /**
   * 注文価格を設定する．
   * @param price 注文価格
   */
  public void setPrice(long price) {
    if (price >= 0) {
      fPrice = price;
    } else {
      String str = "UOrder.setPrice: price = " + price;
      throw new IllegalArgumentException(str);
    }
  }

  /**
   * 注文数量を設定する．
   * @param volume 注文数量
   */
  public void setVolume(long volume) {
    if (volume >= 0) {
      fVolume = volume;
    } else {
      String str = "UOrder.setVolume: volume = " + volume;
      throw new IllegalArgumentException(str);
    }
  }

  /**
   * 約定情報を追加する.
   * @param contarctID 約定ID
   * @param time 約定時刻(実時間)
   * @param volume 約定数量
   * @param date 約定日
   * @param session 約定節
   */
  public void addContractInformation(long contractID, Date time,
                                           long volume, int date, int session) {
    if (time != null && volume > 0 && date >= 1 && session >= 1) {
      fContractVolume += volume;
      UContractInformation info = new UContractInformation(contractID, time,
          volume, date, session);
      fContractInformationArray.addElement(info);
    } else {
      String str = "Error in UOrder.addContractInformation";
      System.out.println("Contract ID: " + contractID + " Time: " + time
                         + " volume: " + volume + " Date: " + date
                         + " Session: " + session);
      throw new IllegalArgumentException(str);
    }
  }

  /**
   * まだ約定価格が設定されていない約定情報に約定価格を設定する．
   * @param price 約定価格
   */
  public void setLatestContractPrice(long price) {
    if (fContractInformationArray.size() > 0) {
      UContractInformation info =
          (UContractInformation) fContractInformationArray.lastElement();
      if (info.getPrice() == -1) {
        info.setPrice(price);
      }
    }
  }

  /**
   * 約定数量を設定する．
   * @param volume 約定数量
   */
  public void setContractVolume(long volume) {
    fContractVolume = volume;
  }

  /**
   * 取消数量を設定する．
   * @param volume 取消数量
   */
  public void setCancelVolume(long volume) {
    fCancelVolume = volume;
  }

  /**
   * ソートのための乱数を設定する．
   * @param r 乱数
   */
  public void setRandomInteger(int r) {
    fRandomNumber = r;
  }

  /**
   * 取消可能な注文を取り消す．
   */
  public void cancel() {
    fCancelVolume = fVolume - fContractVolume;
  }

  /**
   * ユーザーIDを返す．
   * @return ユーザーID
   */
  public int getUserID() {
    return fUserID;
  }

  /**
   * 銘柄名を返す．
   * @return 銘柄名
   */
  public String getBrandName() {
    return fBrandName;
  }

  /**
   * 注文ID返す．
   * @return 注文ID
   */
  public long getOrderID() {
    return fOrderID;
  }

  /**
   * 注文時刻(実時間)を返す．
   * @return 注文時刻(実時間)
   */
  public Date getTime() {
    return fTime;
  }

  /**
   * 注文日を返す．
   * @return 注文日
   */
  public int getDate() {
    return fDate;
  }

  /**
   * 注文節を返す．
   * @retrun 注文節
   */
  public int getSession() {
    return fSession;
  }

  /**
   * 売買区分を返す．
   * @return 売: UOrder.SELL, 買: UOrder.BUY
   */
  public int getSellBuy() {
    return fSellBuy;
  }

  /**
   * 成行指値区分を返す．
   * @return 成行: UOrder.MARKET, 指値: UOrder.LIMIT
   */
  public int getMarketLimit() {
    return fMarketLimit;
  }

  /**
   * 新規返済区分を返す．
   * @return 新規: UOrder.NEW, 返済: UOrder.REPAY
   */
  public int getNewRepay() {
    return fNewRepay;
  }

  /**
   * 注文価格を返す．
   * @return 注文価格
   */
  public long getPrice() {
    return fPrice;
  }

  /**
   * 注文数量を返す．
   * @return 注文数量
   */
  public long getVolume() {
    return fVolume;
  }

  /**
   * 約定情報を返す．
   * @return 複数の約定情報
   */
  public Vector getContractInformationArray() {
    return fContractInformationArray;
  }

  /**
   * 約定数量を返す．
   * @return 約定数量
   */
  public long getContractVolume() {
    return fContractVolume;
  }

  /**
   * 未約定数量を返す．
   * @return 未約定数量
   */
  public long getUncontractOrderVolume() {
    return fVolume - fContractVolume - fCancelVolume;
  }

  /**
   * 取消数量を返す．
   * @return 取消数量
   */
  public long getCancelVolume() {
    return fCancelVolume;
  }

  /**
   * ソートのための乱数を返す．
   * @return 乱数
   */
  public int getRandomNumber() {
    return fRandomNumber;
  }

  /**
   * 内部情報を出力する．
   * @param pw 出力先
   */
  public void printOn(PrintStream pw) {
    pw.println("userID: " + fUserID);
    pw.println("userName: " + fUserName);
    pw.println("brandName: " + fBrandName);
    pw.println("serialNO: " + fOrderID);
    pw.println("time-stump: " + fTime);
    pw.println("date: " + fDate);
    pw.println("session: " + fSession);
    pw.println("sellBuy: " + fSellBuy);
    pw.println("marketLimit: " + fMarketLimit);
    pw.println("newRepay: " + fNewRepay);
    pw.println("price: " + fPrice);
    pw.println("volume: " + fVolume);
    pw.println("contract volume: " + fContractVolume);
    pw.println("cancel volume: " + fCancelVolume);
    Enumeration e = fContractInformationArray.elements();
    while (e.hasMoreElements()) {
      UContractInformation x = (UContractInformation) e.nextElement();
      pw.print("contract(id, time, price, volume, date, session)=");
      pw.print(Long.toString(x.getContractID()) + ", ");
      pw.print(x.getTime().toString() + ", ");
      pw.print(Long.toString(x.getPrice()) + ", ");
      pw.print(Long.toString(x.getVolume()) + ", ");
      pw.println(Integer.toString(x.getDate()) + ", ");
      pw.println(Integer.toString(x.getSession()) + ")");
    }
    pw.println("randomInteger: " + fRandomNumber);
  }

  /**
   * UOrderの複製を作成して返す．
   * @return UOrderの複製
   */
  public Object clone() {
    UOrder cl = new UOrder();
    cl.fUserID = fUserID;
    cl.fUserName = fUserName;
    cl.fBrandName = fBrandName;
    cl.fOrderID = fOrderID;
    cl.fTime = (Date) fTime.clone();
    cl.fDate = fDate;
    cl.fSession = fSession;
    cl.fSellBuy = fSellBuy;
    cl.fMarketLimit = fMarketLimit;
    cl.fNewRepay = fNewRepay;
    cl.fPrice = fPrice;
    cl.fVolume = fVolume;
    cl.fContractVolume = fContractVolume;
    Enumeration e = fContractInformationArray.elements();
    while (e.hasMoreElements()) {
      UContractInformation x = (UContractInformation) e.nextElement();
      fContractInformationArray.addElement(x.clone());
    }
    cl.fContractVolume = fContractVolume;
    cl.fCancelVolume = fCancelVolume;
    cl.fRandomNumber = fRandomNumber;
    return cl;
  }

  /**
   * テスト用メソッド
   */
  public static void main(String[] args) {
    UOrder u = new UOrder();
    Date d = new Date(253300);
    u.setUserID(1);
    u.setBrandName("j300119991200000");
    u.setOrderID(1);
    u.setTime(d);
    u.setDate(2);
    u.setSession(3);
    u.setSellBuy(UOrder.SELL);
    u.setMarketLimit(UOrder.LIMIT);
    u.setNewRepay(UOrder.REPAY);
    u.setPrice(2000);
    u.setVolume(50);
    u.setRandomInteger(1234);
    u.setContractVolume(5);
    u.setCancelVolume(12);
    u.printOn(System.out);
    System.out.println(u.getUncontractOrderVolume());
  }
}
