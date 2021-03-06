package org.umart.cmdClientNet;

import java.io.*;
import java.util.*;

import org.umart.cmdCore.*;


/**
 * UCCOrderRequestNetクラスは, ネットワーク版クライアントにおいて実行される
 * OrderRequestコマンドである.
 * @author 川部 祐司
 */
public class UCCOrderRequestNet extends UCOrderRequestCore implements
    IClientCmdNet {

  /** サーバーからの入力ストリーム */
  private BufferedReader fIn;

  /** サーバーへの出力ストリーム */
  private PrintWriter fOut;

  /**
   * Constructor for UCCOrderRequestNet.
   */
  public UCCOrderRequestNet() {
    super();
    fIn = null;
    fOut = null;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      fRequestInfo.clear();
      fOut.println(CMD_NAME + " " + fBrandName + " " + fNewRepay + " "
                   + fSellBuy + " " + fMarketLimit + " " + fPrice + " " +
                   fVolume);
      StringTokenizer st = new StringTokenizer(fIn.readLine());
      String token = st.nextToken();
      if (token.equals("+ACCEPT")) {
        fStatus.setStatus(true);
        fRequestInfo.put(LONG_ORDER_ID, Long.valueOf(fIn.readLine()));
        fRequestInfo.put(STRING_ORDER_TIME, fIn.readLine());
      } else if (token.equals("+ERROR")) {
        fStatus.setStatus(false);
        int errCode = Integer.parseInt(st.nextToken());
        fStatus.setErrorCode(errCode);
        fStatus.setErrorMessage(fIn.readLine());
      } else {
        System.err.println("Unexpected token in UCCOrderRequestNet: " + token);
        System.exit(5);
      }
      String okMsg = fIn.readLine();
      if (!okMsg.equals("+OK")) {
        System.err.println("Unexpected token in UCCOrderRequestNet: " + okMsg);
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
