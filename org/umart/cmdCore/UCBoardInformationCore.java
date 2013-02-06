package org.umart.cmdCore;

import java.util.*;

/**
 * �����擾���邽�߂�SVMP�R�}���h�̒��ۃN���X�ł��D
 * @author �암 �S�i
 */
public abstract class UCBoardInformationCore implements ICommand {

  /** �񂹂��s��ꂽ����(������)���������߂̃L�[(�l��String�I�u�W�F�N�g) */
  public static final String STRING_LAST_UPDATE_TIME =
      "STRING_LAST_UPDATE_TIME";

  /** �i�i���i�C���������C���������j�̔z��j���������߂̃L�[�i�l��ArrayList�I�u�W�F�N�g�j */
  public static final String ARRAYLIST_BOARD = "ARRAYLIST_BOARD";

  /** ���i���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_PRICE = "LONG_PRICE";

  /** �����������ʂ��������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_BUY_VOLUME = "LONG_BUY_VOLUME";

  /** ���蒍�����ʂ��������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_SELL_VOLUME = "LONG_SELL_VOLUME";

  /** �R�}���h�� */
  public static final String CMD_NAME = "BoardInformation";

  /** �ʖ� */
  public static final String CMD_ALIAS = "302";

  /** �R�}���h�̎��s��� */
  protected UCommandStatus fStatus;

  /** ���ʂ��i�[���邽�߂�HashMap */
  protected HashMap fBoardInfo;

  /**
   * �R���X�g���N�^
   */
  public UCBoardInformationCore() {
    fBoardInfo = new HashMap();
    fStatus = new UCommandStatus();
  }

  /**
   * @see org.umart.cmdCore.ICommand#isNameEqualTo(String)
   */
  public boolean isNameEqualTo(String name) {
    if (name.equalsIgnoreCase(CMD_NAME) || name.equals(CMD_ALIAS)) {
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
    String result = "";
    result += fBoardInfo.get(STRING_LAST_UPDATE_TIME);
    ArrayList board = (ArrayList) fBoardInfo.get(ARRAYLIST_BOARD);
    Iterator itr = board.iterator();
    while (itr.hasNext()) {
      HashMap elem = (HashMap) itr.next();
      result += " " + elem.get(LONG_PRICE)
          + " " + elem.get(LONG_BUY_VOLUME)
          + " " + elem.get(LONG_SELL_VOLUME);
    }
    return result;
  }

  /**
   * ���(HashMap)��Ԃ��D
   * @return ���
   */
  public HashMap getResults() {
    return fBoardInfo;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<BoardInformation>>");
    System.out.println("LastUpdateTime:" +
                       fBoardInfo.get(STRING_LAST_UPDATE_TIME));
    Iterator itr = ( (ArrayList) fBoardInfo.get(ARRAYLIST_BOARD)).iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      System.out.println("Price:" + os.get(LONG_PRICE) + ","
                         + "BuyVolume:" + os.get(LONG_BUY_VOLUME) + ","
                         + "SellVolume:" + os.get(LONG_SELL_VOLUME));
    }
  }
}
