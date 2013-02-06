package org.wmart.core;

import java.util.*;
import java.util.Map.Entry;

/**
 * 約定情報を表すクラス
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WContract {

	/** 約定ID */
	private long fContractID = 0;
	/** 約定時刻 (実時刻) */
	private Date fTime = null;
	/** 約定日 */
	private int fDate = 0;
	/** 約定節 */
	private int fSession = 0;
	/** 約定価格 */
	private double fPrice = 0.0;
	/** 約定比率 (買い注文では 0 or 1) */
	private double fPercentage = 0.0;
	/** 商品名 (買い注文では bundle) */
	private String fGoods = "";
	/** 売り約定価格リスト */
	private HashMap<Integer, Double> fSoldPrices = null;
	/** 売り約定比率リスト */
	private HashMap<Integer, Double> fSoldPercentages = null;
	/** 注文数量 */
	private double fOrderVolume = 0;

	/**
	 * コンストラクタ
	 * 
	 * @param contractID
	 *            約定ID
	 * @param time
	 *            約定時刻 (実時刻)
	 * @param date
	 *            約定日
	 * @param session
	 *            約定節
	 * @param price
	 *            約定価格
	 * @param percentage
	 *            約定比率 (売り注文ではスロットごとの比率の単純合計 (max. スロット数)。買い注文では 0 or 1)
	 * @param goods
	 *            商品名 (売り注文では単一。買い注文では bundle)
	 * @param prices
	 *            売り約定価格リスト
	 * @param percentages
	 *            売り約定比率リスト
	 * @param orderVolume
	 *            注文数量 (スロットごとの数量の単純合計 (max. スロット数))
	 */
	public WContract(long contractID, Date time, int date, int session, double price,
		double percentage, String goods, HashMap<Integer, Double> soldPrices,
		HashMap<Integer, Double> soldPercentages, double orderVolume) {
		fContractID = contractID;
		fTime = time;
		fDate = date;
		fSession = session;
		fPrice = price;
		fPercentage = percentage;
		fGoods = goods;
		fSoldPrices = soldPrices;
		fSoldPercentages = soldPercentages;
		fOrderVolume = orderVolume;
	}

	/**
	 * スロットごとの売り約定価格ハッシュを文字列で取得する
	 * 
	 * @return 売り約定価格リスト "時刻:価格;..."
	 */
	public String encodeSoldPrices() {
		StringBuilder s = new StringBuilder();
		for (Iterator<Entry<Integer, Double>> itr = fSoldPrices.entrySet().iterator(); itr
			.hasNext();) {
			Entry<Integer, Double> entry = itr.next();
			s.append(entry.getKey()).append(":").append(entry.getValue()).append(";");
		}
		return s.toString();
	}

	/**
	 * スロットごとの売り約定数量ハッシュを文字列で取得する
	 * 
	 * @return 売り約定数量リスト "時刻:価格;..."
	 */
	public String encodeSoldVolumes() {
		StringBuilder s = new StringBuilder();
		for (Iterator<Entry<Integer, Double>> itr = fSoldPercentages.entrySet().iterator(); itr
			.hasNext();) {
			Entry<Integer, Double> entry = itr.next();
			s.append(entry.getKey()).append(":").append(entry.getValue() * fOrderVolume)
				.append(";");
		}
		return s.toString();
	}

	/**
	 * 指定スロットの売り約定価格を取得する
	 * 
	 * @param slot
	 * @return 売り約定価格
	 */
	public double getSoldPriceAt(int slot) {
		if (fSoldPrices != null && !fSoldPrices.isEmpty() && fSoldPrices.containsKey(slot)) {
			return fSoldPrices.get(slot);
		}
		return 0.0;
	}

	/**
	 * 指定スロットの売り約定比率を取得する
	 * 
	 * @param slot
	 * @return 売り約定比率
	 */
	public double getSoldPercentageAt(int slot) {
		if (fSoldPercentages != null && !fSoldPercentages.isEmpty()
			&& fSoldPercentages.containsKey(slot)) {
			return fSoldPercentages.get(slot);
		}
		return 0.0;
	}

	/**
	 * 指定スロットの売り約定数量を取得する
	 * 
	 * @param slot
	 * @return
	 */
	public double getSoldVolumeAt(int slot) {
		return getSoldPercentageAt(slot) * fOrderVolume;
	}

	/**
	 * 約定数量を取得する
	 * 
	 * @return
	 */
	public double getVolume() {
		return fPercentage * fOrderVolume;
	}

	/**
	 * 約定IDを取得します。
	 * 
	 * @return 約定ID
	 */
	public long getContractID() {
		return fContractID;
	}

	/**
	 * 約定IDを設定します。
	 * 
	 * @param fContractID
	 *            約定ID
	 */
	public void setContractID(long fContractID) {
		this.fContractID = fContractID;
	}

	/**
	 * 約定時刻 (実時刻)を取得します。
	 * 
	 * @return 約定時刻 (実時刻)
	 */
	public Date getTime() {
		return fTime;
	}

	/**
	 * 約定時刻 (実時刻)を設定します。
	 * 
	 * @param fTime
	 *            約定時刻 (実時刻)
	 */
	public void setTime(Date fTime) {
		this.fTime = fTime;
	}

	/**
	 * 約定日を取得します。
	 * 
	 * @return 約定日
	 */
	public int getDate() {
		return fDate;
	}

	/**
	 * 約定日を設定します。
	 * 
	 * @param fDate
	 *            約定日
	 */
	public void setDate(int fDate) {
		this.fDate = fDate;
	}

	/**
	 * 約定節を取得します。
	 * 
	 * @return 約定節
	 */
	public int getSession() {
		return fSession;
	}

	/**
	 * 約定節を設定します。
	 * 
	 * @param fSession
	 *            約定節
	 */
	public void setSession(int fSession) {
		this.fSession = fSession;
	}

	/**
	 * 約定価格を取得します。
	 * 
	 * @return 約定価格
	 */
	public double getPrice() {
		return fPrice;
	}

	/**
	 * 約定価格を設定します。
	 * 
	 * @param fPrice
	 *            約定価格
	 */
	public void setPrice(double fPrice) {
		this.fPrice = fPrice;
	}

	/**
	 * 約定比率 (買い注文では 0 or 1)を取得します。
	 * 
	 * @return 約定比率 (買い注文では 0 or 1)
	 */
	public double getPercentage() {
		return fPercentage;
	}

	/**
	 * 約定比率 (買い注文では 0 or 1)を設定します。
	 * 
	 * @param fPercentage
	 *            約定比率 (買い注文では 0 or 1)
	 */
	public void setPercentage(double fPercentage) {
		this.fPercentage = fPercentage;
	}

	/**
	 * 商品名 (買い注文では bundle)を取得します。
	 * 
	 * @return 商品名 (買い注文では bundle)
	 */
	public String getGoods() {
		return fGoods;
	}

	/**
	 * 商品名 (買い注文では bundle)を設定します。
	 * 
	 * @param fGoods
	 *            商品名 (買い注文では bundle)
	 */
	public void setGoods(String fGoods) {
		this.fGoods = fGoods;
	}

	/**
	 * 売り約定価格リストを取得します。
	 * 
	 * @return 売り約定価格リスト
	 */
	public HashMap<Integer, Double> getSoldPrices() {
		return fSoldPrices;
	}

	/**
	 * 売り約定価格リストを設定します。
	 * 
	 * @param fSoldPrices
	 *            売り約定価格リスト
	 */
	public void setSoldPrices(HashMap<Integer, Double> fSoldPrices) {
		this.fSoldPrices = fSoldPrices;
	}

	/**
	 * 売り約定比率リストを取得します。
	 * 
	 * @return 売り約定比率リスト
	 */
	public HashMap<Integer, Double> getSoldPercentages() {
		return fSoldPercentages;
	}

	/**
	 * 売り約定比率リストを設定します。
	 * 
	 * @param fSoldPercentages
	 *            売り約定比率リスト
	 */
	public void setSoldPercentages(HashMap<Integer, Double> fSoldPercentages) {
		this.fSoldPercentages = fSoldPercentages;
	}

	/**
	 * 注文数量を取得します。
	 * 
	 * @return 注文数量
	 */
	public double getOrderVolume() {
		return fOrderVolume;
	}

	/**
	 * 注文数量を設定します。
	 * 
	 * @param fTotalOrderVolume
	 *            注文数量
	 */
	public void setOrderVolume(double fOrderVolume) {
		this.fOrderVolume = fOrderVolume;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("WContract(%s) [date%s/session%s, $%s, %s%%, %s]", fContractID, fDate,
			fSession, fPrice, fPercentage * 100, fGoods);
	}

}