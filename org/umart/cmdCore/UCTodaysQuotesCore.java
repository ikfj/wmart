package org.umart.cmdCore;

import java.util.*;

/**
 * UCTodaysQuotesCore�N���X��, �������i�Ɖ�̂��߂̃R�}���h�I�u�W�F�N�g�ł���D
 * @author �암 �S�i
 */
public abstract class UCTodaysQuotesCore implements ICommand {

  /** ���������������߂̃L�[(�l��String�I�u�W�F�N�g) */
  public static final String STRING_BRAND_NAME = "STRING_BRAND_NAME";

  /** ���t���������߂̃L�[(�l��Integer�I�u�W�F�N�g) */
  public static final String INT_DATE = "INT_DATE";

  /** ���݂̔񂹉񐔂��������߂̃L�[(�l��Integer�I�u�W�F�N�g) */
  public static final String INT_BOARD_NO = "INT_BOARD_NO";

  /** �������������߂̃L�[(�l��String�I�u�W�F�N�g) */
  public static final String STRING_TIME = "STRING_TIME";

  /** ��艿�i���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_PRICE = "LONG_PRICE";

  /** ��萔�ʂ��������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_VOLUME = "INT_VOLUME";

  /** ����C�z���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_ASKED_QUOTATION = "LONG_ASKED_QUOTATION";

  /** �����C�z���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_BID_QUOTATION = "LONG_BID_QUOTATION";

  /** �R�}���h�� */
  public static final String CMD_NAME = "TodaysQuotes";

  /** �ʖ� */
  public static final String CMD_ALIAS = "301";

  /** ������ */
  protected String fBrandName;

  /** �K�v�ȏ��̔񂹐� */
  protected int fNoOfBoards;

  /** �R�}���h�̎��s��� */
  protected UCommandStatus fStatus;

  /** ���ʂ��i�[���邽�߂�ArrayList */
  protected ArrayList fQuotesArray;

  /**
   * �R���X�g���N�^
   */
  public UCTodaysQuotesCore() {
    super();
    fBrandName = "";
    fNoOfBoards = -1;
    fQuotesArray = new ArrayList();
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
      fBrandName = st.nextToken();
      fNoOfBoards = Integer.parseInt(st.nextToken());
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * �R�}���h���s�ɕK�v�Ȉ�����ݒ肷��D
   * @param brandName
   * @param noOfBoards
   */
  public void setArguments(String brandName, int noOfBoards) {
    fBrandName = brandName;
    fNoOfBoards = noOfBoards;
  }

  /**
   * @see org.umart.cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = "";
    Iterator itr = fQuotesArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      result += os.get(STRING_BRAND_NAME)
          + " "
          + os.get(INT_DATE)
          + " "
          + os.get(INT_BOARD_NO)
          + " "
          + os.get(STRING_TIME)
          + " "
          + os.get(LONG_PRICE)
          + " "
          + os.get(LONG_VOLUME)
          + " "
          + os.get(LONG_ASKED_QUOTATION)
          + " "
          + os.get(LONG_BID_QUOTATION)
          + " ";
    }
    return result;
  }

  /**
   * �������i�Ɖ��Ԃ��D
   * @return �������i�Ɖ�
   */
  public ArrayList getResults() {
    return fQuotesArray;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<TodaysQuotes " + fBrandName + " " + fNoOfBoards +
                       ">>");
    Iterator itr = fQuotesArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      System.out.println(os.toString());
    }
  }
}
