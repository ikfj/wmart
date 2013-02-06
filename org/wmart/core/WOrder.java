package org.wmart.core;

import java.util.*;

/**
 * 注文を扱うクラス
 * 
 * @author Ikki Fujiwara, NII
 */

public class WOrder {

	/** 「売り」を表す定数 */
	public static int SELL = 1;
	/** 「買い」を表す定数 */
	public static int BUY = 2;
	/** 「成行」を表す定数 */
	public static int MARKET = 1;
	/** 「指値」を表す定数 */
	public static int LIMIT = 2;

	/** ユーザーID */
	private int fUserID = 0;
	/** ログイン名 */
	private String fUserName = "";
	/** 注文ID */
	private long fOrderID = 0;
	/** 注文時刻 (実時間) */
	private Date fTime = null;
	/** 注文日 */
	private int fDate = 0;
	/** 注文節 */
	private int fSession = 0;
	/** 売買区分 */
	private int fSellBuy = 0;
	/** 成行指値区分 */
	private int fMarketLimit = 0;
	/** 市場名 */
	private String fAuctioneerName = "";
	/** 注文価格 */
	private long fOrderPrice = -1;
	/** 注文明細 */
	private TreeMap<String, WOrderSpec> fOrderSpecMap = null;
	/** 売り注文商品名 */
	private String fOrderSellGood = "";
	/** 売り注文数量 */
	private long fOrderSellVolume = 0;
	/** 最早開始時刻 */
	private int fEarliestSlot = -1;
	/** 最遅終了時刻 */
	private int fLatestSlot = -1;
	/** 約定情報 (約定相手ごとに1件) */
	private Vector<WContract> fContracts = null;
	/** コンパレータ用の乱数 */
	private int fRandomNumber;

	/**
	 * コンストラクタ
	 */
	public WOrder() {
		fTime = new Date();
		fOrderSpecMap = new TreeMap<String, WOrderSpec>();
		fContracts = new Vector<WContract>();
	}

	/**
	 * 時刻を 1 ステップ進める (最早時刻と最遅時刻を現在へ近づける)
	 * 
	 * @return 過去になったら false
	 */
	public boolean nextStep() {
		for (Iterator<WOrderSpec> itr = fOrderSpecMap.values().iterator(); itr.hasNext();) {
			WOrderSpec spec = itr.next();
			spec.nextStep();
		}
		fEarliestSlot--;
		fLatestSlot--;
		return (fEarliestSlot <= 0 && fLatestSlot <= 0);
	}

	/**
	 * 注文をキャンセルする
	 */
	public void cancel() {
		fOrderPrice = -1;
	}

	/**
	 * 注文明細を文字列で設定する
	 * 
	 * @param encodedSpecs
	 *            "商品名:希望数量:最早時刻:最遅時刻:延べ時間;" の繰り返し
	 * @return 商品数
	 */
	public int setOrderSpec(String encodedSpecs) {
		fOrderSpecMap.clear();
		assert (!encodedSpecs.isEmpty());
		String[] specArray = encodedSpecs.split(";");
		for (int i = 0; i < specArray.length; i++) {
			addOrderSpec(specArray[i]);
		}
		return specArray.length;
	}

	/**
	 * 注文明細を文字列で1件追加する
	 * 
	 */
	public void addOrderSpec(String encodedSpec) {
		WOrderSpec spec = new WOrderSpec(encodedSpec);
		fOrderSpecMap.put(spec.getName(), spec);
		if (fSellBuy == SELL) {
			assert (fOrderSellGood.isEmpty() && fOrderSellVolume == 0) : "selling order cannot have multiple goods";
			fOrderSellGood = spec.getName();
			fOrderSellVolume = spec.getOrderVolume();
		}
		if (fEarliestSlot == -1 || fEarliestSlot > spec.getArrivalTime()) {
			fEarliestSlot = spec.getArrivalTime();
		}
		if (fLatestSlot == -1 || fLatestSlot < spec.getDeadlineTime()) {
			fLatestSlot = spec.getDeadlineTime();
		}
	}

