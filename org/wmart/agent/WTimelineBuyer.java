package org.wmart.agent;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * �^�C�����C����\���N���X (�����G�[�W�F���g�p)
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WTimelineBuyer {

	/** 1���̃X���b�g�� */
	private int fSessionsPerDay;
	/** �����s��ŉ��X���b�g�O�ɔ������邩 */
	private int fOffsetSpot;
	/** ���� => �����\�� */
	private LinkedHashMap<String, WTimelineBuyerEvent> fSchedule;
	/** �s�ԍ� => ���v */
	private LinkedHashMap<Integer, WTimelineBuyerDemand> fDemands;
	/** ����ID => �����[ */
	private LinkedHashMap<Long, WOrderForm> fOrderForms;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param sessionsPerDay
	 *            1���̃X���b�g��
	 * @param offsetSpot
	 *            �����s��ŉ��X���b�g�O�ɔ������邩
	 */
	public WTimelineBuyer(int sessionsPerDay, int offsetSpot) {
		fSessionsPerDay = sessionsPerDay;
		fOffsetSpot = offsetSpot;
		fSchedule = new LinkedHashMap<String, WTimelineBuyerEvent>();
		fDemands = new LinkedHashMap<Integer, WTimelineBuyerDemand>();
		fOrderForms = new LinkedHashMap<Long, WOrderForm>();
	}

	/**
	 * ���v�t�@�C����ǂݍ���
	 * 
	 * @param demandFilename
	 *            �t�@�C����
	 * @param auctioneer
	 *            �s�ꖼ
	 * @param splitSlot
	 *            ���v��1�X���b�g���ɕ�����
	 * @param splitGood
	 *            ���v��1���i���ɕ�����
	 * @throws Exception
	 */
	public void readFrom(String demandFilename, String auctioneer, boolean splitSlot,
		boolean splitGood) throws Exception {
		int dateColumn;
		if (auctioneer.equals("forward")) {
			dateColumn = 0;
		} else if (auctioneer.equals("spot")) {
			dateColumn = 1;
		} else {
			throw new Exception("Auctioneer name must be either 'forward' or 'spot'.");
		}
		BufferedReader br = new BufferedReader(new FileReader(path(demandFilename)));
		String line = null;
		int lineno = 0;
		while ((line = br.readLine()) != null) {
			lineno++;
			if (!line.startsWith("#")) {
				String[] sp1 = line.split(",");
				String dateSession = sp1[dateColumn];
				int price = Integer.parseInt(sp1[2]);
				String spec = sp1[3];
				String hint = sp1[4];
				// �����\�胊�X�g�Ǝ��v���X�g�ɒ����[��ǉ�
				WTimelineBuyerEvent event = new WTimelineBuyerEvent();
				WTimelineBuyerDemand demand = new WTimelineBuyerDemand();
				if (splitGood) {
					event.addPerGood(auctioneer, hint, spec);
				} else {
					event.add(auctioneer, price, spec);
				}
				if (splitSlot) {
					HashMap<String, Double> uprices = getUnitPriceMap(hint, spec);
					addPerSlot(uprices, event, demand); // �n���� event �͎̂Ă���
				} else {
					add(dateSession, event, demand);
				}
				demand.setTotalOrderPrice(price);
				fDemands.put(lineno, demand);
			}
		}
		br.close();
	}

	/**
	 * �L�^�t�@�C���ɏ����o��
	 * 
	 * @param traceFilename
	 *            �t�@�C����
	 * @throws Exception
	 */
	public void writeTo(String traceFilename) throws Exception {
		PrintWriter pw = new PrintWriter(new FileOutputStream(path(traceFilename), false)); // �㏑��
		pw.println("LineNo,OrderVolume,ContractVolume,Fulfilled,OrderPrice,ContractPrice,Profit,Welfare");
		int countAll = 0;
		int countFulfilled = 0;
		int sumOrderVolume = 0;
		int sumContractVolume = 0;
		long sumOrderPrice = 0;
		double sumContractPrice = 0.0;
		double sumProfit = 0.0;
		double sumWelfare = 0.0;
		for (Iterator<Entry<Integer, WTimelineBuyerDemand>> itr = fDemands.entrySet().iterator(); itr
			.hasNext();) {
			Entry<Integer, WTimelineBuyerDemand> entry = itr.next();
			int lineno = entry.getKey();
			WTimelineBuyerDemand demand = entry.getValue();
			int orderVolume = demand.getTotalOrderVolume();
			int contractVolume = demand.getTotalContractVolume();
			int orderPrice = demand.getTotalOrderPrice();
			double contractPrice = demand.getTotalContractPrice();
			double profit; // ���������̗]��
			double welfare; // �S�̗̂]��
			if (demand.isFulfilled()) {
				// �S���������ꍇ
				profit = demand.getTotalContractProfit();
				welfare = (double) orderPrice - contractPrice;
				pw.println(String.format("%d,%d,%d,1,%d,%.2f,%.2f,%.2f", lineno, orderVolume,
					contractVolume, orderPrice, contractPrice, profit, welfare));
				countFulfilled++;
			} else {
				// �S������Ȃ������ꍇ
				profit = 0.0;
				welfare = -contractPrice;
				pw.println(String.format("%d,%d,%d,0,%d,%.2f,%.2f,%.2f", lineno, orderVolume,
					contractVolume, orderPrice, contractPrice, profit, welfare));
			}
			countAll++;
			sumOrderVolume += orderVolume;
			sumContractVolume += contractVolume;
			sumOrderPrice += orderPrice;
			sumContractPrice += contractPrice;
			sumProfit += profit;
			sumWelfare += welfare;
		}
		double fulfilled = (double) countFulfilled / countAll;
		pw.println(String.format("Total,%d,%d,%.4f,%d,%.2f,%.2f,%.2f", sumOrderVolume,
			sumContractVolume, fulfilled, sumOrderPrice, sumContractPrice, sumProfit, sumWelfare));
		pw.close();
	}

	/**
	 * �����\���1�X���b�g���̒����[�ɕ����ē��߂��Ƃ̃}�b�v�Ǝ��v�ɒǉ�
	 * 
	 * @param uprices
	 *            ���i�ʒP���}�b�v
	 * @param event
	 *            �����\��
	 * @param demand
	 *            ���v
	 */
	private void addPerSlot(HashMap<String, Double> uprices, WTimelineBuyerEvent event,
		WTimelineBuyerDemand demand) {
		ArrayList<WOrderForm> forms = event.getOrderForms();
		for (Iterator<WOrderForm> itr = forms.iterator(); itr.hasNext();) {
			WOrderForm form = itr.next();
			String auctioneer = form.getAuctioneerName();
			String spec = form.getSpec();
			String[] sp1 = spec.split(";");
			int n = sp1.length;
			String[] goods = new String[n];
			int[] volumes = new int[n];
			int[] atimes = new int[n];
			int[] dtimes = new int[n];
			int[] lengths = new int[n];
			int t1 = Integer.MAX_VALUE; // �S�̂̍ő�����
			int t2 = Integer.MIN_VALUE; // �S�̂̍Œx����
			for (int i = 0; i < n; i++) {
				String sp3[] = sp1[i].split(":");
				goods[i] = sp3[0];
				volumes[i] = Integer.parseInt(sp3[1]);
				atimes[i] = Integer.parseInt(sp3[2]);
				dtimes[i] = Integer.parseInt(sp3[3]);
				lengths[i] = Integer.parseInt(sp3[4]);
				if (t1 > atimes[i]) {
					t1 = atimes[i];
				}
				if (t2 < dtimes[i]) {
					t2 = dtimes[i];
				}
			}
			// 1�X���b�g���Ƃɂ܂Ƃ߂�
			for (int t = t1; t <= t2; t++) {
				int t0 = t - fOffsetSpot; // ��������
				int date = (t0 - 1) / fSessionsPerDay + 1;
				int session = (t0 - 1) % fSessionsPerDay + 1;
				String dateSession = date + "/" + session;
				double sum1 = 0.0;
				String spec1 = "";
				for (int i = 0; i < n; i++) {
					// ���Ԃɗ]�T������ꍇ�͑O�l�߂Ƃ���
					if (atimes[i] <= t && t < atimes[i] + lengths[i]) {
						sum1 += uprices.get(goods[i]) * volumes[i];
						spec1 += String.format("%s:%d:%d:%d:%d;", goods[i], volumes[i], t, t, 1);
					}
				}
				if (!spec1.isEmpty()) {
					int price1 = Math.round((float) sum1);
					WTimelineBuyerEvent event1 = new WTimelineBuyerEvent();
					event1.add(auctioneer, price1, spec1);
					add(dateSession, event1, demand);
				}
			}

		}
	}

	/**
	 * �����\��Ɋ܂܂�钍���[����߂��Ƃ̃}�b�v�Ǝ��v�ɒǉ�
	 * 
	 * @param dateSession
	 *            ����
	 * @param event
	 *            �����\��
	 * @param demand
	 *            ���v
	 */
	private void add(String dateSession, WTimelineBuyerEvent event, WTimelineBuyerDemand demand) {
		if (!fSchedule.containsKey(dateSession)) {
			fSchedule.put(dateSession, new WTimelineBuyerEvent());
		}
		fSchedule.get(dateSession).addAll(event);
		demand.addAll(event.getOrderForms());
	}

	/**
	 * ���i�ʒP���}�b�v���擾
	 * 
	 * @param hint
	 *            ���i�ʒP��
	 * @param spec
	 *            ���v����
	 * @return
	 */
	private HashMap<String, Double> getUnitPriceMap(String hint, String spec) {
		HashMap<String, Double> uprices = new HashMap<String, Double>();
		String[] sp1 = spec.split(";");
		String[] sp2 = hint.split(";");
		assert (sp1.length == sp2.length) : "Unmatch number of goods in demand file";
		for (int i = 0; i < sp1.length; i++) {
			String sp3[] = sp1[i].split(":");
			String good = sp3[0];
			double uprice = Double.parseDouble(sp2[i]);
			uprices.put(good, uprice);
		}
		return uprices;
	}

	/**
	 * �w�肳�ꂽ���߂̔����\����擾
	 * 
	 * @param date
	 *            ��
	 * @param session
	 *            ��
	 * @return �����\��
	 */
	public WTimelineBuyerEvent at(int date, int session) {
		String dateSession = date + "/" + session;
		if (!fSchedule.containsKey(dateSession)) {
			return new WTimelineBuyerEvent();
		}
		return fSchedule.get(dateSession);
	}

	/**
	 * ������o�^
	 * 
	 * @param form
	 *            �����[
	 */
	public void putOrder(WOrderForm form) {
		fOrderForms.put(form.getOrderId(), form);
	}

	/**
	 * �o�^���ꂽ�������폜
	 * 
	 * @param orderId
	 */
	public void removeOrder(long orderId) {
		fOrderForms.remove(orderId);
	}

	/**
	 * �o�^���ꂽ�������擾
	 * 
	 * @param orderId
	 *            ����ID
	 * @return �����[
	 */
	public WOrderForm getOrder(long orderId) {
		return fOrderForms.get(orderId);
	}

	/**
	 * �o�^���ꂽ�����ɖ�艿�i��ݒ�
	 * 
	 * @param orderId
	 *            ����ID
	 * @param contractPrice
	 *            ��艿�i
	 */
	public void setContractPrice(long orderId, double contractPrice) {
		fOrderForms.get(orderId).setContractPrice(contractPrice);
	}

	/**
	 * ���v�}�b�v���擾
	 * 
	 * @return ���v�}�b�v
	 */
	public LinkedHashMap<Integer, WTimelineBuyerDemand> getDemands() {
		return fDemands;
	}

	/**
	 * �p�X��؂蕶�������ɍ��킹��
	 */
	private String path(String slashSeparatedPath) {
		return slashSeparatedPath.replace('/', File.separatorChar);
	}

	@Override
	public String toString() {
		return fSchedule.toString();
	}

	/**
	 * �f�o�b�O�p
	 * 
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.err
				.println("usage: java WTimelineBuyer demandFilename auctioneer splitSlot splitGood");
			System.err.println("  auctioneer = forward/spot");
			System.err.println("  splitSlot = true/false");
			System.err.println("  splitGood = true/false");
			System.exit(1);
		}
		boolean splitSlot = Boolean.parseBoolean(args[2]);
		boolean splitGood = Boolean.parseBoolean(args[3]);
		try {
			WTimelineBuyer tl = new WTimelineBuyer(24, 2);
			tl.readFrom(args[0], args[1], splitSlot, splitGood);
			System.out.println(tl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
