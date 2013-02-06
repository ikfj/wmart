package org.umart.cmdCore;

import java.util.*;

/**
 * ���ׂĂ̎Q���҂̔����|�W�V�������Ɖ�邽�߂�SVMP�R�}���h�̒��ۃN���X�ł��D
 * @author �암 �S�i
 */
public abstract class UCAllPositionsCore implements ICommand {

  /** �G�[�W�F���g�����������߂̃L�[(�l��String�I�u�W�F�N�g) */
  public static final String STRING_NAME = "STRING_NAME";

  /** ����܂ł̔��茚�ʐ����������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_SELL_POSITION = "LONG_SELL_POSITION";

  /** ����܂ł̔������ʐ����������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_BUY_POSITION = "LONG_BUY_POSITION";

  /** �R�}���h�� */
  public static final String CMD_NAME = "AllPositions";

  /** �R�}���h�̎��s��� */
  protected UCommandStatus fStatus;

  /** ���ʂ��i�[���邽�߂�ArrayList */
  protected ArrayList fAllPositionsArray;

  /**
   * �R���X�g���N�^
   */
  public UCAllPositionsCore() {
    super();
    fAllPositionsArray = new ArrayList();
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
    String result = "";
    Iterator itr = fAllPositionsArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      result += os.get(STRING_NAME) + " "
          + os.get(LONG_SELL_POSITION) + " "
          + os.get(LONG_BUY_POSITION) + " ";
    }
    return result;
  }

  /**
   * ���ׂĂ̎Q���҂̔����|�W�V������Ԃ��D
   * @return ���ׂĂ̎Q���҂̔����|�W�V����
   */
  public ArrayList getResults() {
    return fAllPositionsArray;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<AllPositions>>");
    Iterator itr = fAllPositionsArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      System.out.println("Name:" + os.get(STRING_NAME) + ","
                         + "SellPosition:" + os.get(LONG_SELL_POSITION) + ","
                         + "BuyPosition:" + os.get(LONG_BUY_POSITION));
    }
  }
}
