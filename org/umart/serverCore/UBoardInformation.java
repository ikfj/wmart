package org.umart.serverCore;

import java.util.*;

/**
 * �񂹌��ʂ�ێ�����N���X�ł��D
 * ���̃N���X��, ServerManager��GUIClient�Ȃǂ��񂹌��ʂ�
 * �������邽�߂̖₢���킹�Ή����邽�߂ɗp�����܂��D
 * @author �암 �T�i
 * @author �X�� �̕�
 * @author ���� ��
 */
public class UBoardInformation {

  /** �񂹂��s��ꂽ����(������) */
  private Date fLastUpdateTime;

  /** ���(UBoardInfoElement)���i�[���邽�߂̃e�[�u�� */
  private Hashtable fTable;

  /** �����������̍��v */
  private long fTotalBuyVolume;

  /** �Œ�̒������i */
  private long fMinPrice;

  /** �ō��̒������i */
  private long fMaxPrice;

  /** ��艿�i */
  private long fContractPrice;

  /** ��萔�� */
  private long fContractVolume;

  /**
   * UBoardInformation�I�u�W�F�N�g�̐�������я��������s���B
   */
  public UBoardInformation() {
    fLastUpdateTime = null;
    fTable = new Hashtable();
    clear();
  }

  /**
   * UBoardInformation�I�u�W�F�N�g�̕�����Ԃ��B
   * @return UBoardInformation�I�u�W�F�N�g�̕���
   */
  public Object clone() {
    UBoardInformation result = new UBoardInformation();
    result.setLastUpdateTime(fLastUpdateTime);
    Enumeration e = fTable.elements();
    while (e.hasMoreElements()) {
      UBoardInfoElement element = (UBoardInfoElement) e.nextElement();
      UBoardInfoElement copy = (UBoardInfoElement) element.clone();
      Long key = new Long(copy.getPrice());
      result.fTable.put(key, copy);
    }
    return result;
  }

  /**
   * �񂹂�������������(������)��Ԃ��B
   * @return �񂹂�������������(������)
   */
  public Date getLastUpdateTime() {
    return fLastUpdateTime;
  }

  /**
   * �񂹂�������������(������)��ݒ肷��B
   * @param time �񂹂�������������(������)
   */
  public void setLastUpdateTime(Date time) {
    fLastUpdateTime = time;
  }

  /**
   * ��萔�ʂ�ݒ肷��B
   * @param volume ��萔��
   */
  public void setContractVolume(long volume) {
    fContractVolume = volume;
  }

  /**
   * ��萔�ʂ�Ԃ��B
   * @return ��萔��
   */
  public long getContractVolume() {
    return fContractVolume;
  }

  /**
   * ��艿�i��ݒ肷��B
   * @param price ��艿�i
   */
  public void setContractPrice(long price) {
    fContractPrice = price;
  }

  /**
   * ��艿�i��Ԃ��B
   * @return ��艿�i
   */
  public long getContractPrice() {
    return fContractPrice;
  }

  /**
   * ���������s���B
   */
  public void clear() {
    fTable.clear();
    Long key = new Long( -1);
    UBoardInfoElement element = new UBoardInfoElement( -1, 0, 0); //���s
    fTable.put(key, element);
    fTotalBuyVolume = 0;
    fMinPrice = -1;
    fMaxPrice = -1;
    fContractPrice = -1;
    fContractVolume = 0;
  }

  /**
   * �����������̍��v��Ԃ��B
   * @return �����������̍��v
   */
  public long getTotalBuyVolume() {
    return fTotalBuyVolume;
  }

  /**
   * �Œ�̒������i��Ԃ��B
   * @return �Œ�̒������i
   */
  public long getMinPrice() {
    return fMinPrice;
  }

  /**
   * �ō��̒������i��Ԃ��B
   * @return �ō��̒������i
   */
  public long getMaxPrice() {
    return fMaxPrice;
  }

  /**
   * ����o�^����B
   * @param sellBuy �����敪 (��: UOrder.SELL, ��: UOrder.BUY)
   * @param marketLimit �w�l���s�敪 (�w�l: UOrder.LIMIT,
   *                                  ���s: UOrder.MARKET)
   * @param price �������i
   * @param volume ��������
   */
  public void addInformation(int sellBuy, int marketLimit, long price,
                             long volume) {
    if (sellBuy == UOrder.BUY) {
      fTotalBuyVolume += volume;
    }
    if (marketLimit == UOrder.LIMIT
        && (fMinPrice < 0 || fMinPrice > price)) {
      fMinPrice = price;
    }
    if (marketLimit == UOrder.LIMIT && fMaxPrice < price) {
      fMaxPrice = price;
    }
    if (marketLimit == UOrder.MARKET) {
      price = -1;
    }
    updateTable(sellBuy, price, volume);
  }

