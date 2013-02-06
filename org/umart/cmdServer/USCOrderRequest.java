package org.umart.cmdServer;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


/**
 * USCOrderRequest�N���X�́C�T�[�o�[��ɂ�����UAgent�ɂ����s�����
 * �����˗��R�}���h�ł���D
 * @author �암 �S�i
 */
public class USCOrderRequest extends UCOrderRequestCore implements IServerCmd {

  /** ���̃R�}���h�����s�����G�[�W�F���g */
  private UAgentForNetworkClient fAgent;

  /** �T�[�o�[�ւ̎Q�� */
  private UMartNetwork fUMart;

  /**
   * Constructor for USCOrderRequest.
   */
  public USCOrderRequest() {
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
      fRequestInfo.clear();
      fStatus = fUMart.doOrderRequest(fRequestInfo, userID, fBrandName,
                                      fNewRepay, fSellBuy, fMarketLimit,
                                      fPrice, fVolume);
      if (fStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        fAgent.sendMessage(fRequestInfo.get(LONG_ORDER_ID).toString());
        fAgent.sendMessage(fRequestInfo.get(STRING_ORDER_TIME).toString());
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
