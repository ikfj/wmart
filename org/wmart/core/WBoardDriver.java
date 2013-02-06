/**
 *
 */
package org.wmart.core;

import java.io.*;
import java.text.*;
import java.util.*;

import org.umart.logger.*;

/**
 * テストドライバ
 * 
 * @author ikki
 * 
 */
public class WBoardDriver {

	// private static Logger log = Logger.getLogger(WBoardTestDriverForward.class);

	/**
	 * @param args
	 *            = 注文ファイル名 (1 個以上)
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: java WBoardTestDriverSpot OrdersFile ...");
			System.exit(1);
		}

		WBoardDriver dr = new WBoardDriver();

		// 引数で指定されたすべてのファイルを順に処理する
		for (int i = 0; i < args.length; i++) {
			String inputFile = args[i];
			String outputFile = inputFile.replaceAll("[a-zA-Z]*(\\d+\\.txt)$", "result$1");

			WBoard board = new WBoard(720);
			try {
				BufferedReader br = new BufferedReader(new FileReader(inputFile));
				dr.readFrom(br, board);
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(5);
			}
			Long drivetimeMS = System.currentTimeMillis();
			board.makeContracts(1, 1, 1);
			drivetimeMS = System.currentTimeMillis() - drivetimeMS;
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
				dr.writeTo(bw, board);
				bw.write("drivetime=" + drivetimeMS.doubleValue() / 1000);
				bw.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(5);
			}
		}
	}

	/**
	 * 注文をファイルから読み込む
	 * 
	 * @param br
	 * @param board
	 * @throws IOException
	 * @throws ParseException
	 */
	private void readFrom(BufferedReader br, WBoard board) throws IOException, ParseException {
		WOrderManager orderManager = new WOrderManager();
		WOrder o = null;

		String line = null;
		int lineNo = 1;
		try {
			while ((line = br.readLine()) != null) {
				ArrayList info = UCsvUtility.split(line);
				Iterator itr = info.iterator();
				String agent = (String) itr.next();
				String sellbuy = (String) itr.next();
				long price = Long.parseLong((String) itr.next());
				if (sellbuy.equalsIgnoreCase("sell")) {
					o = orderManager.createOrder(lineNo, agent, "", "forward", WOrder.SELL, WOrder.LIMIT, price, "", 1,
						1);
					while (itr.hasNext()) {
						String good = (String) itr.next();
						int volume = Integer.parseInt((String) itr.next());
						int atime = Integer.parseInt((String) itr.next());
						int dtime = Integer.parseInt((String) itr.next());
						int ttime = Integer.parseInt((String) itr.next());
						o.addOrderSpec(good, volume, atime, dtime, ttime);
					}
					board.appendOrder(o);
				} else if (sellbuy.equalsIgnoreCase("buy")) {
					o = orderManager.createOrder(lineNo, agent, "", "forward", WOrder.BUY, WOrder.LIMIT, price, "", 1,
						1);
					while (itr.hasNext()) {
						String good = (String) itr.next();
						int volume = Integer.parseInt((String) itr.next());
						int atime = Integer.parseInt((String) itr.next());
						int dtime = Integer.parseInt((String) itr.next());
						int ttime = Integer.parseInt((String) itr.next());
						o.addOrderSpec(good, volume, atime, dtime, ttime);
					}
					board.appendOrder(o);
				} else {
					throw new ParseException("Error: Second word must be 'sell' or 'buy'", lineNo);
				}
				++lineNo;
			}
		} catch (NoSuchElementException nsee) {
			throw new ParseException("Error: NoSuchElement", lineNo);
		} catch (NumberFormatException nfe) {
			throw new ParseException("Error: NumberFormat", lineNo);
		}
	}

	/**
	 * 結果をファイルに書き出す
	 * 
	 * @param bw
	 * @param board
	 * @throws IOException
	 * @throws ParseException
	 */
	private void writeTo(BufferedWriter bw, WBoard board) throws IOException, ParseException {
		for (Iterator itr = board.getOrderArray(); itr.hasNext();) {
			WOrder o = (WOrder) itr.next();
			int lineNo = o.getUserID();
			String agent = o.getUserName();
			double bid = o.getOrderPrice();
			double price = o.getContractPrice();
			if (o.getSellBuy() == WOrder.SELL) {
				double volume = o.getContractVolume();
				bw.write(agent + ",sell," + bid + "," + price + "," + volume);
			} else if (o.getSellBuy() == WOrder.BUY) {
				double volume = o.getContractVolume();
				bw.write(agent + ",buy," + bid + "," + price + "," + volume);
			} else {
				throw new ParseException("Error: order must have 'WOrder.SELL' or 'WOrder.BUY'", lineNo);
			}
			bw.newLine();
		}
		bw.write("runtime=" + board.getRuntime());
		bw.newLine();
	}
}
