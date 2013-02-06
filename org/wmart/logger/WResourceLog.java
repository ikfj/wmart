package org.wmart.logger;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * �G�[�W�F���g���ƁE�Z�b�V�������Ƃ̎������̃��O�������N���X
 * 
 * @author Ikki Fujiwara, NII
 */
public class WResourceLog {

	/** ���t */
	private int fDate;
	/** �� */
	private int fSession;
	/** ���O�C���� */
	private String fLoginName;
	/** ���蒍����� */
	private LinkedHashMap<String, String> fSelling;
	/** ����������� */
	private LinkedHashMap<String, String> fBuying;
	/** ��������� */
	private LinkedHashMap<String, String> fBought;
	/** ���������� */
	private LinkedHashMap<String, String> fCancelBuying;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param date
	 *            ���t
	 * @param session
	 *            ��
	 * @param loginName
	 *            ���O�C����
	 */
	public WResourceLog(int date, int session, String loginName) {
		fDate = date;
		fSession = session;
		fLoginName = loginName;
		fSelling = new LinkedHashMap<String, String>();
		fBuying = new LinkedHashMap<String, String>();
		fBought = new LinkedHashMap<String, String>();
		fCancelBuying = new LinkedHashMap<String, String>();
	}

	/**
	 * ���蒍������ǉ�����
	 * 
	 * @param orderIds
	 *            ����ID��\��������
	 * @param spec
	 *            "���i��:�n�[����,������(0),...,������(slots-1),"
	 */
	public void addSelling(String orderIds, String spec) {
		fSelling.put(orderIds, spec);
	}

	/**
	 * ������������ǉ�����
	 * 
	 * @param orderIds
	 *            ����ID��\��������
	 * @param spec
	 *            "���i��:��]����:�ő�����:�Œx����:���׎���;...;"
	 */
	public void addBuying(String orderIds, String spec) {
		fBuying.put(orderIds, spec);
	}

	/**
	 * ����������ǉ�����
	 * 
	 * @param orderIds
	 *            ����ID��\��������
	 * @param spec
	 *            "���i��:��]����:�ő�����:�Œx����:���׎���;...;"
	 */
	public void addBought(String orderIds, String spec) {
		fBought.put(orderIds, spec);
	}

	/**
	 * �����������ǉ�����
	 * 
	 * @param orderIds
	 *            ����ID��\��������
	 * @param spec
	 *            "���i��:��]����:�ő�����:�Œx����:���׎���;...;"
	 */
	public void addCancelBuying(String orderIds, String spec) {
		fCancelBuying.put(orderIds, spec);
	}

	/**
	 * �o�̓X�g���[���֏����o��
	 * <ul>
	 * <li>���蒍���̏o�͌`��: "��/�� ���O�C���� selling #����ID ���i��:�n�[����,������(0),...,������(slots-1),\n"</li>
	 * <li>���������̏o�͌`��: "��/�� ���O�C���� buying #����ID ���i��:��]����:�ő�����:�Œx����:���׎���;...;\n"</li>
	 * <li>�������̏o�͌`��: "��/�� ���O�C���� bought #����ID ���i��:��]����:�ő�����:�Œx����:���׎���;...;\n"</li>
	 * <li>��������̏o�͌`��: "��/�� ���O�C���� cancelbuying #����ID ���i��:��]����:�ő�����:�Œx����:���׎���;...;\n"</li>
	 * </ul>
	 * 
	 * @param pw
	 *            �o�̓X�g���[��
	 * @throws IOException
	 */
	public void writeTo(PrintWriter pw) throws IOException {
		for (Iterator<Entry<String, String>> itr = fCancelBuying.entrySet().iterator(); itr
			.hasNext();) {
			Entry<String, String> entry = itr.next();
			pw.println(String.format("%d/%d %s cancelbuying %s %s", fDate, fSession, fLoginName,
				entry.getKey(), entry.getValue()));
		}
		for (Iterator<Entry<String, String>> itr = fBought.entrySet().iterator(); itr.hasNext();) {
			Entry<String, String> entry = itr.next();
			pw.println(String.format("%d/%d %s bought %s %s", fDate, fSession, fLoginName,
				entry.getKey(), entry.getValue()));
		}
		for (Iterator<Entry<String, String>> itr = fBuying.entrySet().iterator(); itr.hasNext();) {
			Entry<String, String> entry = itr.next();
			pw.println(String.format("%d/%d %s buying %s %s", fDate, fSession, fLoginName,
				entry.getKey(), entry.getValue()));
		}
		for (Iterator<Entry<String, String>> itr = fSelling.entrySet().iterator(); itr.hasNext();) {
			Entry<String, String> entry = itr.next();
			// pw.println(legend(entry.getValue()));
			pw.println(String.format("%d/%d %s selling %s %s", fDate, fSession, fLoginName,
				entry.getKey(), entry.getValue()));
		}
	}

	/**
	 * ���̓X�g���[������ǂݍ���
	 * 
	 * @param br
	 *            BufferedReader ���̓X�g���[��
	 * @throws IOException
	 */
	public void readFrom(BufferedReader br) throws IOException {
		assert fSelling.isEmpty() : "fSelling is not empty";
		assert fBuying.isEmpty() : "fBuying is not empty";
		assert fBought.isEmpty() : "fBought is not empty";
		assert fCancelBuying.isEmpty() : "fCancelBuying is not empty";
		String line = null;
		while ((line = br.readLine().trim()) != null) {
			if (!line.startsWith("#")) {
				String[] sp1 = line.split(" ");
				String[] sp2 = sp1[0].split("/");
				fDate = Integer.parseInt(sp2[0]);
				fSession = Integer.parseInt(sp2[1]);
				fLoginName = sp1[1];
				String sellBuy = sp1[2];
				String orderId = sp1[3];
				String spec = sp1[4];
				if (sellBuy.compareToIgnoreCase("selling") == 0) {
					addSelling(orderId, spec);
				}
				if (sellBuy.compareToIgnoreCase("buying") == 0) {
					addBuying(orderId, spec);
				}
				if (sellBuy.compareToIgnoreCase("bought") == 0) {
					addBought(orderId, spec);
				}
			}
		}
	}

	/**
	 * ���莑���ʂ̎����ڐ���𐶐�
	 * 
	 * @param spec
	 *            "���i��:�n�[����,������(0),...,������(slots-1),"
	 * @return "�n�[����,�n�[����+1,...,�n�[����+slots"
	 */
	private String legend(String spec) {
		String leg = "#,";
		String[] sp1 = spec.split(",");
		String[] sp2 = sp1[0].split(":");
		int offset = Integer.valueOf(sp2[1]);
		for (int i = 1; i < sp1.length; i++) {
			leg += offset + ",";
			offset++;
		}
		return leg;
	}
}