	/**
	 * 注文明細を1件追加する
	 * 
	 */
	public void addOrderSpec(String name, int orderVolume, int arrivalTime, int deadlineTime,
		int totalTime) {
		WOrderSpec spec = new WOrderSpec(name, orderVolume, arrivalTime, deadlineTime, totalTime);
		fOrderSpecMap.put(name, spec);
		if (fSellBuy == SELL) {
			assert (fOrderSellGood.isEmpty() && fOrderSellVolume == 0) : "selling order cannot have multiple goods";
			fOrderSellGood = name;
			fOrderSellVolume = orderVolume;
		}
		if (fEarliestSlot == -1 || fEarliestSlot > arrivalTime) {
			fEarliestSlot = arrivalTime;
		}
		if (fLatestSlot == -1 || fLatestSlot < deadlineTime) {
			fLatestSlot = deadlineTime;
		}
	}

	/**
	 * 注文明細を文字列で取得する
	 * 
	 * @return "商品名:希望数量:最早時刻:最遅時刻:延べ時間;" の繰り返し
	 */
	public String getOrderSpec() {
		StringBuilder encodedSpecs = new StringBuilder();
		for (Iterator<WOrderSpec> itr = fOrderSpecMap.values().iterator(); itr.hasNext();) {
			WOrderSpec spec = itr.next();
			encodedSpecs.append(spec.encode()).append(";");
		}
		return encodedSpecs.toString();
	}

	/**
	 * 約定情報を追加する.
	 * 
	 * @param contractID
	 *            約定ID
	 * @param time
	 *            約定時刻 (実時刻)
	 * @param date
	 *            約定日
	 * @param session
	 *            約定節
	 * @param overallPrice
	 *            約定価格
	 * @param overallPercentage
	 *            約定比率 (買い注文では 0 or 1)
	 * @param goods
	 *            商品名 (買い注文では bundle)
	 * @param soldPrices
	 *            売り約定価格リスト
	 * @param soldPercentages
	 *            売り約定比率リスト
	 * @param orderVolume
	 *            注文数量 (単純合計)
	 */
	public void addContract(long contractID, Date time, int date, int session, double overallPrice,
		double overallPercentage, String goods, HashMap<Integer, Double> soldPrices,
		HashMap<Integer, Double> soldPercentages, double orderVolume) {
		WContract contract = new WContract(contractID, time, date, session, overallPrice,
			overallPercentage, goods, soldPrices, soldPercentages, orderVolume);
		fContracts.addElement(contract);
	}

	/**
	 * 約定価格を取得する
	 * 
	 * @return 約定価格
	 */
	public double getContractPrice() {
		double sum = 0.0;
		for (Iterator<WContract> itr = fContracts.iterator(); itr.hasNext();) {
			sum += itr.next().getPrice();
		}
		return sum;
	}

	/**
	 * 約定比率を取得する
	 * 
	 * @return 約定比率
	 */
	public double getContractPercentage() {
		double sum = 0.0;
		for (Iterator<WContract> itr = fContracts.iterator(); itr.hasNext();) {
			sum += itr.next().getPercentage();
		}
		return sum;
	}

	/**
	 * 約定数量を取得する
	 * 
	 * @return 約定数量 (買い注文では値に意味なし。0 か否かで判定すること)
	 */
	public double getContractVolume() {
		double sum = 0.0;
		for (Iterator<WContract> itr = fContracts.iterator(); itr.hasNext();) {
			sum += itr.next().getVolume();
		}
		return sum;
	}

	/**
	 * スロットごとの売り約定明細を文字列で取得する
	 * 
	 * @return "商品名:注文数量:最早時刻:最遅時刻:約定数量(最早),...,約定数量(最遅),:約定価格(最早),...,約定価格(最遅),"
	 */
	public String getSoldSpec() {
		StringBuilder encodedSpecs = new StringBuilder();
		encodedSpecs.append(fOrderSellGood).append(":");
		encodedSpecs.append(fOrderSellVolume).append(":");
		encodedSpecs.append(fEarliestSlot).append(":");
		encodedSpecs.append(fLatestSlot).append(":");
		if (fSellBuy == SELL) {
			for (int slot = fEarliestSlot; slot <= fLatestSlot; slot++) {
				encodedSpecs.append(getSoldVolumeAt(slot)).append(",");
			}
			encodedSpecs.append(":");
			for (int slot = fEarliestSlot; slot <= fLatestSlot; slot++) {
				encodedSpecs.append(getSoldPriceAt(slot)).append(",");
			}
		}
		return encodedSpecs.toString();
	}

