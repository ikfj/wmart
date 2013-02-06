package org.umart.logger;

import java.io.*;
import java.util.*;

import org.umart.serverCore.*;


/**
 * �P�ߕ��̔��̃��O�������N���X�ł��D
 * @author ����@��
 */
public class UBoardLog {

  /** ���t���������߂̃L�[ */
  public static final String INT_DATE = "INT_DATE";

  /** �߂��������߂̃L�[ */
  public static final String INT_SESSION = "INT_SESSION";

  /** ���i���������߂̃L�[ */
  public static final String LONG_PRICE = "LONG_PRICE";

  /** ���������ʂ��������߂̃L�[ */
  public static final String LONG_SELL_VOLUME = "LONG_SELL_VOLUME";

  /** ���������ʂ��������߂̃L�[ */
  public static final String LONG_BUY_VOLUME = "LONG_BUY_VOLUME";

  /** �� */
  private int fDate;

  /** �� */
  private int fSession;

  /** ��� (SVMP�R�}���hBoardInformation�ɏ������`�Ŋi�[) */
  private ArrayList fBoardInformation;

  /**
   * �R���X�g���N�^
   * @param date ��
   * @param session ��
   */
  public UBoardLog(int date, int session) {
    fDate = date;
    fSession = session;
    fBoardInformation = new ArrayList();
  }

  /**
   * �R���X�g���N�^
   * @param date ��
   * @param session ��
   * @param boardInfo ���
   */
  public UBoardLog(int date, int session, UBoardInformation boardInfo) {
    this(date, session);
    Enumeration elements = boardInfo.getBoardInfoElements().elements();
    while (elements.hasMoreElements()) {
      UBoardInfoElement element = (UBoardInfoElement) elements.nextElement();
      long price = element.getPrice();
      long sellVolume = element.getSellVolume();
      long buyVolume = element.getBuyVolume();
      fBoardInformation.add(makeElement(date, session, price, sellVolume,
                                        buyVolume));
    }
  }

  /**
   * ���̗v�f�𐶐�����D
   * @param date ��
   * @param session ��
   * @param price ���i
   * @param sellVolume ��������
   * @param buyVolume ��������
   * @return ���̗v�f
   */
  private HashMap makeElement(int date, int session, long price,
                              long sellVolume, long buyVolume) {
    HashMap hash = new HashMap();
    hash.put(UBoardLog.INT_DATE, new Integer(date));
    hash.put(UBoardLog.INT_SESSION, new Integer(session));
    hash.put(UBoardLog.LONG_PRICE, new Long(price));
    hash.put(UBoardLog.LONG_SELL_VOLUME, new Long(sellVolume));
    hash.put(UBoardLog.LONG_BUY_VOLUME, new Long(buyVolume));
    return hash;
  }

  public void writeTo(PrintWriter pw) throws IOException {
    pw.println("Date,Session,Price,SellVolume,BuyVolume");
    Iterator itr = fBoardInformation.iterator();
    while (itr.hasNext()) {
      HashMap hash = (HashMap) itr.next();
      pw.print(hash.get(UBoardLog.INT_DATE).toString() + ",");
      pw.print(hash.get(UBoardLog.INT_SESSION).toString() + ",");
      pw.print(hash.get(UBoardLog.LONG_PRICE).toString() + ",");
      pw.print(hash.get(UBoardLog.LONG_SELL_VOLUME).toString() + ",");
      pw.println(hash.get(UBoardLog.LONG_BUY_VOLUME).toString());
    }
  }

  /**
   * ���̓X�g���[������ǂݍ��ށD
   * @param br ���̓X�g���[��
   * @throws IOException
   */
  public void readFrom(BufferedReader br) throws IOException {
    fBoardInformation.clear();
    br.readLine(); // skip the header
    String line = null;
    while ( (line = br.readLine()) != null) {
      StringTokenizer st = new StringTokenizer(line, ",");
      int date = Integer.parseInt(st.nextToken());
      int boardNo = Integer.parseInt(st.nextToken());
      long price = Long.parseLong(st.nextToken());
      long sellVolume = Long.parseLong(st.nextToken());
      long buyVolume = Long.parseLong(st.nextToken());
      HashMap hash = makeElement(date, boardNo, price, sellVolume, buyVolume);
      fBoardInformation.add(hash);
    }
  }

}