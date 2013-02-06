package org.wmart.agent;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * タイムラインを表すクラス (売りエージェント用)
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WTimelineSeller {

	/** 商品名 */
	private String fGood;
	/** 保持スロット数 */
	private int fSlots;
	/** 始端時刻 */
	private int fOffset;
	/** 供給量 */
	private int fVolume;
	/** 原単価 */
	private int fUnitPrice;
	/** 先物取引対象スロット数 */
	private int fSlotsForward;
	/** 最終スロット番号 */
	private int fMaxSlot;
	/** 在庫リスト */
	private LinkedList<Double> fStockList;
	/** 売上リスト */
	private LinkedList<Double> fSalesList;
	/** 注文中の数量リスト (1ステップごとにリセットされる) */
	private ArrayList<Long> fAskingList;
	/** スロット => 受注記録 */
	private LinkedHashMap<Integer, WTimelineSellerRecord> fRecords;

	/**
	 * コンストラクタ
	 * 
	 * @param good
	 *            商品名
	 * @param slots
	 *            保持スロット数
	 * @param offset
	 *            始端の時刻
	 * @param volume
	 *            供給量
	 * @param unitPrice
	 *            原単価
	 * @param slotsForward
	 *            先物取引対象スロット数
	 * @param maxSlot
	 *            最終スロット番号
	 */
	public WTimelineSeller(String good, int slots, int offset, int volume, int unitPrice,
		int slotsForward, int maxSlot) {
		fGood = good;
		fSlots = slots;
		fOffset = offset;
		fVolume = volume;
		fUnitPrice = unitPrice;
		fSlotsForward = slotsForward;
		fMaxSlot = maxSlot;
		fStockList = new LinkedList<Double>();
		fSalesList = new LinkedList<Double>();
		fAskingList = new ArrayList<Long>();
		fRecords = new LinkedHashMap<Integer, WTimelineSellerRecord>();
		for (long slot = 1; slot <= slots; slot++) {
			// 最初の daysForward 日は先物発注余裕として空ける
			fStockList.add((slot <= fSlotsForward) ? 0.0 : (double) fVolume);
			fSalesList.add(0.0);
			fAskingList.add((long) 0);
		}
	}

	/**
	 * ポインタを進める
	 * 
	 * @param step
	 *            何スロット先まで進めるか。record() のステップ数と合わせる
	 */
	public void succeed(int step) {
		for (int i = 0; i < step; i++) {
			int slot = fOffset + fSlots;
			fOffset++;
			// maxDays を過ぎたら供給終了
			fStockList.add((fMaxSlot < slot) ? 0.0 : (double) fVolume);
			fStockList.remove(0);
			fSalesList.add(0.0);
			fSalesList.remove(0);
		}
		for (int i = 0; i < fAskingList.size(); i++) {
			fAskingList.set(i, (long) 0);
		}
	}

	/**
	 * 現在の状況を記録する
	 * 
	 * @param step
	 *            何スロット先まで記録するか。succeed() のステップ数と合わせる
	 */
	public void record(int step) {
		for (int i = 0; i < step; i++) {
			int slot = fOffset + i;
			WTimelineSellerRecord rec;
			if (!fRecords.containsKey(slot)) {
				rec = new WTimelineSellerRecord();
			} else {
				rec = fRecords.get(slot);
			}
			// daysForward + 1 日目から maxDays 日目まで供給あり
			int volume = (slot <= fSlotsForward || fMaxSlot < slot) ? 0 : fVolume;
			double contractVolume = volume - fStockList.get(i);
			double contractPrice = fSalesList.get(i);
			rec.setTotalOrderVolume(volume);
			rec.setTotalOrderPrice(volume * fUnitPrice);
			rec.setTotalContractVolume(contractVolume);
			rec.setTotalContractPrice(contractPrice);
			rec.setTotalContractProfit(contractPrice - contractVolume * fUnitPrice);
			fRecords.put(slot, rec);
		}
	}

	/**
	 * 記録ファイルに書き出す
	 * 
	 * @param traceFilename
	 *            ファイル名
	 * @throws Exception
	 */
	public void writeTo(String traceFilename) throws Exception {
		PrintWriter pw = new PrintWriter(new FileOutputStream(path(traceFilename), false)); // 上書き
		pw.println("Timeslot,OrderVolume,ContractVolume,Utilization,OrderPrice,ContractPrice,Profit,Welfare");
		long sumOrderVolume = 0;
		double sumContractVolume = 0.0;
		long sumOrderPrice = 0;
		double sumContractPrice = 0.0;
		double sumProfit = 0.0;
		double sumWelfare = 0.0;
		for (Iterator<Entry<Integer, WTimelineSellerRecord>> itr = fRecords.entrySet().iterator(); itr
			.hasNext();) {
			Entry<Integer, WTimelineSellerRecord> entry = itr.next();
			long slot = entry.getKey();
			WTimelineSellerRecord rec = entry.getValue();
			long orderVolume = rec.getTotalOrderVolume();
			double contractVolume = rec.getTotalContractVolume();
			double utilization = contractVolume / orderVolume;
			long orderPrice = rec.getTotalOrderPrice();
			double contractPrice = rec.getTotalContractPrice();
			double profit = rec.getTotalContractProfit(); // 売れた分の余剰
			double welfare = contractPrice - orderPrice; // 全体の余剰
			pw.println(String.format("%d,%d,%.2f,%.4f,%d,%.2f,%.2f,%.2f", slot, orderVolume,
				contractVolume, utilization, orderPrice, contractPrice, profit, welfare));
			sumOrderVolume += orderVolume;
			sumContractVolume += contractVolume;
			sumOrderPrice += orderPrice;
			sumContractPrice += contractPrice;
			sumProfit += profit;
			sumWelfare += welfare;
		}
		double avgUtilization = sumContractVolume / sumOrderVolume;
		pw.println(String.format("Total,%d,%.2f,%.4f,%d,%.2f,%.2f,%.2f", sumOrderVolume,
			sumContractVolume, avgUtilization, sumOrderPrice, sumContractPrice, sumProfit,
			sumWelfare));
		pw.close();
	}

	/**
	 * 売約済とする
	 */
	public void contractAt(int slot, double volume, double price) {
		int i = slot - fOffset;
		assert (i >= 0) : "Timeline.contractAt(): specified slot " + slot + " is in the past!";
		if (volume == 0) {
			return;
		}
		fStockList.set(i, fStockList.get(i) - volume);
		fSalesList.set(i, fSalesList.get(i) + price);
	}

	/**
	 * 注文中とする
	 */
	public void orderAt(int slot, long volume) {
		int i = slot - fOffset;
		assert (i >= 0) : "Timeline.orderAt(): specified slot " + slot + " is in the past!";
		if (volume == 0) {
			return;
		}
		fAskingList.set(i, fAskingList.get(i) + volume);
	}

	/**
	 * 注文をキャンセルする
	 */
	public void cancelAt(int slot, long volume) {
		orderAt(slot, -volume);
	}

	/**
	 * 売約済でも注文中でもない在庫を取得
	 */
	public double getFreeStockAt(int slot) {
		int i = slot - fOffset;
		assert (i >= 0) : "Timeline.getFreeStockAt(): specified slot " + slot + " is in the past!";
		return fStockList.get(i) - fAskingList.get(i);
	}

	/**
	 * 在庫を文字列として取得
	 * 
	 * @return "商品名:始端時刻,在庫(0),...,在庫(slots-1),"
	 */
	public String encodeStock() {
		StringBuilder result = new StringBuilder();
		result.append(String.format("%s:%d,", fGood, fOffset));
		for (int i = 0; i < fStockList.size(); i++) {
			result.append(String.format("%.2f,", fStockList.get(i)));
		}
		return result.toString();
	}

	/**
	 * 始端時刻を取得
	 * 
	 * @return 始端時刻
	 */
	public int getOffset() {
		return fOffset;
	}

	/**
	 * 原価を取得
	 * 
	 * @return 原価
	 */
	public int getUnitPrice() {
		return fUnitPrice;
	}

	/**
	 * パス区切り文字を環境に合わせる
	 */
	private String path(String slashSeparatedPath) {
		return slashSeparatedPath.replace('/', File.separatorChar);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(String.format("\tTimeslot\tSales\tStock\tAsking"));
		for (int i = 0; i < fStockList.size(); i++) {
			result.append(String.format("\n\t%5d:\t%8.2f\t%8.2f\t%8d", i + fOffset,
				fSalesList.get(i), fStockList.get(i), fAskingList.get(i)));
		}
		return result.toString();
	}

	/*
	 * public static void main(String[] args) { WTimelineSeller timeline = new
	 * WTimelineSeller("serviceA", 7, 0, 100.0, 10000.0); HashMap<Integer, Double> volumes;
	 * HashMap<Integer, Double> prices;
	 *
	 * volumes = new HashMap(); volumes.put(2, 10.0); volumes.put(3, 20.0); volumes.put(4, 30.0);
	 * System.out.println(volumes); timeline.reserveResource(volumes); prices = new HashMap();
	 * prices.put(2, -100.0); prices.put(3, -200.0); prices.put(4, -300.0);
	 * System.out.println(prices); timeline.reserveBudget(prices);
	 * System.out.println(timeline.toString()); timeline.succeed(1);
	 *
	 * volumes = new HashMap(); volumes.put(3, 1.0); volumes.put(4, 2.0); volumes.put(5, 3.0);
	 * System.out.println(volumes); timeline.reserveResource(volumes); prices = new HashMap();
	 * prices.put(3, -10.0); prices.put(4, -20.0); prices.put(5, -30.0); System.out.println(prices);
	 * timeline.reserveBudget(prices); System.out.println(timeline.toString()); timeline.succeed(1);
	 *
	 * volumes = new HashMap(); volumes.put(4, 0.1); volumes.put(5, 0.2); volumes.put(6, 0.3);
	 * System.out.println(volumes); timeline.reserveResource(volumes); prices = new HashMap();
	 * prices.put(4, -1.0); prices.put(5, -2.0); prices.put(6, -3.0); System.out.println(prices);
	 * timeline.reserveBudget(prices); System.out.println(timeline.toString()); timeline.succeed(1);
	 * }
	 */
}
