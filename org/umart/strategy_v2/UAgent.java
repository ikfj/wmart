package org.umart.strategy_v2;

import java.util.ArrayList;
import java.util.HashMap;

import org.umart.cmdCore.UCBalancesCore;
import org.umart.cmdCore.UCFuturePriceCore;
import org.umart.cmdCore.UCOrderRequestCore;
import org.umart.cmdCore.UCPositionCore;
import org.umart.cmdCore.UCSpotPriceCore;
import org.umart.cmdCore.UCommandStatus;
import org.umart.serverCore.UOrder;
import org.umart.serverCore.UServerStatus;
import org.umart.strategyCore.UBaseAgent;



/**
 * 全ての戦略クラスの親クラス．
 * @author isao
 *
 */
public class UAgent extends UBaseAgent {

	/** 現物価格系列の長さ */
  public static final int NO_OF_SPOT_PRICES = 120;

  /** 先物価格系列の長さ */
  public static final int NO_OF_FUTURE_PRICES = 60;
	
  /**
   * コンストラクタ
   * @param loginName ログイン名
   * @param passwd パスワード
   * @param realName 実名
   * @param seed 乱数の種
   */
	public UAgent(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

  private int[] getSpotPrices() {
    UCSpotPriceCore cmd = (UCSpotPriceCore)fUmcp.getCommand(UCSpotPriceCore.CMD_NAME);
    cmd.setArguments("j30", UAgent.NO_OF_SPOT_PRICES);
    UCommandStatus status = cmd.doIt();
    if (status.getStatus() == false) {
      System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID);
      System.err.println(status.getErrorMessage() + " in UAgent.getSpotPrices");
      System.exit(5);
    }
    ArrayList spotList = cmd.getResults();
    if (spotList.size() != UAgent.NO_OF_SPOT_PRICES) {
      System.err.println("spotList.size() != this.NO_OF_SPOT_PRICES in UAgent.doActions");
      System.exit(5);
    }
    int[] spotPrices = new int[UAgent.NO_OF_SPOT_PRICES];
    for (int i = 0; i < UAgent.NO_OF_SPOT_PRICES; ++i) {
      HashMap elem = (HashMap) spotList.get(i);
      spotPrices[UAgent.NO_OF_SPOT_PRICES - i - 1] = (int)((Long)elem.get(UCSpotPriceCore.LONG_PRICE)).longValue();
    }
    return spotPrices;
  }

