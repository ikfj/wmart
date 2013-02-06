package org.wmart.core;

import org.apache.log4j.*;

/**
 * 取引所の状態を表す抽象クラス
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public abstract class WServerStatus {

	/** ロガー */
	private static Logger log = Logger.getLogger(WServerStatus.class);
	/** 識別名 */
	protected String fName = "";
	/** 状態を表す文字列の配列 */
	protected String[] fStateStrings = {};
	/** 状態 */
	protected int fState = 0;
	/** 現在の日 */
	protected int fDate = 0;
	/** 現在の節 */
	protected int fSession = 0;

	/**
	 * コンストラクタ
	 */
	public WServerStatus() {
		this("", 0, 0, 0);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param state
	 */
	public WServerStatus(String name) {
		this(name, 0, 0, 0);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param state
	 * @param date
	 * @param session
	 */
	public WServerStatus(int state, int date, int session) {
		this("", state, date, session);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param name
	 * @param state
	 * @param date
	 * @param session
	 */
	public WServerStatus(String name, int state, int date, int session) {
		fName = name;
		fState = state;
		fDate = date;
		fSession = session;
		fStateStrings = null;
	}

	/**
	 * コピーコンストラクタ
	 */
	public WServerStatus(WServerStatus src) {
		fState = src.fState;
		fName = src.fName;
		fStateStrings = src.fStateStrings;
	}

	/**
	 * 状態を文字列で返す
	 */
	public String getStateString() {
		return fStateStrings[fState];
	}

	/**
	 * 状態を取得する
	 * 
	 * @return the state
	 */
	public int getState() {
		return fState;
	}

	/**
	 * 状態を設定する
	 * 
	 * @param state
	 *            the state to set
	 */
	public void setState(int state) {
		fState = state;
		log.debug(toString());
	}

	/**
	 * 現在の日付(U-Mart暦)を返す．
	 * 
	 * @return 現在の日付(U-Mart暦)
	 */
	public int getDate() {
		return fDate;
	}

	/**
	 * 現在の日付(U-Mart暦)を設定する．
	 * 
	 * @param date
	 *            現在の日付(U-Mart暦)
	 */
	public void setDate(int date) {
		fDate = date;
	}

	/**
	 * 日付を1日進める．
	 * 
	 * @return 1日進めた後の日付
	 */
	public int incrementDate() {
		++fDate;
		return fDate;
	}

	/**
	 * 現在の節を返す．
	 * 
	 * @return 現在の節
	 */
	public int getSession() {
		return fSession;
	}

	/**
	 * 現在の節を設定する．
	 * 
	 * @param session
	 *            節
	 */
	public void setSession(int session) {
		fSession = session;
	}

	/**
	 * 節を1進める．
	 * 
	 * @return 1進めた後の節
	 */
	public int incrementSession() {
		++fSession;
		return fSession;
	}

	/**
	 * 節を1に戻す．
	 * 
	 * @return 板寄せ回数
	 */
	public int resetSession() {
		fSession = 1;
		return fSession;
	}

	/**
	 * 名前を取得する
	 * 
	 * @return the name
	 */
	public String getName() {
		return fName;
	}

	/**
	 * 名前を設定する
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		fName = name;
	}

	/**
	 * 状態を標準出力に表示する
	 */
	public void printOn() {
		System.out.println("    " + toString());
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "date" + fDate + "/session" + fSession + " " + getName() + " status: " + getStateString();
	}

}
