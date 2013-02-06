package org.umart.serverCore;

import java.io.*;
import java.text.*;
import java.util.*;

import org.umart.logger.*;


/**
 * 価格系列の定義を管理するクラスです．
 * resources/csv/TimeSeriesDefinitions.csvに対応する情報を管理します．
 * @author 小野　功
 */
public class UTimeSeriesDefinitions {

  /** 時系列のニックネームを引くためのキー */
  public static final String STRING_NICKNAME = "STRING_NICKNAME";

  /** 時系列データのファイルネームを引くためのキー */
  public static final String STRING_FILENAME = "STRING_FILENAME";

  /** 銘柄名を引くためのキー */
  public static final String STRING_BRAND_NAME = "STRING_BRAND_NAME";

  /** 開始ステップを引くためのキー */
  public static final String INT_START_STEP = "INT_START_STEP";

  /** 最大取引日数を引くためのキー */
  public static final String INT_MAX_DATE = "INT_MAX_DATE";

  /** １日あたりの節数を引くためのキー */
  public static final String INT_NO_OF_SESSIONS_PER_DAY = "INT_NO_OF_SESSIONS_PER_DAY";

  /** 時系列定義データを格納するための配列 */
  private ArrayList fArray = new ArrayList();

  /**
   * コンストラクタ
   */
  public UTimeSeriesDefinitions() {
  }

  /**
   * 時系列定義データを読み込む．
   * @param br BufferedReader
   * @throws IOException
   */
  public void readFrom(BufferedReader br) throws IOException, ParseException {
    clear();
    br.readLine(); // Skip the header
    String line = null;
    int lineNo = 1;
    try {
      while ( (line = br.readLine()) != null) {
        ArrayList list = UCsvUtility.split(line);
        Iterator itr = list.iterator();
        addDefinition( (String) itr.next(), (String) itr.next(),
                      (String) itr.next(),
                      Integer.parseInt( (String) itr.next()),
                      Integer.parseInt( (String) itr.next()),
                      Integer.parseInt( (String) itr.next()));
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
   * 時系列定義データを書き出す
   * @param pw PrintWriter
   * @throws IOException
   */
  public void writeTo(PrintWriter pw) throws IOException {
    pw.println("Nickname,Filename,BrandName,StartStep,MaxDate,NoOfSessionsPerDay");
    Iterator itr = fArray.iterator();
    while (itr.hasNext()) {
      HashMap info = (HashMap) itr.next();
      pw.print(info.get(UTimeSeriesDefinitions.STRING_NICKNAME) + ",");
      pw.print(info.get(UTimeSeriesDefinitions.STRING_FILENAME) + ",");
      pw.print(info.get(UTimeSeriesDefinitions.STRING_BRAND_NAME) + ",");
      pw.print(info.get(UTimeSeriesDefinitions.INT_START_STEP) + ",");
      pw.print(info.get(UTimeSeriesDefinitions.INT_MAX_DATE) + ",");
      pw.println(info.get(UTimeSeriesDefinitions.INT_NO_OF_SESSIONS_PER_DAY) + "");
    }
  }

  /**
   * 価格系列定義のイテレータを返す．
   * @return 価格系列定義のイテレータ
   */
  public Iterator getTimeSeriesDefinitions() {
    return fArray.iterator();
  }

  /**
   * 価格系列の定義を全てクリアする．
   *
   */
  public void clear() {
    fArray.clear();
  }

  /**
   * 価格系列の定義を追加する．
   * @param nickname 価格系列のニックネーム
   * @param filename 価格系列のファイル名
   * @param brandName 銘柄名
   * @param startStep 開始ステップ
   * @param maxDate 取引日数
   * @param noOfSessionsPerDay １日あたりの節数
   */
  public void addDefinition(String nickname, String filename, String brandName,
                            int startStep, int maxDate, int noOfSessionsPerDay) {
    HashMap info = new HashMap();
    info.put(UTimeSeriesDefinitions.STRING_NICKNAME, nickname);
    info.put(UTimeSeriesDefinitions.STRING_FILENAME, filename);
    info.put(UTimeSeriesDefinitions.STRING_BRAND_NAME, brandName);
    info.put(UTimeSeriesDefinitions.INT_START_STEP, new Integer(startStep));
    info.put(UTimeSeriesDefinitions.INT_MAX_DATE, new Integer(maxDate));
    info.put(UTimeSeriesDefinitions.INT_NO_OF_SESSIONS_PER_DAY,
             new Integer(noOfSessionsPerDay));
    fArray.add(info);
  }

  /**
   * ニックネームがnicknameである価格系列定義を返す．
   * @param nickname
   * @return
   */
  public HashMap getDefinition(String nickname) {
    Iterator itr = fArray.iterator();
    while (itr.hasNext()) {
      HashMap info = (HashMap) itr.next();
      if (nickname.equals( (String) info.get(UTimeSeriesDefinitions.STRING_NICKNAME))) {
        return info;
      }
    }
    return null;
  }

  /**
   * テストメソッド
   * @param args
   */
  public static void main(String[] args) {
    UTimeSeriesDefinitions timeSeriesDefinitionLog = new UTimeSeriesDefinitions();
    try {
      BufferedReader br = new BufferedReader(new FileReader("resources/csv/TimeSeriesDefinitions.csv"));
      timeSeriesDefinitionLog.readFrom(br);
      br.close();
      PrintWriter pw = new PrintWriter(System.out, true);
      timeSeriesDefinitionLog.writeTo(pw);
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(5);
    }
  }

}
