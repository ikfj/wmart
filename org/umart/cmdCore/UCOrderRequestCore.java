package org.umart.cmdCore;

import java.util.*;

/**
 * UCOrderRequestCore�N���X��, �����˗��̂��߂̃R�}���h�I�u�W�F�N�g�ł���.
 * @author �암 �S�i
 */
public abstract class UCOrderRequestCore implements ICommand {

  /** ����ID���������߂̃L�[(�l��Long�I�u�W�F�N�g) */
  public static final String LONG_ORDER_ID = "LONG_ORDER_ID";

  /** ��������(������)���������߂̃L�[(�l��String�I�u�W�F�N�g) */
  public static final String STRING_ORDER_TIME = "STRING_ORDER_TIME";

  /** �R�}���h�� */
  public static final String CMD_NAME = "OrderRequest";

  /** �ʖ� */
  public static final String CMD_ALIAS = "201";

  /** ������ */
  protected String fBrandName;

  /** �V�K�ԍϋ敪 */
  protected int fNewRepay;

  /** �����敪 */
  protected int fSellBuy;

  /** ���s�w�l�敪 */
  protected int fMarketLimit;

  /** ��]������i */
  protected long fPrice;

  /** ��]������� */
  protected long fVolume;

  /** �R�}���h�̎��s��� */
  protected UCommandStatus fStatus;

  /** ���ʂ��i�[���邽�߂�HashMap */
  protected HashMap fRequestInfo;

  /**
   * �R���X�g���N�^
   */
  public UCOrderRequestCore() {
    super();
    fBrandName = "";
    fNewRepay = -1;
    fSellBuy = -1;
    fMarketLimit = -1;
    fPrice = -1;
    fVolume = -1;
    fRequestInfo = new HashMap();
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
      fNewRepay = Integer.parseInt(st.nextToken());
      fSellBuy = Integer.parseInt(st.nextToken());
      fMarketLimit = Integer.parseInt(st.nextToken());
      fPrice = Long.parseLong(st.nextToken());
      fVolume = Long.parseLong(st.nextToken());
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * �R�}���h���s�ɕK�v�Ȉ�����ݒ肷��D
   * @param brandName
   * @param newRepay
   * @param sellBuy
   * @param marketLimit
   * @param price
   * @param volume
   */
  public void setArguments(String brandName, int newRepay, int sellBuy,
                           int marketLimit, long price, long volume) {
    fBrandName = brandName;
    fNewRepay = newRepay;
    fSellBuy = sellBuy;
    fMarketLimit = marketLimit;
    fPrice = price;
    fVolume = volume;
  }

  /**
   * @see org.umart.cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = fRequestInfo.get(LONG_ORDER_ID) + " "
        + fRequestInfo.get(STRING_ORDER_TIME);
    return result;
  }

  /**
   * �����˗�����Ԃ��D
   * @return �����˗����
   */
  public HashMap getResults() {
    return fRequestInfo;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<OrderRequest " + fBrandName + fNewRepay + fSellBuy
                       + fMarketLimit + fPrice + fVolume + ">>");
    System.out.println("OrderID:" + fRequestInfo.get(LONG_ORDER_ID) + ","
                       + "OrderTime:" + fRequestInfo.get(STRING_ORDER_TIME));
  }
}
