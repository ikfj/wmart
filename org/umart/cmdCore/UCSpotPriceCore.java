package org.umart.cmdCore;

import java.util.*;

/**
 * UCSpotPriceCore�N���X��,
 * �������i���擾���邽�߂̃R�}���h�I�u�W�F�N�g�ł���D
 * @author �암 �S�i
 */
public abstract class UCSpotPriceCore implements ICommand {

  /** ���������������߂̃L�[(�l��String�I�u�W�F�N�g) */
  public static final String STRING_BRAND_NAME = "STRING_BRAND_NAME";

  /** ���t���������߂̃L�[(�l��Integer�I�u�W�F�N�g) */
  public static final String INT_DAY = "INT_DAY";

  /** �񂹉񐔂��������߂̃L�[(�l��Integer�I�u�W�F�N�g) */
  public static final String INT_BOARD_NO = "INT_BOARD_NO";

  /** �X�e�b�v�����������߂̃L�[(�l��Integer�I�u�W�F�N�g) */
  public static final String INT_STEP = "INT_STEP";

  /** �������i���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_PRICE = "LONG_PRICE";

  /** �R�}���h�� */
  public static final String CMD_NAME = "SpotPrice";

  /** ������ */
  protected String fBrandName;

  /** �K�v�ȏ��̃X�e�b�v�� */
  protected int fNoOfSteps;

  /** �R�}���h�̎��s��� */
  protected UCommandStatus fStatus;

  /** ���ʂ��i�[���邽�߂�ArrayList */
  protected ArrayList fSpotArray;

  /**
   * �R���X�g���N�^
   */
  public UCSpotPriceCore() {
    super();
    fBrandName = "";
    fNoOfSteps = -1;
    fSpotArray = new ArrayList();
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
    Iterator itr = fSpotArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      result += os.get(STRING_BRAND_NAME)
          + " "
          + os.get(INT_DAY)
          + " "
          + os.get(INT_BOARD_NO)
          + " "
          + os.get(INT_STEP)
          + " "
          + os.get(LONG_PRICE)
          + " ";
    }
    return result;
  }

  /**
   * �������i��Ԃ��D
   * @return �������i�D�擪�̗v�f�́C���߂̉��i���D
   */
  public ArrayList getResults() {
    return fSpotArray;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<SpotPrice " + fBrandName + " " + fNoOfSteps + ">>");
    Iterator itr = fSpotArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      System.out.println(os.toString());
    }
  }
}
