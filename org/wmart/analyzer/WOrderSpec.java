/*
 * WMart
 * Copyright (C) 2009 Ikki Fujiwara, NII <ikki@nii.ac.jp>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or any later version.
 * See the GNU General Public License for more details.
 *
 * This code comes WITHOUT ANY WARRANTY
 */
package org.wmart.analyzer;

/**
 * 注文に含まれる個々の商品を扱うクラス
 * 
 * @author Ikki Fujiwara @ NII
 * 
 */
public class WOrderSpec {

	/** 商品名 */
	private String fName = "";
	/** 注文数量 */
	private int fOrderVolume = 0;
	/** 最早開始時刻 */
	private int fArrivalTime = 0;
	/** 最遅終了時刻 */
	private int fDeadlineTime = 0;
	/** 所要時間 */
	private int fTotalTime = 0;

	// 数値型の要素の名前リスト
	public static String[] ATTRIBUTES = { "Volume", "ArrivalTime", "DeadlineTime", "TotalTime" };

	/**
	 * コンストラクタ
	 */
	public WOrderSpec() {
		this("", 0, 0, 0, 0);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param name
	 * @param orderVolume
	 * @param arrivalTime
	 * @param deadlineTime
	 * @param totalTime
	 */
	public WOrderSpec(String name, int orderVolume, int arrivalTime, int deadlineTime, int totalTime) {
		fName = name;
		fOrderVolume = orderVolume;
		fArrivalTime = arrivalTime;
		fDeadlineTime = deadlineTime;
		fTotalTime = totalTime;
	}

	/**
	 * コンストラクタ (明細を文字列で設定する)
	 * 
	 * @param encodedSpec
	 *            "商品名:希望数量:最早時刻:最遅時刻:延べ時間"
	 */
	public WOrderSpec(String encodedSpec) {
		decode(encodedSpec);
	}

	/**
	 * 明細を文字列で設定する
	 * 
	 * @param encodedSpec
	 */
	public void decode(String encodedSpec) {
		String[] specElements = encodedSpec.split(":");
		fName = specElements[0];
		fOrderVolume = Integer.parseInt(specElements[1]);
		fArrivalTime = Integer.parseInt(specElements[2]);
		fDeadlineTime = Integer.parseInt(specElements[3]);
		fTotalTime = Integer.parseInt(specElements[4]);
	}

	/**
	 * 明細を文字列で取得する
	 * 
	 * @return "商品名:希望数量:最早時刻:最遅時刻:延べ時間"
	 */
	public String encode() {
		return String.format("%s:%s:%s:%s:%s", fName, fOrderVolume, fArrivalTime, fDeadlineTime,
			fTotalTime);
	}

	/**
	 * 属性名を文字列で指定して属性値を取得する
	 * 
	 * @param key
	 *            属性名
	 * @return 属性値
	 */
	public int get(String key) {
		if (key.equalsIgnoreCase("Volume")) {
			return fOrderVolume;
		} else if (key.equalsIgnoreCase("ArrivalTime")) {
			return fArrivalTime;
		} else if (key.equalsIgnoreCase("DeadlineTime")) {
			return fDeadlineTime;
		} else if (key.equalsIgnoreCase("TotalTime")) {
			return fTotalTime;
		} else {
			throw new IllegalArgumentException(key);
		}
	}

	/**
	 * 時刻を 1 ステップ進める (最早時刻と最遅時刻を現在へ近づける)
	 * 
	 * @return 過去になったら false
	 */
	public boolean nextStep() {
		fArrivalTime--;
		fDeadlineTime--;
		return (fArrivalTime >= 0 && fDeadlineTime >= 0);
	}

	/**
	 * 商品名を取得します。
	 * 
	 * @return 商品名
	 */
	public String getName() {
		return fName;
	}

	/**
	 * 注文数量を取得します。
	 * 
	 * @return 注文数量
	 */
	public int getOrderVolume() {
		return fOrderVolume;
	}

	/**
	 * 最早開始時刻を取得します。
	 * 
	 * @return 最早開始時刻
	 */
	public int getArrivalTime() {
		return fArrivalTime;
	}

	/**
	 * 最遅終了時刻を取得します。
	 * 
	 * @return 最遅終了時刻
	 */
	public int getDeadlineTime() {
		return fDeadlineTime;
	}

	/**
	 * 所要時間を取得します。
	 * 
	 * @return 所要時間
	 */
	public int getTotalTime() {
		return fTotalTime;
	}

	/**
	 * 内容が同じか?
	 */
	public boolean equals(WOrderSpec another) {
		return (fName.equals(another.getName()) && fOrderVolume == another.getOrderVolume()
			&& fArrivalTime == another.getArrivalTime()
			&& fDeadlineTime == another.getDeadlineTime() && fTotalTime == another.getTotalTime());
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return encode();
	}

	/**
	 * デバッグ用
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		WOrderSpec g = new WOrderSpec("CPU", 4, 2, 4, 3);
		System.out.println(g.toString());
	}

}
