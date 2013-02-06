package org.umart.cmdCore;

import java.util.*;

/**
 * UCOrderCancelCore�N���X��, ��������̂��߂̃R�}���h�I�u�W�F�N�g�ł���.
 * @author ���� ��
 */
public abstract class UCOrderCancelCore implements ICommand {

  /** ����ID���������߂̃L�[(�l��Integer�I�u�W�F�N�g) */
  public static final String LONG_ORDER_ID = "LONG_ORDER_ID";

  /** �����t�������������߂̃L�[(�l��String�I�u�W�F�N�g) */
  public static final String STRING_CANCEL_TIME = "STRING_CANCEL_TIME";

  /** ������ʂ��������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_CANCEL_VOLUME = "LONG_CANCEL_VOLUME";

  /** �������ʂ��������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_UNCANCEL_VOLUME = "LONG_UNCANCEL_VOLUME";

  /** �R�}���h�� */
  public static final String CMD_NAME = "OrderCancel";

  /** �ʖ� */
  public static final String CMD_ALIAS = "202";

  /** ����ID */
  protected long fOrderID;

  /** �R�}���h�̎��s��� */
  protected UCommandStatus fStatus;

  /** ���ʂ��i�[���邽�߂�HashMap */
  protected HashMap fCancelInfo;

  /**
   * �R���X�g���N�^
   */
  public UCOrderCancelCore() {
    super();
    fOrderID = -1;
    fCancelInfo = new HashMap();
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
    try {
      fOrderID = Long.parseLong(st.nextToken());
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * �R�}���h���s�ɕK�v�Ȉ�����ݒ肷��D
   * @param orderID
   */
  public void setArguments(long orderID) {
    fOrderID = orderID;
  }

  /**
   * @see org.umart.cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = fCancelInfo.get(STRING_CANCEL_TIME) + " "
        + fCancelInfo.get(LONG_CANCEL_VOLUME) + " "
        + fCancelInfo.get(LONG_UNCANCEL_VOLUME);
    return result;
  }

  /**
   * �����������Ԃ��D
   * @return ����������
   */
  public HashMap getResults() {
    return fCancelInfo;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<OrderCancel " + fOrderID + ">>");
    System.out.println("OrderID:" + fCancelInfo.get(LONG_ORDER_ID) + ","
                       + "CancelTime:" + fCancelInfo.get(STRING_CANCEL_TIME)
                       + ","
                       + "CancelVolume:" + fCancelInfo.get(LONG_CANCEL_VOLUME)
                       + ","
                       + "UncanceldVolume:"
                       + fCancelInfo.get(LONG_UNCANCEL_VOLUME));
  }
}
