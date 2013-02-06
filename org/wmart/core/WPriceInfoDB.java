package org.wmart.core;

import java.io.*;
import java.util.*;

/**
 * 価格系列データベース
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WPriceInfoDB {

	/** 識別名 */
	private String fName = "";
	/** タイムスロット数 */
	private int fSlots;
	/** 最大ステップ数 */
	private int fMaxSteps;

	/** UPriceInfoElementオブジェクトを格納するためのベクタ */
	private Vector<WPriceInfo> fPriceInfoArray;
	/** 現在のUPriceInfoElementを指し示すインデックス。 価格が決定しているのはfPriceInfoArray[fCurrentPtr - 1]までで あることに注意。 */
	private int fCurrentPtr = 0;

	/**
	 * コンストラクタ
	 */
	public WPriceInfoDB(int slots, int maxsteps) {
		fSlots = slots;
		fMaxSteps = maxsteps + 1; // 最終ステップの次まで
		fPriceInfoArray = new Vector<WPriceInfo>();
	}

	/**
	 * 初期化
	 * 
	 */
	public void initialize(String name) {
		fName = name;
		for (int t = 0; t < fMaxSteps; t++) {
			WPriceInfo pi = new WPriceInfo(fSlots);
			fPriceInfoArray.addElement(pi);
		}
		// if (fMaxSteps > fPriceInfoArray.size()) {
		// System.err.println("maxSteps=" + fMaxSteps);
		// System.err.println("fPriceInfoArray.size()=" + fPriceInfoArray.size());
		// System.err.println("Error: initialize error in WPriceInfoDB");
		// System.exit(-1);
		// }
		fCurrentPtr = 0;
		// fDate = 1;
		// fSession = 1;
	}

	/**
	 * 現在のステップの価格表を取得
	 * 
	 * @return 追加された WPriceInfo
	 */
	public WPriceInfo getCurrentPriceInfo() {
		WPriceInfo pi = fPriceInfoArray.elementAt(fCurrentPtr);
		// assert (pi.getDate() == fDate && pi.getSession() == fSession);
		// pi.setDate(fDate);
		// pi.setSession(fSession);
		return pi;
	}

	/**
	 * 直前のステップの価格表を取得
	 * 
	 * @return 追加された WPriceInfo
	 */
	public WPriceInfo getLatestPriceInfo() {
		if (fCurrentPtr < 1) {
			return null;
		}
		WPriceInfo pi = fPriceInfoArray.elementAt(fCurrentPtr - 1);
		return pi;
	}

	/**
	 * 過去steps分の価格表系列を取得
	 * 
	 * @param good
	 *            商品名
	 * @param steps
	 *            必要なステップ数
	 * @return 価格表系列．要素0が直近の価格表である．
	 */
	public String[] getPrices(int steps) {
		if (fCurrentPtr < steps) {
			// System.out.println("getPrices Error!: adjust arraysize " + steps + " -> " +
			// fCurrentPtr);
			steps = fCurrentPtr;
		}
		if (fCurrentPtr < 0) {
			return null;
		}
		String result[] = new String[steps];
		for (int i = 0; i < steps; i++) {
			WPriceInfo pi = fPriceInfoArray.elementAt(fCurrentPtr - i - 1);
			result[i] = pi.encode();
		}
		return result;
	}

	/**
	 * すべてのステップの価格表系列を出力ストリームに書き出す．
	 * "date/session goodA:offset,10,11,12,...;goodB:offset,20,21,22,..."
	 * 
	 * @param pw
	 *            出力ストリーム
	 * @throws IOException
	 */
	public void writeTo(PrintWriter pw) throws IOException {
		Enumeration<WPriceInfo> e = fPriceInfoArray.elements();
		while (e.hasMoreElements()) {
			WPriceInfo pi = e.nextElement();
			pw.printf("%d/%d %s", pi.getDate(), pi.getSession(), pi.encode());
		}
	}

	/**
	 * 現在までのステップの価格表系列を出力ストリームへ書き出す．
	 * "date/session goodA:offset,10,11,12,...;goodB:offset,20,21,22,..."
	 * 
	 * @param pw
	 *            出力ストリーム
	 * @throws IOException
	 */
	public void writePriceInfoBeforeCurrentPtr(PrintWriter pw) throws IOException {
		for (int i = 0; i < fCurrentPtr; ++i) {
			WPriceInfo pi = fPriceInfoArray.get(i);
			pw.printf("%d/%d %s", pi.getDate(), pi.getSession(), pi.encode());
		}
	}

	/**
	 * 直前のステップの全商品の価格表を1行1商品で出力ストリームへ書き出す．
	 * "date/session goodA:offset,10,11,12,...\ndate/session goodB:offset,20,21,22,..."
	 * 
	 * @param pw
	 *            出力ストリーム
	 * @throws IOException
	 */
	public void writeLatestPriceInfo(PrintWriter pw) throws IOException {
		if (fCurrentPtr < 1) {
			return;
		}
		WPriceInfo pi = fPriceInfoArray.get(fCurrentPtr - 1);
		for (Iterator<String> itr = pi.getGoods().iterator(); itr.hasNext();) {
			String good = itr.next();
			writeLatestPriceInfo(good, pw);
		}
	}

	/**
	 * 直前のステップの指定商品の価格リストを出力ストリームへ書き出す． "date/session,goodA:offset,10,11,12,..."
	 * 
	 * @param good
	 *            商品名
	 * @param pw
	 *            出力ストリーム
	 * @throws IOException
	 */
	public void writeLatestPriceInfo(String good, PrintWriter pw) throws IOException {
		if (fCurrentPtr < 1) {
			return;
		}
		WPriceInfo pi = fPriceInfoArray.get(fCurrentPtr - 1);
		pw.printf("%d/%d,%s:%d,%s\n", pi.getDate(), pi.getSession(), good, pi.getOffset(),
			pi.csv(good));
	}

	/**
	 * 約定価格表と約定数量表を追加する
	 * 
	 * @param price
	 */
	public void addPriceInfo(int date, int session, int offset, WOutcomeTable totalPriceTable,
		WOutcomeTable totalVolumeTable) {
		WPriceInfo pi = fPriceInfoArray.elementAt(fCurrentPtr);
		pi.setDate(date);
		pi.setSession(session);
		pi.setTotalPriceTable(totalPriceTable, offset);
		pi.setTotalVolumeTable(totalVolumeTable, offset);
	}

	/**
	 * ポインタを 1 ステップ進める
	 * 
	 */
	public void incrementPointer() {
		if (fCurrentPtr > fMaxSteps) {
			System.err.println("Error: initializerPtr error in WPriceInfoDB");
			System.exit(-1);
		}
		++fCurrentPtr;
		// ++fSession;
		// if (fSession > fSessionsPerDay) {
		// ++fDate;
		// fSession = 1;
		// }
	}

}
