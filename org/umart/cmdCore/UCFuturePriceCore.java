package org.umart.cmdCore;

import java.util.*;

/**
 * �敨���i���擾���邽�߂�SVMP�R�}���h�̒��ۃN���X�ł��D
 * @author �암 �S�i
 */
public abstract class UCFuturePriceCore implements ICommand {

  /** ���������������߂̃L�[(�l��String�I�u�W�F�N�g) */
  public static final String STRING_BRAND_NAME = "STRING_BRAND_NAME";

  /** ���t���������߂̃L�[(�l��Integer�I�u�W�F�N�g) */
  public static final String INT_DAY = "INT_DAY";

  /** �񂹉񐔂��������߂̃L�[(�l��Integer�I�u�W�F�N�g) */
  public static final String INT_BOARD_NO = "INT_BOARD_NO";

  /** �X�e�b�v�����������߂̃L�[(�l��Integer�I�u�W�F�N�g) */
  public static final String INT_STEP = "INT_STEP";

  /** �敨���i���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_PRICE = "LONG_PRICE";

  /** �R�}���h�� */
  public static final String CMD_NAME = "FuturePrice";

  /** ������ */
  protected String fBrandName;

  /** �K�v�ȏ��̃X�e�b�v�� */
  protected int fNoOfSteps;

  /** �R�}���h�̎��s��� */
  protected UCommandStatus fStatus;

  /** ���ʂ��i�[���邽�߂�ArrayList */
  protected ArrayList fFutureArray;

  /**
   * �R���X�g���N�^
   */
  public UCFuturePriceCore() {
    super();
    fBrandName = "";
    fNoOfSteps = -1;
    fFutureArray = new ArrayList();
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
    try {
      fBrandName = st.nextToken();
      fNoOfSteps = Integer.parseInt(st.nextToken());
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * �R�}���h���s�ɕK�v�Ȉ�����ݒ肷��D
   * @param brandName
   * @param noOfSteps
   */
  public void setArguments(String brandName, int noOfSteps) {
    fBrandName = brandName;
    fNoOfSteps = noOfSteps;
  }

  /**
   * @see org.umart.cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = "";
    Iterator itr = fFutureArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      result += os.get(STRING_BRAND_NAME) + " "
                 + os.get(INT_DAY) + " "
          + os.get(INT_BOARD_NO) + " "
          + os.get(INT_STEP) + " "
          + os.get(LONG_PRICE) + " ";
    }
    return result;
  }

  /**
   * �敨���i��Ԃ��D
   * @return �敨���i
   */
  public ArrayList getResults() {
    return fFutureArray;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<FuturePrice " + fBrandName + fNoOfSteps + ">>");
    Iterator itr = fFutureArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      System.out.println(
          "BrandName:"
          + os.get(STRING_BRAND_NAME)
          + ","
          + "Day:"
          + os.get(INT_DAY)
          + ","
          + "BoardNo:"
          + os.get(INT_BOARD_NO)
          + ","
          + "Step:"
          + os.get(INT_STEP)
          + ","
          + "Price:"
          + os.get(LONG_PRICE));
    }
  }
}
