package org.wmart.logger;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * エージェントごと・セッションごとの資源情報のログを扱うクラス
 * 
 * @author Ikki Fujiwara, NII
 */
public class WResourceLog {

	/** 日付 */
	private int fDate;
	/** 節 */
	private int fSession;
	/** ログイン名 */
	private String fLoginName;
	/** 売り注文情報 */
	private LinkedHashMap<String, String> fSelling;
	/** 買い注文情報 */
	private LinkedHashMap<String, String> fBuying;
	/** 買い約定情報 */
	private LinkedHashMap<String, String> fBought;
	/** 買い取消情報 */
	private LinkedHashMap<String, String> fCancelBuying;

	/**
	 * コンストラクタ
	 * 
	 * @param date
	 *            日付
	 * @param session
	 *            節
	 * @param loginName
	 *            ログイン名
	 */
	public WResourceLog(int date, int session, String loginName) {
		fDate = date;
		fSession = session;
		fLoginName = loginName;
		fSelling = new LinkedHashMap<String, String>();
		fBuying = new LinkedHashMap<String, String>();
		fBought = new LinkedHashMap<String, String>();
		fCancelBuying = new LinkedHashMap<String, String>();
	}

	/**
	 * 売り注文情報を追加する
	 * 
	 * @param orderIds
	 *            注文IDを表す文字列
	 * @param spec
	 *            "商品名:始端時刻,資源量(0),...,資源量(slots-1),"
	 */
	public void addSelling(String orderIds, String spec) {
		fSelling.put(orderIds, spec);
	}

	/**
	 * 買い注文情報を追加する
	 * 
	 * @param orderIds
	 *            注文IDを表す文字列
	 * @param spec
	 *            "商品名:希望数量:最早時刻:最遅時刻:延べ時間;...;"
	 */
	public void addBuying(String orderIds, String spec) {
		fBuying.put(orderIds, spec);
	}

	/**
	 * 買い約定情報を追加する
	 * 
	 * @param orderIds
	 *            注文IDを表す文字列
	 * @param spec
	 *            "商品名:希望数量:最早時刻:最遅時刻:延べ時間;...;"
	 */
	public void addBought(String orderIds, String spec) {
		fBought.put(orderIds, spec);
	}

	/**
	 * 買い取消情報を追加する
	 * 
	 * @param orderIds
	 *            注文IDを表す文字列
	 * @param spec
	 *            "商品名:希望数量:最早時刻:最遅時刻:延べ時間;...;"
	 */
	public void addCancelBuying(String orderIds, String spec) {
		fCancelBuying.put(orderIds, spec);
	}

	/**
	 * 出力ストリームへ書き出す
	 * <ul>
	 * <li>売り注文の出力形式: "日/節 ログイン名 selling #注文ID 商品名:始端時刻,資源量(0),...,資源量(slots-1),\n"</li>
	 * <li>買い注文の出力形式: "日/節 ログイン名 buying #注文ID 商品名:希望数量:最早時刻:最遅時刻:延べ時間;...;\n"</li>
	 * <li>買い約定の出力形式: "日/節 ログイン名 bought #注文ID 商品名:希望数量:最早時刻:最遅時刻:延べ時間;...;\n"</li>
	 * <li>買い取消の出力形式: "日/節 ログイン名 cancelbuying #注文ID 商品名:希望数量:最早時刻:最遅時刻:延べ時間;...;\n"</li>
	 * </ul>
	 * 
	 * @param pw
	 *            出力ストリーム
	 * @throws IOException
	 */
	public void writeTo(PrintWriter pw) throws IOException {
		for (Iterator<Entry<String, String>> itr = fCancelBuying.entrySet().iterator(); itr
			.hasNext();) {
			Entry<String, String> entry = itr.next();
			pw.println(String.format("%d/%d %s cancelbuying %s %s", fDate, fSession, fLoginName,
				entry.getKey(), entry.getValue()));
		}
		for (Iterator<Entry<String, String>> itr = fBought.entrySet().iterator(); itr.hasNext();) {
			Entry<String, String> entry = itr.next();
			pw.println(String.format("%d/%d %s bought %s %s", fDate, fSession, fLoginName,
				entry.getKey(), entry.getValue()));
		}
		for (Iterator<Entry<String, String>> itr = fBuying.entrySet().iterator(); itr.hasNext();) {
			Entry<String, String> entry = itr.next();
			pw.println(String.format("%d/%d %s buying %s %s", fDate, fSession, fLoginName,
				entry.getKey(), entry.getValue()));
		}
		for (Iterator<Entry<String, String>> itr = fSelling.entrySet().iterator(); itr.hasNext();) {
			Entry<String, String> entry = itr.next();
			// pw.println(legend(entry.getValue()));
			pw.println(String.format("%d/%d %s selling %s %s", fDate, fSession, fLoginName,
				entry.getKey(), entry.getValue()));
		}
	}

	/**
	 * 入力ストリームから読み込む
	 * 
	 * @param br
	 *            BufferedReader 入力ストリーム
	 * @throws IOException
	 */
	public void readFrom(BufferedReader br) throws IOException {
		assert fSelling.isEmpty() : "fSelling is not empty";
		assert fBuying.isEmpty() : "fBuying is not empty";
		assert fBought.isEmpty() : "fBought is not empty";
		assert fCancelBuying.isEmpty() : "fCancelBuying is not empty";
		String line = null;
		while ((line = br.readLine().trim()) != null) {
			if (!line.startsWith("#")) {
				String[] sp1 = line.split(" ");
				String[] sp2 = sp1[0].split("/");
				fDate = Integer.parseInt(sp2[0]);
				fSession = Integer.parseInt(sp2[1]);
				fLoginName = sp1[1];
				String sellBuy = sp1[2];
				String orderId = sp1[3];
				String spec = sp1[4];
				if (sellBuy.compareToIgnoreCase("selling") == 0) {
					addSelling(orderId, spec);
				}
				if (sellBuy.compareToIgnoreCase("buying") == 0) {
					addBuying(orderId, spec);
				}
				if (sellBuy.compareToIgnoreCase("bought") == 0) {
					addBought(orderId, spec);
				}
			}
		}
	}

	/**
	 * 売り資源量の時刻目盛りを生成
	 * 
	 * @param spec
	 *            "商品名:始端時刻,資源量(0),...,資源量(slots-1),"
	 * @return "始端時刻,始端時刻+1,...,始端時刻+slots"
	 */
	private String legend(String spec) {
		String leg = "#,";
		String[] sp1 = spec.split(",");
		String[] sp2 = sp1[0].split(":");
		int offset = Integer.valueOf(sp2[1]);
		for (int i = 1; i < sp1.length; i++) {
			leg += offset + ",";
			offset++;
		}
		return leg;
	}
}
