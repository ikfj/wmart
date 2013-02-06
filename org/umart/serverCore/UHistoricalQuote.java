package org.umart.serverCore;

/**
 * �����Ƃ̎n�l, ���l, ���l, �I�l, �o������ێ����邽�߂̃N���X�ł��D
 * @author �암 �T�i
 * @author �X�� �̕�
 * @author ���� ��
 */
public class UHistoricalQuote {

  /** ������ */
  private String fBrandName;

  /** ���t */
  private int fDate;

  /** �n�l */
  private long fStartPrice;

  /** ���l */
  private long fHighestPrice;

  /** ���l */
  private long fLowestPrice;

  /** �I�l */
  private long fEndPrice;

  /** �o���� */
  private long fVolume;

  /**
   * UHistoricalQuote�I�u�W�F�N�g�̐���, ���������s���B
   */
  public UHistoricalQuote() {
    fBrandName = null;
    fDate = -1;
    fStartPrice = -1;
    fHighestPrice = -1;
    fLowestPrice = -1;
    fEndPrice = -1;
    fVolume = -1;
  }

  /**
   * UHistoricalQuote�I�u�W�F�N�g�̐���, ���������s���B
   * @param brandName ����
   * @param date ���t
   * @param startPrice �n�l
   * @param highestPrice ���l
   * @param lowestPrice ���l
   * @param endPrice �I�l
   * @param �o����
   */
  public UHistoricalQuote(String brandName, int date, 
  		                     long startPrice, long highestPrice, 
  		                     long lowestPrice, long endPrice,
                           long volume) {
    fBrandName = brandName;
    fDate = date;
    fStartPrice = startPrice;
    fHighestPrice = highestPrice;
    fLowestPrice = lowestPrice;
    fEndPrice = endPrice;
    fVolume = volume;
  }

  /**
   * UHistoricalQuote�I�u�W�F�N�g�̕�����Ԃ��B
   * @return UHistoricalQuote�I�u�W�F�N�g�̕���
   */
  public Object clone() {
    return new UHistoricalQuote(fBrandName, fDate, fStartPrice, fHighestPrice,
                                  fLowestPrice, fEndPrice, fVolume);
  }

  /**
   * ���������o�͂���B
   */
  public void printOn() {
    System.out.print("BrandName:" + fBrandName);
    System.out.print(", Date:" + fDate);
    System.out.print(", StartPrice:" + fStartPrice);
    System.out.print(", HighestPrice:" + fHighestPrice);
    System.out.print(", LowestPrice:" + fLowestPrice);
    System.out.print(", EndPrice:" + fEndPrice);
    System.out.println(", Volume:" + fVolume);
  }

  /**
   * ��������Ԃ��B
   * @return ������
   */
  public String getBrandName() {
    return fBrandName;
  }

  /**
   * ���t��Ԃ��B
   * @return ���t
   */
  public int getDate() {
    return fDate;
  }

  /**
   * �n�l��Ԃ��B
   * @return �n�l
   */
  public long getStartPrice() {
    return fStartPrice;
  }

  /**
   * ���l��Ԃ��B
   * @return ���l
   */
  public long getHighestPrice() {
    return fHighestPrice;
  }

  /**
   * ���l��Ԃ��B
   * @return ���l
   */
  public long getLowestPrice() {
    return fLowestPrice;
  }

  /**
   * �I�l��Ԃ��B
   * @return �I�l
   */
  public long getEndPrice() {
    return fEndPrice;
  }

  /**
   * �o������Ԃ��B
   * @return �o����
   */
  public long getVolume() {
    return fVolume;
  }

  /**
   * ��������ݒ肷��B
   * @param brandName ������
   */
  public void setBrandName(String brandName) {
    fBrandName = brandName;
  }

  /**
   * ���t��ݒ肷��B
   * @param date ���t
   */
  public void setDate(int date) {
    fDate = date;
  }

  /**
   * �n�l��ݒ肷��B
   * @param startPrice �n�l
   */
  public void setStartPrice(long startPrice) {
    fStartPrice = startPrice;
  }

  /**
   * ���l��ݒ肷��B
   * @param highestPrice ���l
   */
  public void setHighestPirce(long highestPrice) {
    fHighestPrice = highestPrice;
  }

  /**
   * ���l��ݒ肷��B
   * @param lowestPrice ���l
   */
  public void setLowestPrice(long lowestPrice) {
    fLowestPrice = lowestPrice;
  }

  /**
   * �I�l��ݒ肷��B
   * @param endPrice �I�l
   */
  public void setEndPrice(long endPrice) {
    fEndPrice = endPrice;
  }

  /**
   * �o������ݒ肷��B
   * @param volume �o����
   */
  public void setVolume(long volume) {
    fVolume = volume;
  }

}
