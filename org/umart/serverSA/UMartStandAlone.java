package org.umart.serverSA;

import org.umart.serverCore.ULoginManager;
import org.umart.serverCore.UMart;
import org.umart.serverCore.UMemberList;
import org.umart.serverCore.UPriceInfoDB;

/**
 * スタンドアロン環境用の取引所クラスです．
 * @author 小野　功
 */
public class UMartStandAlone extends UMart {

  /**
   * UMartStandAloneオブジェクトの生成および初期化を行う：<br>
   * - エージェントは誰もログインしていない状態<br>
   * - 日付は1，板寄せ回数は1<br>
   * - 状態はBEFORE_TRADING<br>
   * @param noOfMembers 会員数
   * @param priceInfoDB 価格系列データベース
   * @param startPoint 価格系列の何番目のデータを初日のデータとするか
   * @param seed 乱数の種
   * @param maxDate サーバー運用日数
   * @param noOfSessionsPerDay 一日あたりの節数
   * @param initialCash 初期所持金
   * @param tradingUnit 取引単位
   * @param marginRate 証拠金率
   * @param feePerUnit 単位当たりの取引手数料
   * @param maxLoan 借り入れ限度額
   * @param interest 借り入れ金利
   */
  public UMartStandAlone(UMemberList members, UPriceInfoDB priceInfoDB,
                         int startPoint, long seed,
                         int maxDate, int noOfSessionsPerDay) {
    super(members, priceInfoDB, startPoint, seed,
          maxDate, noOfSessionsPerDay);
  }

  /**
   * @see org.umart.serverCore.UMart#createLoginManager()
   */
  protected ULoginManager createLoginManager() {
    return new ULoginManager();
  }

}
