package org.wmart.cmdClientSA;

import org.wmart.core.*;

/**
 * ローカルで動作するクライアントのためのSVMPコマンドの定義に必要なインターフェースです．
 * @author 小野　功
 */

public interface IClientCmdSA {

  /**
   * サーバーへの参照とユーザーIDを設定する．
   * @param umart サーバーへの参照
   * @param userID ユーザーID
   */
  public void setConnection(WMart wmart, int userID);

}
