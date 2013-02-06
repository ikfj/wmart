package org.wmart.analyzer;

import java.util.*;

/**
 * 売り手のタイムラインを表すクラス (ビジュアライザ用、配列ベース)
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WTimelineSellerArray {

	/** 名前 */
	private String fName;
	/** 値リスト */
	private ArrayList<Double> fValues;

	/**
	 * コンストラクタ
	 */
	public WTimelineSellerArray(String username) {
		fName = username;
		fValues = new ArrayList<Double>();
	}

	/**
	 * 文字列から上書き読み込み
	 * 
	 * @param encodedTimeline
	 *            "商品名:始端時刻,資源量(0),...,資源量(slots-1),"
	 */
	public void overread(String encodedTimeline) {
		String[] sp1 = encodedTimeline.split(",");
		String[] sp2 = sp1[0].split(":");
		fName = sp2[0];
		int offset = Integer.parseInt(sp2[1]);
		for (int i = 0; i + 1 < sp1.length; i++) {
			setValue(offset + i - 1, Double.valueOf(sp1[i + 1]));
		}
	}

	private void setValue(int index, double value) {
		assert index >= 0;
		while (fValues.size() <= index) {
			fValues.add(0.0);
		}
		fValues.set(index, value);
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
	 * 値リストを取得します。
	 * 
	 * @return 値リスト
	 */
	public ArrayList<Double> getValues() {
		return fValues;
	}

	/**
	 * 値のイテレータを取得します。
	 * 
	 * @return 値のイテレータ
	 */
	public Iterator<Double> iterator() {
		return fValues.iterator();
	}

	/**
	 * 動作テスト用
	 */
	public static void main(String argv[]) {
		WTimelineSellerArray tl = new WTimelineSellerArray("test");
		tl.overread("A:3,1.0,2.0,3.0,");
		System.out.println(tl.getValues().toString());
	}
}
