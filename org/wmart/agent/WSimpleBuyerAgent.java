package org.wmart.agent;

import java.util.*;

import org.apache.log4j.*;
import org.wmart.cmdCore.*;
import org.wmart.core.*;
import org.wmart.logger.*;

/**
 * 買い手
 * 
 * @author Ikki Fujiwara, NII
 */
public class WSimpleBuyerAgent extends WAgent {

	/** 商品名リスト */
	public String[] fGoods = { "A", "B", "C", "D", "E" };
	/** タスク最低単価 */
	public int fMinUnitPrice = 2;
	/** タスク最高単価 */
	public int fMaxUnitPrice = 4;
	/** タスク最大数量 */
	public int fMaxVolume = 20;
	/** ワークフロー最大幅 (並列タスク数) */
	public int fMaxWidth = fGoods.length - 2;
	/** 1回の注文数の期待値 */
	public double fAvgOrders = 1.0;
	/** 1回の注文数の最大値 */
	public int fMaxOrders = 10;
	/** 資源情報ログ */
	private WResourceLog fRLog = null;

	private static Logger log = Logger.getLogger(WSimpleBuyerAgent.class);

	/**
	 * コンストラクタ
	 */
	public WSimpleBuyerAgent(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

	@Override
	public void doActions(int day, int session, int serverState, int maxDays, int sessionsPerDay,
		int sessionsForward, int maxLength) {
		switch (serverState) {
		case WServerStatusMaster.SHUTDOWN:
			fRLog = new WResourceLog(day, session, fLoginName);
			updateStatus(fRLog);
			writeResourceLog(fRLog);
			break;
		case WServerStatusMaster.AFTER_HOURS:
			if (session == sessionsPerDay) { // とりあえず先物専用。最終セッションのみ仕事する
				log.info("■買い注文■");
				fRLog = new WResourceLog(day, session, fLoginName);
				updateStatus(fRLog);
				ArrayList<WOrderForm> forms = new ArrayList<WOrderForm>();
				// 先物注文
				int earliest = (day + 1) * sessionsPerDay + 1; // 取引対象の最早時刻
				int latest = (day + 1) * sessionsPerDay + sessionsForward; // 取引対象の最遅時刻
				int numOrders = poissonDistributed(fAvgOrders, fMaxOrders);
				for (int i = 0; i < numOrders; i++) {
					addForwardOrder(forms, earliest, latest, maxLength);
				}
				// 注文票を送る
				for (Iterator<WOrderForm> itr = forms.iterator(); itr.hasNext();) {
					WOrderForm form = itr.next();
					orderRequest(form);
					fRLog.addBuying("#0", form.getSpec());
				}
				// 資源情報ログを更新
				writeResourceLog(fRLog);
				break;
			}
		}
	}

	/**
	 * 約定状況を取得し、資源情報ログを更新する
	 */
	private void updateStatus(WResourceLog rlog) {
		ArrayList<HashMap<String, String>> arr = getOrderStatus("forward");
		for (Iterator<HashMap<String, String>> itr = arr.iterator(); itr.hasNext();) {
			HashMap<String, String> os = itr.next();
			int sellBuy = Integer.valueOf(os.get(WCOrderStatusCore.INT_SELL_BUY));
			assert (sellBuy == WOrder.BUY);
			long orderPrice = Integer.valueOf(os.get(WCOrderStatusCore.LONG_ORDER_PRICE));
			double contractPrice = Double.valueOf(os.get(WCOrderStatusCore.DOUBLE_CONTRACT_PRICE));
			if (orderPrice < 0 || contractPrice == 0.0) {
				// キャンセル済み or 未約定
				String orderSpec = os.get(WCOrderStatusCore.STRING_ORDER_SPEC);
				if (orderPrice < 0) {
					rlog.addCancelBuying("#0", orderSpec);
					// log.info(String.format("canceled orders [%s]", orderSpec));
				} else {
					rlog.addBuying("#0", orderSpec);
					// log.info(String.format("remaining orders [%s]", orderSpec));
				}
			} else {
				// 約定済み
				String orderSpec = os.get(WCOrderStatusCore.STRING_ORDER_SPEC);
				rlog.addBought("#0", orderSpec);
				// log.info(String.format("contracted orders [%s]", orderSpec));
			}
		}
	}

	/**
	 * 注文票を生成してリストに追加する。 前タスク(1個) → 中間タスク(1～maxwidth個並列) → 後タスク(1個) からなるワークフローを生成。
	 */
	private void addForwardOrder(ArrayList<WOrderForm> forms, int earliest, int latest, int maxlen) {
		assert (fGoods.length >= 2 + fMaxWidth) : "Number of goods must be more than 2 + maxwidth";
		WOrderForm form = new WOrderForm();
		form.setAuctioneerName("forward");
		form.setBuySell(WOrderForm.BUY);

		int x = latest - earliest + 1 - maxlen; // 最大余裕時間
		int n = 2 + decayDistributed(0.5, fMaxWidth); // ワークフローを構成するタスク数
		// [0] = 前タスク
		// [1]～[n-2] = 中間タスク
		// [n-1] = 後タスク
		String[] goods = choose(n, fGoods); // 各タスクの商品名
		int[] volumes = uniform(n, 1, fMaxVolume); // 各タスクの資源量
		int[] lengths = uniform(n, 1, maxlen / 3); // 各タスクの長さ
		int[] atimes = new int[n]; // 各タスクの開始時刻
		int[] dtimes = new int[n]; // 各タスクの終了時刻
		int margin = exponentialDistributed(lambdaForProbabilityAt(x, 0.01), x);
		atimes[0] = earliest + margin;
		dtimes[0] = atimes[0] + lengths[0] - 1;
		int maxMidLength = max(Arrays.copyOfRange(lengths, 1, n - 1)); // 中間タスクの中で最長のもの
		for (int i = 1; i <= n - 2; i++) {
			atimes[i] = dtimes[0] + 1;
			dtimes[i] = atimes[i] + maxMidLength - 1;
		}
		atimes[n - 1] = dtimes[0] + maxMidLength + 1;
		dtimes[n - 1] = atimes[n - 1] + lengths[n - 1] - 1;
		double unitprice = uniformDistributed(fMinUnitPrice, fMaxUnitPrice);
		int price = Math.round((float) unitprice * innerProduct(volumes, lengths));

		StringBuilder spec = new StringBuilder();
		for (int i = 0; i < n; i++) {
			spec.append(String.format("%s:%d:%d:%d:%d;", goods[i], volumes[i], atimes[i],
				dtimes[i], lengths[i]));
		}
		form.setSpec(spec.toString());
		form.setOrderPrice(price);
		forms.add(form);

		StringBuilder logstr = new StringBuilder();
		logstr.append(String.format("%s%d*%d[%d-%d], ", goods[0], volumes[0], lengths[0],
			atimes[0], dtimes[0]));
		for (int i = 1; i < n - 1; i++) {
			logstr.append(String.format("%s%d*%d%s", goods[i], volumes[i], lengths[i],
				(i < n - 2) ? "+" : ""));
		}
		logstr.append(String.format("[%d-%d], ", atimes[1], dtimes[1]));
		logstr.append(String.format("%s%d*%d[%d-%d]", goods[n - 1], volumes[n - 1], lengths[n - 1],
			atimes[n - 1], dtimes[n - 1]));
		logstr.append(String.format(" $%d @%.2f", price, unitprice));
		log.info(logstr);
	}

	/**
	 * ランダムな整数の配列
	 */
	private int[] uniform(int size, int min, int max) {
		Random rand = getRandom();
		int[] u = new int[size];
		for (int i = 0; i < u.length; i++) {
			u[i] = min + rand.nextInt(max - min + 1);
		}
		return u;
	}

	/**
	 * ランダムな値を選ぶ
	 */
	private double uniformDistributed(double min, double max) {
		Random rand = getRandom();
		return min + rand.nextDouble() * (max - min);
	}

	/**
	 * 指数分布に従って値を選ぶ
	 * 
	 * @param lambda
	 * @param max
	 * @return 0～max の整数。max を超える値は max に丸められる。
	 */
	private int exponentialDistributed(double lambda, int max) {
		Random rand = getRandom();
		double v = -Math.log(rand.nextDouble()) / lambda;
		if (v > max) {
			v = max;
		}
		return (int) Math.floor(v);
	}

	/**
	 * 指数分布で、選ばれる値が x 以上になる確率が p となるような係数 λ を求める
	 * 
	 * @param x
	 * @param p
	 * @return
	 */
	private double lambdaForProbabilityAt(double x, double p) {
		return -(Math.log(p) / x);
	}

	/**
	 * Decay 分布に従って値を選ぶ
	 * 
	 * @param alpha
	 *            係数
	 * @param max
	 *            最大値
	 * @return
	 */
	private int decayDistributed(double alpha, int max) {
		Random rand = getRandom();
		for (int k = 1; k < max; k++) {
			if (rand.nextDouble() > alpha) {
				return k;
			}
		}
		return max;
	}

	/**
	 * Poisson 分布に従って値を選ぶ http://en.wikipedia.org/wiki/Poisson_distribution
	 * 
	 * @param lambda
	 *            期待値
	 * @param max
	 *            最大値 (期待値の2倍以上を指定するべき。ただし、あまり大きいとオーバーフローする)
	 * @return
	 */
	private int poissonDistributed(double lambda, int max) {
		Random rand = getRandom();
		double r = rand.nextDouble();
		double p = 0.0;
		for (int k = 0; k < max; k++) {
			p += Math.pow(lambda, k) * Math.exp(-lambda) / factorial(k);
			// max が大きすぎるとここで p が減る可能性がある。パラメータを変えたらチェックするべき
			if (p > r) {
				return k;
			}
		}
		return max;
	}

	/**
	 * 階乗
	 */
	private long factorial(long n) {
		assert (n >= 0);
		long r = 1;
		while (n > 1) {
			r *= n--;
		}
		return r;
	}

	/**
	 * 配列の要素からランダムに n 個を選ぶ
	 * 
	 * @param n
	 *            選ぶ個数
	 * @param given
	 *            配列
	 * @return
	 */
	private String[] choose(int n, String[] given) {
		assert (0 < n && n <= given.length);
		ArrayList<String> a = new ArrayList<String>(Arrays.asList(given));
		ArrayList<String> b = new ArrayList<String>();
		Random rand = getRandom();
		for (int i = 0; i < n; i++) {
			b.add(a.remove(rand.nextInt(a.size())));
		}
		return b.toArray(new String[n]);
	}

	/**
	 * 2つの配列の内積 (対応する要素の積の和)
	 */
	private int innerProduct(int[] a, int[] b) {
		assert (a.length == b.length);
		int sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum += a[i] * b[i];
		}
		return sum;
	}

