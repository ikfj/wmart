package org.wmart.core;

/**
 * 取引所ワーカー状態クラス
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WServerStatusWorker extends WServerStatus {

	/** 初期状態 */
	public static final int INITIALIZED = 0;
	/** 注文受付中 (約定前) */
	public static final int BEFORE_CONCLUSION = 1;
	/** 約定待ち */
	public static final int CONCLUDING = 2;
	/** 注文受付中 (約定後) */
	public static final int AFTER_CONCLUSION = 3;
	/** 約定準備中 */
	public static final int PREPARING = 4;
	/** 状態を表す文字列の配列 */
	public static final String[] STATES = { "INITIALIZED", "BEFORE_CONCLUSION", "CONCLUDING", "AFTER_CONCLUSION",
		"PREPARING", };

	/**
	 * コンストラクタ
	 * 
	 */
	public WServerStatusWorker() {
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
	public WServerStatusWorker(int state, int date, int session) {
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
	public WServerStatusWorker(String name, int state, int date, int session) {
		super(name, state, date, session);
		fStateStrings = STATES;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param name
	 */
	public WServerStatusWorker(String name) {
		super(name);
		fStateStrings = STATES;
	}

	/**
	 * コピーコンストラクタ
	 * 
	 * @param src
	 */
	public WServerStatusWorker(WServerStatus src) {
		super(src);
	}

	/**
	 * 複製を生成
	 * 
	 * @return 複製
	 */
	public Object clone() {
		return new WServerStatusWorker(this);
	}
}