  private int[] getFuturePrices() {
    UCFuturePriceCore cmd = (UCFuturePriceCore)fUmcp.getCommand(UCFuturePriceCore.CMD_NAME);
    cmd.setArguments("j30", UAgent.NO_OF_FUTURE_PRICES);
    UCommandStatus status = cmd.doIt();
    if (status.getStatus() == false) {
      System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID);
      System.err.println(status.getErrorMessage() +
                         " in UAgent.getFuturePrices");
      System.exit(5);
    }
    ArrayList futureList = cmd.getResults();
    if (futureList.size() != UAgent.NO_OF_FUTURE_PRICES) {
      System.err.println("futureList.size():" + futureList.size() +
                         " != this.NO_OF_FUTURE_PRICES" + NO_OF_FUTURE_PRICES +
                         " in UAgent.getFuturePrices");
      System.exit(5);
    }
    int[] futurePrices = new int[UAgent.NO_OF_FUTURE_PRICES];
    for (int i = 0; i < UAgent.NO_OF_FUTURE_PRICES; ++i) {
      HashMap elem = (HashMap)futureList.get(i);
      futurePrices[UAgent.NO_OF_FUTURE_PRICES - i - 1] = (int)((Long)elem.get(UCFuturePriceCore.LONG_PRICE)).longValue();
    }
    return futurePrices;
  }

  private int getPosition() {
    UCPositionCore cmd = (UCPositionCore)fUmcp.getCommand(UCPositionCore.CMD_NAME);
    UCommandStatus status = cmd.doIt();
    if (status.getStatus() == false) {
      System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID +
                         " in UAgent.getPosition");
      System.err.println(status.getErrorMessage() + " in UAgent.getPosition");
      System.exit(5);
    }
    HashMap result = cmd.getResults();
    long todayBuy = ( (Long) result.get(UCPositionCore.LONG_TODAY_BUY)).
        longValue();
    long todaySell = ( (Long) result.get(UCPositionCore.LONG_TODAY_SELL)).
        longValue();
    long yesterdayBuy = ( (Long) result.get(UCPositionCore.LONG_YESTERDAY_BUY)).
        longValue();
    long yesterdaySell = ( (Long) result.get(UCPositionCore.LONG_YESTERDAY_SELL)).
        longValue();
    long buy = todayBuy + yesterdayBuy;
    long sell = todaySell + yesterdaySell;
    return (int) (buy - sell);
  }

  private long getMoney() {
    UCBalancesCore cmd = (UCBalancesCore)fUmcp.getCommand(UCBalancesCore.CMD_NAME);
    UCommandStatus status = cmd.doIt();
    if (status.getStatus() == false) {
      System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID);
      System.err.println(status.getErrorMessage() + " in UAgent.getMoney");
      System.exit(5);
    }
    HashMap bal = cmd.getTodayResults();
    return ( (Long) bal.get(UCBalancesCore.LONG_SURPLUS)).longValue();
  }

  /**
   * @see org.umart.strategyCore.UBaseAgent#doActions()
   */
  public void doActions(int day, int session, int serverState,
                         int maxDays, int noOfSessionsPerDay) {
    if (serverState != UServerStatus.ACCEPT_ORDERS) {
      return;
    }
    int[] spotPrices = getSpotPrices();
    int[] futurePrices = getFuturePrices();
    int position = getPosition();
    long money = getMoney();
    UOrderForm[] forms = makeOrderForms(day, session, maxDays, noOfSessionsPerDay, 
                                        spotPrices, futurePrices, position, money);
    for (int i = 0; i < forms.length; ++i) {
    	orderRequest(forms[i]);
    }
  }

  /**
   * 取引所サーバへ注文を送信する．
   * @param form 注文票
   */
  protected void orderRequest(UOrderForm form) {
    UCOrderRequestCore cmd = (UCOrderRequestCore)fUmcp.getCommand(UCOrderRequestCore.CMD_NAME);
    int sellBuy;
    if (form.getBuySell() == UOrderForm.BUY) {
      sellBuy = UOrder.BUY;
    } else if (form.getBuySell() == UOrderForm.SELL) {
      sellBuy = UOrder.SELL;
    } else {
      return;
    }
    if (form.getPrice() <= 0 || form.getQuantity() <= 0) {
    	return;
    }
    cmd.setArguments("j30", UOrder.NEW, sellBuy, UOrder.LIMIT, form.getPrice(), form.getQuantity());
    UCommandStatus status = cmd.doIt();
    if (status.getStatus() == false) {
      System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID);
      System.err.println(status.getErrorMessage() + " in UAgent.orderRequest");
    }
  }
  
  /**
   * 決算日までの節数を計算する．
   * @param day 日
   * @param session 節
   * @param maxDays 取引日数
   * @param noOfSessionsPerDay 1日の節数
   * @return
   */
  public int calculateRestSessions(int day, int session,
                               int maxDays, int noOfSessionsPerDay) {
    return (maxDays - day) * noOfSessionsPerDay + noOfSessionsPerDay - session + 1;
  }

  /**
   * 成立した直近の価格を返す．
   * @param prices 価格系列．prices[prices.length - 1]が直近．
   * @return 成立した直近の価格
   */
  public int getLatestPrice(int[] prices) {
    for (int i = prices.length - 1; i >= 0; --i) {
      if (prices[i] >= 0) {
        return prices[i];
      }
    }
    return UOrderForm.INVALID_PRICE;
  }
  
  /**
   * 注文票を作成する．
   * デフォルトでは「注文しない」注文票を返すだけなので，子クラスで必ずオーバーライドすること．
   * @param day 日
   * @param session 節
   * @param 取引日数
   * @param noOfSessionsPerDay 1日の節数
   * @param spotPrices 現物価格系列．spotPrices[0]からspotPrices[119]までの120節分のデータが格納されている．spotPrices[119]が直近の価格である．ただし，価格が成立していない場合，-1が入っているので注意すること．
   * @param futurePrices 現物価格系列．spotPrices[0]からspotPrices[119]までの120節分のデータが格納されている．spotPrices[119]が直近の価格である．ただし，価格が成立していない場合，-1が入っているので注意すること．また，取引開始節より前は現物価格が格納されている．
   * @param position ポジション．正ならば買い越し(ロング・ポジション)，負ならば売り越し（ショート・ポジション）を表す．
   * @param money 現金残高．型がlongであることに注意．
   * @return UOrderForm[] 注文票の配列 
   */
  public UOrderForm[] makeOrderForms(int day, int session, 
  	                                  int maxDays, int noOfSessionsPerDay, 
  		                                int[] spotPrices, int[] futurePrices, 
  		                                int position, long money) {
  	UOrderForm[] forms = new UOrderForm [1];
  	forms[0] = new UOrderForm();
  	return forms;
  }
	
}
