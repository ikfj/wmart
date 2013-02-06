package org.wmart.agent;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * タイムラインを表すクラス (買いエージェント用)
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WTimelineBuyer {

	/** 1日のスロット数 */
	private int fSessionsPerDay;
	/** 現物市場で何スロット前に発注するか */
	private int fOffsetSpot;
	/** 日節 => 発注予定 */
	private LinkedHashMap<String, WTimelineBuyerEvent> fSchedule;
	/** 行番号 => 需要 */
	private LinkedHashMap<Integer, WTimelineBuyerDemand> fDemands;
	/** 注文ID => 注文票 */
	private LinkedHashMap<Long, WOrderForm> fOrderForms;

	/**
	 * コンストラクタ
	 * 
	 * @param sessionsPerDay
	 *            1日のスロット数
	 * @param offsetSpot
	 *            現物市場で何スロット前に発注するか
	 */
	public WTimelineBuyer(int sessionsPerDay, int offsetSpot) {
		fSessionsPerDay = sessionsPerDay;
		fOffsetSpot = offsetSpot;
		fSchedule = new LinkedHashMap<String, WTimelineBuyerEvent>();
		fDemands = new LinkedHashMap<Integer, WTimelineBuyerDemand>();
		fOrderForms = new LinkedHashMap<Long, WOrderForm>();
	}

	/**
	 * 需要ファイルを読み込む
	 * 
	 * @param demandFilename
	 *            ファイル名
	 * @param auctioneer
	 *            市場名
	 * @param splitSlot
	 *            需要を1スロットずつに分ける
	 * @param splitGood
	 *            需要を1商品ずつに分ける
	 * @throws Exception
	 */
	public void readFrom(String demandFilename, String auctioneer, boolean splitSlot,
		boolean splitGood) throws Exception {
		int dateColumn;
		if (auctioneer.equals("forward")) {
			dateColumn = 0;
		} else if (auctioneer.equals("spot")) {
			dateColumn = 1;
		} else {
			throw new Exception("Auctioneer name must be either 'forward' or 'spot'.");
		}
		BufferedReader br = new BufferedReader(new FileReader(path(demandFilename)));
		String line = null;
		int lineno = 0;
		while ((line = br.readLine()) != null) {
			lineno++;
			if (!line.startsWith("#")) {
				String[] sp1 = line.split(",");
				String dateSession = sp1[dateColumn];
				int price = Integer.parseInt(sp1[2]);
				String spec = sp1[3];
				String hint = sp1[4];
				// 発注予定リストと需要リストに注文票を追加
				WTimelineBuyerEvent event = new WTimelineBuyerEvent();
				WTimelineBuyerDemand demand = new WTimelineBuyerDemand();
				if (splitGood) {
					event.addPerGood(auctioneer, hint, spec);
				} else {
					event.add(auctioneer, price, spec);
				}
				if (splitSlot) {
					HashMap<String, Double> uprices = getUnitPriceMap(hint, spec);
					addPerSlot(uprices, event, demand); // 渡した event は捨てられる
				} else {
					add(dateSession, event, demand);
				}
				demand.setTotalOrderPrice(price);
				fDemands.put(lineno, demand);
			}
		}
		br.close();
	}

	/**
	 * 記録ファイルに書き出す
	 * 
	 * @param traceFilename
	 *            ファイル名
	 * @throws Exception
	 */
	public void writeTo(String traceFilename) throws Exception {
		PrintWriter pw = new PrintWriter(new FileOutputStream(path(traceFilename), false)); // 上書き
		pw.println("LineNo,OrderVolume,ContractVolume,Fulfilled,OrderPrice,ContractPrice,Profit,Welfare");
		int countAll = 0;
		int countFulfilled = 0;
		int sumOrderVolume = 0;
		int sumContractVolume = 0;
		long sumOrderPrice = 0;
		double sumContractPrice = 0.0;
		double sumProfit = 0.0;
		double sumWelfare = 0.0;
		for (Iterator<Entry<Integer, WTimelineBuyerDemand>> itr = fDemands.entrySet().iterator(); itr
			.hasNext();) {
			Entry<Integer, WTimelineBuyerDemand> entry = itr.next();
			int lineno = entry.getKey();
			WTimelineBuyerDemand demand = entry.getValue();
			int orderVolume = demand.getTotalOrderVolume();
			int contractVolume = demand.getTotalContractVolume();
			int orderPrice = demand.getTotalOrderPrice();
			double contractPrice = demand.getTotalContractPrice();
			double profit; // 買えた分の余剰
			double welfare; // 全体の余剰
			if (demand.isFulfilled()) {
				// 全部揃った場合
				profit = demand.getTotalContractProfit();
				welfare = (double) orderPrice - contractPrice;
				pw.println(String.format("%d,%d,%d,1,%d,%.2f,%.2f,%.2f", lineno, orderVolume,
					contractVolume, orderPrice, contractPrice, profit, welfare));
				countFulfilled++;
			} else {
				// 全部揃わなかった場合
				profit = 0.0;
				welfare = -contractPrice;
				pw.println(String.format("%d,%d,%d,0,%d,%.2f,%.2f,%.2f", lineno, orderVolume,
					contractVolume, orderPrice, contractPrice, profit, welfare));
			}
			countAll++;
			sumOrderVolume += orderVolume;
			sumContractVolume += contractVolume;
			sumOrderPrice += orderPrice;
			sumContractPrice += contractPrice;
			sumProfit += profit;
			sumWelfare += welfare;
		}
		double fulfilled = (double) countFulfilled / countAll;
		pw.println(String.format("Total,%d,%d,%.4f,%d,%.2f,%.2f,%.2f", sumOrderVolume,
			sumContractVolume, fulfilled, sumOrderPrice, sumContractPrice, sumProfit, sumWelfare));
		pw.close();
	}

	/**
	 * 発注予定を1スロットずつの注文票に分けて日節ごとのマップと需要に追加
	 * 
	 * @param uprices
	 *            商品別単価マップ
	 * @param event
	 *            発注予定
	 * @param demand
	 *            需要
	 */
	private void addPerSlot(HashMap<String, Double> uprices, WTimelineBuyerEvent event,
		WTimelineBuyerDemand demand) {
		ArrayList<WOrderForm> forms = event.getOrderForms();
		for (Iterator<WOrderForm> itr = forms.iterator(); itr.hasNext();) {
			WOrderForm form = itr.next();
			String auctioneer = form.getAuctioneerName();
			String spec = form.getSpec();
			String[] sp1 = spec.split(";");
			int n = sp1.length;
			String[] goods = new String[n];
			int[] volumes = new int[n];
			int[] atimes = new int[n];
			int[] dtimes = new int[n];
			int[] lengths = new int[n];
			int t1 = Integer.MAX_VALUE; // 全体の最早時刻
			int t2 = Integer.MIN_VALUE; // 全体の最遅時刻
			for (int i = 0; i < n; i++) {
				String sp3[] = sp1[i].split(":");
				goods[i] = sp3[0];
				volumes[i] = Integer.parseInt(sp3[1]);
				atimes[i] = Integer.parseInt(sp3[2]);
				dtimes[i] = Integer.parseInt(sp3[3]);
				lengths[i] = Integer.parseInt(sp3[4]);
				if (t1 > atimes[i]) {
					t1 = atimes[i];
				}
				if (t2 < dtimes[i]) {
					t2 = dtimes[i];
				}
			}
			// 1スロットごとにまとめる
			for (int t = t1; t <= t2; t++) {
				int t0 = t - fOffsetSpot; // 発注時刻
				int date = (t0 - 1) / fSessionsPerDay + 1;
				int session = (t0 - 1) % fSessionsPerDay + 1;
				String dateSession = date + "/" + session;
				double sum1 = 0.0;
				String spec1 = "";
				for (int i = 0; i < n; i++) {
					// 時間に余裕がある場合は前詰めとする
					if (atimes[i] <= t && t < atimes[i] + lengths[i]) {
						sum1 += uprices.get(goods[i]) * volumes[i];
						spec1 += String.format("%s:%d:%d:%d:%d;", goods[i], volumes[i], t, t, 1);
					}
				}
				if (!spec1.isEmpty()) {
					int price1 = Math.round((float) sum1);
					WTimelineBuyerEvent event1 = new WTimelineBuyerEvent();
					event1.add(auctioneer, price1, spec1);
					add(dateSession, event1, demand);
				}
			}

		}
	}

	/**
	 * 発注予定に含まれる注文票を日節ごとのマップと需要に追加
	 * 
	 * @param dateSession
	 *            日節
	 * @param event
	 *            発注予定
	 * @param demand
	 *            需要
	 */
	private void add(String dateSession, WTimelineBuyerEvent event, WTimelineBuyerDemand demand) {
		if (!fSchedule.containsKey(dateSession)) {
			fSchedule.put(dateSession, new WTimelineBuyerEvent());
		}
		fSchedule.get(dateSession).addAll(event);
		demand.addAll(event.getOrderForms());
	}

	/**
	 * 商品別単価マップを取得
	 * 
	 * @param hint
	 *            商品別単価
	 * @param spec
	 *            需要明細
	 * @return
	 */
	private HashMap<String, Double> getUnitPriceMap(String hint, String spec) {
		HashMap<String, Double> uprices = new HashMap<String, Double>();
		String[] sp1 = spec.split(";");
		String[] sp2 = hint.split(";");
		assert (sp1.length == sp2.length) : "Unmatch number of goods in demand file";
		for (int i = 0; i < sp1.length; i++) {
			String sp3[] = sp1[i].split(":");
			String good = sp3[0];
			double uprice = Double.parseDouble(sp2[i]);
			uprices.put(good, uprice);
		}
		return uprices;
	}

	/**
	 * 指定された日節の発注予定を取得
	 * 
	 * @param date
	 *            日
	 * @param session
	 *            節
	 * @return 発注予定
	 */
	public WTimelineBuyerEvent at(int date, int session) {
		String dateSession = date + "/" + session;
		if (!fSchedule.containsKey(dateSession)) {
			return new WTimelineBuyerEvent();
		}
		return fSchedule.get(dateSession);
	}

	/**
	 * 注文を登録
	 * 
	 * @param form
	 *            注文票
	 */
	public void putOrder(WOrderForm form) {
		fOrderForms.put(form.getOrderId(), form);
	}

	/**
	 * 登録された注文を削除
	 * 
	 * @param orderId
	 */
	public void removeOrder(long orderId) {
		fOrderForms.remove(orderId);
	}

	/**
	 * 登録された注文を取得
	 * 
	 * @param orderId
	 *            注文ID
	 * @return 注文票
	 */
	public WOrderForm getOrder(long orderId) {
		return fOrderForms.get(orderId);
	}

	/**
	 * 登録された注文に約定価格を設定
	 * 
	 * @param orderId
	 *            注文ID
	 * @param contractPrice
	 *            約定価格
	 */
	public void setContractPrice(long orderId, double contractPrice) {
		fOrderForms.get(orderId).setContractPrice(contractPrice);
	}

	/**
	 * 需要マップを取得
	 * 
	 * @return 需要マップ
	 */
	public LinkedHashMap<Integer, WTimelineBuyerDemand> getDemands() {
		return fDemands;
	}

	/**
	 * パス区切り文字を環境に合わせる
	 */
	private String path(String slashSeparatedPath) {
		return slashSeparatedPath.replace('/', File.separatorChar);
	}

	@Override
	public String toString() {
		return fSchedule.toString();
	}

	/**
	 * デバッグ用
	 * 
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.err
				.println("usage: java WTimelineBuyer demandFilename auctioneer splitSlot splitGood");
			System.err.println("  auctioneer = forward/spot");
			System.err.println("  splitSlot = true/false");
			System.err.println("  splitGood = true/false");
			System.exit(1);
		}
		boolean splitSlot = Boolean.parseBoolean(args[2]);
		boolean splitGood = Boolean.parseBoolean(args[3]);
		try {
			WTimelineBuyer tl = new WTimelineBuyer(24, 2);
			tl.readFrom(args[0], args[1], splitSlot, splitGood);
			System.out.println(tl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
