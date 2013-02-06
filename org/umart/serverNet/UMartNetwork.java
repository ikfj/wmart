package org.umart.serverNet;

import org.umart.serverCore.*;

/**
 * ネットワーク環境で動作する取引所です．
 * @author 小野　功
 */
public class UMartNetwork extends UMart {

  /** サーバーソケットのポート番号のデフォルト値 */
  public static final int DEFAULT_PORT = 5010;

  /** サーバーソケットのポート番号 */
  private int fPort = DEFAULT_PORT;

  /**
   * デフォルトポートでUMartNetworkオブジェクトの生成および初期化を行う：<br>
   * - エージェントは誰もログインしていない状態<br>
   * - 日付は1，節は1<br>
   * - 状態はBEFORE_TRADING<br>
   * @param members 会員リスト
   * @param priceInfoDB 価格系列データベース
   * @param startPoint 価格系列の開始ポイント
   * @param seed 乱数の種
   * @param maxDate 取引日数
   * @param noOfSessionsPerDay 1日あたりの節数
   */
  public UMartNetwork(UMemberList members, UPriceInfoDB priceInfoDB,
                      int startPoint, long seed,
                      int maxDate, int noOfSessionsPerDay) {
    this(members, priceInfoDB, startPoint, seed, maxDate, noOfSessionsPerDay, DEFAULT_PORT);
  }

  /**
   * UMartNetworkオブジェクトの生成および初期化を行う：<br>
   * - エージェントは誰もログインしていない状態<br>
   * - 日付は1，節は1<br>
   * - 状態はBEFORE_TRADING<br>
   * @param members 会員リスト
   * @param priceInfoDB 価格系列データベース
   * @param startPoint 価格系列の開始ポイント
   * @param seed 乱数の種
   * @param maxDate 取引日数
   * @param noOfSessionsPerDay 1日あたりの節数
   * @param port サーバーソケットのポート番号
   */
  public UMartNetwork(UMemberList members, UPriceInfoDB priceInfoDB,
                      int startPoint, long seed,
                      int maxDate, int noOfSessionsPerDay,
                      int port) {
    super(members, priceInfoDB, startPoint, seed, maxDate, noOfSessionsPerDay);
    fPort = port;
  }

  /**
   * @see org.umart.serverCore.UMart#createLoginManager()
   */
  protected ULoginManager createLoginManager() {
    return new ULoginManagerNetwork(this);
  }
  
  /**
   * ログインマネージャを起動する．
   */
  public void startLoginManager() {
    ((ULoginManagerNetwork)fLoginManager).startLoop(fPort);
  }

}
