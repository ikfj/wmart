package org.umart.serverCore;

/**
 * 各ユーザー(メンバー)に対する接続中のクライアント数を管理します．
 * U-Martシステムでは，同一ユーザが複数ログインすることを許しているため，
 * 接続中のクライアント数を保持しています．
 * @author 川部 裕司
 * @author 森下 領平
 * @author 小野 功
 */

public class ULoginStatus {

  /** 接続中のクライアント数 */
  private int fNoOfLoginAgents;

  /** ユーザーID */
  private int fUserID;

  /**
   * ULoginStatusオブジェクトを生成, 初期化する。
   * @param userID ユーザーID
   * @param umart U-Martへの参照
   */
  public ULoginStatus(int userID) {
    fUserID = userID;
    fNoOfLoginAgents = 0;
  }

  /**
   * ユーザーIDを返す。
   * @return ユーザーID
   */
  public int getUserID() {
    return fUserID;
  }

  /**
   * ログイン中のユーザー数を1だけ増す。
   */
  synchronized public void incrementNoOfLoginAgents() {
    fNoOfLoginAgents++;
    // System.out.println("No of userID" + fUserID + " is " + fNoOfLoginAgents);
  }

  /**
   * ログイン中のユーザー数を1だけ減らす。
   */
  synchronized public void decrementNoOfLoginAgents() {
    if (fNoOfLoginAgents-- < 0) {
      System.out.println("Agent number is minus");
      System.exit(1);
    }
    System.out.println("No of userID" + fUserID + " is " + fNoOfLoginAgents);
  }

  /**
   * ログイン中のユーザー数を返す．
   * @return ログイン中のユーザー数
   */
  synchronized public int getNoOfLoginAgents() {
    return fNoOfLoginAgents;
  }

}
