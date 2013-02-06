package org.umart.strategy_v2;

/**
 * 注文票クラス
 * @author isao
 *
 */
public class UOrderForm {
	
	/** 何も注文しないことを表す定数 */
	public static final int NONE = 0;
	
	/** 売注文を表す定数 */
	public static final int SELL = 1;
	
	/** 買注文を表す定数 */
	public static final int BUY = 2;
	
	/** 注文区分(何も注文しない:UOrderForm.NONE(=0), 売注文:UOrderForm.SELL(=1), 買注文:UOrderForm.BUY(=2)) */
	private int fBuySell;
	
	/** 注文価格が正しく初期化されていないことを表す定数 */
	public static final int INVALID_PRICE = -1;
	
	/** 注文価格 */
	private int fPrice;
	
	/** 注文数量が正しく初期化されていないことを表す定数 */
	public static final int INVALID_QUANTITY = -1;

	/** 注文数量 */
	private int fQuantity;
	
	/**
	 * コンストラクタ
	 *
	 */
	public UOrderForm() {
		fBuySell = UOrderForm.NONE;
		fPrice = UOrderForm.INVALID_PRICE;
		fQuantity = UOrderForm.INVALID_QUANTITY;
	}
	
	/**
	 * コピーコンストラクタ
	 * @param src コピー元
	 */
	public UOrderForm(UOrderForm src) {
		fBuySell = src.fBuySell;
		fPrice = src.fPrice;
		fQuantity = src.fQuantity;
	}
	
	/**
	 * コピーする．
	 * @param src コピー元
	 * @return コピー後の自分自身
	 */
	public UOrderForm copyFrom(UOrderForm src) {
		fBuySell = src.fBuySell;
		fPrice = src.fPrice;
		fQuantity = src.fQuantity;
		return this;
	}

	/**
	 * 注文区分を返す．
	 * @return buySell 注文しない場合：(UOrderForm.NONE=0)，売りの場合：(UOrderForm.SELL=1)，買いの場合：(UOrderForm.BUY=2)
	 */
	public int getBuySell() {
		return fBuySell;
	}
	
	/**
	 * 注文区分を設定する．
	 * @param buySell 注文区分(何も注文しない:UOrderForm.NONE(=0), 売注文:UOrderForm.SELL(=1), 買注文:UOrderForm.BUY(=2))
	 */
	public void setBuySell(int buySell) {
		fBuySell = buySell;
	}

	/**
	 * 注文価格を返す．
	 * @return price 注文価格
	 */
	public int getPrice() {
		return fPrice;
	}

	/**
	 * 注文価格を設定する．
	 * @param price 注文価格
	 */
	public void setPrice(int price) {
		fPrice = price;
	}

	/**
	 * 注文数量を返す．
	 * @return quantity 注文数量
	 */
	public int getQuantity() {
		return fQuantity;
	}

	/**
	 * 注文数量を設定する．
	 * @param quantity 注文数量
	 */
	public void setQuantity(int quantity) {
		fQuantity = quantity;
	}
	
	/**
	 * 注文区分を文字列で返す．
	 * @return 注文区分を表す文字列
	 */
	public String getBuySellByString() {
		if (fBuySell == UOrderForm.BUY) {
			return "Buy";
		} else if (fBuySell == UOrderForm.SELL) {
			return "Sell";
		} else {
			return "None";
		}
	}
	
}
