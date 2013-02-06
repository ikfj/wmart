package org.umart.cmdServer;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


/**
 * USCMarketStatus�N���X�́C�T�[�o�[��ɂ�����UAgent�ɂ����s�����
 * �}�[�P�b�g��Ԗ₢���킹�̂��߂̃R�}���h�ł���D
 * @author �암 �S�i
 */
public class USCMarketStatus extends UCMarketStatusCore implements IServerCmd {

  /** ���̃R�}���h�����s�����G�[�W�F���g */
  private UAgentForNetworkClient fAgent;

  /** �T�[�o�[�ւ̎Q�� */
  private UMartNetwork fUMart;

  /**
   * Constructor for USCMarketStatus.
   */
  public USCMarketStatus() {
    super();
    fAgent = null;
    fUMart = null;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      fMarketInfo.clear();
      fStatus = fUMart.doMarketStatus(fMarketInfo);
      if (fStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        fAgent.sendMessage(fMarketInfo.get(INT_MARKET_STATUS).toString());
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
