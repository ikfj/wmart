package org.umart.cmdCore;

import java.util.*;

/**
 * UCPositionCore�N���X��, �|�W�V�����Ɖ�̂��߂̃R�}���h�I�u�W�F�N�g�ł���.
 * @author �암 �S�i
 */
public abstract class UCPositionCore implements ICommand {

  /** �����̔��茚�ʐ����������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_TODAY_SELL = "LONG_TODAY_SELL";

  /** �����̔������ʐ����������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_TODAY_BUY = "LONG_TODAY_BUY";

  /** �O���܂ł̔��茚�ʐ����������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_YESTERDAY_SELL = "LONG_YESTERDAY_SELL";

  /** �O���܂ł̔������ʐ����������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_YESTERDAY_BUY = "LONG_YESTERDAY_BUY";

  /** �R�}���h�� */
  public static final String CMD_NAME = "Position";

  /** �R�}���h�̎��s��� */
  protected UCommandStatus fStatus;

  /** ���ʂ��i�[���邽�߂�HashMap */
  protected HashMap fPositionInfo;

  /**
   * �R���X�g���N�^
   */
  public UCPositionCore() {
    super();
    fPositionInfo = new HashMap();
    fStatus = new UCommandStatus();
  }

  /**
   * @see org.umart.cmdCore.ICommand#isNameEqualTo(String)
   */
  public boolean isNameEqualTo(String name) {
    if (name.equalsIgnoreCase(CMD_NAME)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * @see org.umart.cmdCore.ICommand#getName()
   */
  public String getName() {
    return CMD_NAME;
  }

  /**
   * @see org.umart.cmdCore.ICommand#setArguments(StringTokenizer)
   */
  public boolean setArguments(StringTokenizer st) {
    return true;
  }

  /**
   * @see org.umart.cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = fPositionInfo.get(LONG_TODAY_SELL) + " "
        + fPositionInfo.get(LONG_TODAY_BUY) + " "
        + fPositionInfo.get(LONG_YESTERDAY_SELL) + " "
        + fPositionInfo.get(LONG_YESTERDAY_BUY);
    return result;
  }

  /**
   * �����˗�����Ԃ��D
   * @return �����˗����
   */
  public HashMap getResults() {
    return fPositionInfo;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<Position>>");
    System.out.println("TodaySell:" + fPositionInfo.get(LONG_TODAY_SELL) + ","
                       + "TodayBuy:" + fPositionInfo.get(LONG_TODAY_BUY) + ","
                       + "YesterdaySell:" +
                       fPositionInfo.get(LONG_YESTERDAY_SELL) + ","
                       + "YesterdayBuy:" + fPositionInfo.get(LONG_YESTERDAY_BUY));
  }
}
