package org.wmart.agent;

import java.io.*;
import java.util.*;

import org.wmart.cmdCore.*;

/**
 * �}�V���G�[�W�F���g�̃��[�g�N���X�ł��D �S�Ẵ}�V���G�[�W�F���g��UBaseAgent���p�����āC
 * doActions���\�b�h��setParameters���\�b�h���I�[�o�[���C�h����K�v������܂��D
 * �T�[�o�Ƃ̒ʐM�ɂ́CgetUmcp���\�b�h��CProtocolCore�I�u�W�F�N�g���擾������C CProtocolCore�I�u�W�F�N�g����SVMP�R�}���h�I�u�W�F�N�g���擾����
 * �K�؂Ȉ�����ݒ肵����Ɏ��s���܂��D
 * 
 * @author �암 �S�i
 * @author ����@��
 */
public class WBaseAgent {
	
	/** ���O�C���� */
	protected String fLoginName;
	
	/** �p�X���[�h */
	protected String fPasswd;
	
	/** ���ۂ̖��O�iHuman�̏ꍇ�F�����CMachine�̏ꍇ�F�N���X���j */
	protected String fRealName;
	
	/** ���[�UID */
	protected int fUserID;
	
	/** �����̎� */
	protected int fSeed;
	
	/** ���� */
	protected Random fRandom;
	
	/** �v���g�R���E���C�u���� */
	protected WProtocolCore fUmcp;
	
	/** �V�X�e���p�����[�^ */
	protected String[] fSystemParameters;
	
	/** �o�̓X�g���[�� */
	protected OutputStream fOutputStream = null;
	
	/** fOutputStream���琶�������PrintWriter�I�u�W�F�N�g */
	protected PrintWriter fPrintWriter;
	
	/**
	 * UBaseAgent�I�u�W�F�N�g�̐�������я��������s���D
	 * 
	 * @param loginName
	 *            ���O�C����
	 * @param passwd
	 *            �p�X���[�h
	 * @param realName
	 *            ���ۂ̖��O�iHuman�̏ꍇ�F�����CMachine�̏ꍇ�F�N���X���j
	 * @param seed
	 *            �����̎�
	 */
	public WBaseAgent(String loginName, String passwd, String realName, int seed) {
		fLoginName = loginName;
		fPasswd = passwd;
		fRealName = realName;
		fSeed = seed;
		fRandom = new Random(seed);
		fUmcp = null;
		fUserID = -1;
		fSystemParameters = new String[0];
		setOutputStream(System.out); // �f�t�H���g�ł́C�W���o�͂𗘗p����D
	}
	
	/**
	 * �G�[�W�F���g�̃V�X�e���p�����[�^��ݒ肷��D ���̃��\�b�h���I�[�o�[���C�h����ꍇ�C�K���C�ŏ���super.setParameters���ĂԂ��ƁD
	 * 
	 * @param args
	 *            �V�X�e���p�����[�^
	 */
	public void setParameters(String[] args) {
		fSystemParameters = new String[args.length];
		message("**** Parameters given by CSV file ****");
		for (int i = 0; i < args.length; ++i) {
			fSystemParameters[i] = args[i];
			message(args[i]);
		}
		message("****************************************");
	}
	
	/**
	 * ���b�Z�[�W���o�͂���D
	 * 
	 * @param msg
	 *            String ���b�Z�[�W
	 */
	public void message(String msg) {
		if (fPrintWriter != null) {
			fPrintWriter.println(msg);
			fPrintWriter.flush();
		}
	}
	
	/**
	 * ���b�Z�[�W�����s�Ȃ��ŏo�͂���D
	 * 
	 * @param msg
	 *            ���b�Z�[�W
	 */
	public void print(String msg) {
		if (fPrintWriter != null) {
			fPrintWriter.print(msg);
			fPrintWriter.flush();
		}
	}
	
	/**
	 * ���b�Z�[�W�����s����ŏo�͂���D
	 * 
	 * @param msg
	 *            ���b�Z�[�W
	 */
	public void println(String msg) {
		if (fPrintWriter != null) {
			fPrintWriter.println(msg);
			fPrintWriter.flush();
		}
	}
	
	/**
	 * ��O���o�͂���D
	 * 
	 * @param ex
	 *            Exception ��O
	 */
	public void message(Exception ex) {
		if (fPrintWriter != null) {
			ex.printStackTrace(fPrintWriter);
		}
	}
	
