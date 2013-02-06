package org.umart.cmdServer;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


public class USCServerStatus extends UCServerStatusCore implements IServerCmd {

  /** ���̃R�}���h�����s�����G�[�W�F���g */
  private UAgentForNetworkClient fAgent;

  /** �T�[�o�[�ւ̎Q�� */
  UMartNetwork fUMart;

  /** �R���X�g���N�^ */
  public USCServerStatus() {
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
      fData.clear();
      fCommandStatus = fUMart.doServerStatus(fData, userID);
      if (fCommandStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        fAgent.sendMessage(fData.get(UCServerStatusCore.INT_DATE).toString()); // ���݂̓��t�iU-Mart��)
        fAgent.sendMessage(fData.get(UCServerStatusCore.INT_BOARD_NO).toString()); // ���݂̔񂹉�
        fAgent.sendMessage(fData.get(UCServerStatusCore.INT_STATE).toString()); // ���ԑ�
      } else {
        fAgent.sendMessage("+ERROR " + fCommandStatus.getErrorCode());
        fAgent.sendMessage(fCommandStatus.getErrorMessage());
      }
    } catch (Exception e) {
      fAgent.sendMessage("+ERROR " + ICommand.INVALID_ARGUMENTS);
      fAgent.sendMessage("USAGE: SERVERSTATUS");
    }
    fAgent.flushMessage();
    return fCommandStatus;
  }

}
