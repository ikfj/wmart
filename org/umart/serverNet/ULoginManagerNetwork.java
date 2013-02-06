package org.umart.serverNet;

import java.net.*;

import org.umart.serverCore.ULoginManager;

/**
 * ネットワーク越しにログインするエージェントを扱えるようにULoginManagerを機能拡張したクラスです．
 * @author 小野　功
 */
public class ULoginManagerNetwork extends ULoginManager implements Runnable {

  /** クライアントからの接続要求を受け付けるためのサーバーソケット */
  private ServerSocket fServerSocket;

  /** スレッドオブジェクト */
  private Thread fThread;

  /** 終了フラグ */
  private boolean fDoneFlag;

  /** ネットワーク版UMartクラスへの参照 */
  UMartNetwork fUMartNetwork;

  /**
   * コンストラクタ
   * @param umart UMartNetworkオブジェクト
   */
  public ULoginManagerNetwork(UMartNetwork umart) {
    super();
    fUMartNetwork = umart;
    fThread = null;
    fServerSocket = null;

  }

  /**
     ユーザーからの接続要求を監視するメインループ。
   */
  public void run() {
    fDoneFlag = false;
    try {
      while (!fDoneFlag) {
        Socket s = fServerSocket.accept();
        new UAgentForNetworkClient(s, fUMartNetwork);
      }
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
    fThread = null;
  }

  /**
   * ネットワーク・エージェントからの接続受付を開始する．
   * @param port 接続受付ポート
   */
  public void startLoop(int port) {
    try {
      fServerSocket = new ServerSocket(port);
    } catch (Exception e) {
      System.err.println("Error: " + e);
      System.exit(5);
    }
    fThread = new Thread(this);
    fThread.start();
  }

  /**
     メインループを終了する。
   */
  public void stopLoop() {
    fDoneFlag = false;
  }

}
