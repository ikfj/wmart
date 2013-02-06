package org.umart.cmdClientNet;

import java.io.*;

/**
 * IServerCmdインターフェースは，Network版のクライアント上で実行されるコマンドに必要なインターフェースを定義する．
 *
 * @author 小野　功
 */

public interface IClientCmdNet {

  /**
   * サーバーとの通信に必要な入出力のストリームを設定する．
   * @param br 入力
   * @param pw 出力
   */
  public void setConnection(BufferedReader br, PrintWriter pw);

}
