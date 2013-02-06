/**
 *
 */
package org.wmart.core;

import java.util.*;

import org.umart.serverCore.*;

/**
 * すべての注文を管理するクラス
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WOrderManager {

	/** 識別名 */
	private String fName;
	/** 注文履歴 */
	private ArrayList fOrderHistory;
	/** 各ユーザーの注文を管理するWOrderArrayオブジェクトのベクタ */
	private Vector fOrderArrays;
	/** 注文IDを発生するためのカウンタ */
	static private long fOrderID = 1;
	/** 同一板寄せ期間内の注文をソートするための乱数発生器 */
	private Random fRandom;

	/**
	 * コンストラクタ
	 * 
	 * @param name
	 *            識別名
	 */
	public WOrderManager() {
		fOrderArrays = new Vector();
		fOrderHistory = new ArrayList();
		fRandom = URandom.getInstance();
	}

	/**
	 * 引数で指定された注文を新たに作成する
	 * 
	 * @param userID
	 *            ユーザーID
	 * @param userName
	 *            ユーザ名
	 * @param brandName
	 *            商品名 (不使用)
	 * @param auctioneerName
	 *            市場名
	 * @param sellBuy
	 *            売買区分 (売: WOrder.SELL, 買: WOrder.BUY)
	 * @param marketLimit
	 *            成行指値区分 (成行: WOrder.MARKET, 指値: WOrder.LIMIT)
	 * @param price
	 *            注文価格
	 * @param spec
	 *            注文明細 ("商品名:希望数量:最早時刻:最遅時刻:延べ時間;" の繰り返し)
	 * @param date
	 *            注文日
	 * @param session
	 *            注文節
	 * @return 注文 (無効な場合は null が返る)
	 */
	public WOrder createOrder(int userID, String userName, String brandName, String auctioneerName, int sellBuy,
		int marketLimit, long price, String spec, int date, int session) {
		WOrder o = new WOrder();
		o.setUserID(userID);
		o.setUserName(userName);
		o.setBrandName(brandName);
		o.setAuctioneerName(auctioneerName);
		o.setOrderID(++fOrderID);
		o.setTime(new Date());
		o.setDate(date);
		o.setSession(session);
		o.setSellBuy(sellBuy);
		o.setMarketLimit(marketLimit);
		if (marketLimit == WOrder.MARKET) {
			o.setOrderPrice(0);
		} else {
			o.setOrderPrice(price);
		}
		int nGoods = o.setOrderSpec(spec);
		o.setRandomNumber(this.getRandomInteger());
		if (sellBuy == WOrder.SELL && nGoods > 1) {
			o = null; // 売り注文は1商品に限る
		}
		return o;
	}

	/**
	 * 正の整数の乱数を返す．
	 * 
	 * @return 正の整数の乱数
	 */
	public int getRandomInteger() {
		return Math.abs(fRandom.nextInt());
	}

	/**
	 * userIDをもつメンバーの全ての注文を取り消す．
	 * 
	 * @param userID
	 *            注文を取り消したいメンバーのユーザーID
	 */
	public void cancelAllOrdersOfMember(int userID) {
		WOrderArray orderArray = getOrderArray(userID);
		if (orderArray == null) {
			System.out.print("Member" + userID + " cannot be found ");
			System.out.println("in WOrderManager.cancelAllOrdersOfMember");
			return;
		}
		Enumeration orders = orderArray.getUncontractedOrders().elements();
		while (orders.hasMoreElements()) {
			WOrder o = (WOrder) orders.nextElement();
			o.cancel();
			if (o.getContractVolume() > 0) {
				orderArray.getContractedOrders().addElement(o);
			}
		}
		orderArray.getUncontractedOrders().removeAllElements();
	}

	/**
	 * userIDをもつメンバーの全ての注文をVectorに入れて返す．
	 * 
	 * @param userID
	 *            ユーザーID
	 * @return 指定されたメンバーの全ての注文を含むVector
	 */
	public Vector getAllOrders(int userID) {
		Vector result = new Vector();
		WOrderArray orderArray = getOrderArray(userID);
		if (orderArray == null) {
			System.out.print("Member" + userID + " cannot be found ");
			System.out.println("in WOrderManager.getExecutions");
			return result;
		}
		Enumeration contOrders = orderArray.getContractedOrders().elements();
		while (contOrders.hasMoreElements()) {
			WOrder o = (WOrder) contOrders.nextElement();
			result.addElement(o);
		}
		Enumeration uncontOrders = orderArray.getUncontractedOrders().elements();
		while (uncontOrders.hasMoreElements()) {
			WOrder o = (WOrder) uncontOrders.nextElement();
			result.addElement(o);
		}
		return result;
	}

	/**
	 * userIDをもつメンバーのorderIDで指定される注文を取り消す．
	 * 
	 * @param userID
	 *            ユーザーID
	 * @param orderID
	 *            注文ID
	 * @return 取り消された注文．指定した注文が存在しない場合，nullを返す．
	 */
	public WOrder cancelOrder(int userID, long orderID) {
		// 指定されたorderIDの注文が見つからなければnullを返す
		WOrderArray orderArray = getOrderArray(userID);
		if (orderArray == null) {
			System.out.print("Member" + userID + " cannot be found ");
			System.out.println("in WOrderManager.cancelOrder");
			return null;
		}
		Vector uncontractedOrders = orderArray.getUncontractedOrders();
		int size = uncontractedOrders.size();
		int targetIndex = -1;
		for (int i = 0; i < size; ++i) {
			WOrder o = (WOrder) uncontractedOrders.elementAt(i);
			if (o.getOrderID() == orderID) {
				targetIndex = i;
				break;
			}
		}
		if (targetIndex < 0) {
			return null;
		}
		WOrder order = (WOrder) uncontractedOrders.remove(targetIndex);
		order.cancel();
		if (order.getContractVolume() > 0) {
			orderArray.getContractedOrders().addElement(order);
		}
		return order;
	}

	/**
	 * すべてのメンバーの全ての注文を取り除く．
	 */
	public void removeAllOrders() {
		Enumeration e = fOrderArrays.elements();
		while (e.hasMoreElements()) {
			WOrderArray array = (WOrderArray) e.nextElement();
			array.removeAllContractedOrders();
			array.removeAllUncontractedOrders();
		}
	}

	/**
	 * 全注文の履歴から該当注文を返す．
	 * 
	 * @param orderID
	 *            注文ID
	 * @return 注文
	 */
	public WOrder getOrderFromHistory(long orderID) {
		int index = (int) orderID - 1;
		if (index < 0 || index >= fOrderHistory.size()) {
			return null;
		}
		return (WOrder) fOrderHistory.get(index);
	}

	/**
	 * 注文を履歴へ登録する．
	 * 
	 * @param o
	 *            注文
	 */
	public void registerOrderToHistory(WOrder o) {
		fOrderHistory.add(o);
		WOrderArray orderArray = getOrderArray(o.getUserID());
		orderArray.getHistory().add(o);
	}

	/**
	 * userIDの注文管理クラスWOrderArrayを生成する．
	 * 
	 * @param userID
	 *            ユーザーID
	 * @return 成功：true, 失敗: false (同一メンバーが存在する場合)
	 */
	public boolean createOrderArray(int userID) {
		Enumeration e = fOrderArrays.elements();
		while (e.hasMoreElements()) {
			WOrderArray x = (WOrderArray) e.nextElement();
			if (x.getUserID() == userID) {
				System.err.println("The same UserID exists.");
				return false;
			}
		}
		fOrderArrays.addElement(new WOrderArray(userID));
		return true;
	}

	/**
	 * 注文を追加する．
	 * 
	 * @param o
	 *            追加注文
	 * @return 成功：true, 失敗: false (対応するメンバーが見つからないとき)
	 */
	public boolean addOrder(WOrder o) {
		Enumeration e = fOrderArrays.elements();
		while (e.hasMoreElements()) {
			WOrderArray x = (WOrderArray) e.nextElement();
			if (x.getUserID() == o.getUserID()) {
				x.addOrder(o);
				return true;
			}
		}
		return false;
	}

	/**
	 * 引数で指定されたメンバーの注文集合を返す．
	 * 
	 * @param userID
	 *            ユーザーID
	 * @return 注文集合を管理するWOrderArrayオブジェクト． 対応するメンバーのWOrderArrayオブジェクトが 存在しない場合, nullを返すので注意すること．
	 */
	public WOrderArray getOrderArray(int userID) {
		Enumeration e = fOrderArrays.elements();
		while (e.hasMoreElements()) {
			WOrderArray x = (WOrderArray) e.nextElement();
			if (x.getUserID() == userID) {
				return x;
			}
		}
		return null;
	}

	/**
	 * 何人分のメンバーのWOrderArrayを管理しているか返す．
	 * 
	 * @return 管理しているWOrderArrayの数
	 */
	public int getNoOfOrderArrays() {
		return fOrderArrays.size();
	}

	/**
	 * 管理しているWOrderArrayを列挙するためのEnumerationを返す．
	 * 
	 * @return 管理しているWOrderArrayを列挙するためのEnumeration
	 */
	public Enumeration getOrderArrays() {
		return fOrderArrays.elements();
	}

	/**
	 * 現在位置を記憶する
	 */
	public void markCurrentIndex() {
		Enumeration e = fOrderArrays.elements();
		while (e.hasMoreElements()) {
			WOrderArray array = (WOrderArray) e.nextElement();
			array.resetIndexOfLatestContractedOrder();
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return fName;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		fName = name;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("WOrderManager(%s) [OrderArrays=%s]", fName, fOrderArrays);
	}
}
