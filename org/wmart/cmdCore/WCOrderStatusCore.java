package org.wmart.cmdCore;

import java.util.*;

/**
 * UCOrderStatusCoreクラスは, 注文照会のためのコマンドオブジェクトである.
 * 
 * @author 川部 祐司
 * @author Ikki Fujiwara, NII
 */
public abstract class WCOrderStatusCore implements ICommand {

	/** 注文IDを引くためのキー(値はLongオブジェクト) */
	public static final String LONG_ORDER_ID = "LONG_ORDER_ID";
	/** 注文時刻(実時間)を引くためのキー(値はStringオブジェクト) */
	public static final String STRING_ORDER_TIME = "STRING_ORDER_TIME";
	/** 銘柄名を引くためのキー(値はStringオブジェクト) */
	public static final String STRING_BRAND_NAME = "STRING_BRAND_NAME";
	/** 市場名を引くためのキー(値はStringオブジェクト) */
	public static final String STRING_AUCTIONEER_NAME = "STRING_AUCTIONEER_NAME";
	/** 売買区分を引くためのキー(値はIntegerオブジェクト) */
	public static final String INT_SELL_BUY = "INT_SELL_BUY";
	/** 成行指値区分を引くためのキー(値はIntegerオブジェクト) */
	public static final String INT_MARKET_LIMIT = "INT_MARKET_LIMIT";
	/** 注文価格を引くためのキー(値はLongオブジェクト) */
	public static final String LONG_ORDER_PRICE = "LONG_ORDER_PRICE";
	/** 注文明細を引くためのキー(値はStringオブジェクト) */
	public static final String STRING_ORDER_SPEC = "STRING_ORDER_SPEC";
	/** 約定価格を引くためのキー(値はDoubleオブジェクト) */
	public static final String DOUBLE_CONTRACT_PRICE = "DOUBLE_CONTRACT_PRICE";
	/** 約定明細を引くためのキー(値はStringオブジェクト) */
	public static final String STRING_CONTRACT_SPEC = "STRING_CONTRACT_SPEC";
	/** コマンド名 */
	public static final String CMD_NAME = "OrderStatus";
	/** 別名 */
	public static final String CMD_ALIAS = "203";
	/** 市場名 */
	protected String fAuctioneerName;
	/** コマンドの実行状態 */
	protected WCommandStatus fStatus;
	/** 結果を格納するためのArrayList */
	protected ArrayList fStatusArray;

	/**
	 * コンストラクタ
	 */
	public WCOrderStatusCore() {
		super();
		fStatusArray = new ArrayList();
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
		return true;
	}

	/**
	 * コマンド実行に必要な引数を設定する．
	 * 
	 * @param auctioneerName
	 */
	public void setArguments(String auctioneerName) {
		fAuctioneerName = auctioneerName;
	}

	/**
	 * @see org.umart.cmdCore.ICommand#getResultString()
	 */
	public String getResultString() {
		String result = "";
		Iterator itr = fStatusArray.iterator();
		while (itr.hasNext()) {
			HashMap os = (HashMap) itr.next();
			result = os.get(LONG_ORDER_ID) + " " + os.get(STRING_ORDER_TIME) + " " + os.get(STRING_BRAND_NAME) + " "
				+ os.get(STRING_AUCTIONEER_NAME) + " " + os.get(INT_SELL_BUY) + " " + os.get(INT_MARKET_LIMIT) + " "
				+ os.get(LONG_ORDER_PRICE) + " " + os.get(STRING_ORDER_SPEC) + " " + os.get(DOUBLE_CONTRACT_PRICE)
				+ " " + os.get(STRING_CONTRACT_SPEC) + " ";
		}
		return result;
	}

	/**
	 * 注文照会情報を返す．
	 * 
	 * @return 注文照会情報
	 */
	public ArrayList getResults() {
		return fStatusArray;
	}

	/**
	 * @see org.umart.cmdCore.ICommand#doIt()
	 */
	public abstract WCommandStatus doIt();

	/**
	 * @see org.umart.cmdCore.ICommand#printOn()
	 */
	public void printOn() {
		System.out.println("<<OrderStatus>>");
		Iterator itr = fStatusArray.iterator();
		while (itr.hasNext()) {
			HashMap os = (HashMap) itr.next();
			System.out.println("OrderID:" + os.get(LONG_ORDER_ID) + "," + "OrderTime:" + os.get(STRING_ORDER_TIME)
				+ "," + "BrandName:" + os.get(STRING_BRAND_NAME) + "," + "AuctioneerName:"
				+ os.get(STRING_AUCTIONEER_NAME) + "," + "SellBuy:" + os.get(INT_SELL_BUY) + "," + "MarketLimit:"
				+ os.get(INT_MARKET_LIMIT) + "," + "OrderPrice:" + os.get(LONG_ORDER_PRICE) + "," + "Spec:"
				+ os.get(STRING_ORDER_SPEC) + "," + "ContractPrice:" + os.get(DOUBLE_CONTRACT_PRICE) + ","
				+ "SoldVolumes:" + os.get(STRING_CONTRACT_SPEC));
		}
	}
}
