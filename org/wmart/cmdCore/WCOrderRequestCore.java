package org.wmart.cmdCore;

import java.util.*;

/**
 * 注文依頼のためのコマンドクラス。
 * <ul>
 * <li>UCOrderRequestCoreクラスに市場名フィールドと注文明細フィールドを追加した。</li>
 * <ul>
 * <li>int newRepay → String auctioneerName</li>
 * <li>int volume → String spec</li>
 * </ul>
 * <li>String brandName は廃止していない (空欄で使用)。</li> </ul>
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public abstract class WCOrderRequestCore implements ICommand {

	/** 注文IDを引くためのキー(値はLongオブジェクト) */
	public static final String LONG_ORDER_ID = "LONG_ORDER_ID";
	/** 注文時刻(実時間)を引くためのキー(値はStringオブジェクト) */
	public static final String STRING_ORDER_TIME = "STRING_ORDER_TIME";
	/** コマンド名 */
	public static final String CMD_NAME = "OrderRequest";
	/** 別名 */
	public static final String CMD_ALIAS = "201";
	/** 銘柄名 */
	protected String fBrandName;
	/** 市場名 */
	protected String fAuctioneerName;
	/** 売買区分 */
	protected int fSellBuy;
	/** 成行指値区分 */
	protected int fMarketLimit;
	/** 希望取引価格 */
	protected long fPrice;
	/** 注文明細 */
	protected String fSpec;
	/** コマンドの実行状態 */
	protected WCommandStatus fStatus;
	/** 結果を格納するためのHashMap */
	protected HashMap fRequestInfo;

	/**
	 * コンストラクタ
	 */
	public WCOrderRequestCore() {
		super();
		fAuctioneerName = "";
		fBrandName = "";
		fSellBuy = -1;
		fMarketLimit = -1;
		fPrice = -1;
		fSpec = "";
		fRequestInfo = new HashMap();
		fStatus = new WCommandStatus();
	}

	/**
	 * @see org.umart.cmdCore.ICommand#isNameEqualTo(String)
	 */
	public boolean isNameEqualTo(String name) {
		if (name.equalsIgnoreCase(CMD_NAME) || name.equals(CMD_ALIAS)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @see org.umart.cmdCore.ICommand#getName()
	 */
	public String getName() {
		return CMD_NAME;
	}

	/**
	 * @see org.umart.cmdCore.ICommand#setArguments(StringTokenizer)
	 */
	public boolean setArguments(StringTokenizer st) {
		try {
			fBrandName = st.nextToken();
			fAuctioneerName = st.nextToken();
			fSellBuy = Integer.parseInt(st.nextToken());
			fMarketLimit = Integer.parseInt(st.nextToken());
			fPrice = Long.parseLong(st.nextToken());
			fSpec = st.nextToken();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * コマンド実行に必要な引数を設定する．
	 * 
	 * @param brandName
	 * @param auctioneerName
	 * @param sellBuy
	 * @param marketLimit
	 * @param price
	 * @param spec
	 */
	public void setArguments(String brandName, String auctioneerName, int sellBuy, int marketLimit, long price,
		String spec) {
		fBrandName = brandName;
		fAuctioneerName = auctioneerName;
		fSellBuy = sellBuy;
		fMarketLimit = marketLimit;
		fPrice = price;
		fSpec = spec;
	}

	/**
	 * @see org.umart.cmdCore.ICommand#getResultString()
	 */
	public String getResultString() {
		String result = fRequestInfo.get(LONG_ORDER_ID) + " " + fRequestInfo.get(STRING_ORDER_TIME);
		return result;
	}

	/**
	 * 注文依頼情報を返す．
	 * 
	 * @return 注文依頼情報
	 */
	public HashMap getResults() {
		return fRequestInfo;
	}

	/**
	 * @see org.umart.cmdCore.ICommand#doIt()
	 */
	public abstract WCommandStatus doIt();

	/**
	 * @see org.umart.cmdCore.ICommand#printOn()
	 */
	public void printOn() {
		System.out.println("<<OrderRequest " + fBrandName + fAuctioneerName + fSellBuy + fMarketLimit + fPrice + fSpec
			+ ">>");
		System.out.println("OrderID:" + fRequestInfo.get(LONG_ORDER_ID) + "," + "OrderTime:"
			+ fRequestInfo.get(STRING_ORDER_TIME));
	}
}