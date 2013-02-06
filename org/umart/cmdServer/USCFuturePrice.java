package org.umart.cmdServer;

import java.util.*;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


/**
 * USCFuturePrice�N���X�́C�T�[�o�[��ɂ�����UAgent�ɂ����s�����
 * �敨���i���擾���邽�߂̃R�}���h�ł���D
 * @author �암 �S�i
 */
public class USCFuturePrice extends UCFuturePriceCore implements IServerCmd {

  /** ���̃R�}���h�����s�����G�[�W�F���g */
  private UAgentForNetworkClient fAgent;

  /** �T�[�o�[�ւ̎Q�� */
  private UMartNetwork fUMart;

  /**
   * Constructor for USCFuturePrice.
   */
  public USCFuturePrice() {
    super();
    fAgent = null;
    fUMart = null;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      fFutureArray.clear();
      fStatus = fUMart.doFuturePrice(fFutureArray, fBrandName, fNoOfSteps);
      if (fStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        Iterator itr = fFutureArray.iterator();
        while (itr.hasNext()) {
          HashMap hm = (HashMap) itr.next();
          String result = "";
          result += hm.get(STRING_BRAND_NAME).toString();
          result += " " + hm.get(INT_DAY).toString();
          result += ":" + hm.get(INT_BOARD_NO).toString();
          result += ":" + hm.get(INT_STEP).toString();
          result += " " + hm.get(LONG_PRICE).toString();
          fAgent.sendMessage(result);
        }
      } else {
        fAgent.sendMessage("+ERROR " + fStatus.getErrorCode());
        fAgent.sendMessage(fStatus.getErrorMessage());
      }
    } catch (Exception e) {
      e.printStackTrace();
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
