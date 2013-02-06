/**
 *
 */
package org.wmart.core;

import java.io.*;
import java.text.ParseException;
import java.util.*;

import org.umart.serverCore.UMemberList;
import org.wmart.agent.WBaseAgent;
import org.wmart.analyzer.WMartScenarioVisualizer;

/**
 * ���C���N���X
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WMartSimulator {

	/** ������}�X�^�[ */
	private WMart fMaster;

	/**
	 * ���C�����\�b�h
	 * 
	 * @param args
	 *            �R�}���h���C������
	 */
	public static void main(String args[]) {
		if (args.length < 4) {
			System.err
				.println("usage: java WMartSimulator seed maxDays daysForward membersFile [parameter]");
			System.err.println();
			System.err.println("parameter: Colon-separated key-value pairs, e.g.");
			System.err.println("    demandfile=(path)");
			System.err.println("    logdir=(path)");
			System.exit(1);
		}
		int seed = Integer.parseInt(args[0]);
		int maxDays = Integer.parseInt(args[1]);
		int daysForward = Integer.parseInt(args[2]);
		String membersFile = args[3];
		String parameter = (args.length > 4) ? args[4] : null;
		try {
			WMartSimulator sim = new WMartSimulator(seed, maxDays, daysForward, membersFile,
				parameter);
			sim.doLoop();
			System.out.println("Finished in " + sim.fMaster.getLogDir());
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(5);
		}
	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param seed
	 *            �����̎�
	 * @param maxDays
	 *            �^�c�����B�ŏ��� (2+daysForward) ���̓E�H�[���A�b�v���ԂƂ��Ď̂Ă邱�ƂɂȂ�
	 * @param daysForward
	 *            �敨����̑Ώۓ���
	 * @param membersFile
	 *            �����o�[�ݒ�t�@�C��
	 * @param parameter
	 *            �V�X�e���p�����[�^
	 * @throws ParseException
	 * @throws IOException
	 */
	public WMartSimulator(int seed, int maxDays, int daysForward, String membersFile,
		String parameter) throws ParseException, IOException {
		int sessionsPerDay = 24; // 1���̕����� = ���������1���̔񂹉� = �敨�����1���̃X���b�g��
		int slotsForward = sessionsPerDay * daysForward; // �敨����̑ΏۃX���b�g��
		int maxLength = slotsForward / 2; // ���[�N�t���[�̍ő咷��
		if (maxLength > sessionsPerDay) {
			maxLength = sessionsPerDay;
		}
		UMemberList memberInfo = new UMemberList();
		readMemberLog(memberInfo, membersFile);
		fMaster = new WMart(memberInfo, seed, maxDays, sessionsPerDay, slotsForward, maxLength);
		fMaster.initLog(parameter, true); // true:�ȈՃ��O false:�ڍ׃��O
		initAgents(fMaster, memberInfo, parameter);
	}

	/**
	 * ������X�g���ɏ]���C�G�[�W�F���g�𐶐����C������ɓo�^����D
	 * 
	 * @param master
	 *            �����
	 * @param members
	 *            ������X�g
	 * @param extraParameter
	 *            �V�X�e���p�����[�^
	 */
	private void initAgents(WMart master, UMemberList members, String extraParameter) {
		Iterator iter = members.getMembers();
		while (iter.hasNext()) {
			HashMap memberInfo = (HashMap) iter.next();
			String loginName = (String) memberInfo.get(UMemberList.STRING_LOGIN_NAME);
			String passwd = (String) memberInfo.get(UMemberList.STRING_PASSWORD);
			String attribute = (String) memberInfo.get(UMemberList.STRING_ATTRIBUTE);
			String connection = (String) memberInfo.get(UMemberList.STRING_CONNECTION);
			String realName = (String) memberInfo.get(UMemberList.STRING_REAL_NAME);
			String parameter = UMemberList.arrayListToString((ArrayList) memberInfo
				.get(UMemberList.ARRAY_LIST_SYSTEM_PARAMETERS));
			parameter += ":" + extraParameter; // �������痈���V�X�e���p�����[�^���ڂ���
			if (!parameter.matches("logdir")) {
				parameter += ":logdir=" + fMaster.getLogDir().replace(":", "?");
			}
			int seed = ((Integer) memberInfo.get(UMemberList.INT_SEED)).intValue();
			if (attribute.equals("Machine") && connection.equals("Local")) {
				try {
					WBaseAgent agent = WAgentFactory.makeAgent(loginName, passwd, realName,
						parameter, seed, null);
					master.appendStrategy(agent);
				} catch (IllegalArgumentException iae) {
					System.err.println("Cannot initialize " + loginName);
					System.exit(5);
				}
			}
		}
	}

	/**
	 * ������X�g���t�@�C������ǂݍ��ށD
	 * 
	 * @param memberInfo
	 *            ������X�g
	 * @param filename
	 *            �t�@�C����
	 * @throws ParseException
	 * @throws IOException
	 */
	private void readMemberLog(UMemberList memberInfo, String filename) throws ParseException,
		IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		memberInfo.readFrom(br);
		br.close();
	}

	/**
	 * ���C�����[�v
	 * 
	 */
	public void doLoop() {
		while (true) {
			WServerStatus status = fMaster.nextStatus();
			if (status.getState() == WServerStatusMaster.AFTER_HOURS) {
				fMaster.doActionsByLocalAgents();
			} else if (status.getState() == WServerStatusMaster.SHUTDOWN) {
				fMaster.doActionsByLocalAgents();
				break;
			}
		}
		// visualize(fMaster.getLogDir());
	}

	/**
	 * �������O����������
	 * 
	 */
	public void visualize(String logdir) {
		String logfilename = String.format(("%s/%s.csv"), logdir, WMart.RESOURCE_LOG_DIR); // �S���ꏏ�̃t�@�C��
		String imgfilename = logfilename.replaceAll("\\.\\w+$", ".png");
		WMartScenarioVisualizer vis = new WMartScenarioVisualizer();
		try {
			vis.visualize(logfilename, imgfilename, 1000, 600);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(5);
		}
	}
}
