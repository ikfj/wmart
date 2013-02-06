package org.umart.cmdClientNet;

import java.io.*;
import java.util.*;

import org.umart.cmdCore.*;


/**
 * UCCTodaysQuotesNetクラスは, ネットワーク版クライアントにおいて実行される
 * TodaysQuotesコマンドである.
 * @author 川部 祐司
 */
public class UCCTodaysQuotesNet extends UCTodaysQuotesCore implements
    IClientCmdNet {

  /** サーバーからの入力ストリーム */
  private BufferedReader fIn;

  /** サーバーへの出力ストリーム */
  private PrintWriter fOut;

  /**
   * Constructor for UCCTodaysQuotesNet.
   */
  public UCCTodaysQuotesNet() {
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
      fOut.println(CMD_NAME + " " + fBrandName + " " + fNoOfBoards);
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
          os.put(INT_BOARD_NO, Integer.valueOf(ast.nextToken()));
          os.put(LONG_PRICE, Long.valueOf(ast.nextToken()));
          os.put(LONG_VOLUME, Long.valueOf(ast.nextToken()));
          os.put(LONG_ASKED_QUOTATION, Long.valueOf(ast.nextToken()));
          os.put(LONG_BID_QUOTATION, Long.valueOf(ast.nextToken()));
          fQuotesArray.add(os);
        }
      } else if (token.equals("+ERROR")) {
        fStatus.setStatus(false);
        int errCode = Integer.parseInt(st.nextToken());
        fStatus.setErrorCode(errCode);
        fStatus.setErrorMessage(fIn.readLine());
      } else {
        System.err.println(
            "Unexpected token in UCCTodaysQuotesNet: " + token);
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
