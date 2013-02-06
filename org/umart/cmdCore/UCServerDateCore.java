package org.umart.cmdCore;

import java.util.*;

/**
 * UCServerDateCore�N���X�́C�T�[�o�o�ߖ⍇���̂��߂̃R�}���h�I�u�W�F�N�g�ł���D
 * @author ����@��
 */
public abstract class UCServerDateCore implements ICommand {

  /** �o�ߓ������������߂̃L�[ */
  public static final String INT_DAY = "INT_DAY";

  /** ���݂̔񂹉񐔂��������߂̃L�[ */
  public static final String INT_BOARD_NO = "INT_BOARD_NO";

  /** �R�}���h�� */
  public static final String CMD_NAME = "ServerDate";

  /** �ʖ� */
  public static final String CMD_ALIAS = "";

  /** �R�}���h�̎��s��� */
  protected UCommandStatus fStatus;

  /** ���ʂ��i�[���邽�߂�HashMap */
  protected HashMap fServerDateInfo;

  /**
   * Constructor for UCServerDateCore.
   */
  public UCServerDateCore() {
    super();
    fStatus = new UCommandStatus();
    fServerDateInfo = new HashMap();
  }

  /**
   * 	@see org.umart.cmdCore.ICommand#isNameEqualTo(String)
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
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<" + CMD_NAME + ">>");
    System.out.println(fServerDateInfo.toString());
  }

  /**
   * @see org.umart.cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = fServerDateInfo.get(INT_DAY) + " "
        + fServerDateInfo.get(INT_BOARD_NO);
    return result;
  }

  /**
   * ���ʂ�Ԃ��D
   * @return ����
   */
  public HashMap getResults() {
    return fServerDateInfo;
  }

}
