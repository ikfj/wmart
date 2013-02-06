package org.wmart.cmdCore;

import java.util.*;

public abstract class WCServerStatusCore implements ICommand {

	/** �R�}���h�� */
	public static final String CMD_NAME = "ServerStatus";

	/** �R�}���h�̎��s���ʂ̏�� */
	protected WCommandStatus fCommandStatus;

	/** �T�[�o�[��� */
	protected HashMap fData;

	/** ���݂̓��t (U-Mart��) ���������߂̃L�[ */
	public static final String INT_DATE = "INT_DATE";

	/** ���݂̔񂹉񐔂��������߂̃L�[ */
	public static final String INT_BOARD_NO = "INT_BOARD_NO";

	/** ���ԑт��������߂̃L�[ */
	public static final String INT_STATE = "INT_STATE";

	/** �R���X�g���N�^ */
	public WCServerStatusCore() {
		super();
		fCommandStatus = new WCommandStatus();
		fData = new HashMap();
	}

	public boolean isNameEqualTo(String name) {
		if (name.equalsIgnoreCase(CMD_NAME)) {
			return true;
		} else {
			return false;
		}
	}

	public String getName() {
		return CMD_NAME;
	}

	public boolean setArguments(StringTokenizer st) {
		try {
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void setArguments() {
	}

	public WCommandStatus getStatus() {
		return fCommandStatus;
	}

	public void printOn() {
		System.out.println(fData.toString());
	}

	public HashMap getData() {
		return fData;
	}

	public String getResultString() {
		String returnStr = "";
		returnStr += fData.get(WCServerStatusCore.INT_DATE).toString() + "\n";
		returnStr += fData.get(WCServerStatusCore.INT_BOARD_NO).toString() + "\n";
		returnStr += fData.get(WCServerStatusCore.INT_STATE).toString() + "\n";
		return returnStr;
	}

}
