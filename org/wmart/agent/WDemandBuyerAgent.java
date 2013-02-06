package org.wmart.agent;

import java.io.File;
import java.util.*;

import org.apache.log4j.Logger;
import org.wmart.cmdCore.WCOrderStatusCore;
import org.wmart.core.*;
import org.wmart.logger.WResourceLog;

/**
 * �����G�[�W�F���g�B���v�t�@�C����ǂ�
 * 
 * @author Ikki Fujiwara, NII
 */
public abstract class WDemandBuyerAgent extends WAgent {

	/** �s�ꖼ */
	protected String fAuctioneer;
	/** ���v��1�X���b�g���ɕ����� */
	protected boolean fSplitSlot;
	/** ���v��1���i���ɕ����� */
	protected boolean fSplitGood;
	/** ���v�t�@�C���� */
	protected String fDemandFilename;
	/** �L�^�t�@�C���� */
	protected String fTraceFilename;
	/** �^�C�����C�� */
	protected WTimelineBuyer fTimeline;

	private static Logger log = Logger.getLogger(WDemandBuyerAgent.class);

	/**
	 * �R���X�g���N�^
	 */
	public WDemandBuyerAgent(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

	@Override
	public void doActions(int date, int session, int serverState, int maxDays, int sessionsPerDay,
		int sessionsForward, int maxLength) {
		// �^�C�����C����������
		if (fTimeline == null) {
			prepareTimeline(sessionsPerDay, 2);
		}
		WResourceLog resource;
		switch (serverState) {
		case WServerStatusMaster.AFTER_HOURS:
			if (fAuctioneer.equals("forward") && session != sessionsPerDay) {
				break;
			}
			if (fAuctioneer.equals("forward")) {
				log.info(String.format("�������敨������ %d/%d", date, session));
			} else if (fAuctioneer.equals("spot")) {
				log.info(String.format("���������������� %d/%d", date, session));
			}
			resource = new WResourceLog(date, session, fLoginName);
			updateStatus(resource);
			// �����\��ɂ��ƂÂ��Ĕ���
			WTimelineBuyerEvent event = fTimeline.at(date, session);
			for (Iterator<WOrderForm> itr = event.iterator(); itr.hasNext();) {
				WOrderForm form = itr.next();
				long orderId = orderRequest(form);
				assert (orderId >= 0);
				form.setOrderId(orderId);
				fTimeline.putOrder(form);
				resource.addBuying("#" + orderId, form.getSpec());
				log.info(String.format("%s $%d => #%d", form.getSpec(), form.getOrderPrice(),
					form.getOrderId()));
			}
			writeResourceLog(resource);
			break;
		case WServerStatusMaster.SHUTDOWN:
			resource = new WResourceLog(date, session, fLoginName);
			updateStatus(resource);
			traceTimeline();
			writeResourceLog(resource);
			break;
		}
	}

	/**
	 * �^�C�����C��������
	 * 
	 * @param sessionsPerDay
	 *            1���̃Z�b�V������
	 * @param offsetSpot
	 *            �����s��ŉ��X���b�g�O�ɔ������邩
	 */
	private void prepareTimeline(int sessionsPerDay, int offsetSpot) {
		fTimeline = new WTimelineBuyer(sessionsPerDay, offsetSpot);
		try {
			fTimeline.readFrom(fDemandFilename, fAuctioneer, fSplitSlot, fSplitGood);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * �^�C�����C�����L�^
	 */
	private void traceTimeline() {
		fTraceFilename = fLogDir + File.separator + "trace_buyer_" + fLoginName + ".csv";
		try {
			fTimeline.writeTo(fTraceFilename);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���󋵂��擾���A�^�C�����C���Ǝ��������X�V����
	 */
	private void updateStatus(WResourceLog resource) {
		ArrayList<HashMap<String, String>> arr = getOrderStatus(fAuctioneer);
		for (Iterator<HashMap<String, String>> itr = arr.iterator(); itr.hasNext();) {
			HashMap<String, String> os = itr.next();
			int sellBuy = Integer.valueOf(os.get(WCOrderStatusCore.INT_SELL_BUY));
			assert (sellBuy == WOrder.BUY);
			long orderId = Long.valueOf(os.get(WCOrderStatusCore.LONG_ORDER_ID));
			long orderPrice = Long.valueOf(os.get(WCOrderStatusCore.LONG_ORDER_PRICE));
			double contractPrice = Double.valueOf(os.get(WCOrderStatusCore.DOUBLE_CONTRACT_PRICE));
			if (orderPrice < 0 || contractPrice == 0.0) {
				// �L�����Z���ς� or �����
				String orderSpec = os.get(WCOrderStatusCore.STRING_ORDER_SPEC);
				if (orderPrice < 0) {
					fTimeline.removeOrder(orderId);
					resource.addCancelBuying("#" + orderId, orderSpec);
					log.info(String.format("<canceled> #%d %s", orderId, orderSpec));
				} else {
					resource.addBuying("#" + orderId, orderSpec);
					log.info(String
						.format("<remaining> #%d %s $%d", orderId, orderSpec, orderPrice));
				}
			} else {
				// ���ς�
				String orderSpec = os.get(WCOrderStatusCore.STRING_ORDER_SPEC);
				fTimeline.setContractPrice(orderId, contractPrice);
				resource.addBought("#" + orderId, orderSpec);
				log.info(String.format("<contracted> #%d %s $%d $%.2f", orderId, orderSpec,
					orderPrice, contractPrice));
			}
		}
	}

	/**
	 * �G�[�W�F���g�̃V�X�e���p�����[�^��ݒ肷��D
	 * 
	 * @param args
	 *            �V�X�e���p�����[�^
	 */
	@Override
	public void setParameters(String[] args) {
		super.setParameters(args);
		int demandIndex = 0;
		for (int i = 0; i < args.length; ++i) {
			StringTokenizer st = new StringTokenizer(args[i], "= ");
			String key = st.nextToken();
			String value = st.nextToken();
			if (key.equalsIgnoreCase("demandfile")) {
				fDemandFilename = value;
				println("DemandFilename set to " + fDemandFilename);
			} else if (key.equalsIgnoreCase("demandindex")) {
				demandIndex = Integer.valueOf(value);
				println("DemandIndex set to " + demandIndex);
			}
		}
		String[] demandFilenames = fDemandFilename.split(";");
		fDemandFilename = demandFilenames[demandIndex];
	}
}
