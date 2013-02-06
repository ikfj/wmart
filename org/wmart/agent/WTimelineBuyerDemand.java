package org.wmart.agent;

import java.util.*;

/**
 * タイムライン中の1件の需要を表すクラス
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WTimelineBuyerDemand {

	/** 注文票リスト */
	private ArrayList<WOrderForm> fOrderForms;
	/** 合計注文価格 */
	private int fTotalOrderPrice;

	/**
	 * コンストラクタ
	 */
	public WTimelineBuyerDemand() {
		fOrderForms = new ArrayList<WOrderForm>();
		fTotalOrderPrice = 0;
	}

	/**
	 * 注文を追加
	 * 
	 * @param form
	 *            注文票
	 */
	public void add(WOrderForm form) {
		fOrderForms.add(form);
	}

	/**
	 * 注文をマージ
	 * 
	 * @param forms
	 *            注文票のリスト
	 */
	public void addAll(ArrayList<WOrderForm> forms) {
		fOrderForms.addAll(forms);
	}

	/**
	 * 需要が満たされたか?
	 * 
	 * @return true:すべての注文が約定済 false:ひとつ以上の注文が未約定
	 */
	public boolean isFulfilled() {
		for (Iterator<WOrderForm> itr = fOrderForms.iterator(); itr.hasNext();) {
			WOrderForm form = itr.next();
			if (form.getContractPrice() <= 0.0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 合計注文数量を取得
	 * 
	 * @return 注文数量の合計。商品の種類に関係なく単純合計する
	 */
	public int getTotalOrderVolume() {
		int sum = 0;
		for (Iterator<WOrderForm> itr = fOrderForms.iterator(); itr.hasNext();) {
			WOrderForm form = itr.next();
			String[] sp1 = form.getSpec().split(";");
			for (int i = 0; i < sp1.length; i++) {
				String[] sp2 = sp1[i].split(":");
				int volume = Integer.valueOf(sp2[1]);
				int length = Integer.valueOf(sp2[4]);
				sum += volume * length;
			}
		}
		return sum;
	}

	/**
	 * 合計約定数量を取得
	 * 
	 * @return 約定数量の合計。商品の種類に関係なく単純合計する
	 */
	public int getTotalContractVolume() {
		int sum = 0;
		for (Iterator<WOrderForm> itr = fOrderForms.iterator(); itr.hasNext();) {
			WOrderForm form = itr.next();
			if (form.getContractPrice() > 0.0) {
				String[] sp1 = form.getSpec().split(";");
				for (int i = 0; i < sp1.length; i++) {
					String[] sp2 = sp1[i].split(":");
					int volume = Integer.valueOf(sp2[1]);
					int length = Integer.valueOf(sp2[4]);
					sum += volume * length;
				}
			}
		}
		return sum;

	}

	/**
	 * 合計約定価格を取得
	 * 
	 * @return 約定価格の合計。未約定の注文は無視する
	 */
	public double getTotalContractPrice() {
		double sum = 0.0;
		for (Iterator<WOrderForm> itr = fOrderForms.iterator(); itr.hasNext();) {
			WOrderForm form = itr.next();
			if (form.getContractPrice() > 0.0) {
				sum += form.getContractPrice();
			}
		}
		return sum;
	}

	/**
	 * 合計約定利得を取得
	 * 
	 * @return 約定利得の合計。未約定の注文は無視する
	 */
	public double getTotalContractProfit() {
		double sum = 0.0;
		for (Iterator<WOrderForm> itr = fOrderForms.iterator(); itr.hasNext();) {
			WOrderForm form = itr.next();
			if (form.getContractPrice() > 0.0) {
				sum += (double) form.getOrderPrice() - form.getContractPrice();
			}
		}
		return sum;
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

	/**
	 * 合計注文価格を取得します。
	 * 
	 * @return 合計注文価格
	 */
	public int getTotalOrderPrice() {
		return fTotalOrderPrice;
	}

	/**
	 * 合計注文価格を設定します。
	 * 
	 * @param totalOrderPrice
	 *            合計注文価格
	 */
	public void setTotalOrderPrice(int totalOrderPrice) {
		this.fTotalOrderPrice = totalOrderPrice;
	}

}
