package org.umart.serverCore;

import java.util.*;

/**
 * 全メンバーのログインを管理します．
 * @author 川部 裕司
 * @author 森下 領平
 * @author 小野 功
 */

public class ULoginManager {

  /** 全メンバーのログイン状態を格納するためのベクタ */
  private Vector fLoginStatusArray;

  /**
   * ULoginStatusManagerオブジェクトの生成および初期化を行う。
   */
  public ULoginManager() {
    fLoginStatusArray = new Vector();
  }

  /**
   * 引数で与えられるユーザー情報にしたがって新しいログイン状態を
   * 生成, 登録を行う。ただし, 同一ユーザー名の接続状態を作ることは
   * できない。
   * @param id ユーザーID
   * @return true: 成功, false: 失敗
   */
  public boolean creatLoginStatus(int id) {
    Enumeration e = fLoginStatusArray.elements();
    while (e.hasMoreElements()) {
      ULoginStatus x = (ULoginStatus) e.nextElement();
      if (x.getUserID() == id) {
        System.out.print("ERROR:ULoginStatusManager::createLoginStatus: " + id);
        System.err.println(" has already existed.");
        return false;
      }
    }
    fLoginStatusArray.addElement(new ULoginStatus(id));
    return true;
  }

  /**
   * 引数で与えられるユーザーIDのログイン状態を返す。
   * 見つからない場合は, nullを返す。
   * @param userID ユーザーID
   * @return 対応するログイン状態オブジェクト。ただし, 対応する事務所が
   *         見つからない場合はnull。
   */
  public ULoginStatus findLoginStatus(int userID) {
    Enumeration e = fLoginStatusArray.elements();
    while (e.hasMoreElements()) {
      ULoginStatus x = (ULoginStatus) e.nextElement();
      if (x.getUserID() == userID) {
        return x;
      }
    }
    return null;
  }

  /**
   * 登録されている全てのログイン状態を含むベクタを返す。
   * @return ログイン状態を含むベクタ
   */
  public Vector getLoginStatuses() {
    return fLoginStatusArray;
  }

}
