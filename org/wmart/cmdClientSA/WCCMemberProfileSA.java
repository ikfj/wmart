package org.wmart.cmdClientSA;

import org.wmart.cmdCore.*;
import org.wmart.core.*;

public class WCCMemberProfileSA extends WCMemberProfileCore implements IClientCmdSA {

	/** U-Martオブジェクトへの参照 */
	private WMart fWMart;

	/** ユーザID */
	private int fUserID;

	/** コンストラクタ */
	WCCMemberProfileSA() {
		super();
		fWMart = null;
		fUserID = -1;
	}

	public void setConnection(WMart wmart, int userID) {
		fWMart = wmart;
		fUserID = userID;
	}

	public WCommandStatus doIt() {
		fData.clear();
		fCommandStatus = fWMart.doMemberProfile(fData, fUserID, fTargetUserId);
		return fCommandStatus;
	}

}
