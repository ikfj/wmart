package org.umart.cmdServer;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


/**
 * USCSchedule�N���X�́C�T�[�o�[��ɂ�����UAgent�ɂ����s�����^�p�\��⍇���R�}���h�ł���D
 * @author ����@��
 */
public class USCSchedule extends UCScheduleCore implements IServerCmd {

  /** ���̃R�}���h�����s�����G�[�W�F���g */
  private UAgentForNetworkClient fAgent;

  /** �T�[�o�[�ւ̎Q�� */
  private UMartNetwork fUMart;

  /**
   * Constructor for USCSchedule.
   */
  public USCSchedule() {
    super();
    fAgent = null;
    fUMart = null;
  }

  /**
   * @see org.umart.serverCore.ICCommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      fScheduleInfo.clear();
      fStatus = fUMart.doSchedule(fScheduleInfo);
      if (fStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        fAgent.sendMessage(fScheduleInfo.get(INT_MAX_DAY) + " "
                           + fScheduleInfo.get(INT_NO_OF_BOARDS));
      } else {
        fAgent.sendMessage("+ERROR " + fStatus.getErrorCode());
        fAgent.sendMessage(fStatus.getErrorMessage());
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
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
