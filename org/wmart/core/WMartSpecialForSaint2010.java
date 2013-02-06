/**
 *
 */
package org.wmart.core;

import java.io.*;
import java.text.*;
import java.util.*;

import org.umart.serverCore.*;
import org.wmart.agent.*;

/**
 * スタンドアローン実験用メインクラス
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WMartSpecialForSaint2010 {
	
	/** 実験に必要なファイルが置いてあるディレクトリ名 */
	private static final String RESOURCE_DIRECTORY = "resources";
	
	/** エージェント設定ファイル名 */
	private static final String MEMBERS_FILE = "MembersSA.csv";
	
	/** 実験用のワーキングディレクトリ */
	private String fBaseDirectory = "";
	
	/** 取引所マスター */
	private WMart fMaster;
	
	/**
	 * コンストラクタ
	 * 
	 * @param baseDir
	 *            ログを作成するディレクトリ
	 * @param isSimpleLog
	 *            簡易ログにするか？
	 * @param randomSeed
	 *            乱数の種
	 * @param maxDate
	 *            運営日数。最初の (2+daysForward) 日はウォームアップ期間として捨てることになる
	 * @param daysForward
	 *            先物取引の対象日数
	 * @throws ParseException
	 * @throws IOException
	 */
	public WMartSpecialForSaint2010(String baseDir, boolean isSimpleLog, int randomSeed, int maxDate,
		int daysForward) throws ParseException, IOException {
		int sessionsPerDay = 4; // 1日の分割数 = 現物取引の1日の板寄せ回数 = 先物取引の1日のスロット数
		int sessionsForward = sessionsPerDay * daysForward;
		int maxLength = sessionsForward / 2; // ワークフローの最大長さ
		if (maxLength > 24) {
			maxLength = 24;
		}
		fBaseDirectory = baseDir;
		String resDir = fBaseDirectory + File.separator + WMartSpecialForSaint2010.RESOURCE_DIRECTORY;
		UMemberList memberInfo = new UMemberList();
		readMemberLog(memberInfo, resDir + File.separator + WMartSpecialForSaint2010.MEMBERS_FILE);
		fMaster = new WMart(memberInfo, randomSeed, maxDate, sessionsPerDay, sessionsForward,
			maxLength);
		initAgents(fMaster, memberInfo);
		fMaster.initLog(fBaseDirectory, isSimpleLog);
	}
	
	/**
	 * 会員リスト情報に従い，エージェントを生成し，取引所に登録する．
	 * 
	 * @param master
	 *            取引所
	 * @param members
	 *            会員リスト
	 */
	private void initAgents(WMart master, UMemberList members) {
		Iterator iter = members.getMembers();
		while (iter.hasNext()) {
			HashMap memberInfo = (HashMap) iter.next();
			String loginName = (String) memberInfo.get(UMemberList.STRING_LOGIN_NAME);
			String passwd = (String) memberInfo.get(UMemberList.STRING_PASSWORD);
			String attribute = (String) memberInfo.get(UMemberList.STRING_ATTRIBUTE);
			String connection = (String) memberInfo.get(UMemberList.STRING_CONNECTION);
			String realName = (String) memberInfo.get(UMemberList.STRING_REAL_NAME);
			String paramString = UMemberList.arrayListToString((ArrayList) memberInfo
				.get(UMemberList.ARRAY_LIST_SYSTEM_PARAMETERS));
			int seed = ((Integer) memberInfo.get(UMemberList.INT_SEED)).intValue();
			if (attribute.equals("Machine") && connection.equals("Local")) {
				try {
					WBaseAgent strategy = WAgentFactory.makeAgent(loginName, passwd, realName,
						paramString, seed, null);
					master.appendStrategy(strategy);
				} catch (IllegalArgumentException iae) {
					System.err.println("Cannot initialize " + loginName);
					System.exit(5);
				}
			}
		}
	}
	
	/**
	 * 会員リストをファイルから読み込む．
	 * 
	 * @param memberInfo
	 *            会員リスト
	 * @param filename
	 *            ファイル名
	 * @throws ParseException
	 * @throws IOException
	 */
	private void readMemberLog(UMemberList memberInfo, String filename) throws ParseException,
		IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		memberInfo.readFrom(br);
		br.close();
	}
	
	/**
	 * メインループ
	 * 
	 */
	public void doLoop() {
		while (true) {
			WServerStatus status = fMaster.nextStatus();
			if (status.getState() == WServerStatusMaster.AFTER_HOURS) {
				fMaster.doActionsByLocalAgents();
			} else if (status.getState() == WServerStatusMaster.SHUTDOWN) {
				break;
			}
		}
	}
	
	/**
	 * WMartのCUI用のメインメソッド。
	 * 
	 * @param args
	 *            コマンドライン引数
	 */
	public static void main(String args[]) {
		if (args.length != 5) {
			System.err
				.println("usage: java WMartSimulator simple/detail experimentDirectoryName randomSeed maxDate daysForward");
			System.exit(1);
		}
		String logFlagString = args[0];
		boolean isSimpleLog = false;
		if (logFlagString.equals("simple")) {
			isSimpleLog = true;
		} else if (logFlagString.equals("detail")) {
			isSimpleLog = false;
		} else {
			System.err
				.println("usage: java WMartSimulator simple/detail experimentDirectoryName randomSeed maxDate daysForward");
			System.exit(1);
		}
		String baseDir = args[1];
		int randomSeed = Integer.parseInt(args[2]);
		int maxDate = Integer.parseInt(args[3]);
		int daysForward = Integer.parseInt(args[4]);
		try {
			WMartSpecialForSaint2010 cui = new WMartSpecialForSaint2010(baseDir, isSimpleLog, randomSeed,
				maxDate, daysForward);
			cui.doLoop();
			System.out.println("Finished in " + baseDir);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(5);
		}
	}
}
