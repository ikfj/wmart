package org.wmart.agent;

import java.util.*;

/**
 * テスト用エージェントクラス
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WRandomAgent extends WAgent {

	/** 注文価格の幅のデフォルト値 */
	public static final int DEFAULT_WIDTH_OF_PRICE = 20;

	/** 注文数量の最大値のデフォルト値 */
	public static final int DEFAULT_MAX_QUANT = 50;

	/** 注文数量の最小値のデフォルト値 */
	public static final int DEFAULT_MIN_QUANT = 10;

	/** 売/買ポジションの最大値のデフォルト値 */
	public static final int DEFAULT_MAX_POSITION = 300;

	/** 直近の価格が得られないときに利用する価格のデフォルト値 */
	public static final int DEFAULT_NOMINAL_PRICE = 3000;

	/** 注文価格の幅 */
	private int fWidthOfPrice = DEFAULT_WIDTH_OF_PRICE;

	/** 注文数量の最大値 */
	private int fMaxQuant = DEFAULT_MAX_QUANT;

	/** 注文数量の最小値 */
	private int fMinQuant = DEFAULT_MIN_QUANT;

	/** 売/買ポジションの最大値 */
	private int fMaxPosition = DEFAULT_MAX_POSITION;

	/** 直近の価格が得られないときに利用する価格 */
	private int fNominalPrice = DEFAULT_NOMINAL_PRICE;

	/** 注文価格のプロパティ名(WidthOfPrice) */
	public static final String WIDTH_OF_PRICE_KEY = "WidthOfPrice";

	/** 注文数量の最大値のプロパティ名(MaxQuant) */
	public static final String MAX_QUANT_KEY = "MaxQuant";

	/** 注文数量の最小値のプロパティ名(MinQuant) */
	public static final String MIN_QUANT_KEY = "MinQuant";

	/** 売/買ポジションの最大値のプロパティ名(MaxPosition) */
	public static final String MAX_POSITION_KEY = "MaxPosition";

	/** 直近の価格が得られないときに利用する価格のプロパティ名(NominalPrice) */
	public static final String NOMINAL_PRICE_KEY = "NominalPrice";

	/**
	 * コンストラクタ
	 * 
	 * @param loginName
	 *            ログイン名
	 * @param passwd
	 *            パスワード
	 * @param realName
	 *            実名
	 * @param seed
	 *            乱数の種
	 */
	public WRandomAgent(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

	/**
	 * 価格幅を返す．
	 * 
	 * @return 価格幅
	 */
	public int getWidthOfPrice() {
		return fWidthOfPrice;
	}

	/**
	 * 最小注文数量を返す．
	 * 
	 * @return 最小注文数量
	 */
	public int getMinQuant() {
		return fMinQuant;
	}

	/**
	 * 最大注文数量を返す．
	 * 
	 * @return 最大注文数量
	 */
	public int getMaxQuant() {
		return fMaxQuant;
	}

	/**
	 * 最大ポジションを返す．
	 * 
	 * @return 最大ポジション
	 */
	public int getMaxPosition() {
		return fMaxPosition;
	}

	/**
	 * 市場価格が未定のときの注文価格を返す．
	 * 
	 * @return 市場価格が未定のときの注文価格
	 */
	public int getNominalPrice() {
		return fNominalPrice;
	}

	/**
	 * 注文票を作成する．
	 * 
	 * @param day
	 *            日
	 * @param session
	 *            節
	 * @param 取引日数
	 * @param noOfSessionsPerDay
	 *            1日の節数
	 * @param spotPrices
	 *            現物価格表系列．spotPrices[0]からspotPrices[719]までの720節分のデータが格納されている．spotPrices[719]が直近の価格である．
	 *            ただし，価格が成立していない場合，-1が入っているので注意すること．
	 * @param forwardPrices
	 *            先物価格表系列．futurePrices[0]からfuturePrices[29]までの30日分のデータが格納されている．futurePrices[29]が直近の価格である ．
	 *            ただし，価格が成立していない場合，-1が入っているので注意すること．
	 * @param position
	 *            ポジション．正ならば買い越し(ロング・ポジション)，負ならば売り越し（ショート・ポジション）を表す．
	 * @param money
	 *            現金残高．型がlongであることに注意．
	 * @return UOrderForm[] 注文票の配列
	 */
	public WOrderForm[] makeOrderForms(int day, int session, int maxDays, int noOfSessionsPerDay, String[] spotPrices,
		String[] forwardPrices, int position, long money) {
		Random rand = getRandom();
		WOrderForm[] forms = new WOrderForm[1];
		forms[0] = new WOrderForm();
		forms[0].setBuySell(rand.nextInt(2) + 1);

		// 仮 (09/9/7)
		int price = 100 + rand.nextInt(200);
		if (price <= 0) {
			price = 1;
		}
		int volume = rand.nextInt(50);
		int earliest = rand.nextInt(3);
		int latest = earliest + rand.nextInt(3);
		int total = latest - earliest + 1;
		String specstr = "serviceA:" + volume + ":" + earliest + ":" + latest + ":" + total + ";";
		// "商品名:希望数量:最早時刻:最遅時刻:延べ時間;" の繰り返し
		String auctioneer = "forward";

		forms[0].setAuctioneerName(auctioneer);
		forms[0].setOrderPrice(price);
		forms[0].setSpec(specstr);
		println("" + day + "/" + session + ", " + forms[0].getBuySellByString() + ", $" + forms[0].getOrderPrice() + ", ["
			+ forms[0].getSpec());
		return forms;
	}

	/**
	 * エージェントのシステムパラメータを設定する．
	 * 
	 * @param args
	 *            システムパラメータ
	 */
	public void setParameters(String[] args) {
		super.setParameters(args);
		for (int i = 0; i < args.length; ++i) {
			StringTokenizer st = new StringTokenizer(args[i], "= ");
			String key = st.nextToken();
			String value = st.nextToken();
			if (key.equals(WRandomAgent.WIDTH_OF_PRICE_KEY)) {
				fWidthOfPrice = Integer.parseInt(value);
				println("WidthOfPrice has been changed to " + fWidthOfPrice);
			} else if (key.equals(WRandomAgent.MIN_QUANT_KEY)) {
				fMinQuant = Integer.parseInt(value);
				println("MinQuant has been changed to " + fMinQuant);
			} else if (key.equals(WRandomAgent.MAX_QUANT_KEY)) {
				fMaxQuant = Integer.parseInt(value);
				println("MaxQuant has been changed to " + fMaxQuant);
			} else if (key.equals(WRandomAgent.MAX_POSITION_KEY)) {
				fMaxPosition = Integer.parseInt(value);
				println("MaxPosition has been changed to " + fMaxPosition);
			} else if (key.equals(WRandomAgent.NOMINAL_PRICE_KEY)) {
				fNominalPrice = Integer.parseInt(value);
				println("NominalPrice has been changed to " + fNominalPrice);
			} else {
				println("Unknown parameter:" + key + " in RandomStrategy.setParameters");
			}
		}
	}

}
