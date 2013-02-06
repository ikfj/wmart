package org.wmart.analyzer;

import java.util.*;

/**
 * 買い手のタイムラインを表すクラス (ビジュアライザ用、配列ベース)
 * 
 * 主データは2重のリンクリストからなる。
 * <ul>
 * <li>内側のリストは注文が互いに重ならないようにプロットされた1本のタイムラインを表す。</li>
 * <li>外側のリストは時間が重なる注文を分けるために作られた複数のタイムラインを表す。</li>
 * </ul>
 * 副データは1個のツリーマップからなり、すべての注文を保持する。
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WTimelineBuyerArray {

	/** 名前 */
	private String fName;
	/** 主データ */
	private LinkedList<LinkedList<WOrderBuyer>> fOrderList;
	/** 副データ */
	private TreeMap<String, WOrderBuyer> fOrderMap;

	/**
	 * コンストラクタ
	 */
	public WTimelineBuyerArray(String username) {
		fName = username;
		fOrderList = new LinkedList<LinkedList<WOrderBuyer>>();
		fOrderMap = new TreeMap<String, WOrderBuyer>();
	}

	/**
	 * 文字列から追加読み込み
	 * 
	 * @param encodedSpecs
	 *            "商品名:希望数量:最早時刻:最遅時刻:延べ時間;" の繰り返し
	 * @param contracted
	 *            約定済みフラグ
	 */
	public void append(String encodedSpecs, boolean contracted) {
		WOrderBuyer order = new WOrderBuyer(encodedSpecs, contracted);
		String specStr = order.getSpec(); // ソートされる
		assert (!fOrderMap.containsKey(specStr)) : "order " + specStr + " exists";
		fOrderMap.put(specStr, order);
		availableList(order.getEarliest(), order.getLatest()).add(order);
	}

	/**
	 * 文字列から上書き読み込み、約定済みフラグを更新
	 * 
	 * @param encodedSpecs
	 *            "商品名:希望数量:最早時刻:最遅時刻:延べ時間;" の繰り返し
	 * @param contracted
	 *            約定済みフラグ
	 */
	public void overread(String encodedSpecs, boolean contracted) {
		WOrderBuyer order = new WOrderBuyer(encodedSpecs, contracted);
		String specStr = order.getSpec(); // ソートされる
		assert (fOrderMap.containsKey(specStr)) : "order " + specStr + " doesn't exist";
		fOrderMap.get(specStr).setContracted(contracted);
	}

	/**
	 * 既存の注文時間帯と重ならないタイムラインを返す
	 */
	private LinkedList<WOrderBuyer> availableList(int earliest, int latest) {
		for (Iterator<LinkedList<WOrderBuyer>> itr1 = fOrderList.iterator(); itr1.hasNext();) {
			LinkedList<WOrderBuyer> sublist = itr1.next();
			Boolean ok = true;
			for (Iterator<WOrderBuyer> itr2 = sublist.iterator(); itr2.hasNext();) {
				WOrderBuyer order = itr2.next();
				for (Iterator<WOrderSpec> itr3 = order.iterator(); itr3.hasNext();) {
					WOrderSpec spec = itr3.next();
					if (earliest <= spec.getDeadlineTime() && latest >= spec.getArrivalTime()) {
						ok = false;
					}
				}
				if (!ok) {
					break;
				}
			}
			if (ok) {
				return sublist;
			}
		}
		LinkedList<WOrderBuyer> sublist = new LinkedList<WOrderBuyer>();
		fOrderList.add(sublist);
		return sublist;
	}

	/**
	 * 名前を取得します。
	 * 
	 * @return 名前
	 */
	public String getName() {
		return fName;
	}

	/**
	 * 注文リストを取得します。
	 * 
	 * @return 注文リスト
	 */
	public LinkedList<LinkedList<WOrderBuyer>> getOrderList() {
		return fOrderList;
	}

	/**
	 * 注文リストのイテレータを取得します。
	 * 
	 * @return 注文のイテレータ
	 */
	public Iterator<LinkedList<WOrderBuyer>> iterator() {
		return fOrderList.iterator();
	}

	/**
	 * 動作テスト用
	 */
	public static void main(String argv[]) {
		WTimelineBuyerArray tl = new WTimelineBuyerArray("test");
		tl.append("B:5:10:12:3;E:11:13:13:1;D:2:14:16:2;", false); // 10-15
		tl.append("D:17:16:16:1;C:19:17:19:3;B:13:20:20:1;", false); // 16-20
		// tl.append("E:15:13:13:1;A:11:14:14:1;D:7:15:16:2;",false); // 13-16
		// tl.append("A:14:24:25:2;B:15:26:29:1;D:16:26:29:3;C:17:26:29:4;E:16:30:32:3;",false); //
		// 24-32
		System.out.println(tl.getOrderList().toString());
		tl.overread("B:5:10:12:3;E:11:13:13:1;D:2:14:16:2;", true); // 10-15
		tl.overread("D:17:16:16:1;C:19:17:19:3;B:13:20:20:1;", true); // 16-20
		System.out.println(tl.getOrderList().toString());
	}
}
