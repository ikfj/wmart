/**
 *
 */
package org.wmart.analyzer;

import java.util.*;

/**
 * 買い注文を表すクラス (ビジュアライザ用)
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WOrderBuyer {

	/** 注文明細 */
	private TreeMap<String, WOrderSpec> fSpecMap = new TreeMap<String, WOrderSpec>();
	/** 最早時刻 */
	private int fEarliest = Integer.MAX_VALUE;
	/** 最遅時刻 */
	private int fLatest = -Integer.MAX_VALUE;
	/** 約定済みフラグ */
	private boolean fContracted = false;

	/** コンストラクタ */
	public WOrderBuyer() {
	}

	/** コンストラクタ */
	public WOrderBuyer(boolean contracted) {
		fContracted = contracted;
	}

	/** コンストラクタ */
	public WOrderBuyer(String encodedSpecs, boolean contracted) {
		setSpec(encodedSpecs);
		fContracted = contracted;
	}

	/**
	 * 注文明細を文字列で取得する
	 * 
	 * @return "商品名:希望数量:最早時刻:最遅時刻:延べ時間;" の繰り返し (ソートされている)
	 */
	public String getSpec() {
		StringBuilder encodedSpecs = new StringBuilder();
		for (Iterator<WOrderSpec> itr = fSpecMap.values().iterator(); itr.hasNext();) {
			WOrderSpec spec = itr.next();
			encodedSpecs.append(spec.encode()).append(";");
		}
		return encodedSpecs.toString();
	}

	/**
	 * 注文明細を文字列で設定する
	 * 
	 * @param encodedSpecs
	 */
	public void setSpec(String encodedSpecs) {
		fSpecMap.clear();
		String[] specArray = encodedSpecs.split(";");
		for (int i = 0; i < specArray.length; i++) {
			add(specArray[i]);
		}
	}

	/**
	 * 注文明細を文字列で1件追加する
	 * 
	 * @param encodedSpec
	 */
	public void add(String encodedSpec) {
		WOrderSpec spec = new WOrderSpec(encodedSpec);
		add(spec);
	}

	/**
	 * 注文明細を1件追加する
	 * 
	 * @param spec
	 */
	public void add(WOrderSpec spec) {
		fSpecMap.put(spec.encode(), spec);
		if (fEarliest > spec.getArrivalTime()) {
			fEarliest = spec.getArrivalTime();
		}
		if (fLatest < spec.getDeadlineTime()) {
			fLatest = spec.getDeadlineTime();
		}
	}

	/**
	 * 注文明細のイテレータを取得する
	 * 
	 * @return
	 */
	public Iterator<WOrderSpec> iterator() {
		return fSpecMap.values().iterator();
	}

	/**
	 * 注文明細を取得します。
	 * 
	 * @return 注文明細
	 */
	public TreeMap<String, WOrderSpec> getSpecList() {
		return fSpecMap;
	}

	/**
	 * 最早時刻を取得します。
	 * 
	 * @return 最早時刻
	 */
	public int getEarliest() {
		return fEarliest;
	}

	/**
	 * 最早時刻を設定します。
	 * 
	 * @param fEarliest
	 *            最早時刻
	 */
	public void setEarliest(int fEarliest) {
		this.fEarliest = fEarliest;
	}

	/**
	 * 最遅時刻を取得します。
	 * 
	 * @return 最遅時刻
	 */
	public int getLatest() {
		return fLatest;
	}

	/**
	 * 最遅時刻を設定します。
	 * 
	 * @param fLatest
	 *            最遅時刻
	 */
	public void setLatest(int fLatest) {
		this.fLatest = fLatest;
	}

	/**
	 * 約定済みフラグを取得します。
	 * 
	 * @return 約定済みフラグ
	 */
	public boolean isContracted() {
		return fContracted;
	}

	/**
	 * 約定済みフラグを設定します。
	 * 
	 * @param fContracted
	 *            約定済みフラグ
	 */
	public void setContracted(boolean fContracted) {
		this.fContracted = fContracted;
	}

	/**
	 * 注文明細が一致するか?
	 */
	public boolean equals(WOrderBuyer another) {
		return getSpec().equals(another.getSpec());
	}

	@Override
	public String toString() {
		return String.format("%s%s", getSpec(), fContracted ? "bought" : "buying");
	}

	/**
	 * デバッグ用
	 */
	public static void main(String argv[]) {
		WOrderBuyer o1 = new WOrderBuyer("D:8:214:221:8;B:8:204:211:8;A:18:212:213:2;", false);
		WOrderBuyer o2 = new WOrderBuyer("A:18:212:213:2;B:8:204:211:8;D:8:214:221:8;", false);
		System.out.println(o1);
		System.out.println(o2);
		System.out.println(o1.equals(o2));
	}
}
