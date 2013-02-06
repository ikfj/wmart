package org.wmart.agent;

import java.util.*;

/**
 * タイムライン中の1件の発注予定を表すクラス
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WTimelineBuyerEvent {

	/** 注文票リスト */
	private ArrayList<WOrderForm> fOrderForms;

	/**
	 * コンストラクタ
	 */
	public WTimelineBuyerEvent() {
		fOrderForms = new ArrayList<WOrderForm>();
	}

	/**
	 * 注文を追加 (1商品ずつに分ける)
	 * 
	 * @param auctioneer
	 *            市場名
	 * @param hint
	 *            商品別単価
	 * @param spec
	 *            需要明細
	 */
	public void addPerGood(String auctioneer, String hint, String spec) {
		String[] sp1 = spec.split(";");
		String[] sp2 = hint.split(";");
		assert (sp1.length == sp2.length) : "Unmatch number of goods in demand file";
		for (int i = 0; i < sp1.length; i++) {
			String sp3[] = sp1[i].split(":");
			String good = sp3[0];
			int volume = Integer.parseInt(sp3[1]);
			int atime = Integer.parseInt(sp3[2]);
			int dtime = Integer.parseInt(sp3[3]);
			int length = Integer.parseInt(sp3[4]);
			double uprice = Double.parseDouble(sp2[i]);
			int price1 = Math.round((float) uprice * volume * length);
			String spec1 = String.format("%s:%d:%d:%d:%d;", good, volume, atime, dtime, length);
			add(auctioneer, price1, spec1);
		}
	}

	/**
	 * 注文を追加
	 * 
	 * @param auctioneer
	 *            市場名
	 * @param price
	 *            価格
	 * @param spec
	 *            需要明細
	 */
	public void add(String auctioneer, int price, String spec) {
		fOrderForms.add(makeOrderForm(auctioneer, price, spec));
	}

	/**
	 * 注文をマージ
	 * 
	 * @param otherEvent
	 *            発注予定
	 */
	public void addAll(WTimelineBuyerEvent otherEvent) {
		fOrderForms.addAll(otherEvent.getOrderForms());
	}

	/**
	 * 注文票を生成
	 */
	private WOrderForm makeOrderForm(String auctioneer, int price, String spec) {
		WOrderForm form = new WOrderForm();
		form.setAuctioneerName(auctioneer);
		form.setBuySell(WOrderForm.BUY);
		form.setOrderPrice(price);
		form.setSpec(spec);
		return form;
	}

	/**
	 * 注文票のイテレータを取得
	 * 
	 * @return 注文票のイテレータ
	 */
	public Iterator<WOrderForm> iterator() {
		return fOrderForms.iterator();
	}

	/**
	 * 注文票リストを取得します。
	 * 
	 * @return 注文票リスト
	 */
	public ArrayList<WOrderForm> getOrderForms() {
		return fOrderForms;
	}

	/* (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s", fOrderForms);
	}

}
