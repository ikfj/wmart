package org.wmart.agent;

import java.io.*;
import java.util.*;

import org.wmart.cmdCore.*;

/**
 * マシンエージェントのルートクラスです． 全てのマシンエージェントはUBaseAgentを継承して，
 * doActionsメソッドとsetParametersメソッドをオーバーライドする必要があります．
 * サーバとの通信には，getUmcpメソッドでCProtocolCoreオブジェクトを取得した後， CProtocolCoreオブジェクトからSVMPコマンドオブジェクトを取得して
 * 適切な引数を設定した後に実行します．
 * 
 * @author 川部 祐司
 * @author 小野　功
 */
public class WBaseAgent {
	
	/** ログイン名 */
	protected String fLoginName;
	
	/** パスワード */
	protected String fPasswd;
	
	/** 実際の名前（Humanの場合：氏名，Machineの場合：クラス名） */
	protected String fRealName;
	
	/** ユーザID */
	protected int fUserID;
	
	/** 乱数の種 */
	protected int fSeed;
	
	/** 乱数 */
	protected Random fRandom;
	
	/** プロトコル・ライブラリ */
	protected WProtocolCore fUmcp;
	
	/** システムパラメータ */
	protected String[] fSystemParameters;
	
	/** 出力ストリーム */
	protected OutputStream fOutputStream = null;
	
	/** fOutputStreamから生成されるPrintWriterオブジェクト */
	protected PrintWriter fPrintWriter;
	
	/**
	 * UBaseAgentオブジェクトの生成および初期化を行う．
	 * 
	 * @param loginName
	 *            ログイン名
	 * @param passwd
	 *            パスワード
	 * @param realName
	 *            実際の名前（Humanの場合：氏名，Machineの場合：クラス名）
	 * @param seed
	 *            乱数の種
	 */
	public WBaseAgent(String loginName, String passwd, String realName, int seed) {
		fLoginName = loginName;
		fPasswd = passwd;
		fRealName = realName;
		fSeed = seed;
		fRandom = new Random(seed);
		fUmcp = null;
		fUserID = -1;
		fSystemParameters = new String[0];
		setOutputStream(System.out); // デフォルトでは，標準出力を利用する．
	}
	
	/**
	 * エージェントのシステムパラメータを設定する． このメソッドをオーバーライドする場合，必ず，最初にsuper.setParametersを呼ぶこと．
	 * 
	 * @param args
	 *            システムパラメータ
	 */
	public void setParameters(String[] args) {
		fSystemParameters = new String[args.length];
		message("**** Parameters given by CSV file ****");
		for (int i = 0; i < args.length; ++i) {
			fSystemParameters[i] = args[i];
			message(args[i]);
		}
		message("****************************************");
	}
	
	/**
	 * メッセージを出力する．
	 * 
	 * @param msg
	 *            String メッセージ
	 */
	public void message(String msg) {
		if (fPrintWriter != null) {
			fPrintWriter.println(msg);
			fPrintWriter.flush();
		}
	}
	
	/**
	 * メッセージを改行なしで出力する．
	 * 
	 * @param msg
	 *            メッセージ
	 */
	public void print(String msg) {
		if (fPrintWriter != null) {
			fPrintWriter.print(msg);
			fPrintWriter.flush();
		}
	}
	
	/**
	 * メッセージを改行ありで出力する．
	 * 
	 * @param msg
	 *            メッセージ
	 */
	public void println(String msg) {
		if (fPrintWriter != null) {
			fPrintWriter.println(msg);
			fPrintWriter.flush();
		}
	}
	
	/**
	 * 例外を出力する．
	 * 
	 * @param ex
	 *            Exception 例外
	 */
	public void message(Exception ex) {
		if (fPrintWriter != null) {
			ex.printStackTrace(fPrintWriter);
		}
	}
	
	/**
	 * 出力ストリームを設定する
	 * 
	 * @param os
	 *            出力ストリーム
	 */
	public void setOutputStream(OutputStream os) {
		fOutputStream = os;
		if (fOutputStream == null) {
			fPrintWriter = null;
		} else {
			fPrintWriter = new PrintWriter(os, true);
		}
	}
	
	/**
	 * 出力ストリームを返す
	 * 
	 * @return 出力ストリーム
	 */
	public OutputStream getOutputStream() {
		return fOutputStream;
	}
	
	/**
	 * エージェントのシステムパラメータを返す．
	 * 
	 * @return システムパラメータ
	 */
	public String[] getParameters() {
		return fSystemParameters;
	}
	
	/**
	 * CProtocolオブジェクトを設定する．
	 * 
	 * @param umcp
	 *            CProtocolオブジェクト
	 */
	public void setCProtocol(WProtocolCore umcp) {
		fUmcp = umcp;
	}
	
	/**
	 * 注文依頼，キャンセルなどの動作を行う．
	 * 
	 * @param date
	 *            日付
	 * @param session
	 *            板寄せ回数
	 * @param serverState
	 *            サーバー状態
	 * @param maxDate
	 *            運用日数
	 * @param sessionsPerDay
	 *            一日当たりの板寄せ回数
	 * @param slotsForward
	 *            先物取引対象スロット数
	 * @param maxLength
	 *            ワークフローの最大長さ
	 */
	public void doActions(int date, int session, int serverState, int maxDate, int sessionsPerDay,
		int slotsForward, int maxLength) {
	}
	
	/**
	 * プロトコル・ライブラリを返す.
	 * 
	 * @return プロトコル・ライブラリ．
	 */
	public WProtocolCore getUmcp() {
		return fUmcp;
	}
	
	/**
	 * パスワードを返す.
	 * 
	 * @return パスワード
	 */
	public String getPasswd() {
		return fPasswd;
	}
	
	/**
	 * パスワードを設定する.
	 * 
	 * @param passwd
	 *            パスワード
	 */
	public void setPasswd(String passwd) {
		fPasswd = passwd;
	}
	
	/**
	 * ユーザIDを返す.
	 * 
	 * @return ユーザID．
	 */
	public int getUserID() {
		return fUserID;
	}
	
	/**
	 * ユーザーIDを設定する.
	 * 
	 * @param userID
	 *            ユーザーID
	 */
	public void setUserID(int userID) {
		fUserID = userID;
	}
	
	/**
	 * Returns the seed.
	 * 
	 * @return int
	 */
	public int getSeed() {
		return fSeed;
	}
	
	/**
	 * Sets the seed.
	 * 
	 * @param seed
	 *            The seed to set
	 */
	public void setSeed(int seed) {
		fSeed = seed;
		fRandom = new Random(seed);
	}
	
	/**
	 * ログイン名を返す．
	 * 
	 * @return ログイン名
	 */
	public String getLoginName() {
		return fLoginName;
	}
	
	/**
	 * 実際の名前（Humanの場合：氏名，Machineの場合：クラス名）を返す
	 * 
	 * @return 実際の名前
	 */
	public String getRealName() {
		return fRealName;
	}
	
	/**
	 * ログイン名を設定する
	 * 
	 * @param string
	 *            ログイン名
	 */
	public void setLoginName(String string) {
		fLoginName = string;
	}
	
	/**
	 * 実際の名前（Humanの場合：氏名，Machineの場合：クラス名）を設定する
	 * 
	 * @param string
	 *            実際に名前
	 */
	public void setRealName(String string) {
		fRealName = string;
	}
	
	/**
	 * 乱数オブジェクトを返す．
	 * 
	 * @return 乱数オブジェクト
	 */
	public Random getRandom() {
		return fRandom;
	}
	
}
