package org.wmart.agent;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;
import org.wmart.cmdCore.*;
import org.wmart.core.*;
import org.wmart.logger.*;

/**
 * 売りエージェント。先物注文の売れ残りを現物注文する
 * 
 * @author Ikki Fujiwara, NII
 */
public class WDualSellerAgent extends WAgent {

	/** 商品名 */
	public String fGood;
	/** 供給量 */
	public int fVolume;
	/** 原単価 */
	public int fUnitPrice;
	/** 記録ファイル名 */
	protected String fTraceFilename;
	/** タイムライン */
	private WTimelineSeller fTimeline = null;
	/** タイムラインを進める幅 */
	private int fTimelineStep;

	private static Logger log = Logger.getLogger(WDualSellerAgent.class);

	/**
	 * コンストラクタ
	 */
	public WDualSellerAgent(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

	@Override
	public void doActions(int date, int session, int serverState, int maxDays, int sessionsPerDay,
		int slotsForward, int maxLength) {
		fTimelineStep = 1;
		WResourceLog resource;
		switch (serverState) {
		case WServerStatusMaster.AFTER_HOURS:
			// タイムラインを初期化／進める
			prepareTimeline(maxDays, sessionsPerDay, slotsForward);
			resource = new WResourceLog(date, session, fLoginName);
			ArrayList<WOrderForm> forms = new ArrayList<WOrderForm>();
			if (session == sessionsPerDay - 1) {
				updateStatus("forward");
			}
			if (session == sessionsPerDay) {
				log.info(String.format("■売り先物注文■ %d/%d", date, session));
				addOrdersFor(forms, "forward", (date + 1) * sessionsPerDay + 1, slotsForward);
			}
			log.info(String.format("■売り現物注文■ %d/%d", date, session));
			updateStatus("spot");
			addOrdersFor(forms, "spot", (date - 1) * sessionsPerDay + session + 2, 1);
			String orderIds = "";
			// 発注
			for (Iterator<WOrderForm> itr = forms.iterator(); itr.hasNext();) {
				WOrderForm form = itr.next();
				long orderId = orderRequest(form);
				orderIds += "#" + orderId;
			}
			resource.addSelling(orderIds, fTimeline.encodeStock());
			fTimeline.record(fTimelineStep);
			writeResourceLog(resource);
			break;
		case WServerStatusMaster.SHUTDOWN:
			resource = new WResourceLog(date, session, fLoginName);
			updateStatus("forward");
			updateStatus("spot");
			fTimeline.record(fTimelineStep);
			traceTimeline();
			writeResourceLog(resource);
			break;
		}
	}

	/**
	 * タイムラインを準備
	 * 
	 * @param maxDays
	 *            運用日数
	 * @param sessionsPerDay
	 *            1日のセッション数
	 * @param slotsForward
	 *            先物取引対象スロット数
	 */
	private void prepareTimeline(int maxDays, int sessionsPerDay, int slotsForward) {
		if (fTimeline == null) {
			int slots = 2 * sessionsPerDay + slotsForward;
			fTimeline = new WTimelineSeller(fGood, slots, 1, fVolume, fUnitPrice, slotsForward,
				maxDays * sessionsPerDay);
		} else {
			fTimeline.succeed(fTimelineStep);
		}
	}

	/**
	 * タイムラインを記録
	 */
	private void traceTimeline() {
		fTraceFilename = fLogDir + File.separator + "trace_seller_" + fLoginName + ".csv";
		try {
			fTimeline.writeTo(fTraceFilename);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 約定状況を取得し、タイムラインを更新する
	 * 
	 */
	private void updateStatus(String auctioneer) {
		ArrayList<HashMap<String, String>> arr = getOrderStatus(auctioneer);
		for (Iterator<HashMap<String, String>> itr = arr.iterator(); itr.hasNext();) {
			HashMap<String, String> os = itr.next();
			int sellBuy = Integer.valueOf(os.get(WCOrderStatusCore.INT_SELL_BUY));
			assert (sellBuy == WOrder.SELL);
			long orderId = Long.valueOf(os.get(WCOrderStatusCore.LONG_ORDER_ID));
			long orderPrice = Integer.valueOf(os.get(WCOrderStatusCore.LONG_ORDER_PRICE)); // 1スロット単価
			double contractPrice = Double.valueOf(os.get(WCOrderStatusCore.DOUBLE_CONTRACT_PRICE)); // 総額
			if (orderPrice < 0 || contractPrice == 0.0) {
				// キャンセル済み or 未約定
				String orderSpec = os.get(WCOrderStatusCore.STRING_ORDER_SPEC);
				String[] specArray = orderSpec.split(";");
				assert (specArray.length == 1);
				if (orderPrice < 0) {
					log.info(String.format("<canceled> #%d %s", orderId, orderSpec));
				} else {
					log.info(String.format("<remaining> #%d %s $%d/slot", orderId, orderSpec,
						orderPrice));
				}
				for (int i = 0; i < specArray.length; i++) {
					String[] specElements = specArray[i].split(":");
					String name = specElements[0];
					assert (name.equals(fGood));
					int volume = Integer.valueOf(specElements[1]);
					int earliest = Integer.valueOf(specElements[2]);
					int latest = Integer.valueOf(specElements[3]);
					for (int t = earliest; t <= latest; t++) {
						if (orderPrice < 0) {
							fTimeline.cancelAt(t, volume);
						} else {
							fTimeline.orderAt(t, volume);
						}
					}
				}
			} else {
				// 約定済み
				String soldSpec = os.get(WCOrderStatusCore.STRING_CONTRACT_SPEC);
				String[] specElements = soldSpec.split(":");
				String name = specElements[0];
				int earliest = Integer.valueOf(specElements[2]);
				int latest = Integer.valueOf(specElements[3]);
				String[] soldVolumes = specElements[4].split(",");
				String[] soldPrices = specElements[5].split(",");
				assert (name.equals(fGood));
				log.info(String.format("<contracted> #%d %s $%d/slot $%.2f", orderId, soldSpec,
					orderPrice, contractPrice));
				for (int t = earliest; t <= latest; t++) {
					double volume = Double.valueOf(soldVolumes[t - earliest]);
					double price = Double.valueOf(soldPrices[t - earliest]);
					fTimeline.contractAt(t, volume, price);
				}
			}
		}
		log.trace("\n" + fTimeline.toString());
	}

	/**
	 * 注文票を生成してリストに追加する
	 * 
	 * @param forms
	 *            注文票リスト
	 * @param auctioneer
	 *            市場名
	 * @param offset
	 *            取引対象の始端時刻
	 * @param slots
	 *            取引対象のスロット数
	 */
	private void addOrdersFor(ArrayList<WOrderForm> forms, String auctioneer, int offset, int slots) {
		int t0 = offset; // 取引対象の最早時刻
		int t2 = offset + slots - 1; // 取引対象の最遅時刻
		int v0 = (int) fTimeline.getFreeStockAt(t0); // 小数点以下切り捨て
		int u = fTimeline.getUnitPrice();
		// 未注文資源量が等しい区間をまとめて1注文とする
		for (int t1 = t0; t1 <= t2; t1++) {
			int v1 = (int) fTimeline.getFreeStockAt(t1); // 小数点以下切り捨て
			if (v0 != v1) {
				addOrder(forms, auctioneer, u, v0, t0, t1 - 1);
				t0 = t1;
				v0 = v1;
			}
		}
		addOrder(forms, auctioneer, u, v0, t0, t2);
	}

	/**
	 * 注文票を生成してリストに追加する
	 */
	private void addOrder(ArrayList<WOrderForm> forms, String auctioneer, int unitprice,
		int volume, int earliest, int latest) {
		if (unitprice > 0 && volume > 0) {
			int length = latest - earliest + 1;
			int price = unitprice * volume;
			WOrderForm form = new WOrderForm();
			form.setAuctioneerName(auctioneer);
			form.setBuySell(WOrderForm.SELL);
			form.setOrderPrice(price);
			form.setSpec(String.format("%s:%d:%d:%d:%d;", fGood, volume, earliest, latest, length));
			forms.add(form);
			log.info(String.format("%s%d*%d[%d-%d] $%d/slot", fGood, volume, length, earliest,
				latest, price));
		}
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
			if (key.equalsIgnoreCase("good")) {
				fGood = value;
				println("Good set to " + fGood);
			} else if (key.equalsIgnoreCase("price")) {
				fUnitPrice = Integer.valueOf(value);
				println("UnitPrice set to " + fUnitPrice);
			} else if (key.equalsIgnoreCase("volume")) {
				fVolume = Integer.valueOf(value);
				println("Volume set to " + fVolume);
			}
		}
	}

}
