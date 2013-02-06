package org.umart.cmdCore;

import java.util.*;

/**
 * UCServerTimeCore�N���X�́C�T�[�o�����⍇���̂��߂̃R�}���h�I�u�W�F�N�g�ł���D
 * @author ����@��
 */
public abstract class UCServerTimeCore implements ICommand {

  /** �T�[�o�������������߂̃L�[ */
  public static final String STRING_SERVER_TIME = "STRING_SERVER_TIME";

  /** �R�}���h�� */
  public static final String CMD_NAME = "ServerTime";

  /** �ʖ� */
  public static final String CMD_ALIAS = "106";

  /** �R�}���h�̎��s��� */
  protected UCommandStatus fStatus;

  /** ���ʂ��i�[���邽�߂�HashMap */
  protected HashMap fServerTimeInfo;

  /**
   * Constructor for UCServerTimeCore.
   */
  public UCServerTimeCore() {
    super();
    fStatus = new UCommandStatus();
    fServerTimeInfo = new HashMap();
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
    System.out.println(fServerTimeInfo.toString());
  }

  /**
   * @see org.umart.cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = fServerTimeInfo.get(STRING_SERVER_TIME).toString();
    return result;
  }

  /**
   * ���ʂ�Ԃ��D
   * @return ����
   */
  public HashMap getResults() {
    return fServerTimeInfo;
  }

}
