package org.wmart.logger;

import java.io.*;
import java.util.*;

import org.wmart.core.*;

/**
 * �P�ߕ��̖����̃��O�������N���X�ł��D
 * 
 * @author Ikki Fujiwara, NII
 * @author ����@��
 */
public class WExecutionLog {

	/** ���t�������L�[ */
	public static final String INT_DATE = "INT_DATE";

	/** �߂������L�[ */
	public static final String INT_SESSION = "INT_SESSION";

	/** ���O�C�������������߂̃L�[ */
	public static final String STRING_LOGIN_NAME = "STRING_LOGIN_NAME";

	/** ���ID�������L�[ */
	public static final String LONG_CONTRACT_ID = "LONG_CONTRACT_ID";

	/** ��艿�i�������L�[ */
	public static final String DOUBLE_CONTRACT_PRICE = "DOUBLE_CONTRACT_PRICE";

	/** ��萔�ʂ������L�[ */
	public static final String DOUBLE_CONTRACT_VOLUME = "DOUBLE_CONTRACT_VOLUME";

	/** ����ID�������L�[ */
	public static final String LONG_ORDER_ID = "LONG_ORDER_ID";

	/** �� */
	private int fDate;

	/** �� */
	private int fSession;

	/** ��1�񕪂̖���� */
	private ArrayList fExecutionArray;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param date
	 *            ��
	 * @param session
	 *            ��
	 */
	public WExecutionLog(int date, int session) {
		fDate = date;
		fSession = session;
		fExecutionArray = new ArrayList();
	}

	/**
	 * ���ID�̔�r��
	 * 
	 * @author ����@��
	 */
	private class UContractIDComparator implements Comparator {

		/**
		 * ��r
		 * 
		 * @param o1
		 *            �����P
		 * @param o2
		 *            �����Q
		 * @return o1��ID��o2��ID���傫�����+1�C���������-1
		 */
		public int compare(Object o1, Object o2) {
			HashMap info1 = (HashMap) o1;
			HashMap info2 = (HashMap) o2;
			long id1 = ((Long) info1.get(WExecutionLog.LONG_CONTRACT_ID)).longValue();
			long id2 = ((Long) info2.get(WExecutionLog.LONG_CONTRACT_ID)).longValue();
			if (id1 > id2) {
				return 1;
			} else {
				return -1;
			}
		}

	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param date
	 *            ��
	 * @param session
	 *            ��
	 * @param om
	 *            WOrderManager
	 */
	public WExecutionLog(int date, int session, WOrderManager om) {
		this(date, session);
		Enumeration orderArrays = om.getOrderArrays();
		while (orderArrays.hasMoreElements()) {
			WOrderArray orderArray = (WOrderArray) orderArrays.nextElement();
			Enumeration uncontractedOrders = orderArray.getUncontractedOrders().elements();
			while (uncontractedOrders.hasMoreElements()) {
				WOrder o = (WOrder) uncontractedOrders.nextElement();
				checkAndAppendInfo(o);
			}
			Enumeration contractedOrders = orderArray.getContractedOrders().elements();
			while (contractedOrders.hasMoreElements()) {
				WOrder o = (WOrder) contractedOrders.nextElement();
				checkAndAppendInfo(o);
			}
		}
		Collections.sort(fExecutionArray, new UContractIDComparator());
	}

	/**
	 * �����̒������������̒�����C���̖���񃍃O���������Ɛ߂� �Y����������������āCfExecutionArray�ɏ���ǉ�����D
	 * 
	 * @param o
	 *            ����
	 */
	private void checkAndAppendInfo(WOrder o) {
		long orderID = o.getOrderID();
		String userName = o.getUserName();
		Enumeration infos = o.getContracts().elements();
		while (infos.hasMoreElements()) {
			WContract info = (WContract) infos.nextElement();
			if (info.getDate() == fDate && info.getSession() == fSession) {
				HashMap hash = makeExecutionInfo(info.getDate(), info.getSession(), userName, info.getContractID(),
					info.getPrice(), info.getVolume(), orderID);
				fExecutionArray.add(hash);
			}
		}
	}

	/**
	 * �����𐶐����ĕԂ��D
	 * 
	 * @param date
	 *            ��
	 * @param session
	 *            ��
	 * @param userName
	 *            ���[�U��
	 * @param contractID
	 *            ���ID
	 * @param contractPrice
	 *            ��艿�i
	 * @param contractVolume
	 *            ��萔��
	 * @param orderID
	 *            ����ID
	 * @return �����
	 */
	private HashMap makeExecutionInfo(int date, int session, String userName, long contractID, double contractPrice,
		double contractVolume, long orderID) {
		HashMap info = new HashMap();
		info.put(WExecutionLog.INT_DATE, new Integer(date));
		info.put(WExecutionLog.INT_SESSION, new Integer(session));
		info.put(WExecutionLog.STRING_LOGIN_NAME, userName);
		info.put(WExecutionLog.LONG_CONTRACT_ID, new Long(contractID));
		info.put(WExecutionLog.DOUBLE_CONTRACT_PRICE, new Double(contractPrice));
		info.put(WExecutionLog.DOUBLE_CONTRACT_VOLUME, new Double(contractVolume));
		info.put(WExecutionLog.LONG_ORDER_ID, new Long(orderID));
		return info;
	}

	/**
	 * �o�̓X�g���[���Ƀf�[�^�������o���D
	 * 
	 * @param pw
	 *            �o�̓X�g���[��
	 * @throws IOException
	 */
	public void writeTo(PrintWriter pw) throws IOException {
		pw.println("ContractDate,ContractSession,LoginName,ContractID" + ",ContractPrice,ContractVolume,OrderID");
		Iterator itr = fExecutionArray.iterator();
		while (itr.hasNext()) {
			HashMap info = (HashMap) itr.next();
			pw.print(info.get(WExecutionLog.INT_DATE).toString() + ",");
			pw.print(info.get(WExecutionLog.INT_SESSION).toString() + ",");
			pw.print(info.get(WExecutionLog.STRING_LOGIN_NAME).toString() + ",");
			pw.print(info.get(WExecutionLog.LONG_CONTRACT_ID).toString() + ",");
			pw.print(info.get(WExecutionLog.DOUBLE_CONTRACT_PRICE).toString() + ",");
			pw.print(info.get(WExecutionLog.DOUBLE_CONTRACT_VOLUME).toString() + ",");
			pw.println(info.get(WExecutionLog.LONG_ORDER_ID).toString());
		}
	}

	/**
	 * ���̓X�g���[������f�[�^��ǂݍ��ށD
	 * 
	 * @param br
	 *            ���̓X�g���[��
	 * @throws IOException
	 */
	public void readFrom(BufferedReader br) throws IOException {
		fExecutionArray.clear();
		br.readLine(); // skip the header
		String line = null;
		while ((line = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line, ",");
			int date = Integer.parseInt(st.nextToken());
			int session = Integer.parseInt(st.nextToken());
			String userName = st.nextToken();
			long contractID = Long.parseLong(st.nextToken());
			long contractPrice = Long.parseLong(st.nextToken());
			long contractVolume = Long.parseLong(st.nextToken());
			long orderID = Long.parseLong(st.nextToken());
			HashMap info = makeExecutionInfo(date, session, userName, contractID, contractPrice, contractVolume,
				orderID);
			fExecutionArray.add(info);
		}
	}

}
