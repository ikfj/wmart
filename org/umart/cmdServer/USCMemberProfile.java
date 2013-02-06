package org.umart.cmdServer;

import java.text.*;
import java.util.*;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


public class USCMemberProfile extends UCMemberProfileCore implements IServerCmd {

  /** ���̃R�}���h�����s�����G�[�W�F���g */
  private UAgentForNetworkClient fAgent;

  /** �T�[�o�[�ւ̎Q�� */
  UMartNetwork fUMart;

  /** �R���X�g���N�^ */
  public USCMemberProfile() {
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
      fCommandStatus = fUMart.doMemberProfile(fData, userID, fTargetUserId);
      if (fCommandStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        fAgent.sendMessage(fData.get(UCMemberProfileCore.STRING_LOGIN_NAME).
                           toString()); // �����
        fAgent.sendMessage(fData.get(UCMemberProfileCore.STRING_PASSWORD).
                           toString()); // �p�X���[�h
        fAgent.sendMessage(fData.get(UCMemberProfileCore.STRING_ATTRIBUTE).
                           toString()); // �G�[�W�F���g����(Human or Machine)
        fAgent.sendMessage(fData.get(UCMemberProfileCore.STRING_CONNECTION).
                           toString()); // �R�l�N�V����(Remote or Local)
        fAgent.sendMessage(UCMemberProfileCore.arrayListToString( (ArrayList)
            fData.get(UCMemberProfileCore.ARRAY_LIST_ACCESS))); // �A�N�Z�X����
        fAgent.sendMessage(fData.get(UCMemberProfileCore.STRING_REAL_NAME).
                           toString()); // ���ۂ̖��O
        fAgent.sendMessage(UCMemberProfileCore.arrayListToString( (ArrayList)
            fData.get(UCMemberProfileCore.ARRAY_LIST_SYSTEM_PARAMETERS))); // �V�X�e���p�����[�^
        fAgent.sendMessage(fData.get(UCMemberProfileCore.INT_SEED).toString()); // �����̎�
        fAgent.sendMessage(fData.get(UCMemberProfileCore.LONG_INITIAL_CASH).
                           toString()); // �������Y
        fAgent.sendMessage(fData.get(UCMemberProfileCore.LONG_TRADING_UNIT).
                           toString()); // ����P��
        fAgent.sendMessage(fData.get(UCMemberProfileCore.LONG_FEE_PER_UNIT).
                           toString()); // �P�ʎ��������̎萔��
        fAgent.sendMessage(fData.get(UCMemberProfileCore.LONG_MARGIN_RATE).
                           toString()); // �؋�����
        fAgent.sendMessage(fData.get(UCMemberProfileCore.LONG_MAX_LOAN).
                           toString()); // �؂������x�z
        DecimalFormat numFormat = new DecimalFormat();
        numFormat.setMaximumFractionDigits(4);
        String tmp = numFormat.format( ( (Double) fData.get(UCMemberProfileCore.
            DOUBLE_INTEREST)).doubleValue());
        fAgent.sendMessage(fData.get(UCMemberProfileCore.DOUBLE_INTEREST).
                           toString()); // �؂�������
        fAgent.sendMessage(fData.get(UCMemberProfileCore.INT_STATUS).toString()); // ����\(+1),����s�\(-1)
        fAgent.sendMessage(fData.get(UCMemberProfileCore.INT_NO_OF_LOGIN_AGENTS).
                           toString()); // ���O�C�����̃G�[�W�F���g��
        HashMap yesterdayBalance = (HashMap) fData.get(UCMemberProfileCore.
            HASHMAP_YESTERDAY_BALANCE); // �O�����x
        fAgent.sendMessage(yesterdayBalance.get(UCMemberProfileCore.
                                                LONG_INITIAL_CASH).toString()); // ����������
        fAgent.sendMessage(yesterdayBalance.get(UCMemberProfileCore.LONG_LOAN).
                           toString()); // �ؓ���
        fAgent.sendMessage(yesterdayBalance.get(UCMemberProfileCore.
                                                LONG_UNREALIZED_PROFIT).
                           toString()); // ���������v
        fAgent.sendMessage(yesterdayBalance.get(UCMemberProfileCore.LONG_MARGIN).
                           toString()); // �a���؋���
        fAgent.sendMessage(yesterdayBalance.get(UCMemberProfileCore.
                                                LONG_SUM_OF_FEE).toString()); // ���x�����萔��
        fAgent.sendMessage(yesterdayBalance.get(UCMemberProfileCore.
                                                LONG_SUM_OF_INTEREST).toString()); // ���x��������
        fAgent.sendMessage(yesterdayBalance.get(UCMemberProfileCore.LONG_CASH).
                           toString()); // �ۗL����
        fAgent.sendMessage(yesterdayBalance.get(UCMemberProfileCore.LONG_PROFIT).
                           toString()); // �������v
        HashMap todayBalance = (HashMap) fData.get(UCMemberProfileCore.
            HASHMAP_TODAY_BALANCE); // �������x
        fAgent.sendMessage(todayBalance.get(UCMemberProfileCore.
                                            LONG_INITIAL_CASH).toString()); // ����������
        fAgent.sendMessage(todayBalance.get(UCMemberProfileCore.LONG_LOAN).
                           toString()); // �ؓ���
        fAgent.sendMessage(todayBalance.get(UCMemberProfileCore.
                                            LONG_UNREALIZED_PROFIT).toString()); // ���������v
        fAgent.sendMessage(todayBalance.get(UCMemberProfileCore.LONG_MARGIN).
                           toString()); // �a���؋���
        fAgent.sendMessage(todayBalance.get(UCMemberProfileCore.LONG_SUM_OF_FEE).
                           toString()); // ���x�����萔��
        fAgent.sendMessage(todayBalance.get(UCMemberProfileCore.
                                            LONG_SUM_OF_INTEREST).toString()); // ���x��������
        fAgent.sendMessage(todayBalance.get(UCMemberProfileCore.LONG_CASH).
                           toString()); // �ۗL����
        fAgent.sendMessage(todayBalance.get(UCMemberProfileCore.LONG_PROFIT).
                           toString()); // �������v
        HashMap position = (HashMap) fData.get(UCMemberProfileCore.
                                               HASHMAP_POSITION); // �|�W�V����
        fAgent.sendMessage(position.get(UCMemberProfileCore.
            LONG_SUM_OF_SELL_POSITIONS_UNTIL_YESTERDAY).toString()); // �O���܂ł̔��|�W�V�����̍��v
        fAgent.sendMessage(position.get(UCMemberProfileCore.
            LONG_SUM_OF_BUY_POSITIONS_UNTIL_YESTERDAY).toString()); // �O���܂ł̔��|�W�V�����̍��v
        fAgent.sendMessage(position.get(UCMemberProfileCore.
                                        LONG_TODAY_SELL_POSITIONS).toString()); // �����̔��|�W�V�����̍��v
        fAgent.sendMessage(position.get(UCMemberProfileCore.
                                        LONG_TODAY_BUY_POSITIONS).toString()); // �����̔��|�W�V�����̍��v
      } else {
        fAgent.sendMessage("+ERROR " + fCommandStatus.getErrorCode());
        fAgent.sendMessage(fCommandStatus.getErrorMessage());
      }
    } catch (Exception e) {
      fAgent.sendMessage("+ERROR " + ICommand.INVALID_ARGUMENTS);
      fAgent.sendMessage("USAGE: MEMBERPROFILE");
    }
    fAgent.flushMessage();
    return fCommandStatus;
  }

}
