package org.wmart.agent;

/**
 * タイムライン中の1時点における供給状況を表すクラス
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WTimelineSellerRecord {

	/** 合計注文数量 */
	long fTotalOrderVolume;
	/** 合計注文価格 */
	long fTotalOrderPrice;
	/** 合計約定数量 */
	double fTotalContractVolume;
	/** 合計約定価格 */
	double fTotalContractPrice;
	/** 合計約定利益 (未約定分は無視) */
	double fTotalContractProfit;

	/**
	 * コンストラクタ
	 */
	public WTimelineSellerRecord() {
		fTotalOrderVolume = 0;
		fTotalOrderPrice = 0;
		fTotalContractVolume = 0.0;
		fTotalContractPrice = 0.0;
		fTotalContractProfit = 0.0;
	}

	/**
	 * 合計注文数量を取得
	 * 
	 * @return 合計注文数量
	 */
	public long getTotalOrderVolume() {
		return fTotalOrderVolume;
	}

	/**
	 * 合計注文数量を設定
	 * 
	 * @param fTotalOrderVolume
	 *            合計注文数量
	 */
	public void setTotalOrderVolume(long fTotalOrderVolume) {
		this.fTotalOrderVolume = fTotalOrderVolume;
	}

	/**
	 * 合計注文価格を取得
	 * 
	 * @return 合計注文価格
	 */
	public long getTotalOrderPrice() {
		return fTotalOrderPrice;
	}

	/**
	 * 合計注文価格を設定
	 * 
	 * @param fTotalOrderPrice
	 *            合計注文価格
	 */
	public void setTotalOrderPrice(long fTotalOrderPrice) {
		this.fTotalOrderPrice = fTotalOrderPrice;
	}

	/**
	 * 合計約定数量を取得
	 * 
	 * @return 合計約定数量
	 */
	public double getTotalContractVolume() {
		return fTotalContractVolume;
	}

	/**
	 * 合計約定数量を設定
	 * 
	 * @param fTotalContractVolume
	 *            合計約定数量
	 */
	public void setTotalContractVolume(double fTotalContractVolume) {
		this.fTotalContractVolume = fTotalContractVolume;
	}

	/**
	 * 合計約定価格を取得
	 * 
	 * @return 合計約定価格
	 */
	public double getTotalContractPrice() {
		return fTotalContractPrice;
	}

	/**
	 * 合計約定価格を設定
	 * 
	 * @param fTotalContractPrice
	 *            合計約定価格
	 */
	public void setTotalContractPrice(double fTotalContractPrice) {
		this.fTotalContractPrice = fTotalContractPrice;
	}

	/**
	 * 合計約定利益 (未約定分は無視)を取得
	 * 
	 * @return 合計約定利益 (未約定分は無視)
	 */
	public double getTotalContractProfit() {
		return fTotalContractProfit;
	}

	/**
	 * 合計約定利益 (未約定分は無視)を設定
	 * 
	 * @param fTotalContractProfit
	 *            合計約定利益 (未約定分は無視)
	 */
	public void setTotalContractProfit(double fTotalContractProfit) {
		this.fTotalContractProfit = fTotalContractProfit;
	}
}
