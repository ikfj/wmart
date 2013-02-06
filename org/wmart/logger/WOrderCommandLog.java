package org.wmart.logger;

import java.io.*;
import java.util.*;

import org.wmart.core.*;

/**
 * １節分の全ての注文情報のログを取り扱うクラスです．
 * 
 * @author 小野　功
 * @author Ikki Fujiwara, NII
 */
public class WOrderCommandLog {

	/** 日を引くキー */
	public static final String INT_DATE = "INT_DATE";

	/** 節を引くキー */
	public static final String INT_SESSION = "INT_SESSION";

	/** 注文時刻(実時間)を引くキー */
	public static final String STRING_REAL_TIME = "STRING_REAL_TIME";

	/** ログイン名を引くためのキー */
	public static final String STRING_LOGIN_NAME = "STRING_LOGIN_NAME";

	/** コマンド名を引くキー */
	public static final String STRING_COMMAND_NAME = "STRING_COMMAND_NAME";

	/** 注文IDを引くキー */
	public static final String LONG_ORDER_ID = "LONG_ORDER_ID";

	/** 銘柄名を引くキー (不使用) */
	public static final String STRING_BRAND_NAME = "STRING_BRAND_NAME";

	/** 市場名を引くキー */
	public static final String STRING_AUCTIONEER_NAME = "STRING_AUCTIONEER_NAME";

	/** 売買区分を引くキー */
	public static final String INT_SELL_BUY = "INT_SELL_BUY";

	/** 成行指値区分を引くキー */
	public static final String INT_MARKET_LIMIT = "INT_MARKET_LIMIT";

	/** 注文価格を引くキー */
	public static final String LONG_ORDER_PRICE = "LONG_ORDER_PRICE";

	/** 注文明細を引くキー */
	public static final String STRING_ORDER_SPEC = "STRING_ORDER_SPEC";

	/** 同一板寄せ期間内の注文をソートするために利用される乱数を引くためのキー */
	public static final String INT_RANDOM_NUMBER = "INT_RANDOM_NUMBER";

	/** 注文情報の配列 */
	private ArrayList fOrderCommandArray;

	/**
	 * コンストラクタ
	 * 
	 */
	public WOrderCommandLog() {
		fOrderCommandArray = new ArrayList();
	}

	/**
	 * 注文情報を登録する．
	 * 
	 * @param o
	 *            注文
	 */
	public synchronized void registerOrderRequest(WOrder o) {
		HashMap hash = new HashMap();
		hash.put(WOrderCommandLog.INT_DATE, new Integer(o.getDate()));
		hash.put(WOrderCommandLog.INT_SESSION, new Integer(o.getSession()));
		hash.put(WOrderCommandLog.STRING_REAL_TIME, o.getTime());
		hash.put(WOrderCommandLog.STRING_LOGIN_NAME, o.getUserName());
		hash.put(WOrderCommandLog.STRING_COMMAND_NAME, "Request");
		hash.put(WOrderCommandLog.LONG_ORDER_ID, new Long(o.getOrderID()));
		hash.put(WOrderCommandLog.STRING_BRAND_NAME, o.getBrandName());
		hash.put(WOrderCommandLog.STRING_AUCTIONEER_NAME, o.getAuctioneerName());
		hash.put(WOrderCommandLog.INT_SELL_BUY, new Integer(o.getSellBuy()));
		hash.put(WOrderCommandLog.INT_MARKET_LIMIT, new Integer(o.getMarketLimit()));
		hash.put(WOrderCommandLog.LONG_ORDER_PRICE, new Double(o.getOrderPrice()));
		hash.put(WOrderCommandLog.STRING_ORDER_SPEC, o.getOrderSpec());
		hash.put(WOrderCommandLog.INT_RANDOM_NUMBER, new Integer(o.getRandomNumber()));
		fOrderCommandArray.add(hash);
	}

	/**
	 * キャンセル情報を登録する
	 * 
	 * @param date
	 *            日
	 * @param session
	 *            節
	 * @param userName
	 *            ユーザ名
	 * @param orderID
	 *            注文ID
	 * @param cancelVolume
	 *            キャンセル数量
	 */
	public synchronized void registerOrderCancel(int date, int session, String userName, long orderID, long cancelVolume) {
		HashMap hash = new HashMap();
		hash.put(WOrderCommandLog.INT_DATE, new Integer(date));
		hash.put(WOrderCommandLog.INT_SESSION, new Integer(session));
		hash.put(WOrderCommandLog.STRING_REAL_TIME, new Date());
		hash.put(WOrderCommandLog.STRING_LOGIN_NAME, userName);
		hash.put(WOrderCommandLog.STRING_COMMAND_NAME, "Cancel");
		hash.put(WOrderCommandLog.LONG_ORDER_ID, new Long(orderID));
		// hash.put(WOrderCommandLog.LONG_ORDER_VOLUME, new Long(cancelVolume)); //
		// TODO:キャンセルは全量に限る (09/9/7)
		fOrderCommandArray.add(hash);
	}

