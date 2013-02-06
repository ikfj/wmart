package org.umart.cmdCore;

import java.util.*;

/**
 * UCMarketStatusCore�N���X��,
 * �}�[�P�b�g��Ԗ₢���킹�̂��߂̃R�}���h�I�u�W�F�N�g�ł���.
 * @author �암 �S�i
 */
public abstract class UCMarketStatusCore implements ICommand {

  /** �}�[�P�b�g��Ԃ��������߂̃L�[(�l��Integer�I�u�W�F�N�g) */
  public static final String INT_MARKET_STATUS = "INT_MARKET_STATUS";

  /** �R�}���h�� */
  public static final String CMD_NAME = "MarketStatus";

  /** �ʖ� */
  public static final String CMD_ALIAS = "104";

  /** �R�}���h�̎��s��� */
  protected UCommandStatus fStatus;

  /** ���ʂ��i�[���邽�߂�HashMap */
  protected HashMap fMarketInfo;

  /**
   * �R���X�g���N�^
   */
  public UCMarketStatusCore() {
    super();
    fMarketInfo = new HashMap();
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
    String result = "" + fMarketInfo.get(INT_MARKET_STATUS);
    return result;
  }

  /**
   * HashMap��Ԃ��D
   * @return HashMap
   */
  public HashMap getMarketInfo() {
    return fMarketInfo;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<MarketStatus>>");
    System.out.println("MarketStatus:"
                       + fMarketInfo.get(INT_MARKET_STATUS));
  }
}
