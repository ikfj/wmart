package org.umart.serverCore;

import java.util.*;

/**
 * ユーザー一人分の注文集合を管理するクラスです．
 * 注文は, 完全に約定した注文と, 未約定分が残っている注文に分けて管理されます．
 * @author 川部 裕司
 * @author 森下 領平
 * @author 小野 功
 */
public class UOrderArray {

  /** ユーザーID */
  private int fUserID;

  /** 約定済みの注文集合 */
  private Vector fContractedOrders;

  /** 未約定の注文集合 */
  private Vector fUncontractedOrders;

  /** 注文履歴 */
  private ArrayList fHistory;

  /**
   * userIDをもつユーザーのUOrderArrayを作成する．
   * @param userID ユーザーID
   */
  public UOrderArray(int userID) {
    fUserID = userID;
    fContractedOrders = new Vector();
    fUncontractedOrders = new Vector();
    fHistory = new ArrayList();
  }

  /**
   * 注文履歴を返す．
   * @return 注文履歴のリスト
   */
  public ArrayList getHistory() {
    return fHistory;
  }

  /**
   * userIDを返す
   * @return ユーザーID
   */
  public int getUserID() {
    return fUserID;
  }

  /**
   * 約定分の注文集合を返す．
   * @return 約定分の注文集合
   */
  public Vector getContractedOrders() {
    return fContractedOrders;
  }

  /**
   * 未約定分の注文集合を返す．
   * @return 未約定分の注文集合
   */
  public Vector getUncontractedOrders() {
    return fUncontractedOrders;
  }

  /**
   * 注文を追加する．このメソッドは, 追加された注文を
   * 未約定分を含むかチェックし, 適切に分類する．
   * @param o 注文
   */
  public void addOrder(UOrder o) {
    if (o.getUncontractOrderVolume() > 0) {
      fUncontractedOrders.addElement(o);
    } else if (o.getUncontractOrderVolume() == 0) {
      fContractedOrders.addElement(o);
    } else {
      System.out.println("Order volume is under 0");
      System.exit(1);
    }
  }

  /**
   * 約定分の注文を全て取り除く．
   */
  public void removeAllContractedOrders() {
    fContractedOrders.removeAllElements();
  }

  /**
   * 未約定分の注文を全て取り除く．
   */
  public void removeAllUncontractedOrders() {
    fUncontractedOrders.removeAllElements();
  }

  /**
   * 約定分の注文数を返す．
   * @return 約定分の注文数
   */
  public int getNoOfContractedOrders() {
    return fContractedOrders.size();
  }

  /**
   * 未約定分の注文数を返す．
   * @return 未約定分の注文数
   */
  public int getNoOfUncontractedOrders() {
    return fUncontractedOrders.size();
  }

  /**
   * indexで指定される約定分の注文を返す．
   * @param index 添え字
   * @return 注文
   */
  public UOrder getContractedOrderAt(int index) {
    return (UOrder) fContractedOrders.elementAt(index);
  }

  /**
   * indexで指定される未約定分の注文を返す．
   * @param index 添え字
   * @return 注文
   */
  public UOrder getUncontractedOrderAt(int index) {
    return (UOrder) fUncontractedOrders.elementAt(index);
  }

  /**
   * テスト用メソッド
   */
  public static void main(String args[]) {
    UOrderArray uo = new UOrderArray(1);
    for (int i = 0; i < 10; ++i) {
      UOrder o = new UOrder();
      o.setUserID(i + 1);
      o.setBrandName("j300119991200000");
      o.setOrderID(i + 1);
      o.setDate(i + 1);
      o.setSession(i % 3 + 1);
      o.setSellBuy(UOrder.SELL);
      o.setMarketLimit(UOrder.MARKET);
      o.setNewRepay(UOrder.REPAY);
      o.setPrice(2500 + i * 10);
      o.setVolume(i + 20);
      o.setRandomInteger(1234);
      o.setContractVolume(i + 2);
      o.setCancelVolume(i * 2);
      uo.addOrder(o);
    }
    System.out.println("UserID : " + uo.getUserID());
    System.out.println("Contracted Order Number is " + uo.getNoOfContractedOrders());
    System.out.println("Uncontracted Order Number is " + uo.getNoOfUncontractedOrders());
    uo.getContractedOrderAt(0).printOn(System.out);
  }
}
