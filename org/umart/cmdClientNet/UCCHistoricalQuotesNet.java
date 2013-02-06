package org.umart.cmdClientNet;

import java.io.*;
import java.util.*;

import org.umart.cmdCore.*;


/**
 * UCCHistoricalQuotesNetクラスは, ネットワーク版クライアントにおいて実行される
 * HistoricalQuotesコマンドである.
 * @author 川部 祐司
 */
public class UCCHistoricalQuotesNet extends UCHistoricalQuotesCore implements
    IClientCmdNet {

  /** サーバーからの入力ストリーム */
  private BufferedReader fIn;

  /** サーバーへの出力ストリーム */
  private PrintWriter fOut;

  /**
   * Constructor for UCCHistoricalQuotesNet.
   */
  public UCCHistoricalQuotesNet() {
    super();
    fIn = null;
    fOut = null;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      fQuotesArray.clear();
      fOut.println(CMD_NAME + " " + fBrandName + " " + fNoOfDays);
      StringTokenizer st = new StringTokenizer(fIn.readLine());
      String token = st.nextToken();
      if (token.equals("+ACCEPT")) {
        fStatus.setStatus(true);
        String s = "";
        while (! (s = fIn.readLine()).equals("+OK")) {
          HashMap os = new HashMap();
          StringTokenizer ast = new StringTokenizer(s);
          os.put(STRING_BRAND_NAME, ast.nextToken());
          os.put(INT_DATE, Integer.valueOf(ast.nextToken()));
          os.put(LONG_START_PRICE, Long.valueOf(ast.nextToken()));
          os.put(LONG_HIGHEST_PRICE, Long.valueOf(ast.nextToken()));
          os.put(LONG_LOWEST_PRICE, Long.valueOf(ast.nextToken()));
          os.put(LONG_END_PRICE, Long.valueOf(ast.nextToken()));
          os.put(LONG_VOLUME, Long.valueOf(ast.nextToken()));
          fQuotesArray.add(os);
        }
      } else if (token.equals("+ERROR")) {
        fStatus.setStatus(false);
        int errCode = Integer.parseInt(st.nextToken());
        fStatus.setErrorCode(errCode);
        fStatus.setErrorMessage(fIn.readLine());
      } else {
        System.err.println(
            "Unexpected token in UCCHistoricalQuotesNet: " + token);
        System.exit(5);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }
    return fStatus;
  }

  /**
   * @see org.umart.cmdClientNet.IClientCmdNet#setConnection(BufferedReader, PrintWriter)
   */
  public void setConnection(BufferedReader br, PrintWriter pw) {
    fIn = br;
    fOut = pw;
  }
}
