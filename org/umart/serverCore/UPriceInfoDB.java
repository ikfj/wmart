package org.umart.serverCore;

import java.io.*;
import java.text.*;
import java.util.*;


import org.umart.logger.*;

/**
 * 価格系列を扱うクラスです．
 * このクラスは，現在を指し示すポインタを保持しています．
 * resources/csv/j30.csvのデータフォーマットをサポートしています．
 * @author 川部 裕司
 * @author 森下 領平
 * @author 小野 功
 */

public class UPriceInfoDB {

  /** UPriceInfoElementオブジェクトを格納するためのベクタ */
  private Vector fPriceInfoArray;

  /** 現在のUPriceInfoElementを指し示すインデックス。
    先物価格が決定しているのはfPriceInfoArray[fCurrentPtr - 1]までで
    あることに注意。*/
  private int fCurrentPtr;

  /** 市場をどのUPriceInfoElementから開始するかを指し示すインデックス */
  private int fInitialPtr;

  /** サーバー運用日数 */
  private int fMaxDate;

  /** 一日当たりの節数 */
  private int fNoOfSessionsPerDay;

  /** 最大ステップ数 */
  private int fMaxSteps;

  /** 日 */
  private int fDate;

  /** 節 */
  private int fSession;

  /**
   * コンストラクタ
   */
  public UPriceInfoDB() {
    fPriceInfoArray = new Vector();
    fCurrentPtr = 0;
    fInitialPtr = 0;
    fMaxSteps = 0;
    fMaxDate = 0;
    fNoOfSessionsPerDay = 0;
    fDate = -1;
    fSession = -1;
  }

  /**
   * 開始ステップをポインタに設定する．
   * @param ptr 開始ステップ
   * @param maxDate 取引日数
   * @param noOfSessionsPerDay １日あたりの節数
   */
  public void initializePtr(int ptr, int maxDate, int noOfSessionsPerDay) {
    fMaxDate = maxDate;
    fNoOfSessionsPerDay = noOfSessionsPerDay;
    fMaxSteps = fMaxDate * fNoOfSessionsPerDay;
    if (ptr + fMaxSteps > fPriceInfoArray.size()) {
      System.err.println("ptr=" + ptr + ", maxSteps=" + fMaxSteps);
      System.err.println("fPriceInfoArray.size()=" + fPriceInfoArray.size());
      System.err.println("Error: initializePtr error in UPriceInfoDB");
      System.exit( -1);
    }
    fInitialPtr = ptr;
    fCurrentPtr = fInitialPtr;
    {
      for (int i = 0; i < ptr; i++) {
        UPriceInfo pi = (UPriceInfo) fPriceInfoArray.elementAt(i);
        pi.setFuturePrice(pi.getSpotPrice());
      }
    }
    {
      for (int i = ptr; i < fPriceInfoArray.size(); i++) {
        UPriceInfo pi = (UPriceInfo) fPriceInfoArray.elementAt(i);
        pi.setFuturePrice(UPriceInfo.INVALID_PRICE);
      }
    }
    fDate = 1;
    fSession = 1;
  }

  /**
   * 過去steps分の現物価格系列を返す．
   * @param steps 必要なステップ数
   * @return 現物価格系列．要素0が直近の価格である．
   */
  public long[] getSpotPrices(int steps) {
    if ( (fCurrentPtr - steps) < 0) {
      System.out.println("getSpotPrices Error!: adjust arraysize "
                         + steps + " -> " + fCurrentPtr);
      steps = fCurrentPtr;
    }
    long result[] = new long[steps];
    for (int i = 0; i < steps; i++) {
      UPriceInfo pi = (UPriceInfo) fPriceInfoArray.elementAt(fCurrentPtr - i -
          1);
      result[i] = pi.getSpotPrice();
    }
    return result;
  }

  /**
   * 過去steps分の先物価格系列を返す．
   * @param steps 必要なステップ数
   * @return 先物価格系列．要素0が直近の価格である．
   */
  public long[] getFuturePrices(int steps) {
    if ( (fCurrentPtr - steps) < 0) {
      System.out.println("getFuturePrices Error!: adjust arraysize "
                         + steps + " -> " + fCurrentPtr);
      steps = fCurrentPtr;
    }
    long result[] = new long[steps];
    for (int i = 0; i < steps; i++) {
      UPriceInfo pi = (UPriceInfo) fPriceInfoArray.elementAt(fCurrentPtr - i -
          1);
      result[i] = pi.getFuturePrice();
    }
    return result;
  }

  /**
   * 先物価格を設定し，ポインタを１ステップ進める．
   * @param futurePrice
   */
  public void addCurrentPriceAndIncrementPtr(long futurePrice) {
    UPriceInfo pi = (UPriceInfo) fPriceInfoArray.elementAt(fCurrentPtr);
    long spotPrice = pi.getSpotPrice();
    pi.setFuturePrice(futurePrice);
    pi.setDate(fDate);
    pi.setSession(fSession);
    incrementPtr();
    ++fSession;
    if (fSession > fNoOfSessionsPerDay) {
      ++fDate;
      fSession = 1;
    }
  }

