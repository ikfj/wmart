package org.umart.cmdCore;

import java.util.*;

/**
 * UCSpotIntervalCore�N���X��, �������i�̍X�V�p�x�̖₢���킹�̂��߂�
 * �R�}���h�I�u�W�F�N�g�ł���.
 * @author �암 �S�i
 */
public abstract class UCSpotIntervalCore implements ICommand {

  /** �������i�̍X�V�p�x���������߂̃L�[(�l��Integer�I�u�W�F�N�g) */
  public static final String INT_SPOT_INTERVAL = "INT_SPOT_INTERVAL";

  /** �R�}���h�� */
  public static final String CMD_NAME = "SpotInterval";

  /** �R�}���h�̎��s��� */
  protected UCommandStatus fStatus;

  /** ���ʂ��i�[���邽�߂�HashMap */
  protected HashMap fIntervalInfo;

  /**
   * �R���X�g���N�^
   */
  public UCSpotIntervalCore() {
    super();
    fIntervalInfo = new HashMap();
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
    return result;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * �������i�̍X�V�p�x��Ԃ��D
   * @return �������i�̍X�V�p�x
   */
  public HashMap getResults() {
    return fIntervalInfo;
  }

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<SpotInterval>>");
    System.out.println("SpotInterval:"
                       + fIntervalInfo.get(INT_SPOT_INTERVAL));
  }
}
