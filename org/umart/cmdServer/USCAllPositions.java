package org.umart.cmdServer;

import java.util.*;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


/**
 * USCAllPositions�N���X�́C�T�[�o�[��ɂ�����UAgent�ɂ����s�����
 * �S����̃|�W�V�����Ɖ�R�}���h�ł���D
 * @author �암 �S�i
 */
public class USCAllPositions extends UCAllPositionsCore implements IServerCmd {

  /** ���̃R�}���h�����s�����G�[�W�F���g */
  private UAgentForNetworkClient fAgent;

  /** �T�[�o�[�ւ̎Q�� */
  private UMartNetwork fUMart;

  /**
   * Constructor for USCAllPositions.
   */
  public USCAllPositions() {
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
      fAllPositionsArray.clear();
      fStatus = fUMart.doAllPositions(fAllPositionsArray, userID);
      if (fStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        Iterator itr = fAllPositionsArray.iterator();
        while (itr.hasNext()) {
          HashMap hm = (HashMap) itr.next();
          fAgent.sendMessage(hm.get(STRING_NAME).toString());
          fAgent.sendMessage(hm.get(LONG_SELL_POSITION).toString());
          fAgent.sendMessage(hm.get(LONG_BUY_POSITION).toString());
        }
      } else {
        fAgent.sendMessage("+ERROR " + fStatus.getErrorCode());
        fAgent.sendMessage(fStatus.getErrorMessage());
      }
      fAgent.flushMessage();
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