  /**
   * 決算日の先物価格を設定する．
   *
   */
  public void setSettlementPrice() {
    UPriceInfo prevPi = (UPriceInfo)fPriceInfoArray.elementAt(fCurrentPtr - 1);
    UPriceInfo curPi = (UPriceInfo)fPriceInfoArray.elementAt(fCurrentPtr);
    long spotPrice = prevPi.getSpotPrice();
    curPi.setSpotPrice(spotPrice);
    curPi.setFuturePrice(spotPrice);
    curPi.setDate(fDate);
    curPi.setSession(fSession);
    incrementPtr();
  }

  /**
   * 入力ストリームから価格系列を読み込む．
   * @param br 入力ストリーム
   * @throws IOException
   * @throws ParseException
   */
  public void readFrom(BufferedReader br) throws IOException, ParseException {
    String s;
    br.readLine(); // skip the header
    int lineNo = 1;
    try {
      while ( (s = br.readLine()) != null) {
        ArrayList info = UCsvUtility.split(s);
        Iterator itr = info.iterator();
        int date = Integer.parseInt( (String) itr.next());
        int session = Integer.parseInt( (String) itr.next());
        long spotPrice = Long.parseLong( (String) itr.next());
        String tmp = (String) itr.next();
        long futurePrice = UPriceInfo.INVALID_PRICE;
        if (!tmp.equals("")) {
          futurePrice = Long.parseLong(tmp);
        }
        UPriceInfo pi = new UPriceInfo();
        pi.setDate(date);
        pi.setSession(session);
        pi.setSpotPrice(spotPrice);
        pi.setFuturePrice(session);
        fPriceInfoArray.addElement(pi);
        ++lineNo;
      }
    } catch (NumberFormatException nfe) {
      throw new ParseException("Error in UTimeSeriesDefinitionLog.readFrom",
                               lineNo);
    } catch (NoSuchElementException nsee) {
      throw new ParseException("Error in UTimeSeriesDefinitionLog.readFrom",
                               lineNo);
    }
  }

  /**
   * 価格系列を出力ストリームに書き出す．
   * @param pw 出力ストリーム
   * @throws IOException
   */
  public void writeTo(PrintWriter pw) throws IOException {
    pw.println("Date,Session,SpotPrice,FuturePrice");
    Enumeration e = fPriceInfoArray.elements();
    while (e.hasMoreElements()) {
      UPriceInfo pi = (UPriceInfo) e.nextElement();
      pw.print(pi.getDate() + ",");
      pw.print(pi.getSession() + ",");
      pw.print(pi.getSpotPrice() + ",");
      if (pi.getFuturePrice() == UPriceInfo.INVALID_PRICE) {
        pw.println();
      } else {
        pw.println(pi.getFuturePrice());
      }
    }
  }

  /**
   * これまでの価格系列を出力ストリームへ書き出す．
   * @param pw 出力ストリーム
   * @throws IOException
   */
  public void writePriceInfoBeforeCurrentPtr(PrintWriter pw) throws IOException {
    pw.println("Date,Session,SpotPrice,FuturePrice");
    for (int i = 0; i < fCurrentPtr; ++i) {
      UPriceInfo pi = (UPriceInfo) fPriceInfoArray.get(i);
      pw.print(pi.getDate() + ",");
      pw.print(pi.getSession() + ",");
      pw.print(pi.getSpotPrice() + ",");
      if (pi.getFuturePrice() == UPriceInfo.INVALID_PRICE) {
        pw.println();
      } else {
        pw.println(pi.getFuturePrice());
      }
    }
  }

  /**
   * 最新の価格系列を出力ストリームへ書き出す．
   * @param pw 出力ストリーム
   * @throws IOException
   */
  public void writeLatestPriceInfo(PrintWriter pw) throws IOException {
    UPriceInfo pi = (UPriceInfo) fPriceInfoArray.get(fCurrentPtr - 1);
    pw.print(pi.getDate() + ",");
    pw.print(pi.getSession() + ",");
    pw.print(pi.getSpotPrice() + ",");
    if (pi.getFuturePrice() == UPriceInfo.INVALID_PRICE) {
      pw.println();
    } else {
      pw.println(pi.getFuturePrice());
    }
  }

  /**
   * ポインタを１ステップ進める．
   *
   */
  private void incrementPtr() {
    if (fCurrentPtr > fInitialPtr + fMaxSteps) {
      System.err.println("Error: initializerPtr error in UPriceInfoDB");
      System.exit( -1);
    }
    ++fCurrentPtr;
  }

}
