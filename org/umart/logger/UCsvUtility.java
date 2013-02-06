package org.umart.logger;

import java.util.*;

/**
 * CSV形式データを扱うためのユーティリティクラスです．
 * @author 小野　功
 */
public class UCsvUtility {

  /**
   * CSV形式に従う一行分の文字列を要素に分解し，それらの要素をArrayListに格納して返す．
   * @param 文字列
   * @return トークンの配列
   */
  public static ArrayList split(String line) {
    ArrayList result = new ArrayList();
    StringTokenizer st = new StringTokenizer(line, ",", true);
    String latestToken = "";
    while (true) {
      try {
        String token = st.nextToken();
        latestToken = token;
        if (token.equals(",")) {
          result.add("");
        } else {
          result.add(token);
          token = st.nextToken();
          latestToken = token;
          if (!token.equals(",")) {
            System.err.println("Error: Wrong format in UMemberLog.readLineIntoArray");
            System.exit(5);
          }
        }
      } catch (NoSuchElementException nsee) {
        if (latestToken.equals(",")) {
          result.add("");
        }
        break;
      }
    }
    return result;
  }

}