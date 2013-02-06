package org.umart.cmdServer;

import java.util.*;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


/**
 * USCExecutions�N���X�́C�T�[�o�[��ɂ�����UAgent�ɂ����s�����
 * ���Ɖ�R�}���h�ł���D
 * @author �암 �S�i
 */
public class USCExecutions extends UCExecutionsCore implements IServerCmd {

  /** ���̃R�}���h�����s�����G�[�W�F���g */
  private UAgentForNetworkClient fAgent;

  /** �T�[�o�[�ւ̎Q�� */
  private UMartNetwork fUMart;

  /**
   * Constructor for USCExecutions.
   */
  public USCExecutions() {
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
      fExecutionsArray.clear();
      fStatus = fUMart.doExecutions(fExecutionsArray, userID);
      if (fStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        Iterator itr = fExecutionsArray.iterator();
        while (itr.hasNext()) {
          HashMap hm = (HashMap) itr.next();
          fAgent.sendMessage(hm.get(LONG_CONTRACT_ID).toString() + " "
                             + hm.get(STRING_CONTRACT_TIME).toString() + " "
                             + hm.get(LONG_ORDER_ID).toString() + " "
                             + hm.get(STRING_BRAND_NAME).toString() + " "
                             + hm.get(INT_NEW_REPAY).toString() + " "
                             + hm.get(INT_SELL_BUY).toString() + " "
                             + hm.get(LONG_PRICE).toString() + " "
                             + hm.get(LONG_VOLUME).toString());
        }
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
