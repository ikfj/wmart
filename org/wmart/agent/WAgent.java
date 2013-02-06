package org.wmart.agent;

import java.io.*;
import java.util.*;

import org.wmart.cmdCore.*;
import org.wmart.core.*;
import org.wmart.logger.*;

/**
 * エージェントの親クラス
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WAgent extends WBaseAgent {

	/** ログ出力先ディレクトリ名 */
	protected String fLogDir;

	public WAgent(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

	/**
	 * @see org.umart.strategyCore.UBaseAgent#doActions()
	 */
	@Override
	public void doActions(int day, int session, int serverState, int maxDate, int sessionsPerDay,
		int slotsForward, int maxLength) {
		// マシンエージェントは AFTER_HOURS に活動する
		if (serverState != WServerStatusMaster.AFTER_HOURS) {
			return;
		}
	}

	/**
	 * 発注する
	 * 
	 * @param form
	 *            注文票
	 * @return 注文ID -1:注文票が不正 -2:発注結果がエラー
	 */
	protected long orderRequest(WOrderForm form) {
		WCOrderRequestCore cmd = (WCOrderRequestCore) fUmcp.getCommand(WCOrderRequestCore.CMD_NAME);
		int sellBuy;
		if (form.getBuySell() == WOrderForm.BUY) {
			sellBuy = WOrder.BUY;
		} else if (form.getBuySell() == WOrderForm.SELL) {
			sellBuy = WOrder.SELL;
		} else {
			return -1;
		}
		if (form.getOrderPrice() <= 0) {
			return -1;
		}
		cmd.setArguments("", form.getAuctioneerName(), sellBuy, WOrder.LIMIT, form.getOrderPrice(),
			form.getSpec());
		WCommandStatus status = cmd.doIt();
		if (status.getStatus() == false) {
			System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID);
			System.err.println(status.getErrorMessage() + " in WAgent.orderRequest");
			return -2;
		}
		HashMap<String, String> results = cmd.getResults();
		return Long.valueOf(results.get(WCOrderRequestCore.LONG_ORDER_ID));
	}

	/**
	 * 注文状態を取得する
	 */
	protected ArrayList<HashMap<String, String>> getOrderStatus(String auctioneerName) {
		WCOrderStatusCore cmd = (WCOrderStatusCore) fUmcp.getCommand(WCOrderStatusCore.CMD_NAME);
		cmd.setArguments(auctioneerName);
		WCommandStatus status = cmd.doIt();
		if (status.getStatus() == false) {
			System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID);
			System.err.println(status.getErrorMessage() + " in WAgent.getOrderStatus");
			System.exit(5);
		}
		return cmd.getResults();
	}

	/**
	 * 資源情報をファイルに書き出す
	 */
	protected void writeResourceLog(WResourceLog rlog) {
		// String filepath = String.format(("%s/%s/%s.csv"), fLogDir, WMart.RESOURCE_LOG_DIR,
		// fLoginName); // 個別のファイル
		String filepath = String.format(("%s/%s.csv"), fLogDir, WMart.RESOURCE_LOG_DIR); // 全員一緒のファイル
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(filepath, true)); // 追記
			rlog.writeTo(pw);
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * エージェントのシステムパラメータを設定する．
	 * 
	 * @param args
	 *            システムパラメータ
	 */
	@Override
	public void setParameters(String[] args) {
		super.setParameters(args);
		for (int i = 0; i < args.length; ++i) {
			StringTokenizer st = new StringTokenizer(args[i], "= ");
			String key = st.nextToken();
			String value = st.nextToken();
			if (key.equalsIgnoreCase("logdir")) {
				fLogDir = value.replace("?", ":");
				println("LogDir set to " + fLogDir);
			}
		}
	}
}