  /**
   * �e�[�u���̍X�V���s���B
   * @param sellBuy �����敪 (��: UOrder.SELL, ��: UOrder.BUY)
   * @param price �������i (���s�����̏ꍇ, -1)
   * @param volume ��������
   */
  private void updateTable(int sellBuy, long price, long volume) {
    Long key = new Long(price);
    UBoardInfoElement x = (UBoardInfoElement)fTable.get(key);
    if (x == null) {
      long sellVolume = 0;
      long buyVolume = 0;
      if (sellBuy == UOrder.SELL) {
        sellVolume = volume;
      } else {
        buyVolume = volume;
      }
      UBoardInfoElement newElement =
          new UBoardInfoElement(price, sellVolume, buyVolume);
      fTable.put(key, newElement);
    } else {
      if (sellBuy == UOrder.SELL) {
        x.addToSellVolume(volume);
      } else {
        x.addToBuyVolume(volume);
      }
    }
  }

  /**
   * ���������o�͂���B
   */
  public void printOn() {
    System.out.println("Last update time: " + fLastUpdateTime);
    Vector v = getBoardInfoElements();
    Enumeration e = v.elements();
    while (e.hasMoreElements()) {
      UBoardInfoElement element = (UBoardInfoElement) e.nextElement();
      element.printOn();
    }
  }

  /**
   * UBoardInfoElementComparator��, UBoardInfoElement���\�[�g���邽�߂�
   * �p������N���X�ł���B
   */
  private class UBoardInfoElementComparator implements Comparator {
    /**
       UBoardInfoElement�I�u�W�F�N�g�ł���o1��o2�̉��i���r����B<br>
       @param o1 UBoardInfoElement�I�u�W�F�N�g
       @param o2 UBoardInfoElement�I�u�W�F�N�g
       @return -1: o1����o2�̕������i���������ꍇ,
                0: o1��o2�̉��i�������ꍇ,
               +1: o1����o2�̕������i���傫���ꍇ
     */
    public int compare(Object o1, Object o2) {
      UBoardInfoElement e1 = (UBoardInfoElement) o1;
      UBoardInfoElement e2 = (UBoardInfoElement) o2;
      if (e1.getPrice() < e2.getPrice()) {
        return -1;
      } else if (e1.getPrice() > e2.getPrice()) {
        return 1;
      } else {
        return 0;
      }
    }
  }

  /**
   * �e�[�u���ɓo�^����Ă���SUBoardInfoElement�I�u�W�F�N�g��
   * �i�[���ꂽ�x�N�^��Ԃ��BUBoardInfoElement�I�u�W�F�N�g��,
   * ���i�̏����Ń\�[�g����Ă���B<br>
   * @return UBoardInfoElement�̊i�[����Ă���x�N�^
   */
  public Vector getBoardInfoElements() {
    Vector result = new Vector();
    Enumeration e = fTable.elements();
    while (e.hasMoreElements()) {
      result.addElement(e.nextElement());
    }
    Collections.sort(result, new UBoardInfoElementComparator());
    return result;
  }

  /**
   * �e�X�g�p���\�b�h
   */
  public static void main(String args[]) {
    UBoardInformation bi = new UBoardInformation();
    bi.setLastUpdateTime(new Date());
    bi.printOn();
    bi.addInformation(UOrder.SELL, UOrder.MARKET, -1, 50);
    bi.addInformation(UOrder.BUY, UOrder.MARKET, 3101, 20);
    bi.addInformation(UOrder.BUY, UOrder.LIMIT, 3000, 10);
    bi.addInformation(UOrder.BUY, UOrder.LIMIT, 3100, 50);
    bi.addInformation(UOrder.BUY, UOrder.LIMIT, 3000, 150);
    bi.setLastUpdateTime(new Date());
    bi.printOn();
    UBoardInformation copy = (UBoardInformation) bi.clone();
    bi.clear();
    bi.setLastUpdateTime(new Date());
    bi.printOn();
    System.out.println("****** clone ******");
    copy.printOn();
  }

}
