package org.umart.serverCore;

/**
 * �񂹂��Ƃ̖�艿�i�Ɩ�萔�ʂ�ێ����邽�߂̃N���X�ł��D
 * �܂�, ���Ȃ��̐߂ɂ��Ă͔���C�z�Ɣ����C�z��
 * �ێ����Ă��܂��D
 * @author �암 �T�i
 * @author �X�� �̕�
 * @author ���� ��
 */

public class UTodaysQuote {

  /** ������ */
  private String fBrandName;

  /** �� */
  private int fDate;

  /** �� */
  private int fSession;

  /** ��艿�i */
  private long fPrice;

  /** ��萔�� */
  private long fVolume;

  /** ����C�z */
  private long fAskedQuotation;

  /** �����C�z */
  private long fBidQuotation;

  /**
   * UTodaysQuote�I�u�W�F�N�g�̐���, ���������s���B<br>
   */
  public UTodaysQuote() {
    fBrandName = null;
    fDate = -1;
    fSession = -1;
    fPrice = -1;
    fVolume = -1;
    fAskedQuotation = -1;
    fBidQuotation = -1;
  }

  /**
   * UTodaysQuote�I�u�W�F�N�g�̐���, ���������s���B<br>
   * @param brandName ������
   * @param date ���t
   * @param session ��
   * @param price ��艿�i
   * @param volume ��萔��
   * @param askedQuotation ����C�z
   * @param bidQuotation �����C�z
   */
  public UTodaysQuote(String brandName, int date, int session, long price,
                       long volume, long askedQuotation, long bidQuotation) {
    fBrandName = brandName;
    fDate = date;
    fSession = session;
    fPrice = price;
    fVolume = volume;
    fAskedQuotation = askedQuotation;
    fBidQuotation = bidQuotation;
  }

  /**
   * UTodaysQuote�I�u�W�F�N�g�̕�����Ԃ��B
   * @return UTodaysQuote�I�u�W�F�N�g�̕���
   */
  public Object clone() {
    return new UTodaysQuote(fBrandName, fDate, fSession, fPrice,
                              fVolume, fAskedQuotation, fBidQuotation);
  }

  /**
   * ���������o�͂���B
   */
  public void printOn() {
    System.out.print("BrandName:" + fBrandName);
    System.out.print(", Date:" + fDate);
    System.out.print(", BoardNo:" + fSession);
    System.out.print(", Price:" + fPrice);
    System.out.print(", Volume:" + fVolume);
    System.out.print(", AskedQuotation:" + fAskedQuotation);
    System.out.println(", BidQuotation:" + fBidQuotation);
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
   * �߂�Ԃ��B
   * @return ��
   */
  public int getSession() {
    return fSession;
  }

  /**
   * ��艿�i��Ԃ��B
   * @return ��艿�i
   */
  public long getPrice() {
    return fPrice;
  }

  /**
   * ��萔�ʂ�Ԃ��B
   * @return ��萔��
   */
  public long getVolume() {
    return fVolume;
  }

  /**
   * ����C�z��Ԃ��B
   * @return ����C�z
   */
  public long getAskedQuotation() {
    return fAskedQuotation;
  }

  /**
   * �����C�z��Ԃ��B
   * @return �����C�z
   */
  public long getBidQuotation() {
    return fBidQuotation;
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
   * �߂�ݒ肷��B
   * @param session ��
   */
  public void setSession(int session) {
    fSession = session;
  }

  /**
   * ��艿�i��ݒ肷��B
   * @param price ��艿�i
   */
  public void setPrice(long price) {
    fPrice = price;
  }

  /**
   * ��萔�ʂ�ݒ肷��B
   * @param volume ��萔��
   */
  public void setVolume(long volume) {
    fVolume = volume;
  }

  /**
   * ����C�z��ݒ肷��B
   * @param askedQuotation ����C�z
   */
  public void setAskedQuotation(long askedQuotation) {
    fAskedQuotation = askedQuotation;
  }

  /**
   * �����C�z��ݒ肷��B
   * @param bidQuotation �����C�z
   */
  public void setBidQuotation(long bidQuotation) {
    fBidQuotation = bidQuotation;
  }

}
