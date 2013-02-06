package org.umart.cmdCore;

import java.util.*;

/**
 * ����������擾���邽�߂�SVMP�R�}���h�̒��ۃN���X�ł��D
 * ��������Ƃ́C������ۗ̕L�����C������̊Ǘ����̔��|�W�V�����C���|�W�V�����C������̂��Ƃł��D
 * @author isao
 *
 */
public abstract class UCExchangeProfileCore implements ICommand {

  /** �R�}���h�� */
  public static final String CMD_NAME = "ExchangeProfile";

  /** �R�}���h�̎��s���ʂ̏�� */
  protected UCommandStatus fCommandStatus;

  /** �������� */
  protected HashMap fData;

  /** �ۗL�������������߂̃L�[ */
  public static final String LONG_CASH = "LONG_CASH";

  /** ������̊Ǘ����̔��|�W�V�������������߂̃L�[ */
  public static final String LONG_SELL_POSITION = "LONG_SELL_POSITION";

  /** ������̊Ǘ����̔��|�W�V�������������߂̃L�[ */
  public static final String LONG_BUY_POSITION = "LONG_BUY_POSITION";

  /** ��������������߂̃L�[ */
  public static final String INT_NO_OF_MEMBERS = "INT_NO_OF_MEMBERS";

  /**�@�R���X�g���N�^ */
  public UCExchangeProfileCore() {
    super();
    fCommandStatus = new UCommandStatus();
    fData = new HashMap();
  }

  /*
   * (non-Javadoc)
   * @see org.umart.cmdCore.ICommand#isNameEqualTo(java.lang.String)
   */
  public boolean isNameEqualTo(String name) {
    if (name.equalsIgnoreCase(CMD_NAME)) {
      return true;
    } else {
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * @see org.umart.cmdCore.ICommand#getName()
   */
  public String getName() {
    return CMD_NAME;
  }

  /*
   * (non-Javadoc)
   * @see org.umart.cmdCore.ICommand#setArguments(java.util.StringTokenizer)
   */
  public boolean setArguments(StringTokenizer st) {
    try {
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
  
  /*
   * (non-Javadoc)
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println(fData.toString());
  }

  /**
   * ���������Ԃ��D
   * @return ��������
   */
  public HashMap getData() {
    return fData;
  }

  /*
   * (non-Javadoc)
   * @see org.umart.cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String returnStr = "";
    returnStr += fData.get(UCExchangeProfileCore.LONG_CASH).toString() + "\n";
    returnStr += fData.get(UCExchangeProfileCore.LONG_SELL_POSITION).toString() + "\n";
    returnStr += fData.get(UCExchangeProfileCore.LONG_BUY_POSITION).toString() + "\n";
    returnStr += fData.get(UCExchangeProfileCore.INT_NO_OF_MEMBERS).toString() + "\n";
    return returnStr;
  }

}
