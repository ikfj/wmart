/*
 * Created on 2003/06/07
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.umart.cmdServer;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


/**
 * @author isao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class USCLogout extends UCLogoutCore implements IServerCmd {

  /** ���̃R�}���h�����s�����G�[�W�F���g */
  private UAgentForNetworkClient fAgent;

  /** �T�[�o�[�ւ̎Q�� */
  private UMartNetwork fUMart;

  /**
   * �R���X�g���N�^
   */
  public USCLogout() {
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
    fAgent.sendMessage("+ACCEPT");
    fAgent.logout();
    fStatus.setStatus(true);
    fAgent.flushMessage();
    return fStatus;
  }

}
