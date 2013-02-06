package org.umart.cmdCore;

import java.util.*;

/**
 * ���Ɖ�̂��߂�SVMP�R�}���h�̒��ۃN���X�ł��D
 * @author �암 �S�i
 */
public abstract class UCExecutionsCore implements ICommand {

  /** ���ID���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_CONTRACT_ID = "LONG_CONTRACT_ID";

  /** ��莞��(������)���������߂̃L�[(�l��String�I�u�W�F�N�g) */
  public static final String STRING_CONTRACT_TIME = "STRING_CONTRACT_TIME";

  /** ����ID���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_ORDER_ID = "LONG_ORDER_ID";

  /** ����ID���������߂̃L�[(�l��String�I�u�W�F�N�g) */
  public static final String STRING_BRAND_NAME = "STRING_BRAND_NAME";

  /** �V�K�ԍϋ敪���������߂̃L�[(�l��Integer�I�u�W�F�N�g) */
  public static final String INT_NEW_REPAY = "INT_NEW_REPAY";

  /** �����敪���������߂̃L�[(�l��Integer�I�u�W�F�N�g) */
  public static final String INT_SELL_BUY = "INT_SELL_BUY";

  /** ���i���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_PRICE = "LONG_PRICE";

  /** ��萔�ʂ��������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_VOLUME = "LONG_VOLUME";

  /** �R�}���h�� */
  public static final String CMD_NAME = "Executions";

  /** �ʖ� */
  public static final String CMD_ALIAS = "204";

  /** �R�}���h�̎��s��� */
  protected UCommandStatus fStatus;

  /** ���ʂ��i�[���邽�߂�ArrayList */
  protected ArrayList fExecutionsArray;

  /**
   * �R���X�g���N�^
   */
  public UCExecutionsCore() {
    super();
    fExecutionsArray = new ArrayList();
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
    Iterator itr = fExecutionsArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      result += os.get(LONG_CONTRACT_ID) + " "
                 + os.get(STRING_CONTRACT_TIME) + " "
                 + os.get(LONG_ORDER_ID) + " "
                 + os.get(STRING_BRAND_NAME) + " "
                 + os.get(INT_NEW_REPAY) + " "
                 + os.get(INT_SELL_BUY) + " "
                 + os.get(LONG_PRICE) + " "
                 + os.get(LONG_VOLUME);
    }
    return result;
  }

  /**
   * ������Ԃ��D
   * @return �����
   */
  public ArrayList getResults() {
    return fExecutionsArray;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<Executions>>");
    Iterator itr = fExecutionsArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      System.out.println("ContractID:" + os.get(LONG_CONTRACT_ID) + ","
                          + "ContractTime:" + os.get(STRING_CONTRACT_TIME) + ","
                          + "OrderID:" + os.get(LONG_ORDER_ID) + ","
                          + "BrandName:" + os.get(STRING_BRAND_NAME) + ","
                          + "NewRepay:" + os.get(INT_NEW_REPAY) + ","
                          + "SellBuy:" + os.get(INT_SELL_BUY) + ","
                          + "Price:" + os.get(LONG_PRICE) + ","
                          + "Volume:" + os.get(LONG_VOLUME));
    }
  }
}
