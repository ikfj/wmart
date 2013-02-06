package org.umart.cmdCore;

import java.util.*;

/**
 * UCScheduleCore�N���X�́C�^�p�\��⍇�����s���R�}���h�I�u�W�F�N�g�ł���D
 * @author ����@��
 */
public abstract class UCScheduleCore implements ICommand {

  /** �T�[�o�^�p�������������߂̃L�[ */
  public static final String INT_MAX_DAY = "INT_MAX_DAY";

  /** �P���̔񂹉񐔂��������߂̃L�[ */
  public static final String INT_NO_OF_BOARDS = "INT_NO_OF_BOARDS";

  /** �R�}���h�� */
  public static final String CMD_NAME = "Schedule";

  /** �ʖ� */
  public static final String CMD_ALIAS = "";

  /** �R�}���h�̎��s��� */
  protected UCommandStatus fStatus;

  /** ���ʂ��i�[���邽�߂�HashMap */
  protected HashMap fScheduleInfo;

  /**
   * Constructor for UCScheduleCore.
   */
  public UCScheduleCore() {
    super();
    fStatus = new UCommandStatus();
    fScheduleInfo = new HashMap();
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
   * @see ICommand#getResultString()
   */
  public String getResultString() {
    String result = fScheduleInfo.get(INT_MAX_DAY) + " "
        + fScheduleInfo.get(INT_NO_OF_BOARDS);
    return result;
  }

  /**
   * ���ʂ�Ԃ��D
   * @return ����
   */
  public HashMap getResults() {
    return fScheduleInfo;
  }

  /**
   * @see ICCommand#setArguments(StringTokenizer)
   */
  public boolean setArguments(StringTokenizer st) {
    return true;
  }

  /**
   * @see ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<" + CMD_NAME + ">>");
    System.out.println(fScheduleInfo.toString());
  }

}
