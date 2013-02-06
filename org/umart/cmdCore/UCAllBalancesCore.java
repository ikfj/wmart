package org.umart.cmdCore;

import java.util.*;

/**
 * UCAllBalancesCore�N���X��,
 * ���ׂĂ̎Q���҂̌����c�����Ɖ�邽�߂̃R�}���h�I�u�W�F�N�g�ł���D
 * @author �암 �S�i
 */
public abstract class UCAllBalancesCore implements ICommand {

  /** �G�[�W�F���g�����������߂̃L�[(�l��String�I�u�W�F�N�g) */
  public static final String STRING_NAME = "STRING_NAME";

  /** �����c�����������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_CASH = "LONG_CASH";

  /** �؋����z���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_MARGIN = "LONG_MARGIN";

  /** ���������v���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_UNREALIZED_PROFIT =
      "LONG_UNREALIZED_PROFIT";

  /** �������v���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_SETTLED_PROFIT = "LONG_SETTLED_PROFIT";

  /** �����������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_FEE = "LONG_FEE";

  /** �Z���������������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_INTEREST = "LONG_INTEREST";

  /** ������Z���z���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_LOAN = "LONG_LOAN";

  /** �]�T���z���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_SURPLUS = "LONG_SURPLUS";

  /** ����܂ł̔��茚�ʐ����������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_SELL_POSITION = "LONG_SELL_POSITION";

  /** ����܂ł̔������ʐ����������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_BUY_POSITION = "LONG_BUY_POSITION";

  /** �R�}���h�� */
  public static final String CMD_NAME = "AllBalances";

  /** �R�}���h�̎��s��� */
  protected UCommandStatus fStatus;

  /** ���ʂ��i�[���邽�߂�ArrayList */
  protected ArrayList fAllBalancesArray;

  /**
   * �R���X�g���N�^
   */
  public UCAllBalancesCore() {
    super();
    fAllBalancesArray = new ArrayList();
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
    Iterator itr = fAllBalancesArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      result += os.get(STRING_NAME) + " "
          + os.get(LONG_CASH) + " "
          + os.get(LONG_MARGIN) + " "
          + os.get(LONG_UNREALIZED_PROFIT) + " "
          + os.get(LONG_SETTLED_PROFIT) + " "
          + os.get(LONG_FEE) + " "
          + os.get(LONG_INTEREST) + " "
          + os.get(LONG_LOAN) + " "
          + os.get(LONG_SURPLUS) + " "
          + os.get(LONG_SELL_POSITION) + " "
          + os.get(LONG_BUY_POSITION);
    }
    return result;
  }

  /**
   * ���ׂĂ̎Q���҂̌����c����Ԃ��D
   * @return ���ׂĂ̎Q���҂̌����c��
   */
  public ArrayList getResults() {
    return fAllBalancesArray;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<AllBalances>>");
    Iterator itr = fAllBalancesArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      System.out.println("Name:" + os.get(STRING_NAME) + ","
                         + "Cash:" + os.get(LONG_CASH) + ","
                         + "Margin:" + os.get(LONG_MARGIN) + ","
                         + "UnrealizedProfit:" + os.get(LONG_UNREALIZED_PROFIT) +
                         ","
                         + "SettledProfit:" + os.get(LONG_SETTLED_PROFIT) + ","
                         + "Fee:" + os.get(LONG_FEE) + ","
                         + "Interest:" + os.get(LONG_INTEREST) + ","
                         + "Loan:" + os.get(LONG_LOAN) + ","
                         + "Surplus:" + os.get(LONG_SURPLUS) + ","
                         + "SellPosition:"
                         + os.get(LONG_SELL_POSITION) + ","
                         + "BuyPosition:" + os.get(LONG_BUY_POSITION));
    }
  }
}
