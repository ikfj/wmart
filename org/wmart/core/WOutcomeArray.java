/**
 *
 */
package org.wmart.core;

import java.util.*;

/**
 * 売り注文の約定明細を扱うクラス。スロット番号で指定されるリスト形式の数値データを持つ
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WOutcomeArray {

	/** 値がないときのデフォルト値 */
	private double fDefaultValue;
	/** スロット数 */
	private int fSlots;
	/** スロットごとのリスト */
	private ArrayList<Double> fArray = null;

	/**
	 * コンストラクタ
	 * 
	 * @param slots
	 *            スロット数
	 * @param defaultValue
	 *            デフォルト値
	 */
	public WOutcomeArray(int slots, double defaultValue) {
		fDefaultValue = defaultValue;
		fSlots = slots;
		fArray = new ArrayList<Double>();
		for (int i = 0; i < slots; i++) {
			fArray.add(fDefaultValue);
		}
	}

	/**
	 * 全スロットの値を文字列として取得
	 * 
	 * @return 数値表 "10:11:12..."
	 */
	public String encode() {
		StringBuilder result = new StringBuilder();
		for (Iterator<Double> itr = fArray.iterator(); itr.hasNext();) {
			result.append(itr.next());
			if (itr.hasNext()) {
				result.append(":");
			}
		}
		return result.toString();
	}

	/**
	 * 全スロットの値を CSV で取得
	 * 
	 * @param good
	 * @return
	 */
	public String csv() {
		StringBuilder result = new StringBuilder();
		for (Iterator<Double> itr = fArray.iterator(); itr.hasNext();) {
			result.append(itr.next()).append(",");
		}
		return result.toString();
	}

	/**
	 * 全スロットの合計値を取得
	 */
	public double sum() {
		double sum = 0.0;
		for (Iterator<Double> itr = fArray.iterator(); itr.hasNext();) {
			sum += itr.next();
		}
		return sum;
	}

	/**
	 * 要素に値を加算
	 * 
	 * @param slot
	 * @param value
	 */
	public void add(int slot, double value) {
		assert fDefaultValue == 0.0 : "The default value must be 0.0 to add another value.";
		fArray.set(slot, fArray.get(slot) + value);
	}

	/**
	 * 要素に値を設定
	 * 
	 * @param slot
	 * @param value
	 */
	public void put(int slot, double value) {
		assert (slot > 0 && slot < fArray.size());
		fArray.set(slot, value);
	}

	/**
	 * 要素の値を取得
	 * 
	 * @param slot
	 * @return
	 */
	public double get(int slot) {
		assert (slot > 0 && slot < fArray.size());
		return fArray.get(slot);
	}

	/**
	 * イテレータを取得
	 */
	public Iterator<Double> iterator() {
		return fArray.iterator();
	}

}
