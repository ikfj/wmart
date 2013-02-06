package org.wmart.agent;

/**
 * 注文票クラス
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WOrderForm {

	/** 何も注文しないことを表す定数 */
	public static final int NONE = 0;
	/** 売い注文を表す定数 */
	public static final int SELL = 1;
	/** 買い注文を表す定数 */
	public static final int BUY = 2;
	/** 価格が初期化されていないことを表す定数 */
	public static final int INVALID_PRICE = -1;
	/** 未発注であることを表す定数 */
	public static final long INVALID_ORDER_ID = -1;

	/** 注文ID */
	private long fOrderId;
	/** 市場名 */
	private String fAuctioneerName;
	/** 注文区分 */
	private int fBuySell;
	/** 注文明細 */
	private String fSpec;
	/** 注文価格 */
	private int fOrderPrice;
	/** 約定価格 */
	private double fContractPrice;

	/**
	 * コンストラクタ
	 * 
	 */
	public WOrderForm() {
		fOrderId = INVALID_ORDER_ID;
		fAuctioneerName = "";
		fBuySell = NONE;
		fSpec = "";
		fOrderPrice = INVALID_PRICE;
		fContractPrice = INVALID_PRICE;
	}

	/**
	 * コピーコンストラクタ
	 * 
	 * @param src
	 *            コピー元
	 */
	public WOrderForm(WOrderForm src) {
		fOrderId = src.fOrderId;
		fAuctioneerName = src.fAuctioneerName;
		fBuySell = src.fBuySell;
		fSpec = src.fSpec;
		fOrderPrice = src.fOrderPrice;
		fContractPrice = src.fContractPrice;
	}

	/**
	 * コピーする．
	 * 
	 * @param src
	 *            コピー元
	 * @return コピー後の自分自身
	 */
	public WOrderForm copyFrom(WOrderForm src) {
		fOrderId = src.fOrderId;
		fAuctioneerName = src.fAuctioneerName;
		fBuySell = src.fBuySell;
		fSpec = src.fSpec;
		fOrderPrice = src.fOrderPrice;
		fContractPrice = src.fContractPrice;
		return this;
	}

	/**
	 * 注文区分を文字列で返す．
	 * 
	 * @return 注文区分を表す文字列
	 */
	public String getBuySellByString() {
		if (fBuySell == WOrderForm.BUY) {
			return "Buy";
		} else if (fBuySell == WOrderForm.SELL) {
			return "Sell";
		} else {
			return "None";
		}
	}

	/**
	 * 注文区分を返す．
	 * 
	 * @return buySell
	 *         注文しない場合：(WOrderForm.NONE=0)，売りの場合：(WOrderForm.SELL=1)，買いの場合：(WOrderForm.BUY=2)
	 */
	public int getBuySell() {
		return fBuySell;
	}

	/**
	 * 注文区分を設定する．
	 * 
	 * @param buySell
	 *            注文区分(何も注文しない:WOrderForm.NONE(=0), 売注文:WOrderForm.SELL(=1), 買注文:WOrderForm.BUY(=2))
	 */
	public void setBuySell(int buySell) {
		fBuySell = buySell;
	}

	/**
	 * 注文IDを取得します。
	 * 
	 * @return 注文ID
	 */
	public long getOrderId() {
		return fOrderId;
	}

	/**
	 * 注文IDを設定します。
	 * 
	 * @param fOrderId
	 *            注文ID
	 */
	public void setOrderId(long fOrderId) {
		this.fOrderId = fOrderId;
	}

	/**
	 * 注文IDを削除する
	 */
	public void removeOrderId() {
		this.fOrderId = INVALID_ORDER_ID;
	}

	/**
	 * 市場名を取得する
	 * 
	 * @return the auctioneerName
	 */
	public String getAuctioneerName() {
		return fAuctioneerName;
	}

	/**
	 * 市場名を設定する
	 * 
	 * @param auctioneerName
	 *            the auctioneerName to set
	 */
	public void setAuctioneerName(String auctioneerName) {
		fAuctioneerName = auctioneerName;
	}

	/**
	 * 注文明細を取得する
	 * 
	 * @return spec ("商品名:希望数量:最早時刻:最遅時刻:延べ時間;" の繰り返し)
	 */
	public String getSpec() {
		return fSpec;
	}

	/**
	 * 注文明細を設定する
	 * 
	 * @param spec
	 *            注文明細 ("商品名:希望数量:最早時刻:最遅時刻:延べ時間;" の繰り返し)
	 */
	public void setSpec(String spec) {
		fSpec = spec;
	}

	/**
	 * 注文価格を返す．
	 * 
	 * @return price 注文価格
	 */
	public int getOrderPrice() {
		return fOrderPrice;
	}

	/**
	 * 注文価格を設定する．
	 * 
	 * @param price
	 *            注文価格
	 */
	public void setOrderPrice(int price) {
		fOrderPrice = price;
	}

	/**
	 * 注文価格を削除する
	 */
	public void removeOrderPrice() {
		fOrderPrice = INVALID_PRICE;
	}

	/**
	 * 約定価格を取得します。
	 * 
	 * @return 約定価格
	 */
	public double getContractPrice() {
		return fContractPrice;
	}

	/**
	 * 約定価格を設定します。
	 * 
	 * @param fTotalContractPrice
	 *            約定価格
	 */
	public void setContractPrice(double contractPrice) {
		fContractPrice = contractPrice;
	}

	/**
	 * 約定価格を削除する
	 */
	public void removeContractPrice() {
		fContractPrice = INVALID_PRICE;
	}

	@Override
	public String toString() {
		return String.format("[#%d %s %s %s $%d $%.2f]", fOrderId, fAuctioneerName,
			getBuySellByString(), fSpec, fOrderPrice, fContractPrice);
	}

}
