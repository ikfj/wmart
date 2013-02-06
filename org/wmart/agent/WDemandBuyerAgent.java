package org.wmart.agent;

import java.io.File;
import java.util.*;

import org.apache.log4j.Logger;
import org.wmart.cmdCore.WCOrderStatusCore;
import org.wmart.core.*;
import org.wmart.logger.WResourceLog;

/**
 * 買いエージェント。需要ファイルを読む
 * 
 * @author Ikki Fujiwara, NII
 */
public abstract class WDemandBuyerAgent extends WAgent {

	/** 市場名 */
	protected String fAuctioneer;
	/** 需要を1スロットずつに分ける */
	protected boolean fSplitSlot;
	/** 需要を1商品ずつに分ける */
	protected boolean fSplitGood;
	/** 需要ファイル名 */
	protected String fDemandFilename;
	/** 記録ファイル名 */
	protected String fTraceFilename;
	/** タイムライン */
	protected WTimelineBuyer fTimeline;

	private static Logger log = Logger.getLogger(WDemandBuyerAgent.class);

	/**
	 * コンストラクタ
	 */
	public WDemandBuyerAgent(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

	@Override
	public void doActions(int date, int session, int serverState, int maxDays, int sessionsPerDay,
		int sessionsForward, int maxLength) {
		// タイムラインを初期化
		if (fTimeline == null) {
			prepareTimeline(sessionsPerDay, 2);
		}
		WResourceLog resource;
		switch (serverState) {
		case WServerStatusMaster.AFTER_HOURS:
			if (fAuctioneer.equals("forward") && session != sessionsPerDay) {
				break;
			}
			if (fAuctioneer.equals("forward")) {
				log.info(String.format("■買い先物注文■ %d/%d", date, session));
			} else if (fAuctioneer.equals("spot")) {
				log.info(String.format("■買い現物注文■ %d/%d", date, session));
			}
			resource = new WResourceLog(date, session, fLoginName);
			updateStatus(resource);
			// 注文予定にもとづいて発注
			WTimelineBuyerEvent event = fTimeline.at(date, session);
			for (Iterator<WOrderForm> itr = event.iterator(); itr.hasNext();) {
				WOrderForm form = itr.next();
				long orderId = orderRequest(form);
				assert (orderId >= 0);
				form.setOrderId(orderId);
				fTimeline.putOrder(form);
				resource.addBuying("#" + orderId, form.getSpec());
				log.info(String.format("%s $%d => #%d", form.getSpec(), form.getOrderPrice(),
					form.getOrderId()));
			}
			writeResourceLog(resource);
			break;
		case WServerStatusMaster.SHUTDOWN:
			resource = new WResourceLog(date, session, fLoginName);
			updateStatus(resource);
			traceTimeline();
			writeResourceLog(resource);
			break;
		}
	}

	/**
	 * タイムラインを準備
	 * 
	 * @param sessionsPerDay
	 *            1日のセッション数
	 * @param offsetSpot
	 *            現物市場で何スロット前に発注するか
	 */
	private void prepareTimeline(int sessionsPerDay, int offsetSpot) {
		fTimeline = new WTimelineBuyer(sessionsPerDay, offsetSpot);
		try {
			fTimeline.readFrom(fDemandFilename, fAuctioneer, fSplitSlot, fSplitGood);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * タイムラインを記録
	 */
	private void traceTimeline() {
		fTraceFilename = fLogDir + File.separator + "trace_buyer_" + fLoginName + ".csv";
		try {
			fTimeline.writeTo(fTraceFilename);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 約定状況を取得し、タイムラインと資源情報を更新する
	 */
	private void updateStatus(WResourceLog resource) {
		ArrayList<HashMap<String, String>> arr = getOrderStatus(fAuctioneer);
		for (Iterator<HashMap<String, String>> itr = arr.iterator(); itr.hasNext();) {
			HashMap<String, String> os = itr.next();
			int sellBuy = Integer.valueOf(os.get(WCOrderStatusCore.INT_SELL_BUY));
			assert (sellBuy == WOrder.BUY);
			long orderId = Long.valueOf(os.get(WCOrderStatusCore.LONG_ORDER_ID));
			long orderPrice = Long.valueOf(os.get(WCOrderStatusCore.LONG_ORDER_PRICE));
			double contractPrice = Double.valueOf(os.get(WCOrderStatusCore.DOUBLE_CONTRACT_PRICE));
			if (orderPrice < 0 || contractPrice == 0.0) {
				// キャンセル済み or 未約定
				String orderSpec = os.get(WCOrderStatusCore.STRING_ORDER_SPEC);
				if (orderPrice < 0) {
					fTimeline.removeOrder(orderId);
					resource.addCancelBuying("#" + orderId, orderSpec);
					log.info(String.format("<canceled> #%d %s", orderId, orderSpec));
				} else {
					resource.addBuying("#" + orderId, orderSpec);
					log.info(String
						.format("<remaining> #%d %s $%d", orderId, orderSpec, orderPrice));
				}
			} else {
				// 約定済み
				String orderSpec = os.get(WCOrderStatusCore.STRING_ORDER_SPEC);
				fTimeline.setContractPrice(orderId, contractPrice);
				resource.addBought("#" + orderId, orderSpec);
				log.info(String.format("<contracted> #%d %s $%d $%.2f", orderId, orderSpec,
					orderPrice, contractPrice));
			}
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
		int demandIndex = 0;
		for (int i = 0; i < args.length; ++i) {
			StringTokenizer st = new StringTokenizer(args[i], "= ");
			String key = st.nextToken();
			String value = st.nextToken();
			if (key.equalsIgnoreCase("demandfile")) {
				fDemandFilename = value;
				println("DemandFilename set to " + fDemandFilename);
			} else if (key.equalsIgnoreCase("demandindex")) {
				demandIndex = Integer.valueOf(value);
				println("DemandIndex set to " + demandIndex);
			}
		}
		String[] demandFilenames = fDemandFilename.split(";");
		fDemandFilename = demandFilenames[demandIndex];
	}
}
