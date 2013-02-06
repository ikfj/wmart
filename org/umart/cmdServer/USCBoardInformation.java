package org.umart.cmdServer;

import java.util.*;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


/**
 * �T�[�o�[��ɂ�����UAgent�ɂ����s���������擾���邽�߂�SVMP�R�}���h�N���X�ł��D
 * @author �암 �S�i
 */
public class USCBoardInformation extends UCBoardInformationCore implements IServerCmd {

  /** ���̃R�}���h�����s�����G�[�W�F���g */
  private UAgentForNetworkClient fAgent;

  /** �T�[�o�[�ւ̎Q�� */
  private UMartNetwork fUMart;

  /**
   * �R���X�g���N�^
   */
  public USCBoardInformation() {
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
      fBoardInfo.clear();
      fBoardInfo.clear();
      fStatus = fUMart.doBoardInformation(fBoardInfo);
      if (fStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        fAgent.sendMessage(fBoardInfo.get(STRING_LAST_UPDATE_TIME).toString());
        ArrayList infoArray = (ArrayList) fBoardInfo.get(UCBoardInformationCore.
            ARRAYLIST_BOARD);
        // �ȉ��C���s�������
        {
          Iterator itr = infoArray.iterator();
          while (itr.hasNext()) {
            HashMap hm = (HashMap) itr.next();
            if ( ( (Long) hm.get(LONG_PRICE)).longValue() < 0) {
              fAgent.sendMessage(hm.get(LONG_SELL_VOLUME).toString() + " "
                                 + hm.get(LONG_BUY_VOLUME).toString());
              break;
            }
          }
        }
        // �ȉ��C���s�������ȊO
        {
          Iterator itr = infoArray.iterator();
          while (itr.hasNext()) {
            HashMap hm = (HashMap) itr.next();
            if ( ( (Long) hm.get(LONG_PRICE)).longValue() >= 0) {
              fAgent.sendMessage(hm.get(LONG_PRICE).toString() + " "
                                 + hm.get(LONG_SELL_VOLUME).toString() + " "
                                 + hm.get(LONG_BUY_VOLUME).toString());
            }
          }
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
