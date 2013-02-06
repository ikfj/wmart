package org.umart.cmdServer;

import org.umart.serverNet.*;

/**
 * サーバ上で動作するSVMPコマンドクラスに必要なインターフェースを定義しています．
 * @author 小野　功
 */

public interface IServerCmd {

  /**
   * このコマンドを実行したエージェントとサーバーを設定する．
   * @param agent このコマンドを実行したエージェント
   * @param umart サーバーへの参照
   */
  public void setConnection(UAgentForNetworkClient agent, UMartNetwork umart);

}
