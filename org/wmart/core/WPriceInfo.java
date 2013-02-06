package org.wmart.core;

import java.util.*;

/**
 * 価格系列データベースに含まれる1ステップの価格情報
 *
 * @author Ikki Fujiwara, NII
 *
 */
public class WPriceInfo {

	/** 無効な価格 */
	public static final long INVALID_PRICE = -1;

	/** 日 */
	private int fDate = 0;
	/** 節 */
	private int fSession = 0;
	/** 始端のセッション番号 */
	private int fOffset = 0;
	/** スロット数 */
	private int fSlots = 0;
	/** 商品ごと・スロットごとの市場価格 (平均約定単価) 表 */
	private WOutcomeTable fMarketPriceTable = null;
	/** 商品ごと・スロットごとの合計約定価格表 */
	private WOutcomeTable fTotalPriceTable = null;
	/** 商品ごと・スロットごとの合計約定数量表 */
	private WOutcomeTable fTotalVolumeTable = null;

	/**
	 * コンストラクタ
	 */
	public WPriceInfo(int slots) {
		fSlots = slots;
	}

	/**
	 * 全商品の全スロットの市場価格表を文字列で取得する
	 *
	 * @return 価格表 "goodA:offset,10,11,12,...;goodB:offset,21,22,23,...;"
	 */
	public String encode() {
		if (fMarketPriceTable == null) {
			computeMarketPriceTable();
		}
		return fMarketPriceTable.encode();
	}

	/**
	 * 指定商品の全スロットの市場価格リストを CSV で取得する
	 *
	 * @return 価格リスト "10,11,12..."
	 */
	public String csv(String good) {
		if (fMarketPriceTable == null) {
			computeMarketPriceTable();
		}
		return fMarketPriceTable.csv(good);
	}

	/**
	 * 指定商品のあるスロットの市場価格を取得する
	 *
	 * @param good
	 * @return
	 */
	public double getPrice(String good, int slot) {
		if (fMarketPriceTable == null) {
			computeMarketPriceTable();
		}
		return fMarketPriceTable.get(good, slot);
	}

	/**
	 * 市場価格表を計算する
	 *
	 */
	private void computeMarketPriceTable() {
		assert (fTotalPriceTable != null && fTotalVolumeTable != null) : "TotalPriceTable and TotalVolumeTable are not set!";
		fMarketPriceTable = new WOutcomeTable(fSlots, fOffset, WPriceInfo.INVALID_PRICE);
		for (Iterator<String> itr = fTotalPriceTable.goodsIterator(); itr.hasNext();) {
			String good = itr.next();
			for (int slot = fOffset; slot < fOffset + fSlots; slot++) {
				double value = fTotalPriceTable.get(good, slot) / fTotalVolumeTable.get(good, slot);
				fMarketPriceTable.put(good, slot, value);
			}
		}
	}

	/**
	 * 商品名リストを取得する
	 *
	 * @return 商品名を羅列した ArrayList
	 */
	public ArrayList<String> getGoods() {
		ArrayList<String> goods = new ArrayList<String>();
		if (fTotalPriceTable != null) {
			for (Iterator<String> itr = fTotalPriceTable.goodsIterator(); itr.hasNext();) {
				goods.add(itr.next());
			}
		}
		return goods;
	}

	/**
	 * 商品ごと・スロットごとの合計約定価格表を設定します。
	 *
	 * @param fTotalPriceTable
	 *            商品ごと・スロットごとの合計約定価格表
	 */
	public void setTotalPriceTable(WOutcomeTable totalPriceTable, int offset) {
		if (fOffset <= 0) {
			fOffset = offset;
		}
		assert (fOffset == offset) : "offset differs!";
		fTotalPriceTable = totalPriceTable;
	}

	/**
	 * 商品ごと・スロットごとの合計約定数量表を設定します。
	 *
	 * @param fTotalVolumeTable
	 *            商品ごと・スロットごとの合計約定数量表
	 */
	public void setTotalVolumeTable(WOutcomeTable totalVolumeTable, int offset) {
		if (fOffset <= 0) {
			fOffset = offset;
		}
		assert (fOffset == offset) : "offset differs!";
		fTotalVolumeTable = totalVolumeTable;
	}

	/**
	 * 節を返す．
	 *
	 * @return 節
	 */
	public int getSession() {
		return fSession;
	}

	/**
	 * 日を返す．
	 *
	 * @return 日
	 */
	public int getDate() {
		return fDate;
	}

	/**
	 * 節を設定する．
	 *
	 * @param session
	 *            節
	 */
	public void setSession(int session) {
		fSession = session;
	}

	/**
	 * 日を設定する．
	 *
	 * @param date
	 *            日
	 */
	public void setDate(int date) {
		fDate = date;
	}

	/**
	 * 始端のセッション番号を取得します。
	 *
	 * @return 始端のセッション番号
	 */
	public int getOffset() {
		return fOffset;
	}

}
