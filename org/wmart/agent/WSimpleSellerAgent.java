package org.wmart.agent;

import java.util.*;

import org.apache.log4j.*;
import org.wmart.cmdCore.*;
import org.wmart.core.*;
import org.wmart.logger.*;

/**
 * 売り手
 * 
 * @author Ikki Fujiwara, NII
 */
public class WSimpleSellerAgent extends WAgent {

	/** 商品名 */
	public String fGood = "A";
	/** 最低単価 */
	public int fMinUnitPrice = 1;
	/** 最高単価 */
	public int fMaxUnitPrice = 1;
	/** 数量 */
	public int fVolume = 100;
	/** タイムライン */
	private WTimelineSeller fTimeline = null;

	private static Logger log = Logger.getLogger(WSimpleSellerAgent.class);

	/**
	 * コンストラクタ
	 */
	public WSimpleSellerAgent(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

	@Override
	public void doActions(int day, int session, int serverState, int maxDays, int sessionsPerDay,
		int slotsForward, int maxLength) {
		switch (serverState) {
		case WServerStatusMaster.SHUTDOWN:
			updateTimeline(0, maxDays, sessionsPerDay, slotsForward); // 進めない
			updateLog(day, session);
			break;
		case WServerStatusMaster.AFTER_HOURS:
			if (session == sessionsPerDay) {
				log.info("■売り注文■");
				updateTimeline(sessionsPerDay, maxDays, sessionsPerDay, slotsForward); // 1日分進める
				ArrayList<WOrderForm> forms = new ArrayList<WOrderForm>();
				// 先物注文。未注文資源量が等しい区間を1注文とする
				int t0 = (day + 1) * sessionsPerDay + 1; // 取引対象の最早時刻
				int t2 = (day + 1) * sessionsPerDay + slotsForward; // 取引対象の最遅時刻
				int v0 = (int) fTimeline.getFreeStockAt(t0); // 小数点以下切り捨て
				double u = uniformDistributed(fMinUnitPrice, fMaxUnitPrice);
				for (int t1 = t0; t1 <= t2; t1++) {
					int v1 = (int) fTimeline.getFreeStockAt(t1); // 小数点以下切り捨て
					if (v0 != v1) {
						addOrder(forms, "forward", u, v0, t0, t1 - 1);
						t0 = t1;
						v0 = v1;
					}
				}
				addOrder(forms, "forward", u, v0, t0, t2);
				// 注文票を送る
				for (Iterator<WOrderForm> itr = forms.iterator(); itr.hasNext();) {
					WOrderForm form = itr.next();
					orderRequest(form);
				}
				// 資源情報ログを更新
				updateLog(day, session);
			}
			break;
		}
	}

	/**
	 * 約定状況を取得し、タイムラインを更新する
	 * 
	 * @param step
	 *            シフトするステップ数
	 */
	private void updateTimeline(int step, int maxDays, int sessionsPerDay, int slotsForward) {
		if (fTimeline == null) {
			int slots = 2 * sessionsPerDay + slotsForward;
			assert (fMinUnitPrice == fMaxUnitPrice);
			fTimeline = new WTimelineSeller(fGood, slots, 1, fVolume, fMinUnitPrice, slotsForward,
				maxDays * sessionsPerDay);
		} else {
			fTimeline.succeed(step);
		}
		ArrayList<HashMap<String, String>> arr = getOrderStatus("forward");
		for (Iterator<HashMap<String, String>> itr = arr.iterator(); itr.hasNext();) {
			HashMap<String, String> os = itr.next();
			int sellBuy = Integer.valueOf(os.get(WCOrderStatusCore.INT_SELL_BUY));
			assert (sellBuy == WOrder.SELL);
			long orderPrice = Integer.valueOf(os.get(WCOrderStatusCore.LONG_ORDER_PRICE));
			double contractPrice = Double.valueOf(os.get(WCOrderStatusCore.DOUBLE_CONTRACT_PRICE));
			if (orderPrice < 0 || contractPrice == 0.0) {
				// キャンセル済み or 未約定
				String orderSpec = os.get(WCOrderStatusCore.STRING_ORDER_SPEC);
				String[] specArray = orderSpec.split(";");
				assert (specArray.length == 1);
				// log.info(String.format((orderPrice < 0) ? "canceled " : "remaining "
				// + "orders [%s]", orderSpec));
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
				// log.info(String.format("contracted orders [%s]", soldSpec));
				String name = specElements[0];
				int earliest = Integer.valueOf(specElements[2]);
				int latest = Integer.valueOf(specElements[3]);
				assert (name.equals(fGood));
				String[] soldVolumes = specElements[4].split(",");
				for (int t = earliest; t <= latest; t++) {
					double volume = Double.valueOf(soldVolumes[t - earliest]);
					fTimeline.contractAt(t, volume, contractPrice);
				}
			}
		}
		log.trace("\n" + fTimeline.toString());
	}

	/**
	 * 資源情報ログを更新する
	 */
	private void updateLog(int day, int session) {
		WResourceLog rlog = new WResourceLog(day, session, fLoginName);
		rlog.addSelling("#0", fTimeline.encodeStock());
		writeResourceLog(rlog);
	}

	/**
	 * 注文票を生成してリストに追加する
	 */
	private void addOrder(ArrayList<WOrderForm> forms, String auctioneer, double unitprice,
		int volume, int earliest, int latest) {
		if (unitprice > 0 && volume > 0) {
			int length = latest - earliest + 1;
			int price = Math.round((float) unitprice * volume);
			WOrderForm form = new WOrderForm();
			form.setAuctioneerName(auctioneer);
			form.setBuySell(WOrderForm.SELL);
			form.setOrderPrice(price);
			form.setSpec(String.format("%s:%d:%d:%d:%d;", fGood, volume, earliest, latest, length));
			forms.add(form);
			log.info(String.format("%s%d*%d[%d-%d] $%d @%.2f", fGood, volume, length, earliest,
				latest, price, unitprice));
		}
	}

	/**
	 * ランダムな値を選ぶ
	 */
	private double uniformDistributed(double min, double max) {
		Random rand = getRandom();
		return min + rand.nextDouble() * (max - min);
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
			} else if (key.equalsIgnoreCase("minprice")) {
				fMinUnitPrice = Integer.valueOf(value);
				println("MinUnitPrice set to " + fMinUnitPrice);
			} else if (key.equalsIgnoreCase("maxprice")) {
				fMaxUnitPrice = Integer.valueOf(value);
				println("MaxUnitPrice set to " + fMaxUnitPrice);
			} else if (key.equalsIgnoreCase("volume")) {
				fVolume = Integer.valueOf(value);
				println("Volume set to " + fVolume);
			}
		}
	}

}
