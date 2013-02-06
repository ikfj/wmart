package org.umart.serverCore;

import java.io.*;

/**
 * �����(su)�̌������������N���X�ł��D
 * �������Ƃ��ẮC�����c���C����|�W�V�����C�����|�W�V������
 * �����Ă��܂��D
 * @author �암 �T�i
 * @author �X�� �̕�
 * @author ���� ��
 */
public class UExchangeAccount {

  /** ���[�U�[ID */
  private final int fUserID = 0;

  /** ���[�U�[�� */
  private final String fUserName = "su";

  /** �p�X���[�h */
  private final String fPasswd = "supasswd";

  /** �����c�� */
  private long fCash;

  /** ����|�W�V���� (�j�Y�҂̔���|�W�V�����������p��) */
  private long fSellPosition;

  /** �����|�W�V���� (�j�Y�҂̔����|�W�V�����������p��) */
  private long fBuyPosition;

  /**
   * ���������ꂽ������̌����𐶐�����D
   */
  public UExchangeAccount() {
    fCash = 0;
    fSellPosition = fBuyPosition = 0;
  }

  /**
   * UExchangeAccount�̕����𐶐�����D
   * @return UExchangeAccount�I�u�W�F�N�g�̕���
   */
  public Object clone() {
    UExchangeAccount result = new UExchangeAccount();
    result.fCash = fCash;
    result.fSellPosition = fSellPosition;
    result.fBuyPosition = fBuyPosition;
    return result;
  }

  /**
   * ���������o�͂���D
   * @param pw �o�͐�
   */
  public void printOn(PrintWriter pw) {
    try {
      pw.println("fUserID = " + fUserID);
      pw.println("fUserName = " + fUserName);
      pw.println("fPasswd = " + fPasswd);
      pw.println("fCash = " + fCash);
      pw.println("fSellPosition = " + fSellPosition);
      pw.println("fBuyPosition = " + fBuyPosition);
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
  }

  /**
   * ���[�U�[ID��Ԃ��B
   * @return ���[�U�[ID
   */
  public int getUserID() {
    return fUserID;
  }

  /**
   * ���[�U�[����Ԃ��D
   * @return ���[�U�[��
   */
  public String getUserName() {
    return fUserName;
  }

  /**
   * �p�X���[�h��Ԃ��D
   * @return �p�X���[�h
   */
  public String getPasswd() {
    return fPasswd;
  }

  /**
   * �����|�W�V������Ԃ��D
   * @return �����|�W�V����
   */
  public long getBuyPosition() {
    return fBuyPosition;
  }

  /**
   * ����|�W�V������Ԃ��D
   * @return ����|�W�V����
   */
  public long getSellPosition() {
    return fSellPosition;
  }

  /**
   * �����|�W�V������ݒ肷��D
   * @param buyPosition �����|�W�V����
   */
  public void setBuyPosition(long buyPosition) {
    fBuyPosition = buyPosition;
  }

  /**
   * ����|�W�V������ݒ肷��D
   * @param sellPosition ����|�W�V����
   */
  public void setSellPosition(long sellPosition) {
    fSellPosition = sellPosition;
  }

  /**
   * �����c����Ԃ��D
   * @return �����c��
   */
  public synchronized long getCash() {
    // System.err.println(Thread.currentThread().getName());
    return fCash;
  }

  /**
   * �����c����ݒ肷��D
   * @param cash �����c��
   */
  public synchronized void setCash(long cash) {
    // System.err.println(Thread.currentThread().getName());
    fCash = cash;
  }

  /**
   * cash�����݂̌����c���ɉ�����D
   * @param cash �����c���̑���
   */
  public synchronized void addCash(long cash) {
    // System.err.println(Thread.currentThread().getName());
    fCash += cash;
    //System.out.println(cash + "," + fCash);
  }

  /**
   * sellPosition�����݂̔���|�W�V�����ɉ�����D
   * @param sellPosition ����|�W�V�����̑���
   */
  public void addSellPosition(long sellPosition) {
    fSellPosition += sellPosition;
  }

  /**
   * buyPosition�����݂̔����|�W�V�����ɉ�����D
   * @param buyPosition �����|�W�V�����̑���
   */
  public void addBuyPosition(long buyPosition) {
    fBuyPosition += buyPosition;
  }

  /**
   * ����/�����|�W�V������0�ɏ���������D
   */
  public void clearPosition() {
    fSellPosition = 0;
    fBuyPosition = 0;
  }

}
