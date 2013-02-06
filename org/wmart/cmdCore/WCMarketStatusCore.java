package org.wmart.cmdCore;

import java.util.*;

/**
 * 取引所ワーカー状態問い合わせのためのコマンドクラス。
 * <ul>
 * <li>UCMarketStatusCore クラスに市場名フィールドを追加した。</li>
 * <ul>
 * <li>String auctioneerName</li>
 * </ul>
 * </ul>
 * 
 * @author Ikki Fujiwara, NII
 */
public abstract class WCMarketStatusCore implements ICommand {

	/** マーケット状態を引くためのキー(値はIntegerオブジェクト) */
	public static final String INT_MARKET_STATUS = "INT_MARKET_STATUS";

	/** コマンド名 */
	public static final String CMD_NAME = "MarketStatus";

	/** 別名 */
	public static final String CMD_ALIAS = "104";

	/** 市場名 */
	protected String fAuctioneerName;

	/** コマンドの実行状態 */
	protected WCommandStatus fStatus;

	/** 結果を格納するためのHashMap */
	protected HashMap fMarketInfo;

	/**
	 * コンストラクタ
	 */
	public WCMarketStatusCore() {
		super();
		fAuctioneerName = "";
		fMarketInfo = new HashMap();
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
		String result = "" + fMarketInfo.get(INT_MARKET_STATUS);
		return result;
	}

	/**
	 * HashMapを返す．
	 * 
	 * @return HashMap
	 */
	public HashMap getMarketInfo() {
		return fMarketInfo;
	}

	/**
	 * @see org.umart.cmdCore.ICommand#doIt()
	 */
	public abstract WCommandStatus doIt();

	/**
	 * @see org.umart.cmdCore.ICommand#printOn()
	 */
	public void printOn() {
		System.out.println("<<MarketStatus " + fAuctioneerName + ">>");
		System.out.println("MarketStatus:" + fMarketInfo.get(INT_MARKET_STATUS));
	}
}
