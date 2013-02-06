package org.wmart.core;

/**
 * 取引所マスター状態クラス
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WServerStatusMaster extends WServerStatus {

	/** 最初のセッションの開始前 */
	public static final int BEFORE_TRADING = 0;
	/** 注文受付中 (約定と並行) */
	public static final int BUSINESS_HOURS = 1;
	/** 約定待ち */
	public static final int CONCLUDING_AUCTIONS = 2;
	/** 注文受付中 (約定後) */
	public static final int AFTER_HOURS = 3;
	/** 約定準備中 */
	public static final int PREPARING_AUCTIONS = 4;
	/** 最後のセッションの約定待ち */
	public static final int AFTER_TRADING = 7;
	/** 全セッション終了後 */
	public static final int SHUTDOWN = 8;
	/** スーパーユーザーログイン待ち */
	public static final int SU_LOGIN = 9;
	/** 状態を表す文字列の配列 */
	public static final String[] STATES = { "BEFORE_TRADING", "BUSINESS_HOURS",
		"CONCLUDING_AUCTIONS", "AFTER_HOURS", "PREPARING_AUCTIONS", "RESERVED", "RESERVED",
		"AFTER_TRADING", "SHUTDOWN", "SU_LOGIN", };

	/**
	 * コンストラクタ
	 * 
	 */
	public WServerStatusMaster() {
		super();
		fStateStrings = STATES;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param state
	 * @param date
	 * @param session
	 */
	public WServerStatusMaster(int state, int date, int session) {
		super(state, date, session);
		fStateStrings = STATES;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param name
	 * @param state
	 * @param date
	 * @param session
	 */
	public WServerStatusMaster(String name, int state, int date, int session) {
		super(name, state, date, session);
		fStateStrings = STATES;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param name
	 */
	public WServerStatusMaster(String name) {
		super(name);
		fStateStrings = STATES;
	}

	/**
	 * コピーコンストラクタ
	 * 
	 * @param src
	 */
	public WServerStatusMaster(WServerStatus src) {
		super(src);
	}

	/**
	 * 複製を生成
	 * 
	 * @return 複製
	 */
	public Object clone() {
		return new WServerStatusMaster(this);
	}

}
