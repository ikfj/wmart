package org.wmart.cmdCore;

import java.util.*;

/**
 * UCServerTimeCoreクラスは，サーバ時刻問合せのためのコマンドオブジェクトである．
 * 
 * @author 小野　功
 */
public abstract class WCServerTimeCore implements ICommand {

	/** サーバ時刻を引くためのキー */
	public static final String STRING_SERVER_TIME = "STRING_SERVER_TIME";

	/** コマンド名 */
	public static final String CMD_NAME = "ServerTime";

	/** 別名 */
	public static final String CMD_ALIAS = "106";

	/** コマンドの実行状態 */
	protected WCommandStatus fStatus;

	/** 結果を格納するためのHashMap */
	protected HashMap fServerTimeInfo;

	/**
	 * Constructor for WCServerTimeCore.
	 */
	public WCServerTimeCore() {
		super();
		fStatus = new WCommandStatus();
		fServerTimeInfo = new HashMap();
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
	 * @see org.umart.cmdCore.ICommand#doIt()
	 */
	public abstract WCommandStatus doIt();

	/**
	 * @see org.umart.cmdCore.ICommand#printOn()
	 */
	public void printOn() {
		System.out.println("<<" + CMD_NAME + ">>");
		System.out.println(fServerTimeInfo.toString());
	}

	/**
	 * @see org.umart.cmdCore.ICommand#getResultString()
	 */
	public String getResultString() {
		String result = fServerTimeInfo.get(STRING_SERVER_TIME).toString();
		return result;
	}

	/**
	 * 結果を返す．
	 * 
	 * @return 結果
	 */
	public HashMap getResults() {
		return fServerTimeInfo;
	}

}
