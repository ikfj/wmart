package org.umart.serverCore;

import java.util.*;

/**
 * Quote���(UTodaysQuote��UHistoricalQuote)�������N���X�ł��D
 * @author �암 �T�i
 * @author �X�� �̕�
 * @author ���� ��
 */

public class UQuoteDB {

  /** UTodaysQuote�I�u�W�F�N�g�̃x�N�^ */
  private Vector fTodaysQuotes;

  /** UHistoricalQuote�I�u�W�F�N�g�̃x�N�^ */
  private Vector fHistoricalQuotes;

  /**
     UQuoteDB�I�u�W�F�N�g�̐���, ���������s���B<br>
   */
  public UQuoteDB() {
    fTodaysQuotes = new Vector();
    fHistoricalQuotes = new Vector();
  }

  /**
   * 1�ߕ��̏���ǉ�����B
   * @param brandName ������
   * @param date ���t
   * @param session ��
   * @param price ��艿�i
   * @param volume ��萔��
   * @param askedQuotation ����C�z
   * @param bidQuotation �����C�z
   */
  public void addTodaysQuote(String brandName, int date, int session,
                             long price, long volume,
                             long askedQuotation, long bidQuotation) {
    UTodaysQuote quote = new UTodaysQuote(brandName, date, session, price,
                                          volume, askedQuotation, bidQuotation);
    fTodaysQuotes.addElement(quote);
  }

  /**
   * �o�^����Ă���UTodaysQuote�I�u�W�F�N�g�̐���Ԃ��B
   * @return UTodaysQuote�I�u�W�F�N�g�̐�
   */
  public int getNoOfTodaysQuotes() {
    return fTodaysQuotes.size();
  }

  /**
   * index�Ԗڂ�UTodaysQuote�I�u�W�F�N�g��Ԃ��B
   * @param index ���Ԗڂ�UTodaysQuote��?
   * @return index�Ԗڂ�UTodaysQuote�I�u�W�F�N�g
   */
  public UTodaysQuote getTodaysQuote(int index) {
    if (index < 0 || index >= fTodaysQuotes.size()) {
      System.out.print("Error: ");
      System.out.print("index < 0 || index >= fTodaysQuotes.size() ");
      System.out.println("in UQuoteDB.getTodaysQuote");
      System.exit(5);
    }
    return (UTodaysQuote) fTodaysQuotes.elementAt(index);
  }

  /**
   * �������̏����k�񂵂�Historical Quote�֓o�^����B
   */
  public void update() {
    int size = fTodaysQuotes.size();
    if (size == 0) {
      return;
    }
    UTodaysQuote todaysQuote = getTodaysQuote(0);
    String brandName = todaysQuote.getBrandName();
    int date = todaysQuote.getDate();
    long startPrice = todaysQuote.getPrice();
    long highestPrice = startPrice;
    long lowestPrice = startPrice;
    long volume = todaysQuote.getVolume();
    for (int i = 1; i < size; ++i) {
      todaysQuote = getTodaysQuote(i);
      long price = todaysQuote.getPrice();
      if (price >= 0) {
        if (highestPrice < 0 || highestPrice < price) {
          highestPrice = price;
        }
        if (lowestPrice < 0 || lowestPrice > price) {
          lowestPrice = price;
        }
      }
      volume += todaysQuote.getVolume();
    }
    todaysQuote = getTodaysQuote(size - 1);
    long endPrice = todaysQuote.getPrice();
    UHistoricalQuote hq = new UHistoricalQuote(brandName, date, startPrice,
                                               highestPrice, lowestPrice,
                                               endPrice, volume);
    fHistoricalQuotes.addElement(hq);
    fTodaysQuotes.removeAllElements();
  }

  /**
   * �o�^����Ă���UHistoricalQuote�I�u�W�F�N�g�̐���Ԃ��B
   * @return UHistoricalQuote�I�u�W�F�N�g�̐�
   */
  public int getNoOfHistoricalQuotes() {
    return fHistoricalQuotes.size();
  }

  /**
   * index�Ԗڂ�UHistoricalQuote�I�u�W�F�N�g��Ԃ��B
   * @param index ���Ԗڂ�UHistoricalQuote��?
   * @return index�Ԗڂ�UHistoricalQuote�I�u�W�F�N�g
   */
  public UHistoricalQuote getHistoricalQuote(int index) {
    if (index < 0 || index >= fHistoricalQuotes.size()) {
      System.out.print("Error: ");
      System.out.print("index < 0 || ");
      System.out.print("index >= fHistoricalQuotes.size() ");
      System.out.println("in UQuoteDB.getHistoricalQuote");
      System.exit(5);
    }
    return (UHistoricalQuote) fHistoricalQuotes.elementAt(index);
  }

  /**
   * ���������o�͂���B
   */
  public void printOn() {
    System.out.println("****** Today's Quotes ********");
    for (int i = 0; i < getNoOfTodaysQuotes(); ++i) {
      getTodaysQuote(i).printOn();
    }
    System.out.println("****** Historical Quotes ********");
    for (int i = 0; i < getNoOfHistoricalQuotes(); ++i) {
      getHistoricalQuote(i).printOn();
    }
  }

  /**
   * �e�X�g���\�b�h�B
   */
  public static void main(String args[]) {
    UQuoteDB db = new UQuoteDB();
    db.addTodaysQuote("j30", 1, 1, 3000, 50, 0, 0);
    db.addTodaysQuote("j30", 1, 2, 3100, 20, 0, 0);
    db.addTodaysQuote("j30", 1, 3, 2950, 10, 0, 0);
    db.addTodaysQuote("j30", 1, 4, 3300, 100, 0, 0);
    db.printOn();
    db.update();
    db.printOn();
    db.addTodaysQuote("j30", 2, 1, -1, 0, 3000, 0);
    db.addTodaysQuote("j30", 2, 2, 4000, 20, 0, 0);
    db.addTodaysQuote("j30", 2, 3, 2000, 10, 0, 0);
    db.addTodaysQuote("j30", 2, 4, 3500, 100, 0, 0);
    db.printOn();
    db.update();
    db.printOn();
  }

}
