package org.wmart.agent;

import java.util.*;

import org.apache.log4j.*;
import org.wmart.core.*;

/**
 * 売り手
 * 
 * @author Ikki Fujiwara, NII
 */
public class WSpecialSellerAgentForSaint2010 extends WAgent {

	/** 商品名 */
	public String fGood = "A";
	/** 最低単価 */
	public int fMinUnitPrice = 1;
	/** 最高単価 */
	public int fMaxUnitPrice = 1;
	/** 数量 */
	public int fVolume = 100;

	private static Logger log = Logger.getLogger(WSpecialSellerAgentForSaint2010.class);

	/**
	 * コンストラクタ
	 */
	public WSpecialSellerAgentForSaint2010(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

	@Override
	public void doActions(int day, int session, int serverState, int maxDays, int sessionsPerDay, int slotsForward,
		int maxLength) {
		if (serverState != WServerStatusMaster.AFTER_HOURS) {
			return; // マシンエージェントは AFTER_HOURS に活動する
		}
		if (session != sessionsPerDay) {
			return; // 最終セッションのみ仕事する
		}
		ArrayList<WOrderForm> forms = new ArrayList<WOrderForm>();

		// 先物注文。未注文資源量が等しい区間を1注文とする
		int t0 = (day + 1) * sessionsPerDay + 1; // 取引対象の最早時刻
		int t2 = (day + 1) * sessionsPerDay + slotsForward; // 取引対象の最遅時刻
		log.info("■売り注文■");
		addOrder(forms, "forward", -1, -1, t0, t2, day);

		// 注文票を送る
		for (Iterator<WOrderForm> itr = forms.iterator(); itr.hasNext();) {
			WOrderForm form = itr.next();
			orderRequest(form);
		}
	}

	/**
	 * 注文票を生成してリストに追加する
	 */
	private void addOrder(ArrayList<WOrderForm> forms, String auctioneer, double unitprice0, int volume0, int earliest,
		int latest, int day) {
		WOrderForm form = new WOrderForm();
		form.setAuctioneerName(auctioneer);
		form.setBuySell(WOrderForm.SELL);
		String good = "";
		int price = -1;
		int volume = 0;
		int length = 0;
		int atime = -1;
		int dtime = -1;
		if (fLoginName.equalsIgnoreCase("provider1")) {
			good = "serviceA";
			price = 20;
			volume = 40;
			length = 4;
			atime = 0 + earliest;
			dtime = 3 + earliest;
		}
		if (fLoginName.equalsIgnoreCase("provider2")) {
			good = "serviceB";
			price = 15;
			volume = (day == 1) ? 30 : 20;
			length = 4;
			atime = 0 + earliest;
			dtime = 3 + earliest;
		}
		if (fLoginName.equalsIgnoreCase("provider3")) {
			good = "serviceB";
			price = 9;
			volume = (day == 1) ? 30 : 20;
			length = 2;
			atime = 2 + earliest;
			dtime = 3 + earliest;
		}
		// 現物市場
		if (day >= 2) {
			length = 1;
			atime = earliest;
			dtime = earliest;
		}
		double unitprice = price;
		form.setSpec(String.format("%s:%d:%d:%d:%d;", good, volume, atime, dtime, length));
		form.setOrderPrice(price);
		forms.add(form);
		log.info(String.format("\t%s%d*%d[%d-%d] $%d @%.2f", good, volume, length, atime, dtime, price, unitprice));
	}

	/**
	 * 注文票を生成してリストに追加する
	 */
	private void addOrder1(ArrayList<WOrderForm> forms, String auctioneer, double unitprice, int volume, int earliest,
		int latest) {
		if (unitprice > 0 && volume > 0) {
			int length = latest - earliest + 1;
			int price = Math.round((float) unitprice * volume);
			WOrderForm form = new WOrderForm();
			form.setAuctioneerName(auctioneer);
			form.setBuySell(WOrderForm.SELL);
			form.setOrderPrice(price);
			form.setSpec(String.format("%s:%d:%d:%d:%d;", fGood, volume, earliest, latest, length));
			forms.add(form);
			log.info(String.format("\t%s%d*%d[%d-%d] $%d @%.2f", fGood, volume, length, earliest, latest, price,
				unitprice));
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
