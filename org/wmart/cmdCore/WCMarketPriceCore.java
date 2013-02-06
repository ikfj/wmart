package org.wmart.cmdCore;

import java.util.*;

/**
 * 市場価格を取得するためのSVMPコマンドの抽象クラスです．
 * 
 * @author 川部 祐司
 * @author Ikki Fujiwara, NII
 */
public abstract class WCMarketPriceCore implements ICommand {

	/** 銘柄名を引くためのキー(値はStringオブジェクト) */
	public static final String STRING_BRAND_NAME = "STRING_BRAND_NAME";

	/** 市場名を引くためのキー(値はStringオブジェクト) */
	public static final String STRING_AUCTIONEER_NAME = "STRING_AUCTIONEER_NAME";

	/** 日付を引くためのキー(値はIntegerオブジェクト) */
	public static final String INT_DAY = "INT_DAY";

	/** 板寄せ回数を引くためのキー(値はIntegerオブジェクト) */
	public static final String INT_BOARD_NO = "INT_BOARD_NO";

	/** ステップ数を引くためのキー(値はIntegerオブジェクト) */
	public static final String INT_STEP = "INT_STEP";

	/** 価格を引くためのキー(値はStringオブジェクト) */
	public static final String STRING_MARKET_PRICE = "STRING_MARKET_PRICE";

	/** コマンド名 */
	public static final String CMD_NAME = "MarketPrice";

	/** 銘柄名 */
	protected String fBrandName;

	/** 市場名 */
	protected String fAuctioneerName;

	/** 必要な情報のステップ数 */
	protected int fNoOfSteps;

	/** コマンドの実行状態 */
	protected WCommandStatus fStatus;

	/** 結果を格納するためのArrayList */
	protected ArrayList fArray;

	/**
	 * コンストラクタ
	 */
	public WCMarketPriceCore() {
		super();
		fBrandName = "";
		fAuctioneerName = "";
		fNoOfSteps = -1;
		fArray = new ArrayList();
		fStatus = new WCommandStatus();
	}

	/**
	 * @see org.umart.cmdCore.ICommand#isNameEqualTo(String)
	 */
	public boolean isNameEqualTo(String name) {
		if (name.equalsIgnoreCase(CMD_NAME)) {
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
			fNoOfSteps = Integer.parseInt(st.nextToken());
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
	 * @param noOfSteps
	 */
	public void setArguments(String brandName, String auctioneerName, int noOfSteps) {
		fBrandName = brandName;
		fAuctioneerName = auctioneerName;
		fNoOfSteps = noOfSteps;
	}

	/**
	 * @see org.umart.cmdCore.ICommand#getResultString()
	 */
	public String getResultString() {
		String result = "";
		Iterator itr = fArray.iterator();
		while (itr.hasNext()) {
			HashMap os = (HashMap) itr.next();
			result += os.get(STRING_BRAND_NAME) + " " + os.get(STRING_AUCTIONEER_NAME) + " " + os.get(INT_DAY) + " "
				+ os.get(INT_BOARD_NO) + " " + os.get(INT_STEP) + " " + os.get(STRING_MARKET_PRICE) + " ";
		}
		return result;
	}

	/**
	 * 市場価格を返す．
	 * 
	 * @return 市場価格
	 */
	public ArrayList getResults() {
		return fArray;
	}

	/**
	 * @see org.umart.cmdCore.ICommand#doIt()
	 */
	public abstract WCommandStatus doIt();

	/**
	 * @see org.umart.cmdCore.ICommand#printOn()
	 */
	public void printOn() {
		System.out.println("<<MarketPrice " + fBrandName + fNoOfSteps + ">>");
		Iterator itr = fArray.iterator();
		while (itr.hasNext()) {
			HashMap os = (HashMap) itr.next();
			System.out.println("BrandName:" + os.get(STRING_BRAND_NAME) + "," + "AuctioneerName:"
				+ os.get(STRING_AUCTIONEER_NAME) + "," + "Day:" + os.get(INT_DAY) + "," + "BoardNo:"
				+ os.get(INT_BOARD_NO) + "," + "Step:" + os.get(INT_STEP) + "," + "MarketPrice:"
				+ os.get(STRING_MARKET_PRICE));
		}
	}
}
