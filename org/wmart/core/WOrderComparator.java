package org.wmart.core;

import java.util.*;

/**
 * WOrderオブジェクトを辞書式順序に ソートするために利用するComparatorです．
 * 
 * @author 川部 裕司
 * @author 森下 領平
 * @author 小野 功
 */
public class WOrderComparator implements Comparator {

	/**
	 * 注文形式(成行売り，指値，成行買いの順)，価格(昇順)， 売り買い(売り，買いの順)，板寄せ期間(売り注文は昇順，買い注文は 降順), 板寄せ期間が同じ注文に関してはランダムに辞書式順序で注文を ソートするために利用される．
	 * 
	 * @param o1
	 *            注文1
	 * @param o2
	 *            注文2
	 * @return もしo1 &lt; o2ならば-1, o1 &gt; o2ならば+1
	 */
	public int compare(Object o1, Object o2) {
		WOrder u1 = (WOrder) o1;
		WOrder u2 = (WOrder) o2;
		int u1M = getMarketLimitPriority(u1);
		int u2M = getMarketLimitPriority(u2);
		if (u1M < u2M) {
			return -1;
		} else if (u1M > u2M) {
			return 1;
		}
		if (u1.getOrderPrice() < u2.getOrderPrice()) {
			return -1;
		} else if (u1.getOrderPrice() > u2.getOrderPrice()) {
			return 1;
		}
		if (u1.getSellBuy() < u2.getSellBuy()) {
			return -1;
		} else if (u1.getSellBuy() > u2.getSellBuy()) {
			return 1;
		}
		if (u1.getSellBuy() == WOrder.SELL) {
			if (u1.getSession() < u2.getSession()) {
				return -1;
			} else if (u1.getSession() > u2.getSession()) {
				return 1;
			}
		} else {
			if (u1.getSession() < u2.getSession()) {
				return 1;
			} else if (u1.getSession() > u2.getSession()) {
				return -1;
			}
		}
		if (u1.getRandomNumber() < u2.getRandomNumber()) {
			return -1;
		} else {
			return 1;
		}
	}

	/**
	 * 注文形式による優先順位を返す．
	 * 
	 * @param o
	 *            注文
	 * @return もし「成行売り」ならば0, 「指値」ならば1, 「成行買い」ならば2
	 */
	private final int getMarketLimitPriority(WOrder o) {
		if (o.getMarketLimit() == WOrder.MARKET) {
			if (o.getSellBuy() == WOrder.SELL) {
				return 0;
			} else {
				return 2;
			}
		} else {
			return 1;
		}
	}

}
