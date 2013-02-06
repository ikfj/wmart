package org.umart.cmdServer;

import java.util.*;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


public class USCAccountHistory extends UCAccountHistoryCore implements
    IServerCmd {

  /** ���̃R�}���h�����s�����G�[�W�F���g */
  private UAgentForNetworkClient fAgent;

  /** �T�[�o�[�ւ̎Q�� */
  UMartNetwork fUMart;

  /** �R���X�g���N�^ */
  public USCAccountHistory() {
    super();
    fAgent = null;
    fUMart = null;
  }

  public void setConnection(UAgentForNetworkClient agent, UMartNetwork umart) {
    fAgent = agent;
    fUMart = umart;
  }

  public UCommandStatus doIt() {
    try {
      int userID = fAgent.getLoginStatus().getUserID();
      fArray.clear();
      fCommandStatus = fUMart.doAccountHistory(fArray, userID, fTargetUserId,
                                               fNoOfDays);
      if (fCommandStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        Iterator itr = fArray.iterator();
        while (itr.hasNext()) {
          HashMap data = (HashMap) itr.next();
          fAgent.sendMessage(data.get(UCAccountHistoryCore.STRING_NAME).
                             toString()); // ���[�U�[��
          fAgent.sendMessage(data.get(UCAccountHistoryCore.INT_USER_ID).
                             toString()); // ���[�U�[ID
          fAgent.sendMessage(data.get(UCAccountHistoryCore.INT_STEP).toString()); // �X�e�b�v
          fAgent.sendMessage(data.get(UCAccountHistoryCore.
                                      LONG_UNREALIZED_PROFIT).toString()); // ���������v
          fAgent.sendMessage(data.get(UCAccountHistoryCore.LONG_SELL_POSITION).
                             toString()); // ���|�W�V�����̍��v
          fAgent.sendMessage(data.get(UCAccountHistoryCore.LONG_BUY_POSITION).
                             toString()); // ���|�W�V�����̍��v
        }
      } else {
        fAgent.sendMessage("+ERROR " + fCommandStatus.getErrorCode());
        fAgent.sendMessage(fCommandStatus.getErrorMessage());
      }
    } catch (Exception e) {
      fAgent.sendMessage("+ERROR " + ICommand.INVALID_ARGUMENTS);
      fAgent.sendMessage("USAGE: ACCOUNTHISTORY");
    }
    fAgent.flushMessage();
    return fCommandStatus;
  }

}
