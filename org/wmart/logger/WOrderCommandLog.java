package org.wmart.logger;

import java.io.*;
import java.util.*;

import org.wmart.core.*;

/**
 * �P�ߕ��̑S�Ă̒������̃��O����舵���N���X�ł��D
 * 
 * @author ����@��
 * @author Ikki Fujiwara, NII
 */
public class WOrderCommandLog {

	/** ���������L�[ */
	public static final String INT_DATE = "INT_DATE";

	/** �߂������L�[ */
	public static final String INT_SESSION = "INT_SESSION";

	/** ��������(������)�������L�[ */
	public static final String STRING_REAL_TIME = "STRING_REAL_TIME";

	/** ���O�C�������������߂̃L�[ */
	public static final String STRING_LOGIN_NAME = "STRING_LOGIN_NAME";

	/** �R�}���h���������L�[ */
	public static final String STRING_COMMAND_NAME = "STRING_COMMAND_NAME";

	/** ����ID�������L�[ */
	public static final String LONG_ORDER_ID = "LONG_ORDER_ID";

	/** �������������L�[ (�s�g�p) */
	public static final String STRING_BRAND_NAME = "STRING_BRAND_NAME";

	/** �s�ꖼ�������L�[ */
	public static final String STRING_AUCTIONEER_NAME = "STRING_AUCTIONEER_NAME";

	/** �����敪�������L�[ */
	public static final String INT_SELL_BUY = "INT_SELL_BUY";

	/** ���s�w�l�敪�������L�[ */
	public static final String INT_MARKET_LIMIT = "INT_MARKET_LIMIT";

	/** �������i�������L�[ */
	public static final String LONG_ORDER_PRICE = "LONG_ORDER_PRICE";

	/** �������ׂ������L�[ */
	public static final String STRING_ORDER_SPEC = "STRING_ORDER_SPEC";

	/** ����񂹊��ԓ��̒������\�[�g���邽�߂ɗ��p����闐�����������߂̃L�[ */
	public static final String INT_RANDOM_NUMBER = "INT_RANDOM_NUMBER";

	/** �������̔z�� */
	private ArrayList fOrderCommandArray;

	/**
	 * �R���X�g���N�^
	 * 
	 */
	public WOrderCommandLog() {
		fOrderCommandArray = new ArrayList();
	}

	/**
	 * ��������o�^����D
	 * 
	 * @param o
	 *            ����
	 */
	public synchronized void registerOrderRequest(WOrder o) {
		HashMap hash = new HashMap();
		hash.put(WOrderCommandLog.INT_DATE, new Integer(o.getDate()));
		hash.put(WOrderCommandLog.INT_SESSION, new Integer(o.getSession()));
		hash.put(WOrderCommandLog.STRING_REAL_TIME, o.getTime());
		hash.put(WOrderCommandLog.STRING_LOGIN_NAME, o.getUserName());
		hash.put(WOrderCommandLog.STRING_COMMAND_NAME, "Request");
		hash.put(WOrderCommandLog.LONG_ORDER_ID, new Long(o.getOrderID()));
		hash.put(WOrderCommandLog.STRING_BRAND_NAME, o.getBrandName());
		hash.put(WOrderCommandLog.STRING_AUCTIONEER_NAME, o.getAuctioneerName());
		hash.put(WOrderCommandLog.INT_SELL_BUY, new Integer(o.getSellBuy()));
		hash.put(WOrderCommandLog.INT_MARKET_LIMIT, new Integer(o.getMarketLimit()));
		hash.put(WOrderCommandLog.LONG_ORDER_PRICE, new Double(o.getOrderPrice()));
		hash.put(WOrderCommandLog.STRING_ORDER_SPEC, o.getOrderSpec());
		hash.put(WOrderCommandLog.INT_RANDOM_NUMBER, new Integer(o.getRandomNumber()));
		fOrderCommandArray.add(hash);
	}

	/**
	 * �L�����Z������o�^����
	 * 
	 * @param date
	 *            ��
	 * @param session
	 *            ��
	 * @param userName
	 *            ���[�U��
	 * @param orderID
	 *            ����ID
	 * @param cancelVolume
	 *            �L�����Z������
	 */
	public synchronized void registerOrderCancel(int date, int session, String userName, long orderID, long cancelVolume) {
		HashMap hash = new HashMap();
		hash.put(WOrderCommandLog.INT_DATE, new Integer(date));
		hash.put(WOrderCommandLog.INT_SESSION, new Integer(session));
		hash.put(WOrderCommandLog.STRING_REAL_TIME, new Date());
		hash.put(WOrderCommandLog.STRING_LOGIN_NAME, userName);
		hash.put(WOrderCommandLog.STRING_COMMAND_NAME, "Cancel");
		hash.put(WOrderCommandLog.LONG_ORDER_ID, new Long(orderID));
		// hash.put(WOrderCommandLog.LONG_ORDER_VOLUME, new Long(cancelVolume)); //
		// TODO:�L�����Z���͑S�ʂɌ��� (09/9/7)
		fOrderCommandArray.add(hash);
	}