	/**
	 * 配列要素の最大値
	 */
	private int max(int[] a) {
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < a.length; i++) {
			if (a[i] > max) {
				max = a[i];
			}
		}
		return max;
	}

	/**
	 * エージェントのシステムパラメータを設定する．
	 * 
	 * @param args
	 *            システムパラメータ
	 */
	@Override
	public void setParameters(String[] args) {
		super.setParameters(args);
		for (int i = 0; i < args.length; ++i) {
			StringTokenizer st = new StringTokenizer(args[i], "= ");
			String key = st.nextToken();
			String value = st.nextToken();
			if (key.equalsIgnoreCase("goods")) {
				fGoods = value.split(";");
				println("Goods set to " + fGoods);
			} else if (key.equalsIgnoreCase("minprice")) {
				fMinUnitPrice = Integer.valueOf(value);
				println("MinUnitPrice set to " + fMinUnitPrice);
			} else if (key.equalsIgnoreCase("maxprice")) {
				fMaxUnitPrice = Integer.valueOf(value);
				println("MaxUnitPrice set to " + fMaxUnitPrice);
			} else if (key.equalsIgnoreCase("maxvolume")) {
				fMaxVolume = Integer.valueOf(value);
				println("MaxVolume set to " + fMaxVolume);
				// } else if (key.equalsIgnoreCase("maxlength")) {
				// fMaxLength = Integer.valueOf(value);
				// println("MaxLength set to " + fMaxLength);
			} else if (key.equalsIgnoreCase("maxwidth")) {
				fMaxWidth = Integer.valueOf(value);
				println("MaxWidth set to " + fMaxWidth);
			} else if (key.equalsIgnoreCase("maxorders")) {
				fMaxOrders = Integer.valueOf(value);
				println("MaxOrders set to " + fMaxOrders);
			} else if (key.equalsIgnoreCase("avgorders")) {
				fAvgOrders = Integer.valueOf(value);
				println("AvgOrders set to " + fAvgOrders);
			}
		}
	}

}
