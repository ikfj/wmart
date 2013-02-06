package org.umart.cmdCore;

import java.util.*;

/**
 * ����J�n�����猻�݂܂ł̔����擾���邽�߂�SVMP�R�}���h�̒��ۃN���X�ł��D
 * @author �암 �S�i
 */
public abstract class UCBoardDataCore implements ICommand {

  /** �����������̍��v���������߂̃L�[(���̂�Long�I�u�W�F�N�g) */
  public static final String LONG_TOTAL_BUY_VOLUME = "LONG_TOTAL_BUY_VOLUME";

  /** �Œ�̒������i���������߂̃L�[(���̂�Long�I�u�W�F�N�g) */
  public static final String LONG_MIN_PRICE = "LONG_MIN_PRICE";

  /** �ō��̒������i���������߂̃L�[(���̂�Long�I�u�W�F�N�g) */
  public static final String LONG_MAX_PRICE = "LONG_MAX_PRICE";

  /** ��艿�i���������߂̃L�[(���̂�Long�I�u�W�F�N�g) */
  public static final String LONG_CONTRACT_PRICE = "LONG_CONTRACT_PRICE";

  /** ��萔�ʂ��������߂̃L�[(���̂�Long�I�u�W�F�N�g) */
  public static final String LONG_CONTRACT_VOLUME = "LONG_CONTRACT_VOLUME";

  /** ���i���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_PRICE = "LONG_PRICE";

  /** ���蔃�����������߂̃L�[(�l��Integer�I�u�W�F�N�g) */
  public static final String STRING_SELL_BUY = "STRING_SELL_BUY";

  /** �������ʂ��������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_VOLUME = "LONG_VOLUME";

  /** �R�}���h�� */
  public static final String CMD_NAME = "BoardData";

  /** �R�}���h�̎��s��� */
  protected UCommandStatus fStatus;

  /** ���ʂ��i�[���邽�߂�ArrayList */
  protected ArrayList fBoardDataArray;

  /** ���ʂ��i�[���邽�߂�HashMap */
  protected HashMap fBoardDataInfo;

  /**
   * �R���X�g���N�^
   */
  public UCBoardDataCore() {
    fBoardDataArray = new ArrayList();
    fBoardDataInfo = new HashMap();
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
    result += fBoardDataInfo.get(LONG_TOTAL_BUY_VOLUME) + " "
              + fBoardDataInfo.get(LONG_MIN_PRICE) + " "
              + fBoardDataInfo.get(LONG_MAX_PRICE) + " "
              + fBoardDataInfo.get(LONG_CONTRACT_PRICE) + " "
              + fBoardDataInfo.get(LONG_CONTRACT_VOLUME) + " ";
    Iterator itr = fBoardDataArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      result += os.get(LONG_PRICE) + " " + os.get(STRING_SELL_BUY)
                 + " " + os.get(LONG_VOLUME) + " ";
    }
    return result;
  }

  /**
   * �����(ArrayList)��Ԃ��D
   * @return �����
   */
  public ArrayList getBoardDataArray() {
    return fBoardDataArray;
  }

  /**
   * �����(HashMap)��Ԃ��D
   * @return �����
   */
  public HashMap getBoardDataInfo() {
    return fBoardDataInfo;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<BoardData>>");
    System.out.println("TotalBuyVolume:" + fBoardDataInfo.get(LONG_TOTAL_BUY_VOLUME) + ","
                        + "MinPrice:" + fBoardDataInfo.get(LONG_MIN_PRICE) + ","
                        + "MaxPrice:" + fBoardDataInfo.get(LONG_MAX_PRICE) + ","
                        + "ContractPrice:" + fBoardDataInfo.get(LONG_CONTRACT_PRICE) + ","
                        + "ContractVolume:" + fBoardDataInfo.get(LONG_CONTRACT_VOLUME));
    Iterator itr = fBoardDataArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      System.out.println("Price:" + os.get(LONG_PRICE) + "," 
      		               + "SellBuy:" + os.get(STRING_SELL_BUY) + ","
                         + "Volume:" + os.get(LONG_VOLUME));
    }
  }
}
