/**
 *
 */
package org.wmart.core;

import java.io.*;
import java.text.ParseException;
import java.util.*;

import org.umart.serverCore.UMemberList;
import org.wmart.agent.WBaseAgent;
import org.wmart.analyzer.WMartScenarioVisualizer;

/**
 * メインクラス
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WMartSimulator {

	/** 取引所マスター */
	private WMart fMaster;

	/**
	 * メインメソッド
	 * 
	 * @param args
	 *            コマンドライン引数
	 */
	public static void main(String args[]) {
		if (args.length < 4) {
			System.err
				.println("usage: java WMartSimulator seed maxDays daysForward membersFile [parameter]");
			System.err.println();
			System.err.println("parameter: Colon-separated key-value pairs, e.g.");
			System.err.println("    demandfile=(path)");
			System.err.println("    logdir=(path)");
			System.exit(1);
		}
		int seed = Integer.parseInt(args[0]);
		int maxDays = Integer.parseInt(args[1]);
		int daysForward = Integer.parseInt(args[2]);
		String membersFile = args[3];
		String parameter = (args.length > 4) ? args[4] : null;
		try {
			WMartSimulator sim = new WMartSimulator(seed, maxDays, daysForward, membersFile,
				parameter);
			sim.doLoop();
			System.out.println("Finished in " + sim.fMaster.getLogDir());
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(5);
		}
	}

	/**
	 * コンストラクタ
	 * 
	 * @param seed
	 *            乱数の種
	 * @param maxDays
	 *            運営日数。最初の (2+daysForward) 日はウォームアップ期間として捨てることになる
	 * @param daysForward
	 *            先物取引の対象日数
	 * @param membersFile
	 *            メンバー設定ファイル
	 * @param parameter
	 *            システムパラメータ
	 * @throws ParseException
	 * @throws IOException
	 */
	public WMartSimulator(int seed, int maxDays, int daysForward, String membersFile,
		String parameter) throws ParseException, IOException {
		int sessionsPerDay = 24; // 1日の分割数 = 現物取引の1日の板寄せ回数 = 先物取引の1日のスロット数
		int slotsForward = sessionsPerDay * daysForward; // 先物取引の対象スロット数
		int maxLength = slotsForward / 2; // ワークフローの最大長さ
		if (maxLength > sessionsPerDay) {
			maxLength = sessionsPerDay;
		}
		UMemberList memberInfo = new UMemberList();
		readMemberLog(memberInfo, membersFile);
		fMaster = new WMart(memberInfo, seed, maxDays, sessionsPerDay, slotsForward, maxLength);
		fMaster.initLog(parameter, true); // true:簡易ログ false:詳細ログ
		initAgents(fMaster, memberInfo, parameter);
	}

	/**
	 * 会員リスト情報に従い，エージェントを生成し，取引所に登録する．
	 * 
	 * @param master
	 *            取引所
	 * @param members
	 *            会員リスト
	 * @param extraParameter
	 *            システムパラメータ
	 */
	private void initAgents(WMart master, UMemberList members, String extraParameter) {
		Iterator iter = members.getMembers();
		while (iter.hasNext()) {
			HashMap memberInfo = (HashMap) iter.next();
			String loginName = (String) memberInfo.get(UMemberList.STRING_LOGIN_NAME);
			String passwd = (String) memberInfo.get(UMemberList.STRING_PASSWORD);
			String attribute = (String) memberInfo.get(UMemberList.STRING_ATTRIBUTE);
			String connection = (String) memberInfo.get(UMemberList.STRING_CONNECTION);
			String realName = (String) memberInfo.get(UMemberList.STRING_REAL_NAME);
			String parameter = UMemberList.arrayListToString((ArrayList) memberInfo
				.get(UMemberList.ARRAY_LIST_SYSTEM_PARAMETERS));
			parameter += ":" + extraParameter; // 引数から来たシステムパラメータを載せる
			if (!parameter.matches("logdir")) {
				parameter += ":logdir=" + fMaster.getLogDir().replace(":", "?");
			}
			int seed = ((Integer) memberInfo.get(UMemberList.INT_SEED)).intValue();
			if (attribute.equals("Machine") && connection.equals("Local")) {
				try {
					WBaseAgent agent = WAgentFactory.makeAgent(loginName, passwd, realName,
						parameter, seed, null);
					master.appendStrategy(agent);
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
				fMaster.doActionsByLocalAgents();
				break;
			}
		}
		// visualize(fMaster.getLogDir());
	}

	/**
	 * 資源ログを可視化する
	 * 
	 */
	public void visualize(String logdir) {
		String logfilename = String.format(("%s/%s.csv"), logdir, WMart.RESOURCE_LOG_DIR); // 全員一緒のファイル
		String imgfilename = logfilename.replaceAll("\\.\\w+$", ".png");
		WMartScenarioVisualizer vis = new WMartScenarioVisualizer();
		try {
			vis.visualize(logfilename, imgfilename, 1000, 600);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(5);
		}
	}
}
