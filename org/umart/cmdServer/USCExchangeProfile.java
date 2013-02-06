package org.umart.cmdServer;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


/**
 * �T�[�o�[��ɂ�����UAgent�ɂ����s��������������擾���邽�߂�SVMP�R�}���h�N���X�ł��D
 * ��������Ƃ́C������ۗ̕L�����C������̊Ǘ����̔��|�W�V�����C���|�W�V�����C������̂��Ƃł��D
 * @author ����@��
 */
public class USCExchangeProfile extends UCExchangeProfileCore implements
    IServerCmd {

  /** ���̃R�}���h�����s�����G�[�W�F���g */
  private UAgentForNetworkClient fAgent;

  /** �T�[�o�[�ւ̎Q�� */
  UMartNetwork fUMart;

  /** �R���X�g���N�^ */
  public USCExchangeProfile() {
    super();
    fAgent = null;
    fUMart = null;
  }

  public void setConnection(UAgentForNetworkClient agent, UMartNetwork umart) {
    fAgent = agent;
    fUMart = umart;
  }

  public UCommandStatus doIt() {
    try {
      int userID = fAgent.getLoginStatus().getUserID();
      fData.clear();
      fCommandStatus = fUMart.doExchangeProfile(fData, userID);
      if (fCommandStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        fAgent.sendMessage(fData.get(UCExchangeProfileCore.LONG_CASH).toString()); // �ۗL����
        fAgent.sendMessage(fData.get(UCExchangeProfileCore.LONG_SELL_POSITION).toString()); // ������̊Ǘ����̔��|�W�V����
        fAgent.sendMessage(fData.get(UCExchangeProfileCore.LONG_BUY_POSITION).toString()); // ������̊Ǘ����̔��|�W�V����
        fAgent.sendMessage(fData.get(UCExchangeProfileCore.INT_NO_OF_MEMBERS).toString()); // �����
      } else {
        fAgent.sendMessage("+ERROR " + fCommandStatus.getErrorCode());
        fAgent.sendMessage(fCommandStatus.getErrorMessage());
      }
    } catch (Exception e) {
      fAgent.sendMessage("+ERROR " + ICommand.INVALID_ARGUMENTS);
      fAgent.sendMessage("USAGE: EXCHANGEPROFILE");
    }
    fAgent.flushMessage();
    return fCommandStatus;
  }

}