	/**
	 * 指定時刻の売り約定価格を取得する
	 * 
	 * @param slot
	 *            時刻
	 * @return 売り約定価格
	 */
	public double getSoldPriceAt(int slot) {
		double sum = 0.0;
		for (Iterator<WContract> itr = fContracts.iterator(); itr.hasNext();) {
			sum += itr.next().getSoldPriceAt(slot);
		}
		return sum;
	}

	/**
	 * 指定時刻の売り約定比率を取得する
	 * 
	 * @param slot
	 *            時刻
	 * @return 売り約定比率
	 */
	public double getSoldPercentageAt(int slot) {
		double sum = 0.0;
		for (Iterator<WContract> itr = fContracts.iterator(); itr.hasNext();) {
			WContract contract = itr.next();
			sum += contract.getSoldPercentageAt(slot);
		}
		return sum;
	}

	/**
	 * 指定時刻の売り約定数量を取得する
	 * 
	 * @param slot
	 *            時刻
	 * @return 売り約定数量
	 */
	public double getSoldVolumeAt(int slot) {
		double sum = 0.0;
		for (Iterator<WContract> itr = fContracts.iterator(); itr.hasNext();) {
			WContract contract = itr.next();
			sum += contract.getSoldVolumeAt(slot);
		}
		return sum;
	}

	/**
	 * ユーザーIDを取得します。
	 * 
	 * @return ユーザーID
	 */
	public int getUserID() {
		return fUserID;
	}

	/**
	 * ユーザーIDを設定します。
	 * 
	 * @param fUserID
	 *            ユーザーID
	 */
	public void setUserID(int fUserID) {
		this.fUserID = fUserID;
	}

	/**
	 * ログイン名を取得します。
	 * 
	 * @return ログイン名
	 */
	public String getUserName() {
		return fUserName;
	}

	/**
	 * ログイン名を設定します。
	 * 
	 * @param fUserName
	 *            ログイン名
	 */
	public void setUserName(String fUserName) {
		this.fUserName = fUserName;
	}

	/**
	 * 銘柄名を取得します。
	 * 
	 * @return 銘柄名
	 */
	public String getBrandName() {
		return fOrderSellGood;
	}

	/**
	 * 銘柄名を設定します。
	 * 
	 * @param fBrandName
	 *            銘柄名
	 */
	public void setBrandName(String fBrandName) {
		this.fOrderSellGood = fBrandName;
	}

	/**
	 * 注文IDを取得します。
	 * 
	 * @return 注文ID
	 */
	public long getOrderID() {
		return fOrderID;
	}

	/**
	 * 注文IDを設定します。
	 * 
	 * @param fOrderID
	 *            注文ID
	 */
	public void setOrderID(long fOrderID) {
		this.fOrderID = fOrderID;
	}

	/**
	 * 注文時刻(実時間)を取得します。
	 * 
	 * @return 注文時刻(実時間)
	 */
	public Date getTime() {
		return fTime;
	}

	/**
	 * 注文時刻(実時間)を設定します。
	 * 
	 * @param fTime
	 *            注文時刻(実時間)
	 */
	public void setTime(Date fTime) {
		this.fTime = fTime;
	}

	/**
	 * 注文日を取得します。
	 * 
	 * @return 注文日
	 */
	public int getDate() {
		return fDate;
	}

	/**
	 * 注文日を設定します。
	 * 
	 * @param fDate
	 *            注文日
	 */
	public void setDate(int fDate) {
		this.fDate = fDate;
	}

	/**
	 * 注文節を取得します。
	 * 
	 * @return 注文節
	 */
	public int getSession() {
		return fSession;
	}

	/**
	 * 注文節を設定します。
	 * 
	 * @param fSession
	 *            注文節
	 */
	public void setSession(int fSession) {
		this.fSession = fSession;
	}

	/**
	 * 売買区分を取得します。
	 * 
	 * @return 売買区分
	 */
	public int getSellBuy() {
		return fSellBuy;
	}

