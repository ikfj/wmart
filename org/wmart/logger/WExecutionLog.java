package org.wmart.logger;

import java.io.*;
import java.util.*;

import org.wmart.core.*;

/**
 * １節分の約定情報のログを扱うクラスです．
 * 
 * @author Ikki Fujiwara, NII
 * @author 小野　功
 */
public class WExecutionLog {

	/** 日付を引くキー */
	public static final String INT_DATE = "INT_DATE";

	/** 節を引くキー */
	public static final String INT_SESSION = "INT_SESSION";

	/** ログイン名を引くためのキー */
	public static final String STRING_LOGIN_NAME = "STRING_LOGIN_NAME";

	/** 約定IDを引くキー */
	public static final String LONG_CONTRACT_ID = "LONG_CONTRACT_ID";

	/** 約定価格を引くキー */
	public static final String DOUBLE_CONTRACT_PRICE = "DOUBLE_CONTRACT_PRICE";

	/** 約定数量を引くキー */
	public static final String DOUBLE_CONTRACT_VOLUME = "DOUBLE_CONTRACT_VOLUME";

	/** 注文IDを引くキー */
	public static final String LONG_ORDER_ID = "LONG_ORDER_ID";

	/** 日 */
	private int fDate;

	/** 節 */
	private int fSession;

	/** 板寄せ1回分の約定情報 */
	private ArrayList fExecutionArray;

	/**
	 * コンストラクタ
	 * 
	 * @param date
	 *            日
	 * @param session
	 *            節
	 */
	public WExecutionLog(int date, int session) {
		fDate = date;
		fSession = session;
		fExecutionArray = new ArrayList();
	}

	/**
	 * 約定IDの比較器
	 * 
	 * @author 小野　功
	 */
	private class UContractIDComparator implements Comparator {

		/**
		 * 比較
		 * 
		 * @param o1
		 *            約定情報１
		 * @param o2
		 *            約定情報２
		 * @return o1のIDがo2のIDより大きければ+1，小さければ-1
		 */
		public int compare(Object o1, Object o2) {
			HashMap info1 = (HashMap) o1;
			HashMap info2 = (HashMap) o2;
			long id1 = ((Long) info1.get(WExecutionLog.LONG_CONTRACT_ID)).longValue();
			long id2 = ((Long) info2.get(WExecutionLog.LONG_CONTRACT_ID)).longValue();
			if (id1 > id2) {
				return 1;
			} else {
				return -1;
			}
		}

	}

	/**
	 * コンストラクタ
	 * 
	 * @param date
	 *            日
	 * @param session
	 *            節
	 * @param om
	 *            WOrderManager
	 */
	public WExecutionLog(int date, int session, WOrderManager om) {
		this(date, session);
		Enumeration orderArrays = om.getOrderArrays();
		while (orderArrays.hasMoreElements()) {
			WOrderArray orderArray = (WOrderArray) orderArrays.nextElement();
			Enumeration uncontractedOrders = orderArray.getUncontractedOrders().elements();
			while (uncontractedOrders.hasMoreElements()) {
				WOrder o = (WOrder) uncontractedOrders.nextElement();
				checkAndAppendInfo(o);
			}
			Enumeration contractedOrders = orderArray.getContractedOrders().elements();
			while (contractedOrders.hasMoreElements()) {
				WOrder o = (WOrder) contractedOrders.nextElement();
				checkAndAppendInfo(o);
			}
		}
		Collections.sort(fExecutionArray, new UContractIDComparator());
	}

	/**
	 * 引数の注文がもつ約定情報の中から，この約定情報ログが扱う日と節に 該当する約定情報を見つけて，fExecutionArrayに情報を追加する．
	 * 
	 * @param o
	 *            注文
	 */
	private void checkAndAppendInfo(WOrder o) {
		long orderID = o.getOrderID();
		String userName = o.getUserName();
		Enumeration infos = o.getContracts().elements();
		while (infos.hasMoreElements()) {
			WContract info = (WContract) infos.nextElement();
			if (info.getDate() == fDate && info.getSession() == fSession) {
				HashMap hash = makeExecutionInfo(info.getDate(), info.getSession(), userName, info.getContractID(),
					info.getPrice(), info.getVolume(), orderID);
				fExecutionArray.add(hash);
			}
		}
	}

	/**
	 * 約定情報を生成して返す．
	 * 
	 * @param date
	 *            日
	 * @param session
	 *            節
	 * @param userName
	 *            ユーザ名
	 * @param contractID
	 *            約定ID
	 * @param contractPrice
	 *            約定価格
	 * @param contractVolume
	 *            約定数量
	 * @param orderID
	 *            注文ID
	 * @return 約定情報
	 */
	private HashMap makeExecutionInfo(int date, int session, String userName, long contractID, double contractPrice,
		double contractVolume, long orderID) {
		HashMap info = new HashMap();
		info.put(WExecutionLog.INT_DATE, new Integer(date));
		info.put(WExecutionLog.INT_SESSION, new Integer(session));
		info.put(WExecutionLog.STRING_LOGIN_NAME, userName);
		info.put(WExecutionLog.LONG_CONTRACT_ID, new Long(contractID));
		info.put(WExecutionLog.DOUBLE_CONTRACT_PRICE, new Double(contractPrice));
		info.put(WExecutionLog.DOUBLE_CONTRACT_VOLUME, new Double(contractVolume));
		info.put(WExecutionLog.LONG_ORDER_ID, new Long(orderID));
		return info;
	}

	/**
	 * 出力ストリームにデータを書き出す．
	 * 
	 * @param pw
	 *            出力ストリーム
	 * @throws IOException
	 */
	public void writeTo(PrintWriter pw) throws IOException {
		pw.println("ContractDate,ContractSession,LoginName,ContractID" + ",ContractPrice,ContractVolume,OrderID");
		Iterator itr = fExecutionArray.iterator();
		while (itr.hasNext()) {
			HashMap info = (HashMap) itr.next();
			pw.print(info.get(WExecutionLog.INT_DATE).toString() + ",");
			pw.print(info.get(WExecutionLog.INT_SESSION).toString() + ",");
			pw.print(info.get(WExecutionLog.STRING_LOGIN_NAME).toString() + ",");
			pw.print(info.get(WExecutionLog.LONG_CONTRACT_ID).toString() + ",");
			pw.print(info.get(WExecutionLog.DOUBLE_CONTRACT_PRICE).toString() + ",");
			pw.print(info.get(WExecutionLog.DOUBLE_CONTRACT_VOLUME).toString() + ",");
			pw.println(info.get(WExecutionLog.LONG_ORDER_ID).toString());
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
		fExecutionArray.clear();
		br.readLine(); // skip the header
		String line = null;
		while ((line = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line, ",");
			int date = Integer.parseInt(st.nextToken());
			int session = Integer.parseInt(st.nextToken());
			String userName = st.nextToken();
			long contractID = Long.parseLong(st.nextToken());
			long contractPrice = Long.parseLong(st.nextToken());
			long contractVolume = Long.parseLong(st.nextToken());
			long orderID = Long.parseLong(st.nextToken());
			HashMap info = makeExecutionInfo(date, session, userName, contractID, contractPrice, contractVolume,
				orderID);
			fExecutionArray.add(info);
		}
	}

}
