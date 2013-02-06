package org.umart.cmdClientNet;

import java.io.*;
import java.util.*;

import org.umart.cmdCore.*;


/**
 * UCCFuturePriceNetクラスは, ネットワーク版クライアントにおいて実行される
 * FuturePriceコマンドである.
 * @author 川部 祐司
 */
public class UCCFuturePriceNet extends UCFuturePriceCore implements
    IClientCmdNet {

  /** サーバーからの入力ストリーム */
  private BufferedReader fIn;

  /** サーバーへの出力ストリーム */
  private PrintWriter fOut;

  /**
   * Constructor for UCCFuturePriceNet.
   */
  public UCCFuturePriceNet() {
    super();
    fIn = null;
    fOut = null;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fFutureArray.clear();
    try {
      fOut.println(CMD_NAME + " " + fBrandName + " " + fNoOfSteps);
      StringTokenizer st = new StringTokenizer(fIn.readLine());
      String token = st.nextToken();
      if (token.equals("+ACCEPT")) {
        fStatus.setStatus(true);
        String line = null;
        while (! (line = fIn.readLine()).equals("+OK")) {
          st = new StringTokenizer(line);
          String brandName = st.nextToken();
          String timeInfo = st.nextToken();
          StringTokenizer st2 = new StringTokenizer(timeInfo, ":");
          Integer date = Integer.valueOf(st2.nextToken());
          Integer boardNo = Integer.valueOf(st2.nextToken());
          Integer noOfSteps = Integer.valueOf(st2.nextToken());
          Long price = Long.valueOf(st.nextToken());
          HashMap os = new HashMap();
          os.put(STRING_BRAND_NAME, brandName);
          os.put(INT_DAY, date);
          os.put(INT_BOARD_NO, boardNo);
          os.put(INT_STEP, noOfSteps);
          os.put(LONG_PRICE, price);
          fFutureArray.add(os);
        }
      } else if (token.equals("+ERROR")) {
        fStatus.setStatus(false);
        int errCode = Integer.parseInt(st.nextToken());
        fStatus.setErrorCode(errCode);
        fStatus.setErrorMessage(fIn.readLine());
      } else {
        System.err.println(
            "Unexpected token in UCCSpotPriceNet: " + token);
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
