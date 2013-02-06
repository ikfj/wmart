package org.wmart.core;

import java.util.*;

/**
 * ユーザーひとり分の注文集合を管理するクラス。一部または全部が約定済みである注文と、全部が未約定である注文に分けて管理する。
 * 
 * @author 川部 裕司
 * @author 森下 領平
 * @author 小野 功
 * @author Ikki Fujiwara, NII
 */
public class WOrderArray {

	/** ユーザーID */
	private int fUserID;
	/** 一部／全部約定済み注文リスト */
	private Vector fContractedOrders;
	/** 全部未約定注文リスト */
	private Vector fUncontractedOrders;
	/** 注文履歴 */
	private ArrayList fHistory;
	/** 約定済み注文リストの中で直近セッションで成立した分が始まるインデックス */
	private int fIndexOfLatestContractedOrder;

	/**
	 * userIDをもつユーザーのWOrderArrayを作成する．
	 * 
	 * @param userID
	 *            ユーザーID
	 */
	public WOrderArray(int userID) {
		fUserID = userID;
		fContractedOrders = new Vector();
		fUncontractedOrders = new Vector();
		fHistory = new ArrayList();
	}

	/**
	 * 注文を追加する。一部または全部が約定済みなら約定済みリストに、全部が未約定なら未約定リストに追加する。
	 * 
	 * @param o
	 *            注文
	 */
	public void addOrder(WOrder o) {
		if (o.getContractVolume() > 0) {
			fContractedOrders.addElement(o);
		} else if (o.getContractVolume() == 0) {
			fUncontractedOrders.addElement(o);
		} else {
			System.out.println("Contract volume is under 0");
			System.exit(1);
		}
	}

	/**
	 * 約定分の注文を全て取り除く．
	 */
	public void removeAllContractedOrders() {
		fContractedOrders.removeAllElements();
	}

	/**
	 * 未約定分の注文を全て取り除く．
	 */
	public void removeAllUncontractedOrders() {
		fUncontractedOrders.removeAllElements();
	}

	/**
	 * 約定済み注文リストの中で直近セッションで成立した分が始まるインデックスを取得します。
	 * 
	 * @return 約定済み注文リストの中で直近セッションで成立した分が始まるインデックス
	 */
	public int getIndexOfLatestContractedOrder() {
		return fIndexOfLatestContractedOrder;
	}

	/**
	 * 約定済み注文リストの中で次のセッションで成立する分が始まるインデックスを記憶します。
	 */
	public void resetIndexOfLatestContractedOrder() {
		this.fIndexOfLatestContractedOrder = fContractedOrders.size();
	}

	/**
	 * 約定分の注文数を返す．
	 * 
	 * @return 約定分の注文数
	 */
	public int getNoOfContractedOrders() {
		return fContractedOrders.size();
	}

	/**
	 * 未約定分の注文数を返す．
	 * 
	 * @return 未約定分の注文数
	 */
	public int getNoOfUncontractedOrders() {
		return fUncontractedOrders.size();
	}

	/**
	 * indexで指定される約定分の注文を返す．
	 * 
	 * @param index
	 *            添え字
	 * @return 注文
	 */
	public WOrder getContractedOrderAt(int index) {
		return (WOrder) fContractedOrders.elementAt(index);
	}

	/**
	 * indexで指定される未約定分の注文を返す．
	 * 
	 * @param index
	 *            添え字
	 * @return 注文
	 */
	public WOrder getUncontractedOrderAt(int index) {
		return (WOrder) fUncontractedOrders.elementAt(index);
	}

	/**
	 * 注文履歴を返す．
	 * 
	 * @return 注文履歴のリスト
	 */
	public ArrayList getHistory() {
		return fHistory;
	}

	/**
	 * userIDを返す
	 * 
	 * @return ユーザーID
	 */
	public int getUserID() {
		return fUserID;
	}

	/**
	 * 約定分の注文集合を返す．
	 * 
	 * @return 約定分の注文集合
	 */
	public Vector getContractedOrders() {
		return fContractedOrders;
	}

	/**
	 * 未約定分の注文集合を返す．
	 * 
	 * @return 未約定分の注文集合
	 */
	public Vector getUncontractedOrders() {
		return fUncontractedOrders;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("\nWOrderArray(user%s) [\nUncontracted=%s, \nContracted=%s\n]", fUserID,
			fUncontractedOrders, fContractedOrders);
	}

}
