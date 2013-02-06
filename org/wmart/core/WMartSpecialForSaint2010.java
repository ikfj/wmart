/**
 *
 */
package org.wmart.core;

import java.io.*;
import java.text.*;
import java.util.*;

import org.umart.serverCore.*;
import org.wmart.agent.*;

/**
 * �X�^���h�A���[�������p���C���N���X
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WMartSpecialForSaint2010 {
	
	/** �����ɕK�v�ȃt�@�C�����u���Ă���f�B���N�g���� */
	private static final String RESOURCE_DIRECTORY = "resources";
	
	/** �G�[�W�F���g�ݒ�t�@�C���� */
	private static final String MEMBERS_FILE = "MembersSA.csv";
	
	/** �����p�̃��[�L���O�f�B���N�g�� */
	private String fBaseDirectory = "";
	
	/** ������}�X�^�[ */
	private WMart fMaster;
	
	/**
	 * �R���X�g���N�^
	 * 
	 * @param baseDir
	 *            ���O���쐬����f�B���N�g��
	 * @param isSimpleLog
	 *            �ȈՃ��O�ɂ��邩�H
	 * @param randomSeed
	 *            �����̎�
	 * @param maxDate
	 *            �^�c�����B�ŏ��� (2+daysForward) ���̓E�H�[���A�b�v���ԂƂ��Ď̂Ă邱�ƂɂȂ�
	 * @param daysForward
	 *            �敨����̑Ώۓ���
	 * @throws ParseException
	 * @throws IOException
	 */
	public WMartSpecialForSaint2010(String baseDir, boolean isSimpleLog, int randomSeed, int maxDate,
		int daysForward) throws ParseException, IOException {
		int sessionsPerDay = 4; // 1���̕����� = ���������1���̔񂹉� = �敨�����1���̃X���b�g��
		int sessionsForward = sessionsPerDay * daysForward;
		int maxLength = sessionsForward / 2; // ���[�N�t���[�̍ő咷��
		if (maxLength > 24) {
			maxLength = 24;
		}
		fBaseDirectory = baseDir;
		String resDir = fBaseDirectory + File.separator + WMartSpecialForSaint2010.RESOURCE_DIRECTORY;
		UMemberList memberInfo = new UMemberList();
		readMemberLog(memberInfo, resDir + File.separator + WMartSpecialForSaint2010.MEMBERS_FILE);
		fMaster = new WMart(memberInfo, randomSeed, maxDate, sessionsPerDay, sessionsForward,
			maxLength);
		initAgents(fMaster, memberInfo);
		fMaster.initLog(fBaseDirectory, isSimpleLog);
	}
	
	/**
	 * ������X�g���ɏ]���C�G�[�W�F���g�𐶐����C������ɓo�^����D
	 * 
	 * @param master
	 *            �����
	 * @param members
	 *            ������X�g
	 */
	private void initAgents(WMart master, UMemberList members) {
		Iterator iter = members.getMembers();
		while (iter.hasNext()) {
			HashMap memberInfo = (HashMap) iter.next();
			String loginName = (String) memberInfo.get(UMemberList.STRING_LOGIN_NAME);
			String passwd = (String) memberInfo.get(UMemberList.STRING_PASSWORD);
			String attribute = (String) memberInfo.get(UMemberList.STRING_ATTRIBUTE);
			String connection = (String) memberInfo.get(UMemberList.STRING_CONNECTION);
			String realName = (String) memberInfo.get(UMemberList.STRING_REAL_NAME);
			String paramString = UMemberList.arrayListToString((ArrayList) memberInfo
				.get(UMemberList.ARRAY_LIST_SYSTEM_PARAMETERS));
			int seed = ((Integer) memberInfo.get(UMemberList.INT_SEED)).intValue();
			if (attribute.equals("Machine") && connection.equals("Local")) {
				try {
					WBaseAgent strategy = WAgentFactory.makeAgent(loginName, passwd, realName,
						paramString, seed, null);
					master.appendStrategy(strategy);
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
				break;
			}
		}
	}
	
	/**
	 * WMart��CUI�p�̃��C�����\�b�h�B
	 * 
	 * @param args
	 *            �R�}���h���C������
	 */
	public static void main(String args[]) {
		if (args.length != 5) {
			System.err
				.println("usage: java WMartSimulator simple/detail experimentDirectoryName randomSeed maxDate daysForward");
			System.exit(1);
		}
		String logFlagString = args[0];
		boolean isSimpleLog = false;
		if (logFlagString.equals("simple")) {
			isSimpleLog = true;
		} else if (logFlagString.equals("detail")) {
			isSimpleLog = false;
		} else {
			System.err
				.println("usage: java WMartSimulator simple/detail experimentDirectoryName randomSeed maxDate daysForward");
			System.exit(1);
		}
		String baseDir = args[1];
		int randomSeed = Integer.parseInt(args[2]);
		int maxDate = Integer.parseInt(args[3]);
		int daysForward = Integer.parseInt(args[4]);
		try {
			WMartSpecialForSaint2010 cui = new WMartSpecialForSaint2010(baseDir, isSimpleLog, randomSeed,
				maxDate, daysForward);
			cui.doLoop();
			System.out.println("Finished in " + baseDir);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(5);
		}
	}
}
