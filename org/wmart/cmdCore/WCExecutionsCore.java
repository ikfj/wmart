package org.wmart.cmdCore;

import java.util.*;

/**
 * 約定照会のためのSVMPコマンドの抽象クラスです．
 * 
 * @author 川部 祐司
 * @author Ikki Fujiwara, NII
 */
public abstract class WCExecutionsCore implements ICommand {

	/** 約定IDを引くためのキー(値はLongオブジェクト) */
	public static final String LONG_CONTRACT_ID = "LONG_CONTRACT_ID";
	/** 約定時刻(実時間)を引くためのキー(値はStringオブジェクト) */
	public static final String STRING_CONTRACT_TIME = "STRING_CONTRACT_TIME";
	/** 注文IDを引くためのキー(値はLongオブジェクト) */
	public static final String LONG_ORDER_ID = "LONG_ORDER_ID";
	/** 銘柄IDを引くためのキー(値はStringオブジェクト) */
	public static final String STRING_BRAND_NAME = "STRING_BRAND_NAME";
	/** 市場名を引くためのキー(値はStringオブジェクト) */
	public static final String STRING_AUCTIONEER_NAME = "STRING_AUCTIONEER_NAME";
	/** 売買区分を引くためのキー(値はIntegerオブジェクト) */
	public static final String INT_SELL_BUY = "INT_SELL_BUY";
	/** 注文価格を引くためのキー(値はLongオブジェクト) */
	public static final String LONG_ORDER_PRICE = "LONG_ORDER_PRICE";
	/** 注文明細を引くためのキー(値はStringオブジェクト) */
	public static final String STRING_ORDER_SPEC = "STRING_ORDER_SPEC";
	/** 約定価格を引くためのキー(値はDoubleオブジェクト) */
	public static final String DOUBLE_CONTRACT_PRICE = "DOUBLE_CONTRACT_PRICE";
	/** 約定数量を引くためのキー(値はDoubleオブジェクト) */
	public static final String DOUBLE_CONTRACT_VOLUME = "DOUBLE_CONTRACT_VOLUME";
	/** 売り約定価格リストを引くためのキー(値はStringオブジェクト) */
	public static final String STRING_SOLD_PRICES = "STRING_SOLD_PRICES";
	/** 売り約定数量リストを引くためのキー(値はStringオブジェクト) */
	public static final String STRING_SOLD_VOLUMES = "STRING_CONTRACT_SPEC";
	/** コマンド名 */
	public static final String CMD_NAME = "Executions";
	/** 別名 */
	public static final String CMD_ALIAS = "204";
	/** 市場名 */
	protected String fAuctioneerName;
	/** コマンドの実行状態 */
	protected WCommandStatus fStatus;
	/** 結果を格納するためのArrayList */
	protected ArrayList fExecutionsArray;

	/**
	 * コンストラクタ
	 */
	public WCExecutionsCore() {
		super();
		fExecutionsArray = new ArrayList();
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
			fAuctioneerName = st.nextToken();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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
		Iterator itr = fExecutionsArray.iterator();
		while (itr.hasNext()) {
			HashMap os = (HashMap) itr.next();
			result += os.get(LONG_CONTRACT_ID) + " " + os.get(STRING_CONTRACT_TIME) + " " + os.get(LONG_ORDER_ID) + " "
				+ os.get(STRING_BRAND_NAME) + " " + os.get(STRING_AUCTIONEER_NAME) + " " + os.get(INT_SELL_BUY) + " "
				+ os.get(DOUBLE_CONTRACT_PRICE) + " " + os.get(DOUBLE_CONTRACT_VOLUME) + " "
				+ os.get(STRING_SOLD_PRICES) + " " + os.get(STRING_SOLD_VOLUMES);
		}
		return result;
	}

	/**
	 * 約定情報を返す．
	 * 
	 * @return 約定情報
	 */
	public ArrayList getResults() {
		return fExecutionsArray;
	}

	/**
	 * @see org.umart.cmdCore.ICommand#doIt()
	 */
	public abstract WCommandStatus doIt();

	/**
	 * @see org.umart.cmdCore.ICommand#printOn()
	 */
	public void printOn() {
		System.out.println("<<Executions>>");
		Iterator itr = fExecutionsArray.iterator();
		while (itr.hasNext()) {
			HashMap os = (HashMap) itr.next();
			System.out.println("ContractID:" + os.get(LONG_CONTRACT_ID) + "," + "ContractTime:"
				+ os.get(STRING_CONTRACT_TIME) + "," + "OrderID:" + os.get(LONG_ORDER_ID) + "," + "BrandName:"
				+ os.get(STRING_BRAND_NAME) + "," + "AuctioneerName:" + os.get(STRING_AUCTIONEER_NAME) + ","
				+ "SellBuy:" + os.get(INT_SELL_BUY) + "," + "ContractPrice:" + os.get(DOUBLE_CONTRACT_PRICE) + ","
				+ "ContractSpec:" + os.get(DOUBLE_CONTRACT_VOLUME) + "," + "SoldPrices:" + os.get(STRING_SOLD_PRICES)
				+ "," + "SoldVolumes:" + os.get(STRING_SOLD_VOLUMES));
		}
	}
}
