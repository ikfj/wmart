/*
 * Created on 2003/06/10
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.umart.cmdServer;

import java.util.*;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


/**
 * @author isao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class USCOrderIDHistory extends UCOrderIDHistoryCore implements
    IServerCmd {

  /** ���̃R�}���h�����s�����G�[�W�F���g */
  private UAgentForNetworkClient fAgent;

  /** �T�[�o�[�ւ̎Q�� */
  private UMartNetwork fUMart;

  /**
   *
   */
  public USCOrderIDHistory() {
    super();
    fAgent = null;
    fUMart = null;
  }

  /* (non-Javadoc)
   * @see org.umart.cmdServer.IServerCmd#setConnection(org.umart.serverNet.UAgent, org.umart.serverNet.UMartNetwork)
   */
  public void setConnection(UAgentForNetworkClient agent, UMartNetwork umart) {
    fAgent = agent;
    fUMart = umart;
  }

  /* (non-Javadoc)
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      int userID = fAgent.getLoginStatus().getUserID();
      fOrderIDHistory.clear();
      fCommandStatus = fUMart.doOrderIDHistory(userID, fOrderIDHistory,
                                               fTargetUserID, fNoOfSteps);
      if (fCommandStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        Iterator itr = fOrderIDHistory.iterator();
        while (itr.hasNext()) {
          Long id = (Long) itr.next();
          fAgent.sendMessage(id.toString());
        }
      } else {
        fAgent.sendMessage("+ERROR " + fCommandStatus.getErrorCode());
        fAgent.sendMessage(fCommandStatus.getErrorMessage());
      }
    } catch (Exception e) {
      fAgent.sendMessage("+ERROR " + ICommand.INVALID_ARGUMENTS);
      fAgent.sendMessage("USAGE: ORDERCANCEL <ORDERID>");
    }
    fAgent.flushMessage();
    return fCommandStatus;
  }

}
