package org.wmart.cmdCore;

import java.util.*;

/**
 * 現金残高照会のためのSVMPコマンドの抽象クラスです．
 * 
 * @author 川部 祐司
 */
public abstract class WCBalancesCore implements ICommand {

	/** 現金残高を引くためのキー(値はLongオブジェクト) */
	public static final String LONG_CASH = "LONG_CASH";

	/** 証拠金額を引くためのキー(値はLongオブジェクト) */
	public static final String LONG_MARGIN = "LONG_MARGIN";

	/** 未実現損益を引くためのキー(値はLongオブジェクト) */
	public static final String LONG_UNREALIZED_PROFIT = "LONG_UNREALIZED_PROFIT";

	/** 実現利益を引くためのキー(値はLongオブジェクト) */
	public static final String LONG_SETTLED_PROFIT = "LONG_SETTLED_PROFIT";

	/** 会費を引くためのキー(値はLongオブジェクト) */
	public static final String LONG_FEE = "LONG_FEE";

	/** 融資利息を引くためのキー(値はLongオブジェクト) */
	public static final String LONG_INTEREST = "LONG_INTEREST";

	/** 取引所融資額を引くためのキー(値はLongオブジェクト) */
	public static final String LONG_LOAN = "LONG_LOAN";

	/** 余裕金額を引くためのキー(値はLongオブジェクト) */
	public static final String LONG_SURPLUS = "LONG_SURPLUS";

	/** 昨日までの売り建玉数を引くためのキー(値はLongオブジェクト) */
	public static final String LONG_SELL_POSITION = "LONG_SELL_POSITION";

	/** 昨日までの買い建玉数を引くためのキー(値はLongオブジェクト) */
	public static final String LONG_BUY_POSITION = "LONG_BUY_POSITION";

	/** コマンド名 */
	public static final String CMD_NAME = "Balances";

	/** 別名 */
	public static final String CMD_ALIAS = "205";

	/** コマンドの実行状態 */
	protected WCommandStatus fStatus;

	/** 結果(当日の資産情報)を格納するためのHashMap */
	protected HashMap fTodayInfo;

	/** 結果(前日の資産情報)を格納するためのHashMap */
	protected HashMap fYesterdayInfo;

	/**
	 * コンストラクタ
	 */
	public WCBalancesCore() {
		super();
		fTodayInfo = new HashMap();
		fYesterdayInfo = new HashMap();
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
	 * @see org.umart.cmdCore.ICommand#getResultString()
	 */
	public String getResultString() {
		String result = fTodayInfo.get(LONG_CASH) + " " + fTodayInfo.get(LONG_MARGIN) + " "
			+ fTodayInfo.get(LONG_UNREALIZED_PROFIT) + " " + fTodayInfo.get(LONG_SETTLED_PROFIT) + " "
			+ fTodayInfo.get(LONG_FEE) + " " + fTodayInfo.get(LONG_INTEREST) + " " + fTodayInfo.get(LONG_LOAN) + " "
			+ fTodayInfo.get(LONG_SURPLUS) + " " + fTodayInfo.get(LONG_SELL_POSITION) + " "
			+ fTodayInfo.get(LONG_BUY_POSITION) + " " + fYesterdayInfo.get(LONG_CASH) + " "
			+ fYesterdayInfo.get(LONG_MARGIN) + " " + fYesterdayInfo.get(LONG_UNREALIZED_PROFIT) + " "
			+ fYesterdayInfo.get(LONG_SETTLED_PROFIT) + " " + fYesterdayInfo.get(LONG_FEE) + " "
			+ fYesterdayInfo.get(LONG_INTEREST) + " " + fYesterdayInfo.get(LONG_LOAN) + " "
			+ fYesterdayInfo.get(LONG_SURPLUS) + " " + fYesterdayInfo.get(LONG_SELL_POSITION) + " "
			+ fYesterdayInfo.get(LONG_BUY_POSITION);
		return result;
	}

	/**
	 * 当日の資産情報を返す．
	 * 
	 * @return 当日の資産情報
	 */
	public HashMap getTodayResults() {
		return fTodayInfo;
	}

	/**
	 * 前日の資産情報を返す．
	 * 
	 * @return 前日の資産情報
	 */
	public HashMap getYesterdayResults() {
		return fYesterdayInfo;
	}

	/**
	 * @see org.umart.cmdCore.ICommand#doIt()
	 */
	public abstract WCommandStatus doIt();

	/**
	 * @see org.umart.cmdCore.ICommand#printOn()
	 */
	public void printOn() {
		System.out.println("<<Balances>>");
		System.out.println("<Today>");
		System.out.println(fTodayInfo.toString());
		System.out.println("<Yesterday>");
		System.out.println(fYesterdayInfo.toString());
	}
}
