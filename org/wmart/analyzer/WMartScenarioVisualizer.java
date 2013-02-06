package org.wmart.analyzer;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.Map.Entry;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.xy.*;

/**
 * �V�i���I�r�W���A���C�U�B1��̃V�i���I�ɑ΂��A�����ʂ̑����𒍕��P�ʁE���i�P�ʂŉ�������B
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WMartScenarioVisualizer {

	private TreeMap<String, NumberAxis> fGoodAxes = new TreeMap<String, NumberAxis>();

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: java WMartScenarioVisualizer ResourceLogFile ...");
			System.exit(1);
		}
		// �����Ŏw�肳�ꂽ���ׂẴt�@�C�������ɏ�������
		for (int i = 0; i < args.length; i++) {
			String logfilename = args[i];
			String imgfilename = logfilename.replaceAll("\\.\\w+$", ".png");
			WMartScenarioVisualizer vis = new WMartScenarioVisualizer();
			try {
				vis.visualize(logfilename, imgfilename, 4000, 600);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(5);
			}
		}
	}

	/**
	 * ���O�t�@�C������^�C���`���[�g�𐶐�
	 * 
	 * @param logFilename
	 *            ���̓��O�t�@�C����
	 * @param imgFilename
	 *            �o�͉摜�t�@�C����
	 * @param width
	 *            �o�͉摜��
	 * @param height
	 *            �o�͉摜����
	 * @throws IOException
	 */
	public void visualize(String logFilename, String imgFilename, int width, int height)
		throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path(logFilename)));
		CombinedDomainXYPlot cmbplot = createCmbPlot(br);
		br.close();
		StandardChartTheme theme = (StandardChartTheme) StandardChartTheme.createLegacyTheme();
		ChartFactory.setChartTheme(theme);
		JFreeChart chart = new JFreeChart("Supply & Demand", JFreeChart.DEFAULT_TITLE_FONT,
			cmbplot, true);
		ChartUtilities.saveChartAsPNG(new File(path(imgFilename)), chart, width, height);
	}

	/**
	 * �����v���b�g�𐶐�
	 * 
	 * @param br
	 *            ���̓X�g���[��
	 * @return �����v���b�g
	 */
	private CombinedDomainXYPlot createCmbPlot(BufferedReader br) {
		NumberAxis xAxis = new NumberAxis("step");
		CombinedDomainXYPlot cmbplot = new CombinedDomainXYPlot(xAxis);
		TreeMap<String, WTimelineSellerArray> sellerTimelines = new TreeMap<String, WTimelineSellerArray>();
		TreeMap<String, WTimelineBuyerArray> buyerTimelines = new TreeMap<String, WTimelineBuyerArray>();
		String line = null;
		int lineNo = 1;
		try {
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#")) {
					String[] sp1 = line.split(" ");
					String[] sp2 = sp1[0].split("/");
					int date = Integer.parseInt(sp2[0]);
					int session = Integer.parseInt(sp2[1]);
					String username = sp1[1];
					String sellbuy = sp1[2];
					String orderId = sp1[3];
					String spec = sp1[4];
					if (sellbuy.equalsIgnoreCase("selling")) {
						if (!sellerTimelines.containsKey(username)) {
							sellerTimelines.put(username, new WTimelineSellerArray(username));
						}
						sellerTimelines.get(username).overread(spec);
					} else if (sellbuy.equalsIgnoreCase("buying")) {
						if (!buyerTimelines.containsKey(username)) {
							buyerTimelines.put(username, new WTimelineBuyerArray(username));
						}
						buyerTimelines.get(username).append(spec, false);
					} else if (sellbuy.equalsIgnoreCase("bought")) {
						if (!buyerTimelines.containsKey(username)) {
							buyerTimelines.put(username, new WTimelineBuyerArray(username));
						}
						buyerTimelines.get(username).overread(spec, true);
					} else {
						throw new ParseException(
							"Error: The second word must be 'selling', 'buying' or 'bought'",
							lineNo);
					}
					++lineNo;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Iterator<Entry<String, WTimelineSellerArray>> itr = sellerTimelines.entrySet()
			.iterator(); itr.hasNext();) {
			Entry<String, WTimelineSellerArray> entry = itr.next();
			addSellerPlot(cmbplot, entry.getValue());
		}
		for (Iterator<Entry<String, WTimelineBuyerArray>> itr = buyerTimelines.entrySet()
			.iterator(); itr.hasNext();) {
			Entry<String, WTimelineBuyerArray> entry = itr.next();
			addBuyerPlot(cmbplot, entry.getValue());
		}
		return cmbplot;
	}

	/**
	 * �����v���b�g�ɔ��蒍���̃v���b�g��ǉ�
	 * 
	 * @param cmbplot
	 * @param timeline
	 */
	public void addSellerPlot(CombinedDomainXYPlot cmbplot, WTimelineSellerArray timeline) {
		CategoryTableXYDataset data = new CategoryTableXYDataset();
		ArrayList<Double> values = timeline.getValues();
		for (int i = 0; i < values.size(); i++) {
			data.add(i, values.get(i), timeline.getName());
		}
		NumberAxis xAxis = new NumberAxis("step");
		NumberAxis yAxis = new NumberAxis(timeline.getName());
		StackedXYBarRenderer renderer = new StackedXYBarRenderer();
		renderer.setShadowVisible(false);
		XYPlot plot = new XYPlot(data, xAxis, yAxis, renderer);
		cmbplot.add(plot);
	}

	/**
	 * �����v���b�g�ɔ��������̃v���b�g��ǉ�
	 * 
	 * @param cmbplot
	 * @param timeline
	 */
	public void addBuyerPlot(CombinedDomainXYPlot cmbplot, WTimelineBuyerArray timeline) {
		LinkedList<LinkedList<WOrderBuyer>> orderList = timeline.getOrderList();
		for (Iterator<LinkedList<WOrderBuyer>> itr1 = orderList.iterator(); itr1.hasNext();) {
			LinkedList<WOrderBuyer> sublist = itr1.next();
			CategoryTableXYDataset data = new CategoryTableXYDataset();
			for (Iterator<WOrderBuyer> itr2 = sublist.iterator(); itr2.hasNext();) {
				WOrderBuyer order = itr2.next();
				boolean contracted = order.isContracted();
				for (Iterator<WOrderSpec> itr3 = order.iterator(); itr3.hasNext();) {
					WOrderSpec spec = itr3.next();
					int a = spec.getArrivalTime();
					int d = spec.getDeadlineTime();
					int o = spec.getTotalTime();
					for (int t = a; t < a + o; t++) {
						data.add(t, spec.getOrderVolume(), spec.getName());
					}
					for (int t = a + o; t <= d; t++) {
						data.add(t, spec.getOrderVolume(), spec.getName() + "~");
					}
					// TODO
					data.setAutoWidth(false);
				}
			}
			NumberAxis xAxis = new NumberAxis("step");
			NumberAxis yAxis = new NumberAxis(timeline.getName());
			StackedXYBarRenderer renderer = new StackedXYBarRenderer();
			renderer.setShadowVisible(false);
			XYPlot plot = new XYPlot(data, xAxis, yAxis, renderer);
			cmbplot.add(plot);
		}
	}

	/**
	 * �p�X��؂蕶�������ɍ��킹��
	 */
	private String path(String slashSeparatedPath) {
		return slashSeparatedPath.replace('/', File.separatorChar);
	}

}