	/**
	 * �������̃C�e���[�^��Ԃ��D
	 * 
	 * @return �������̃C�e���[�^
	 */
	public Iterator getOrderCommands() {
		return fOrderCommandArray.iterator();
	}

	/**
	 * �S�Ă̒��������N���A����D
	 * 
	 */
	public void clear() {
		fOrderCommandArray.clear();
	}

	/**
	 * �o�̓X�g���[���փf�[�^�������o���D
	 * 
	 * @param pw
	 *            �o�̓X�g���[��
	 * @throws IOException
	 */
	public synchronized void writeTo(PrintWriter pw) throws IOException {
		pw.println("Date,Session,RealTime,LoginName,Command,OrderID,BrandName,NewRepay,"
			+ "SellBuy,MarketLimit,Price,Spec,RandomNumber");
		Iterator itr = fOrderCommandArray.iterator();
		while (itr.hasNext()) {
			HashMap hash = (HashMap) itr.next();
			pw.print(hash.get(WOrderCommandLog.INT_DATE).toString() + ",");
			pw.print(hash.get(WOrderCommandLog.INT_SESSION).toString() + ",");
			pw.print(hash.get(WOrderCommandLog.STRING_REAL_TIME) + ",");
			pw.print(hash.get(WOrderCommandLog.STRING_LOGIN_NAME).toString() + ",");
			String cmdName = (String) hash.get(WOrderCommandLog.STRING_COMMAND_NAME);
			pw.print(cmdName + ",");
			pw.print(hash.get(WOrderCommandLog.LONG_ORDER_ID).toString() + ",");
			if (cmdName.equals("Request")) {
				pw.print(hash.get(WOrderCommandLog.STRING_BRAND_NAME).toString() + ",");
				pw.print(hash.get(WOrderCommandLog.STRING_AUCTIONEER_NAME).toString() + ",");
				pw.print(hash.get(WOrderCommandLog.INT_SELL_BUY).toString() + ",");
				pw.print(hash.get(WOrderCommandLog.INT_MARKET_LIMIT).toString() + ",");
				pw.print(hash.get(WOrderCommandLog.LONG_ORDER_PRICE).toString() + ",");
				pw.print(hash.get(WOrderCommandLog.STRING_ORDER_SPEC).toString() + ",");
				pw.println(hash.get(WOrderCommandLog.INT_RANDOM_NUMBER).toString());
			} else {
				pw.print(",,,,,");
				// pw.println("-" + hash.get(WOrderCommandLog.LONG_ORDER_VOLUME).toString() + ",");
				pw.println(","); // TODO: �b��
			}
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
		br.readLine(); // skip the header
		String line = null;
		while ((line = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line, ",");
			HashMap hash = new HashMap();
			hash.put(WOrderCommandLog.INT_DATE, Integer.valueOf(st.nextToken()));
			hash.put(WOrderCommandLog.INT_SESSION, Integer.valueOf(st.nextToken()));
			hash.put(WOrderCommandLog.STRING_REAL_TIME, st.nextToken());
			hash.put(WOrderCommandLog.STRING_LOGIN_NAME, Integer.valueOf(st.nextToken()));
			String cmdName = st.nextToken();
			hash.put(WOrderCommandLog.STRING_COMMAND_NAME, cmdName);
			hash.put(WOrderCommandLog.LONG_ORDER_ID, Long.valueOf(st.nextToken()));
			if (cmdName.equals("Request")) {
				hash.put(WOrderCommandLog.STRING_BRAND_NAME, st.nextToken());
				hash.put(WOrderCommandLog.STRING_AUCTIONEER_NAME, st.nextToken());
				hash.put(WOrderCommandLog.INT_SELL_BUY, Integer.valueOf(st.nextToken()));
				hash.put(WOrderCommandLog.INT_MARKET_LIMIT, Integer.valueOf(st.nextToken()));
				hash.put(WOrderCommandLog.LONG_ORDER_PRICE, Long.valueOf(st.nextToken()));
				hash.put(WOrderCommandLog.STRING_ORDER_SPEC, st.nextToken());
				hash.put(WOrderCommandLog.INT_RANDOM_NUMBER, Integer.valueOf(st.nextToken()));
			}
			fOrderCommandArray.add(hash);
		}
	}

}
