package org.umart.serverCore;

/**
 * ���鉿�i�ɂ����锄�蒍�����Ɣ�����������ێ�����N���X�ł��D
 * UBoardInformation�N���X�����p���܂��D
 * @author �암 �T�i
 * @author �X�� �̕�
 * @author ���� ��
 */
public class UBoardInfoElement {

  /** ���i�B������, ���s�����̂Ƃ���-1���ݒ肳���B */
  private long fPrice;

  /** ���蒍���� */
  private long fSellVolume;

  /** ���������� */
  private long fBuyVolume;

  /**
   * UBoardInfoElement�̐�������я�����(���s����,
   * ����/����������:0)���s���B
   */
  public UBoardInfoElement() {
    fPrice = -1;
    fSellVolume = 0;
    fBuyVolume = 0;
  }

  /**
   * UBoardInfoElement�̐�������я��������s���B
   * @param price ���i
   * @param sellVolume ���蒍����
   * @param buyVolume ����������
   */
  public UBoardInfoElement(long price, long sellVolume, long buyVolume) {
    fPrice = price;
    fSellVolume = sellVolume;
    fBuyVolume = buyVolume;
  }

  /**
   * UBoardInfoElement�I�u�W�F�N�g�̕�����Ԃ��B
   * @return UBoardInfoElement�I�u�W�F�N�g
   */
  public Object clone() {
    return new UBoardInfoElement(fPrice, fSellVolume, fBuyVolume);
  }

  /**
   * ���i��Ԃ��B
   * @return ���i
   */
  public long getPrice() {
    return fPrice;
  }

  /**
   * ���蒍������Ԃ��B
   * @return ���蒍����
   */
  public long getSellVolume() {
    return fSellVolume;
  }

  /**
   * ������������Ԃ��B
   * @return ����������
   */
  public long getBuyVolume() {
    return fBuyVolume;
  }

  /**
   * ���i��ݒ肷��B
   * @param price ���i��ݒ肷��B
   */
  public void setPrice(long price) {
    fPrice = price;
  }

  /**
   * ���蒍������ݒ肷��B
   * @param sellVolume ���蒍����
   */
  public void setSellVolume(long sellVolume) {
    fSellVolume = sellVolume;
  }

  /**
   * ���蒍������volume��������B
   * @param volume ���蒍�����̑���
   */
  public void addToSellVolume(long volume) {
    fSellVolume += volume;
  }

  /**
   * ������������ݒ肷��B
   * @param buyVolume ����������
   */
  public void setBuyVolume(long buyVolume) {
    fBuyVolume = buyVolume;
  }

  /**
   * ������������volume��������B
   * @param volume �����������̑���
   */
  public void addToBuyVolume(long volume) {
    fBuyVolume += volume;
  }

  /**
   * ���s����(���i:-1), ����/������������0�ɏ���������B
   */
  public void clear() {
    fPrice = -1;
    fBuyVolume = fSellVolume = 0;
  }

  /**
   * ���������o�͂���B
   */
  public void printOn() {
    System.out.print("Price:" + fPrice);
    System.out.print(", fSellVolume:" + fSellVolume);
    System.out.println(", fBuyVolume:" + fBuyVolume);
  }

}