	/**
	 * 売買区分を設定します。
	 * 
	 * @param sellBuy
	 *            売買区分
	 */
	public void setSellBuy(int sellBuy) {
		assert (sellBuy == SELL || sellBuy == BUY);
		this.fSellBuy = sellBuy;
	}

	/**
	 * 成行指値区分を取得します。
	 * 
	 * @return 成行指値区分
	 */
	public int getMarketLimit() {
		return fMarketLimit;
	}

	/**
	 * 成行指値区分を設定します。
	 * 
	 * @param marketLimit
	 *            成行指値区分
	 */
	public void setMarketLimit(int marketLimit) {
		assert (marketLimit == MARKET || marketLimit == LIMIT);
		this.fMarketLimit = marketLimit;
	}

	/**
	 * 市場名を設定します。
	 * 
	 * @param fAuctioneerName
	 *            市場名
	 */
	public void setAuctioneerName(String fAuctioneerName) {
		this.fAuctioneerName = fAuctioneerName;
	}

	/**
	 * 市場名を取得します。
	 * 
	 * @return 市場名
	 */
	public String getAuctioneerName() {
		return fAuctioneerName;
	}

	/**
	 * 注文価格を取得します。
	 * 
	 * @return 注文価格
	 */
	public long getOrderPrice() {
		return fOrderPrice;
	}

	/**
	 * 注文価格を設定します。
	 * 
	 * @param price
	 *            注文価格
	 */
	public void setOrderPrice(long price) {
		fOrderPrice = price;
	}

	/**
	 * 注文明細を取得します。
	 * 
	 * @return 注文明細
	 */
	public TreeMap<String, WOrderSpec> getOrderSpecMap() {
		return fOrderSpecMap;
	}

	/**
	 * 最早開始時刻を取得します。
	 * 
	 * @return 最早開始時刻
	 */
	public int getEarliestSlot() {
		return fEarliestSlot;
	}

	/**
	 * 最遅終了時刻を取得します。
	 * 
	 * @return 最遅終了時刻
	 */
	public int getLatestSlot() {
		return fLatestSlot;
	}

	/**
	 * 約定情報を取得します。
	 * 
	 * @return 約定情報
	 */
	public Vector<WContract> getContracts() {
		return fContracts;
	}

	/**
	 * コンパレータ用の乱数を取得します。
	 * 
	 * @return コンパレータ用の乱数
	 */
	public int getRandomNumber() {
		return fRandomNumber;
	}

	/**
	 * コンパレータ用の乱数を設定します。
	 * 
	 * @param RandomNumber
	 *            コンパレータ用の乱数
	 */
	public void setRandomNumber(int RandomNumber) {
		this.fRandomNumber = RandomNumber;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("\nWOrder");
		str.append(String.format("(%s) [date%s/session%s, %s, %s, $%s, ", fOrderID, fDate,
			fSession, (fSellBuy == SELL) ? "sell" : "buy", (fMarketLimit == MARKET) ? "market"
				: "limit", fOrderPrice));
		for (Iterator<WOrderSpec> itr = fOrderSpecMap.values().iterator(); itr.hasNext();) {
			WOrderSpec spec = itr.next();
			str.append(spec.toString()).append(";");
		}
		str.append(", ");
		str.append("[");
		for (Iterator<WContract> itr = fContracts.iterator(); itr.hasNext();) {
			WContract cont = itr.next();
			str.append(cont.toString());
		}
		str.append("]");
		return str.append("]").toString();
	}

	/*
	 * テスト用
	 */
	public static void main(String args[]) {
		WOrder order = new WOrder();
		order.addOrderSpec("CPU", 4, 3, 6, 2);
		order.addOrderSpec("Storage", 9, 3, 8, 4);
		order.addOrderSpec("Network", 1, 2, 5, 4);
		order.setOrderSpec("CPU:4:3:6:2;Storage:9:3:8:4;Network:1:2:5:4;");

		TreeMap specMap = order.getOrderSpecMap();
		for (Iterator iterator = specMap.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<String, WOrderSpec> entry = (Map.Entry<String, WOrderSpec>) iterator.next();
			System.out.println(entry.getKey() + " = " + entry.getValue());
		}
	}

}
