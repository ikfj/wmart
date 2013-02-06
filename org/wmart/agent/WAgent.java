package org.wmart.agent;

import java.io.*;
import java.util.*;

import org.wmart.cmdCore.*;
import org.wmart.core.*;
import org.wmart.logger.*;

/**
 * �G�[�W�F���g�̐e�N���X
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WAgent extends WBaseAgent {

	/** ���O�o�͐�f�B���N�g���� */
	protected String fLogDir;

	public WAgent(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

	/**
	 * @see org.umart.strategyCore.UBaseAgent#doActions()
	 */
	@Override
	public void doActions(int day, int session, int serverState, int maxDate, int sessionsPerDay,
		int slotsForward, int maxLength) {
		// �}�V���G�[�W�F���g�� AFTER_HOURS �Ɋ�������
		if (serverState != WServerStatusMaster.AFTER_HOURS) {
			return;
		}
	}

	/**
	 * ��������
	 * 
	 * @param form
	 *            �����[
	 * @return ����ID -1:�����[���s�� -2:�������ʂ��G���[
	 */
	protected long orderRequest(WOrderForm form) {
		WCOrderRequestCore cmd = (WCOrderRequestCore) fUmcp.getCommand(WCOrderRequestCore.CMD_NAME);
		int sellBuy;
		if (form.getBuySell() == WOrderForm.BUY) {
			sellBuy = WOrder.BUY;
		} else if (form.getBuySell() == WOrderForm.SELL) {
			sellBuy = WOrder.SELL;
		} else {
			return -1;
		}
		if (form.getOrderPrice() <= 0) {
			return -1;
		}
		cmd.setArguments("", form.getAuctioneerName(), sellBuy, WOrder.LIMIT, form.getOrderPrice(),
			form.getSpec());
		WCommandStatus status = cmd.doIt();
		if (status.getStatus() == false) {
			System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID);
			System.err.println(status.getErrorMessage() + " in WAgent.orderRequest");
			return -2;
		}
		HashMap<String, String> results = cmd.getResults();
		return Long.valueOf(results.get(WCOrderRequestCore.LONG_ORDER_ID));
	}

	/**
	 * ������Ԃ��擾����
	 */
	protected ArrayList<HashMap<String, String>> getOrderStatus(String auctioneerName) {
		WCOrderStatusCore cmd = (WCOrderStatusCore) fUmcp.getCommand(WCOrderStatusCore.CMD_NAME);
		cmd.setArguments(auctioneerName);
		WCommandStatus status = cmd.doIt();
		if (status.getStatus() == false) {
			System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID);
			System.err.println(status.getErrorMessage() + " in WAgent.getOrderStatus");
			System.exit(5);
		}
		return cmd.getResults();
	}

	/**
	 * ���������t�@�C���ɏ����o��
	 */
	protected void writeResourceLog(WResourceLog rlog) {
		// String filepath = String.format(("%s/%s/%s.csv"), fLogDir, WMart.RESOURCE_LOG_DIR,
		// fLoginName); // �ʂ̃t�@�C��
		String filepath = String.format(("%s/%s.csv"), fLogDir, WMart.RESOURCE_LOG_DIR); // �S���ꏏ�̃t�@�C��
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(filepath, true)); // �ǋL
			rlog.writeTo(pw);
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
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
		for (int i = 0; i < args.length; ++i) {
			StringTokenizer st = new StringTokenizer(args[i], "= ");
			String key = st.nextToken();
			String value = st.nextToken();
			if (key.equalsIgnoreCase("logdir")) {
				fLogDir = value.replace("?", ":");
				println("LogDir set to " + fLogDir);
			}
		}
	}
}
