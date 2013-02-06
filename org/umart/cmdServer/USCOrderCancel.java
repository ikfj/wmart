package org.umart.cmdServer;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


/**
 * USCOrderCancel�N���X�́C�T�[�o�[��ɂ�����UAgent�ɂ����s����钍������R�}���h�ł���D
 * @author ����@��
 */
public class USCOrderCancel extends UCOrderCancelCore implements IServerCmd {

  /** ���̃R�}���h�����s�����G�[�W�F���g */
  private UAgentForNetworkClient fAgent;

  /** �T�[�o�[�ւ̎Q�� */
  private UMartNetwork fUMart;

  /**
   * Constructor for USCOrderCancel.
   */
  public USCOrderCancel() {
    super();
    fAgent = null;
    fUMart = null;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      int userID = fAgent.getLoginStatus().getUserID();
      fCancelInfo.clear();
      fStatus = fUMart.doOrderCancel(fCancelInfo, userID, fOrderID);
      if (fStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        fAgent.sendMessage(fCancelInfo.get(LONG_ORDER_ID).toString());
        fAgent.sendMessage(fCancelInfo.get(STRING_CANCEL_TIME).toString());
        fAgent.sendMessage(fCancelInfo.get(LONG_CANCEL_VOLUME).toString());
        fAgent.sendMessage(fCancelInfo.get(LONG_UNCANCEL_VOLUME).toString());
      } else {
        fAgent.sendMessage("+ERROR " + fStatus.getErrorCode());
        fAgent.sendMessage(fStatus.getErrorMessage());
      }
    } catch (Exception e) {
      fAgent.sendMessage("+ERROR " + ICommand.INVALID_ARGUMENTS);
      fAgent.sendMessage("USAGE: ORDERCANCEL <ORDERID>");
    }
    fAgent.flushMessage();
    return fStatus;
  }

  /**
   * @see org.umart.cmdServer.IServerCmd#setConnection(UAgentForNetworkClient, UMartNetwork)
   */
  public void setConnection(UAgentForNetworkClient agent, UMartNetwork umart) {
    fAgent = agent;
    fUMart = umart;
  }

}
