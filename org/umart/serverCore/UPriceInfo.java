package org.umart.serverCore;

/**
 * UPriceInfoDB�ŗp�������X�e�b�v���̉��i������舵���N���X�ł��D
 * ���C�߁C�������i�C�敨���i��ێ����Ă��܂��D
 * @author �암 �T�i
 * @author �X�� �̕�
 * @author ���� ��
 */
public class UPriceInfo {

	/** ���i���������Ă��Ȃ��Ƃ��ɗp����萔 */
  public static final long INVALID_PRICE = -1;

  /** �� */
  private int fDate;

  /** �� */
  private int fSession;

  /** �������i */
  private long fSpotPrice;

  /** �敨���i�B������, ���i�����݂��Ȃ��ꍇ��, -1���ݒ肳���B */
  private long fFuturePrice;

  /**
   * UPriceInfo�I�u�W�F�N�g�̐����Ə��������s���B
   */
  public UPriceInfo() {
    fDate = 0;
    fSession = 0;
    fSpotPrice = UPriceInfo.INVALID_PRICE;
    fFuturePrice = UPriceInfo.INVALID_PRICE;
  }

  /**
   * �������i��Ԃ��B
   * @return �������i
   */
  public long getSpotPrice() {
    return fSpotPrice;
  }

  /**
   * �敨���i��Ԃ��B
   * @return �敨���i�B���i�����݂��Ȃ��ꍇ��, -1���Ԃ�B
   */
  public long getFuturePrice() {
    return fFuturePrice;
  }

  /**
   * �������i��ݒ肷��B
   * @param spotPrice �������i
   */
  public void setSpotPrice(long spotPrice) {
    fSpotPrice = spotPrice;
  }

  /**
   * �敨���i��ݒ肷��B
   * @param futurePrice �敨���i�B���i�����݂��Ȃ��ꍇ�� -1��ݒ肷��B
   */
  public void setFuturePrice(long futurePrice) {
    fFuturePrice = futurePrice;
  }

  /**
   * �߂�Ԃ��D
   * @return ��
   */
  public int getSession() {
    return fSession;
  }

  /**
   * ����Ԃ��D
   * @return ��
   */
  public int getDate() {
    return fDate;
  }

  /**
   * �߂�ݒ肷��D
   * @param session ��
   */
  public void setSession(int session) {
    fSession = session;
  }

  /**
   * ����ݒ肷��D
   * @param date ��
   */
  public void setDate(int date) {
    fDate = date;
  }

}