	/**
	 * 注文情報のイテレータを返す．
	 * 
	 * @return 注文情報のイテレータ
	 */
	public Iterator getOrderCommands() {
		return fOrderCommandArray.iterator();
	}

	/**
	 * 全ての注文情報をクリアする．
	 * 
	 */
	public void clear() {
		fOrderCommandArray.clear();
	}

	/**
	 * 出力ストリームへデータを書き出す．
	 * 
	 * @param pw
	 *            出力ストリーム
	 * @throws IOException
	 */
	public synchronized void writeTo(PrintWriter pw) throws IOException {
		pw.println("Date,Session,RealTime,LoginName,Command,OrderID,BrandName,NewRepay,"
			+ "SellBuy,MarketLimit,Price,Spec,RandomNumber");
		Iterator itr = fOrderCommandArray.iterator();
		while (itr.hasNext()) {
			HashMap hash = (HashMap) itr.next();
			pw.print(hash.get(WOrderCommandLog.INT_DATE).toString() + ",");
			pw.print(hash.get(WOrderCommandLog.INT_SESSION).toString() + ",");
			pw.print(hash.get(WOrderCommandLog.STRING_REAL_TIME) + ",");
			pw.print(hash.get(WOrderCommandLog.STRING_LOGIN_NAME).toString() + ",");
			String cmdName = (String) hash.get(WOrderCommandLog.STRING_COMMAND_NAME);
			pw.print(cmdName + ",");
			pw.print(hash.get(WOrderCommandLog.LONG_ORDER_ID).toString() + ",");
			if (cmdName.equals("Request")) {
				pw.print(hash.get(WOrderCommandLog.STRING_BRAND_NAME).toString() + ",");
				pw.print(hash.get(WOrderCommandLog.STRING_AUCTIONEER_NAME).toString() + ",");
				pw.print(hash.get(WOrderCommandLog.INT_SELL_BUY).toString() + ",");
				pw.print(hash.get(WOrderCommandLog.INT_MARKET_LIMIT).toString() + ",");
				pw.print(hash.get(WOrderCommandLog.LONG_ORDER_PRICE).toString() + ",");
				pw.print(hash.get(WOrderCommandLog.STRING_ORDER_SPEC).toString() + ",");
				pw.println(hash.get(WOrderCommandLog.INT_RANDOM_NUMBER).toString());
			} else {
				pw.print(",,,,,");
				// pw.println("-" + hash.get(WOrderCommandLog.LONG_ORDER_VOLUME).toString() + ",");
				pw.println(","); // TODO: 暫定
			}
		}
	}

	/**
	 * 入力ストリームからデータを読み込む．
	 * 
	 * @param br
	 *            入力ストリーム
	 * @throws IOException
	 */
	public void readFrom(BufferedReader br) throws IOException {
		br.readLine(); // skip the header
		String line = null;
		while ((line = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line, ",");
			HashMap hash = new HashMap();
			hash.put(WOrderCommandLog.INT_DATE, Integer.valueOf(st.nextToken()));
			hash.put(WOrderCommandLog.INT_SESSION, Integer.valueOf(st.nextToken()));
			hash.put(WOrderCommandLog.STRING_REAL_TIME, st.nextToken());
			hash.put(WOrderCommandLog.STRING_LOGIN_NAME, Integer.valueOf(st.nextToken()));
			String cmdName = st.nextToken();
			hash.put(WOrderCommandLog.STRING_COMMAND_NAME, cmdName);
			hash.put(WOrderCommandLog.LONG_ORDER_ID, Long.valueOf(st.nextToken()));
			if (cmdName.equals("Request")) {
				hash.put(WOrderCommandLog.STRING_BRAND_NAME, st.nextToken());
				hash.put(WOrderCommandLog.STRING_AUCTIONEER_NAME, st.nextToken());
				hash.put(WOrderCommandLog.INT_SELL_BUY, Integer.valueOf(st.nextToken()));
				hash.put(WOrderCommandLog.INT_MARKET_LIMIT, Integer.valueOf(st.nextToken()));
				hash.put(WOrderCommandLog.LONG_ORDER_PRICE, Long.valueOf(st.nextToken()));
				hash.put(WOrderCommandLog.STRING_ORDER_SPEC, st.nextToken());
				hash.put(WOrderCommandLog.INT_RANDOM_NUMBER, Integer.valueOf(st.nextToken()));
			}
			fOrderCommandArray.add(hash);
		}
	}

}
