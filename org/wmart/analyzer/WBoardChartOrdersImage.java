/**
 * 
 */
package org.wmart.analyzer;

import java.io.*;
import java.text.*;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.awt.image.*;
import javax.imageio.*;

import org.umart.logger.UCsvUtility;
import org.wmart.core.WBoard;
import org.wmart.core.WOrder;
import org.wmart.core.WOrderManager;
import org.wmart.core.WOrderSpec;

/**
 * 競争率チャートを画像で描く
 * 
 * @author ikki
 * 
 */
public class WBoardChartOrdersImage {

	/**
	 * @param args
	 *            = 注文ファイル名 (1 個以上)
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: java WBoardChartOrdersImage OrdersFile ...");
			System.exit(1);
		}

		WBoardChartOrdersImage cm = new WBoardChartOrdersImage();

		// 引数で指定されたすべてのファイルを順に処理する
		for (int i = 0; i < args.length; i++) {
			String inputFile = args[i];
			String outputFile = inputFile.replaceAll("[a-zA-Z]*(\\d+)\\.txt$", "chart$1.png");

			WBoard board = new WBoard();
			try {
				BufferedReader br = new BufferedReader(new FileReader(inputFile));
				cm.readFrom(br, board);
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(5);
			}
			// Long drivetimeMS = System.currentTimeMillis();
			// board.makeContracts(1, 1);
			// drivetimeMS = System.currentTimeMillis() - drivetimeMS;
			try {
				BufferedImage bi = new BufferedImage(800, 120, BufferedImage.TYPE_INT_RGB);
				cm.writeTo(bi, board);
				ImageIO.write(bi, "png", new File(outputFile));
				bi.flush();
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
	private void readFrom(BufferedReader br, WBoard board) throws IOException,
		ParseException {
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
					o = orderManager.createOrder(lineNo, agent, WOrder.SELL, price, 1, 1);
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
					o = orderManager.createOrder(lineNo, agent, WOrder.BUY, price, 1, 1);
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
	 * 結果をバッファに書き出す
	 * 
	 * @param bw
	 * @param board
	 * @throws IOException
	 * @throws ParseException
	 */
	private void writeTo(BufferedImage bi, WBoard board) throws IOException, ParseException {
		Graphics2D gr = bi.createGraphics();
		int nslots = 0;
		int ngoods = 0;
		TreeSet<String> goods = new TreeSet<String>();
		HashMap<String, TreeMap<Integer, Double>> sup = new HashMap();
		HashMap<String, TreeMap<Integer, Double>> dem = new HashMap();

		for (Iterator itr1 = board.getOrderArray(); itr1.hasNext();) {
			WOrder o = (WOrder) itr1.next();
			for (Iterator<WOrderSpec> itr2 = o.getOrderSpecMap().values().iterator(); itr2
				.hasNext();) {
				WOrderSpec os = itr2.next();
				String good = os.getName();
				double volume = os.getOrderVolume();
				int atime = os.getArrivalTime();
				int dtime = os.getDeadlineTime();
				int ttime = os.getTotalTime();
				if (nslots < dtime) {
					nslots = dtime;
				}
				goods.add(good);
				if (!sup.containsKey(good)) {
					sup.put(good, new TreeMap<Integer, Double>());
				}
				if (!dem.containsKey(good)) {
					dem.put(good, new TreeMap<Integer, Double>());
				}
				for (int t = atime; t <= dtime; t++) {
					TreeMap<Integer, Double> arr = null;
					if (o.getSellBuy() == WOrder.SELL) {
						arr = sup.get(good);
					} else if (o.getSellBuy() == WOrder.BUY) {
						arr = dem.get(good);
					}
					if (!arr.containsKey(t)) {
						arr.put(t, volume);
					} else {
						arr.put(t, arr.get(t) + volume);
					}
				}
			}
		}
		ngoods = goods.size();

		int leftmargin = 60;
		int topmargin = 20;
		int xscale = (bi.getWidth() - leftmargin) / (nslots + 1);
		int yscale = (bi.getHeight() - topmargin) / (ngoods);
		int fontsize = 12;
		Font font = new Font("Arial", 0, fontsize);
		gr.setFont(font);
		gr.setBackground(new Color(255, 255, 255));
		gr.clearRect(0, 0, bi.getWidth(), bi.getHeight());
		for (int t = 0; t <= nslots + 1; t++) {
			if (t % ((nslots + 1) / 4) == 0) {
				gr.setColor(new Color(0, 0, 0));
				gr.drawString("" + t, t * xscale + leftmargin, fontsize + 2);
			}
		}
		int g = 0;
		for (Iterator<String> itr3 = goods.iterator(); itr3.hasNext();) {
			String good = itr3.next();
			gr.setColor(new Color(0, 0, 0));
			gr.drawString(good, 0, g * yscale + topmargin + fontsize - 2);
			TreeMap<Integer, Double> suparr = sup.get(good);
			TreeMap<Integer, Double> demarr = dem.get(good);
			for (int t = 0; t <= nslots; t++) {
				Rectangle2D.Double rect = new Rectangle2D.Double(t * xscale + leftmargin, g
					* yscale + topmargin, xscale, yscale);
				if (!suparr.containsKey(t)) {
					suparr.put(t, 0.0);
				}
				if (!demarr.containsKey(t)) {
					demarr.put(t, 0.0);
				}
				double ratio = -1;
				if (suparr.get(t) > 0.0) {
					ratio = demarr.get(t) / suparr.get(t);
				}
				if (ratio >= 2.0) {
					gr.setColor(new Color(255, 0, 0));
				} else if (ratio > 1.0) {
					gr.setColor(new Color(255, 128, 0));
				} else if (ratio == 1.0) {
					gr.setColor(new Color(255, 255, 0));
				} else if (ratio > 0.5) {
					gr.setColor(new Color(0, 255, 0));
				} else if (ratio > 0.0) {
					gr.setColor(new Color(0, 255, 255));
				} else if (ratio == 0.0) {
					gr.setColor(new Color(0, 0, 255));
				} else {
					gr.setColor(new Color(255, 255, 255));
				}
				gr.fill(rect);
				// bw.write("<td bgcolor='" + color + "'>" + decimal1.format(ratio));
			}
			g++;
		}
		// bw.write("\n</tbody></table>\n<hr />");
		// bw.newLine();

		gr.dispose();
	}
}
