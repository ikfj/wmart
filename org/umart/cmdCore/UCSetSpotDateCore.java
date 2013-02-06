package org.umart.cmdCore;

import java.util.*;

/**
 * UCSetScheduleCore�N���X�́C�������i�n��J�n�|�C���g�ݒ�̂��߂̃R�}���h�I�u�W�F�N�g�ł���D
 * @author ����@��
 */
public abstract class UCSetSpotDateCore implements ICommand {

  /** �R�}���h�� */
  public static final String CMD_NAME = "SetSpotDate";

  /** �ʖ� */
  public static final String CMD_ALIAS = "";

  /** �R�}���h�̎��s��� */
  protected UCommandStatus fStatus;

  /** �������i�n��J�n�|�C���g */
  protected int fStartPoint;

  /**
   * Constructor for UCSetSpotDateCore.
   */
  public UCSetSpotDateCore() {
    super();
    fStatus = new UCommandStatus();
    fStartPoint = -1;
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
    try {
      fStartPoint = Integer.valueOf(st.nextToken()).intValue();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 	�R�}���h���s�ɕK�v�Ȉ�����ݒ肷��D
   * @param startPoint �������i�n��J�n�|�C���g
   */
  public void setArguments(int startPoint) {
    fStartPoint = startPoint;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<" + CMD_NAME + " " + fStartPoint + ">>");
  }

  /**
   * @see org.umart.cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = "";
    return result;
  }

}