	/**
	 * �o�̓X�g���[����ݒ肷��
	 * 
	 * @param os
	 *            �o�̓X�g���[��
	 */
	public void setOutputStream(OutputStream os) {
		fOutputStream = os;
		if (fOutputStream == null) {
			fPrintWriter = null;
		} else {
			fPrintWriter = new PrintWriter(os, true);
		}
	}
	
	/**
	 * �o�̓X�g���[����Ԃ�
	 * 
	 * @return �o�̓X�g���[��
	 */
	public OutputStream getOutputStream() {
		return fOutputStream;
	}
	
	/**
	 * �G�[�W�F���g�̃V�X�e���p�����[�^��Ԃ��D
	 * 
	 * @return �V�X�e���p�����[�^
	 */
	public String[] getParameters() {
		return fSystemParameters;
	}
	
	/**
	 * CProtocol�I�u�W�F�N�g��ݒ肷��D
	 * 
	 * @param umcp
	 *            CProtocol�I�u�W�F�N�g
	 */
	public void setCProtocol(WProtocolCore umcp) {
		fUmcp = umcp;
	}
	
	/**
	 * �����˗��C�L�����Z���Ȃǂ̓�����s���D
	 * 
	 * @param date
	 *            ���t
	 * @param session
	 *            �񂹉�
	 * @param serverState
	 *            �T�[�o�[���
	 * @param maxDate
	 *            �^�p����
	 * @param sessionsPerDay
	 *            ���������̔񂹉�
	 * @param slotsForward
	 *            �敨����ΏۃX���b�g��
	 * @param maxLength
	 *            ���[�N�t���[�̍ő咷��
	 */
	public void doActions(int date, int session, int serverState, int maxDate, int sessionsPerDay,
		int slotsForward, int maxLength) {
	}
	
	/**
	 * �v���g�R���E���C�u������Ԃ�.
	 * 
	 * @return �v���g�R���E���C�u�����D
	 */
	public WProtocolCore getUmcp() {
		return fUmcp;
	}
	
	/**
	 * �p�X���[�h��Ԃ�.
	 * 
	 * @return �p�X���[�h
	 */
	public String getPasswd() {
		return fPasswd;
	}
	
	/**
	 * �p�X���[�h��ݒ肷��.
	 * 
	 * @param passwd
	 *            �p�X���[�h
	 */
	public void setPasswd(String passwd) {
		fPasswd = passwd;
	}
	
	/**
	 * ���[�UID��Ԃ�.
	 * 
	 * @return ���[�UID�D
	 */
	public int getUserID() {
		return fUserID;
	}
	
	/**
	 * ���[�U�[ID��ݒ肷��.
	 * 
	 * @param userID
	 *            ���[�U�[ID
	 */
	public void setUserID(int userID) {
		fUserID = userID;
	}
	
	/**
	 * Returns the seed.
	 * 
	 * @return int
	 */
	public int getSeed() {
		return fSeed;
	}
	
	/**
	 * Sets the seed.
	 * 
	 * @param seed
	 *            The seed to set
	 */
	public void setSeed(int seed) {
		fSeed = seed;
		fRandom = new Random(seed);
	}
	
	/**
	 * ���O�C������Ԃ��D
	 * 
	 * @return ���O�C����
	 */
	public String getLoginName() {
		return fLoginName;
	}
	
	/**
	 * ���ۂ̖��O�iHuman�̏ꍇ�F�����CMachine�̏ꍇ�F�N���X���j��Ԃ�
	 * 
	 * @return ���ۂ̖��O
	 */
	public String getRealName() {
		return fRealName;
	}
	
	/**
	 * ���O�C������ݒ肷��
	 * 
	 * @param string
	 *            ���O�C����
	 */
	public void setLoginName(String string) {
		fLoginName = string;
	}
	
	/**
	 * ���ۂ̖��O�iHuman�̏ꍇ�F�����CMachine�̏ꍇ�F�N���X���j��ݒ肷��
	 * 
	 * @param string
	 *            ���ۂɖ��O
	 */
	public void setRealName(String string) {
		fRealName = string;
	}
	
	/**
	 * �����I�u�W�F�N�g��Ԃ��D
	 * 
	 * @return �����I�u�W�F�N�g
	 */
	public Random getRandom() {
		return fRandom;
	}
	
}
